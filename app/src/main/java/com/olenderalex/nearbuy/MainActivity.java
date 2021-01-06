
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
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.olenderalex.nearbuy.Utils.Util;
import com.olenderalex.nearbuy.Model.Users;

public class MainActivity extends AppCompatActivity {
    private Button loginBtnUser;
    private Button loginBtnSeller;
    private Button sellerSighUpBtn;
    private Button userSignUpBtn;
    private String parentDbName;

    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginBtnUser = findViewById(R.id.btn_login_user);
        sellerSighUpBtn = findViewById(R.id.btn_seller_signUp);
        userSignUpBtn = findViewById(R.id.btn_user_signUp);
        loadingBar = new ProgressDialog(this);
        loginBtnSeller = findViewById(R.id.btn_login_seller);




        loginBtnUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentLogin = new Intent(MainActivity.this, LoginActivity.class);
                intentLogin.putExtra("Table name", Util.usersDbName);
                startActivity(intentLogin);
            }
        });
        loginBtnSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentLogin = new Intent(MainActivity.this, LoginActivity.class);
                intentLogin.putExtra("Table name", Util.sellerDbName);
                startActivity(intentLogin);
            }
        });
        userSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentReg = new Intent(MainActivity.this, RegistrationActivity.class);
                intentReg.putExtra("Table name", Util.usersDbName);
                startActivity(intentReg);
            }
        });
        sellerSighUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentReg = new Intent(MainActivity.this, RegistrationActivity.class);
                intentReg.putExtra("Table name", Util.sellerDbName);
                startActivity(intentReg);
            }
        });


        /*---------------------------------------------------------------------------------------------
        This block allow to user enter in app if User saved his login data in previous session
         */
        Paper.init(this);

        String userPhoneKey = Paper.book().read(Util.userPhoneKey);
        String userPasswordKey = Paper.book().read(Util.userPasswordKey);
        parentDbName = Paper.book().read(Util.currentUserDbName);

        String sellerPhoneKey = Paper.book().read(Util.sellerPhoneKey);
        String sellerPasswordKey = Paper.book().read(Util.sellerPasswordKey);


        if (userPhoneKey != "" && userPasswordKey != "") {
            if (!TextUtils.isEmpty(userPhoneKey) && !TextUtils.isEmpty(userPasswordKey)) {
                allowAccess(userPhoneKey, userPasswordKey);

                loadingBar.setTitle("You are logging in...");
                loadingBar.setMessage("Please wait...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
            }
        }

        if (sellerPhoneKey != "" && sellerPasswordKey != "") {
            if (!TextUtils.isEmpty(sellerPhoneKey) && !TextUtils.isEmpty(sellerPasswordKey)) {
                parentDbName= Util.sellerDbName;
                allowAccess(sellerPhoneKey, sellerPasswordKey);

                loadingBar.setTitle("You are logging in....");
                loadingBar.setMessage("Please wait...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
            }

        }
        //-----------------------------------------------------------------------------------------------------

    }

    private void allowAccess(final String phone, final String password) {

        final DatabaseReference RootRef;
        RootRef= FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(parentDbName).child(phone).exists()){

                    Users userData =snapshot.child(parentDbName).child(phone).getValue(Users.class);

                    /*
                    Checking if account with entered phone number exists in data base
                     */
                    if(userData.getPhone().equals(phone))
                    {

                        if(userData.getPassword().equals(password)) {

                            /*
                            Entering as seller
                             */
                            if (parentDbName.equals(Util.sellerDbName)) {

                                loadingBar.dismiss();
                                Intent intent = new Intent(MainActivity.this, SellerCategoryActivity.class);
                                Util.currentOnlineUser=userData;
                                startActivity(intent);
                            }

                             /*
                            Entering as user
                             */
                            else {

                                loadingBar.dismiss();
                                Intent intentLogin = new Intent(MainActivity.this, HomeActivity.class);
                                Util.currentOnlineUser=userData;
                                startActivity(intentLogin);
                            }
                        }else
                        {

                            Toast.makeText(MainActivity.this,"Incorrect password",Toast.LENGTH_LONG).show();
                        }
                    }

                }else{
                    Toast.makeText(MainActivity.this,"Account with this phone number do not exists",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}