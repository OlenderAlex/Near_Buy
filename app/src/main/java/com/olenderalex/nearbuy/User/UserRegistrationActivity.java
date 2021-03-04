
package com.olenderalex.nearbuy.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.olenderalex.nearbuy.MainActivity;
import com.olenderalex.nearbuy.R;
import com.olenderalex.nearbuy.Utils.Util;

import java.util.HashMap;

public class UserRegistrationActivity extends AppCompatActivity {

    private Button signUpBtn;
    private EditText nameEt;
    private TextView phoneEt;
    private EditText passwordEt,confirmPasswordEt;
    private ProgressDialog loadingBar;
    private String parentDbName=Util.usersDbName;
    private String phoneExtra="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        phoneExtra = getIntent().getExtras().get(Util.userPhone).toString();

        signUpBtn=findViewById(R.id.registr_create_new_account_btn);
        nameEt=findViewById(R.id.register_name_et);
        passwordEt=findViewById(R.id.register_password_et);
        confirmPasswordEt=findViewById(R.id.register_confirm_password_et);
        phoneEt=findViewById(R.id.register_phone_tv);

        phoneEt.setText("Your phone : " +phoneExtra);
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
        String phone=phoneExtra;
        String name=nameEt.getText().toString();
        String password=passwordEt.getText().toString();
        String confirmPass=confirmPasswordEt.getText().toString();

        if(TextUtils.isEmpty(phone)){
            Toast.makeText(this,"Please Enter your phone number.",Toast.LENGTH_LONG).show();
        }
        else

            if(TextUtils.isEmpty((name))){
            Toast.makeText(this,"Please Enter your name.",Toast.LENGTH_LONG).show();
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

            ValidatePhoneNumber(name , phone ,password);
        }
    }


    //Checking if an account with the same email exists
    private void ValidatePhoneNumber(final String name,final String phone,final  String password) {
        final DatabaseReference RootRef;
        RootRef= FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                // User registration
                if(!(snapshot.child(parentDbName).child(phone).exists())){
                    HashMap<String, Object> userDataMap = new HashMap<>();
                        userDataMap.put(Util.userPhone, phone);
                        userDataMap.put(Util.userPassword, password);
                        userDataMap.put(Util.userName, name);
                        userDataMap.put(Util.userAddress, "");
                        userDataMap.put(Util.userCity, "");
                         userDataMap.put(Util.userImage, "");


                    RootRef.child(parentDbName).child(phone).updateChildren(userDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(UserRegistrationActivity.this,
                                        "Your account created.",Toast.LENGTH_LONG).show();
                                loadingBar.dismiss();

                                Intent intentLogin =new Intent(UserRegistrationActivity.this,
                                        MainActivity.class);
                                startActivity(intentLogin);
                            }else
                                Toast.makeText(UserRegistrationActivity.this ,
                                        "Ooops ,there are some problems. Please try again",Toast.LENGTH_LONG).show();

                        }
                    });

                }
                else{
                    Toast.makeText(UserRegistrationActivity.this ,
                            "This phone number already exists",Toast.LENGTH_LONG).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}