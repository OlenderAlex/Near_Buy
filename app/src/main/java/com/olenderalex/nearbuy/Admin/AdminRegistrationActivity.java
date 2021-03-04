package com.olenderalex.nearbuy.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.olenderalex.nearbuy.R;
import com.olenderalex.nearbuy.Utils.Util;

import java.util.HashMap;

public class AdminRegistrationActivity extends AppCompatActivity {

    private EditText nameEt,loginEt;
    private EditText passwordEt,confirmPasswordEt;
    private ProgressDialog loadingBar;
    private final String parentDbName=Util.adminDbName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_registration);


        Button signUpBtn = findViewById(R.id.admin_reg_create_new_account_btn);
        nameEt=findViewById(R.id.admin_reg_name_et);
        loginEt=findViewById(R.id.login_admin_reg);
        passwordEt=findViewById(R.id.pass_admin_reg);
        confirmPasswordEt=findViewById(R.id.confirm_pass_admin_reg);

        loadingBar=new ProgressDialog(this);
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateAccount();
            }
        });
    }
    //Creating an new account and storing the data in database
    private void CreateAccount(){
        String login=loginEt.getText().toString();
        String name=nameEt.getText().toString();
        String password=passwordEt.getText().toString();
        String confirmPass=confirmPasswordEt.getText().toString();

        if(TextUtils.isEmpty(login)){
            Toast.makeText(this,"Please Enter login for your new Admin.",Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty((name))){
            Toast.makeText(this,"Please Enter  name.",Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty((password))){
            Toast.makeText(this,"Please Enter a password.",Toast.LENGTH_LONG).show();
        }
        else if(!password.equals(confirmPass)){
            Toast.makeText(this,"Password are not confirmed",Toast.LENGTH_LONG).show();
        }
        else{
            loadingBar.setTitle("Create Account");
            loadingBar.setMessage("Please wait...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            //Checking if an account with the same email exists

            createAdminAccount(name , login ,password);
        }
    }


    //Checking if an account with the same email exists
    private void createAdminAccount(final String name,final String login,final  String password) {
        final DatabaseReference RootRef;
        RootRef= FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                // User registration
                if(!(snapshot.child(parentDbName).child(login).exists())){
                    HashMap<String, Object> userDataMap = new HashMap<>();
                        userDataMap.put(Util.adminLogin, login);
                        userDataMap.put(Util.adminPassword, password);
                        userDataMap.put(Util.adminName, name);

                    RootRef.child(parentDbName).child(login).updateChildren(userDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(AdminRegistrationActivity.this ,"New Admin account created.",Toast.LENGTH_LONG).show();
                                loadingBar.dismiss();

                                Intent intentLogin =new Intent(AdminRegistrationActivity.this,AdminHomeActivity.class);
                                intentLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intentLogin);
                                finish();
                            }else
                                Toast.makeText(AdminRegistrationActivity.this ,"Ooops ,there are some problems. Please try again",Toast.LENGTH_LONG).show();

                        }
                    });

                }
                else{
                    Toast.makeText(AdminRegistrationActivity.this ,"This phone number already exists",Toast.LENGTH_LONG).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}