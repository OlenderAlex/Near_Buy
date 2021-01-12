package com.olenderalex.nearbuy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import io.paperdb.Paper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.olenderalex.nearbuy.Model.Admin;
import com.olenderalex.nearbuy.Utils.Util;
import com.olenderalex.nearbuy.Model.Users;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private EditText inputLogin, inputPassword;
    private Button loginButton;
    private ProgressDialog loadingBar;
    private String parentDbName=Util.usersDbName;
    private CheckBox chkBoxRememberMe;
    private TextView loginAdminTv;
    private TextView loginUserTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.btn_login);
        inputLogin = findViewById(R.id.phone_number_login);
        inputPassword = findViewById(R.id.pass_et_login);
        loadingBar = new ProgressDialog(this);
        loginAdminTv = findViewById(R.id.tv_login_admin);
        loginUserTv = findViewById(R.id.tv_login_user);


        chkBoxRememberMe = findViewById(R.id.login_remember_me_chkbox);
        Paper.init(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginUser();
            }
        });
        loginAdminTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parentDbName = Util.adminDbName;
                loginAdminTv.setVisibility(View.GONE);
                loginUserTv.setVisibility(View.VISIBLE);
            }
        });
        loginUserTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parentDbName = Util.usersDbName;
                loginUserTv.setVisibility(View.GONE);
                loginAdminTv.setVisibility(View.VISIBLE);
            }
        });



    }


    /*
    Checking if the data that user typed is correct
     */
    public void LoginUser() {
        String login = inputLogin.getText().toString();
        String password = inputPassword.getText().toString();

        //check if phone is entered
        if (TextUtils.isEmpty(login)) {
            Toast.makeText(this, "Please Enter your phone number.", Toast.LENGTH_LONG).show();
        }
        else
        //check if password is entered
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please Enter a password.", Toast.LENGTH_LONG).show();
        } else {


            allowAccountAccess(login, password);
            loadingBar.setTitle("Log in...");
            loadingBar.setMessage("Please wait...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
        }
    }



    /*
    Comparing the entered account data with realtime database.
    Allowing or denying access to user account
     */

    private void allowAccountAccess(final String login, final String password) {

        checkBoxCheck(login,password);



        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(parentDbName).child(login).exists()) {


                    //Log in for users
                    if (parentDbName.equals(Util.usersDbName)) {
                        Users userData = snapshot.child(parentDbName).child(login).getValue(Users.class);

                        // Checking if account with entered phone number exists in data base
                        if (Objects.requireNonNull(userData).getPhone().equals(login)) {
                            if (userData.getPassword().equals(password)) {
                                Toast.makeText(LoginActivity.this, "Logged in successfully", Toast.LENGTH_LONG).show();
                                loadingBar.dismiss();
                                //Current online user
                                Util.currentOnlineUser = userData;
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);

                            } else {
                                loadingBar.dismiss();
                                Toast.makeText(LoginActivity.this, "Incorrect password", Toast.LENGTH_LONG).show();
                            }

                        } else {
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this,
                                    "Account with this login or phone number do not exists", Toast.LENGTH_LONG).show();
                        }
                    }

                    loadingBar.dismiss();
                    //Log in for admins
                    if (parentDbName.equals(Util.adminDbName)) {
                        Admin adminData = snapshot.child(parentDbName).child(login).getValue(Admin.class);
                        // Checking if account with entered logi exists in data base
                        if (Objects.requireNonNull(adminData).getLogin().equals(login)) {

                            if (adminData.getPassword().equals(password)) {
                                Toast.makeText(LoginActivity.this, "Logged in successfully", Toast.LENGTH_LONG).show();
                                loadingBar.dismiss();
                                //Current online admin
                                Util.currentOnlineAdmin = adminData;
                                Intent intent = new Intent(LoginActivity.this, AdminHomeActivity.class);
                                startActivity(intent);

                            } else {
                                loadingBar.dismiss();
                                Toast.makeText(LoginActivity.this, "Incorrect password", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this,
                                    "Account with this login or phone number do not exists", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkBoxCheck(final String login, final String password) {

        //Saving user account data in android phone memory for staying logged in
        if (chkBoxRememberMe.isChecked()) {
            if (parentDbName.equals(Util.usersDbName)) {
                Paper.book().write(Util.userPhoneKey, login);
                Paper.book().write(Util.userPasswordKey, password);
                Paper.book().write(Util.currentUserDbName, parentDbName);

            }

            else {
                Paper.book().write(Util.adminLoginKey, login);
                Paper.book().write(Util.adminPasswordKey, password);
                Paper.book().write(Util.currentUserDbName, parentDbName);
            }
        }
    }

}
