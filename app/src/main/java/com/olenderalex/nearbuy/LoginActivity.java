package com.olenderalex.nearbuy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import io.paperdb.Paper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.olenderalex.nearbuy.Utils.Util;
import com.olenderalex.nearbuy.Model.Users;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private EditText inputPhoneNumber, inputPassword;
    private Button loginButton;
    private ProgressDialog loadingBar;


    private String parentDbName;

    private CheckBox chkBoxRememberMe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.btn_login);
        inputPhoneNumber = findViewById(R.id.phone_number_login);
        inputPassword = findViewById(R.id.pass_et_login);
        loadingBar = new ProgressDialog(this);


        chkBoxRememberMe = findViewById(R.id.login_remember_me_chkbox);
        Paper.init(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginUser();
            }
        });

        parentDbName = getIntent().getExtras().get("Table name").toString();

    }


    /*
    Checking if the data that user typed is correct
     */
    public void LoginUser() {
        String phone = inputPhoneNumber.getText().toString();
        String password = inputPassword.getText().toString();

        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Please Enter your phone number.", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please Enter a password.", Toast.LENGTH_LONG).show();
        } else {


            AllowAccountAccess(phone, password);
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

    private void AllowAccountAccess(final String phone, final String password) {

        //Saving user account data in android phone memory for staying logged in
        if (chkBoxRememberMe.isChecked()) {
            if (parentDbName.equals(Util.usersDbName)) {
                Paper.book().write(Util.userPhoneKey, phone);
                Paper.book().write(Util.userPasswordKey, password);
                Paper.book().write(Util.currentUserDbName, parentDbName);

            }

            else {
                Paper.book().write(Util.adminPhoneKey, phone);
                Paper.book().write(Util.adminPasswordKey, password);
                Paper.book().write(Util.currentUserDbName, parentDbName);
            }
        }


        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(parentDbName).child(phone).exists()) {

                    Users userData = snapshot.child(parentDbName).child(phone).getValue(Users.class);

                    // Checking if account with entered phone number exists in data base
                    if (Objects.requireNonNull(userData).getPhone().equals(phone)) {

                        if (userData.getPassword().equals(password)) {

                            /*
                            Entering a shop owner account
                             */
                            if (parentDbName.equals(Util.adminDbName)) {

                                Toast.makeText(LoginActivity.this, "Logged in successfully", Toast.LENGTH_LONG).show();
                                loadingBar.dismiss();

                                //Current online user
                                Util.currentOnlineUser= userData;

                                Intent intent = new Intent(LoginActivity.this, SellerCategoryActivity.class);
                                startActivity(intent);
                            }


                            /*
                            Entering a user account
                             */

                            else if (parentDbName.equals(Util.usersDbName)) {
                                Toast.makeText(LoginActivity.this, "Logged in successfully", Toast.LENGTH_LONG).show();
                                loadingBar.dismiss();
                                //Current online user
                                Util.currentOnlineUser= userData;

                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);

                            }

                        } else {
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this, "Incorrect password", Toast.LENGTH_LONG).show();
                        }
                    }

                } else {
                    loadingBar.dismiss();
                    Toast.makeText(LoginActivity.this,
                            "Account with this phone number do not exists", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
