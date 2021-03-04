package com.olenderalex.nearbuy.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import io.paperdb.Paper;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.olenderalex.nearbuy.MainActivity;
import com.olenderalex.nearbuy.Model.Users;
import com.olenderalex.nearbuy.R;
import com.olenderalex.nearbuy.Utils.Util;

import java.util.concurrent.TimeUnit;

public class PhoneVerificationActivity extends AppCompatActivity {

    private Button getCodeBtn;
    private EditText etPhoneNumber;

    private static final String TAG = "PhoneVerificationActivity";
    private String phoneNumber ="";
    private String forgotPassword="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);


        forgotPassword=getIntent().getExtras().get(Util.userPassword).toString();
        getCodeBtn = findViewById(R.id.get_code_verification);
        etPhoneNumber = findViewById(R.id.etPhoneNumber_verification);

        getCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = etPhoneNumber.getText().toString();
                if (phoneNumber.isEmpty())
                    Toast.makeText(PhoneVerificationActivity.this,
                            "Enter your phone number", Toast.LENGTH_SHORT).show();
                else {
                    //verify phone number
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber, 60, TimeUnit.SECONDS, PhoneVerificationActivity.this,
                            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                @Override
                                public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                                }

                                @SuppressLint("LongLogTag")
                                @Override
                                public void onVerificationFailed(FirebaseException e) {
                                    Log.d(TAG, "onVerificationFailed:"+e.getLocalizedMessage());
                                }

                                @Override
                                public void onCodeSent(final String verificationId,
                                                       PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                    super.onCodeSent(verificationId, forceResendingToken);
                                    //
                                    Dialog dialog = new Dialog(PhoneVerificationActivity.this);
                                    dialog.setContentView(R.layout.verify_popup);

                                    final EditText etVerifyCode = dialog.findViewById(R.id.enter_code_et);
                                    Button btnVerifyCode = dialog.findViewById(R.id.verify_phone_btn);
                                    btnVerifyCode.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String verificationCode = etVerifyCode.getText().toString();
                                            if(verificationId.isEmpty()) return;
                                            //create a credential
                                            PhoneAuthCredential credential=PhoneAuthProvider.
                                                    getCredential(verificationId,verificationCode);

                                          //If user clicked on forgot password
                                           if(forgotPassword.equals(Util.forgotPassword)){
                                               checkVerificationAndGetUserData(credential);
                                            }
                                           //If user want to sign up
                                           else
                                           {
                                               checkVerification(credential);
                                           }
                                        }
                                    });

                                    dialog.show();
                                }
                            });
                }
            }
        });

    }

    //
    private void checkVerification(PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            phoneNumber =etPhoneNumber.getText().toString();
                            Intent intent = new Intent(PhoneVerificationActivity.this,
                                    UserRegistrationActivity.class);
                            intent.putExtra(Util.userPhone, phoneNumber);
                            startActivity(intent);
                            finish();
                        }else {
                            Log.d(TAG, "onComplete:"+task.getException().getLocalizedMessage());
                        }
                    }
                });
    }

    //
    private void checkVerificationAndGetUserData(PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            phoneNumber=etPhoneNumber.getText().toString();
                            getUserInfo(phoneNumber);

                        }else {
                            Log.d(TAG, "onComplete:"+task.getException().getLocalizedMessage());
                        }
                    }
                });
    }

    //Getting user data and saving in local storage for account access
    private void getUserInfo(final String phone) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(Util.usersDbName).child(phone).exists()) {
                    Users userData = snapshot.child(Util.usersDbName).child(phone).getValue(Users.class);

                            //Saving user account data in android phone memory for account access
                            Paper.book().write(Util.currentLoginKey, userData.getPhone());
                            Paper.book().write(Util.currentPasswordKey, userData.getPassword());
                            Paper.book().write(Util.currentDbName, Util.usersDbName);

                    Intent intent = new Intent(PhoneVerificationActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(PhoneVerificationActivity.this ,
                            "No user with this phone number . Please try another phone number",Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}