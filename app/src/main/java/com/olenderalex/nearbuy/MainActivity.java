
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

public class MainActivity extends AppCompatActivity {
    private Button loginBtnUser;

    private TextView adminSighUpTv;
    private Button userSignUpBtn;
    private String parentDbName;

    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginBtnUser = findViewById(R.id.btn_login_user);
        adminSighUpTv = findViewById(R.id.signup_admin);
        userSignUpBtn = findViewById(R.id.btn_user_signUp);
        loadingBar = new ProgressDialog(this);

        loginBtnUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentLogin = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intentLogin);
            }
        });

        userSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentReg = new Intent(MainActivity.this, PhoneVerificationActivity.class);
                intentReg.putExtra("Table name", Util.usersDbName);
                startActivity(intentReg);
            }
        });
        adminSighUpTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentReg = new Intent(MainActivity.this, AdminRegistrationActivity.class);
                intentReg.putExtra("Table name", Util.adminDbName);
                startActivity(intentReg);
            }
        });


        /*---------------------------------------------------------------------------------------------
        This block allow to user enter in app if User saved his login data in previous session
         */
        Paper.init(this);

        String userPhoneKey = Paper.book().read(Util.userPhoneKey);
        String userPasswordKey = Paper.book().read(Util.userPasswordKey);

        String sellerPhoneKey = Paper.book().read(Util.adminLoginKey);
        String sellerPasswordKey = Paper.book().read(Util.adminPasswordKey);

        parentDbName = Paper.book().read(Util.currentUserDbName);

        if (userPhoneKey != "" && userPasswordKey != "") {
            if (!TextUtils.isEmpty(userPhoneKey) && !TextUtils.isEmpty(userPasswordKey)) {
                allowAccountAccess(userPhoneKey, userPasswordKey);

                loadingBar.setTitle("You are logging in...");
                loadingBar.setMessage("Please wait...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
            }
        }

        if (userPhoneKey != "" && userPasswordKey != "") {
            if (!TextUtils.isEmpty(sellerPhoneKey) && !TextUtils.isEmpty(sellerPasswordKey)) {
                parentDbName = Util.adminDbName;
                allowAccountAccess(sellerPhoneKey, sellerPasswordKey);

                loadingBar.setTitle("You are logging in....");
                loadingBar.setMessage("Please wait...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
            }

        }
        //-----------------------------------------------------------------------------------------------------

    }


    private void allowAccountAccess(final String login, final String password) {
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
                                Toast.makeText(MainActivity.this, "Logged in successfully", Toast.LENGTH_LONG).show();
                                loadingBar.dismiss();
                                //Current online user
                                Util.currentOnlineUser = userData;
                                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                startActivity(intent);

                            } else {
                                loadingBar.dismiss();
                                Toast.makeText(MainActivity.this, "Incorrect password", Toast.LENGTH_LONG).show();
                            }

                        } else {
                            loadingBar.dismiss();
                            Toast.makeText(MainActivity.this,
                                    "Account with this login or phone number do not exists", Toast.LENGTH_LONG).show();
                        }
                    }


                    //Log in for admins
                    if (parentDbName.equals(Util.adminDbName)) {
                        Admin adminData = snapshot.child(parentDbName).child(login).getValue(Admin.class);
                        // Checking if account with entered logi exists in data base
                        if (Objects.requireNonNull(adminData).getLogin().equals(login)) {

                            if (adminData.getPassword().equals(password)) {
                                Toast.makeText(MainActivity.this, "Logged in successfully", Toast.LENGTH_LONG).show();
                                loadingBar.dismiss();
                                //Current online admin
                                Util.currentOnlineAdmin = adminData;
                                Intent intent = new Intent(MainActivity.this, AdminHomeActivity.class);
                                startActivity(intent);

                            } else {
                                loadingBar.dismiss();
                                Toast.makeText(MainActivity.this, "Incorrect password", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            loadingBar.dismiss();
                            Toast.makeText(MainActivity.this,
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
}



//
//        final DatabaseReference RootRef;
//        RootRef= FirebaseDatabase.getInstance().getReference();
//
//        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.child(parentDbName).child(phone).exists()){
//
//                    Users userData =snapshot.child(parentDbName).child(phone).getValue(Users.class);
//
//                    /*
//                    Checking if account with entered phone number exists in data base
//                     */
//                    if(Objects.requireNonNull(userData).getPhone().equals(phone))
//                    {
//
//                        if(userData.getPassword().equals(password)) {
//
//                            /*
//                            Entering as seller
//                             */
//                            if (parentDbName.equals(Util.adminDbName)) {
//
//                                loadingBar.dismiss();
//                                Intent intent = new Intent(MainActivity.this, SellerCategoryActivity.class);
//                                Util.currentOnlineUser=userData;
//                                startActivity(intent);
//                            }
//
//                             /*
//                            Entering as user
//                             */
//                            else {
//
//                                loadingBar.dismiss();
//                                Intent intentLogin = new Intent(MainActivity.this, HomeActivity.class);
//                                Util.currentOnlineUser=userData;
//                                startActivity(intentLogin);
//                            }
//                        }else
//                        {
//
//                            Toast.makeText(MainActivity.this,"Incorrect password",Toast.LENGTH_LONG).show();
//                        }
//                    }
//
//                }else{
//                    Toast.makeText(MainActivity.this,"Account with this phone number do not exists",Toast.LENGTH_LONG).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
