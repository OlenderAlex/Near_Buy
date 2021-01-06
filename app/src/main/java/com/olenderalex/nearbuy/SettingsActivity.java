package com.olenderalex.nearbuy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.olenderalex.nearbuy.Utils.Util;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageActivity;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {
    private CircleImageView profileImage;
    private EditText nameEt,phoneEt,addressEt,passwordEt;
    private TextView updateAccountImageTextBtn , closeTextBtn, saveTextButton;

    private StorageTask uploadTask;

    private Uri imageUri;
    private String myUrl ="";
    private StorageReference storageImageReference;

    private String parentDbName;
    private boolean clicked = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Paper.init(this);

        storageImageReference= FirebaseStorage.getInstance().getReference().child(Util.profileImagesStorageName);
        parentDbName = Paper.book().read(Util.currentUserDbName);

        Log.i("dbname",parentDbName);

        profileImage=findViewById(R.id.setting_profile_image);
        nameEt=findViewById(R.id.settings_name);
        passwordEt=findViewById(R.id.settings_password);
        phoneEt=findViewById(R.id.settings_phone_number);
        addressEt=findViewById(R.id.settings_address);
        updateAccountImageTextBtn=findViewById(R.id.settings_update_profile_pic);
        closeTextBtn=findViewById(R.id.settings_close);
        saveTextButton=findViewById(R.id.settings_save);

        userInfoDisplay(profileImage,nameEt, phoneEt,addressEt);


        closeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clicked){

                    userInfoSaved();
                }
                else{

                    updateOnlyUserInfo();
                }
            }
        });


        updateAccountImageTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicked=true;
                CropImage.activity(imageUri).
                        setAspectRatio(1,1)
                        .start(SettingsActivity.this);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode== CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE&&resultCode==RESULT_OK&&
                data!=null){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri=result.getUri();

            profileImage.setImageURI(imageUri);

        }
        else{
            Toast.makeText(this,"Picture is not added",Toast.LENGTH_SHORT);
            startActivity(new Intent(SettingsActivity.this,SettingsActivity.class));
            finish();
        }
    }





    private void userInfoSaved() {

        if(TextUtils.isEmpty(nameEt.getText().toString())){
            Toast.makeText(SettingsActivity.this,"Enter your name",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(passwordEt.getText().toString())){
            Toast.makeText(SettingsActivity.this,"Enter your password",Toast.LENGTH_SHORT).show();
        }if(TextUtils.isEmpty(phoneEt.getText().toString())){
            Toast.makeText(SettingsActivity.this,"Enter your phone number",Toast.LENGTH_SHORT).show();
        }if(TextUtils.isEmpty(addressEt.getText().toString())){
            Toast.makeText(SettingsActivity.this,"Enter your address",Toast.LENGTH_SHORT).show();
        }
        else if(clicked){
            uploadImage();
        }
    }

    private void uploadImage() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update profile");
        progressDialog.setMessage("Account is updating");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (imageUri != null) {
            final StorageReference fileRef = storageImageReference.child(Util.currentOnlineUser.getPhone() + ".jpg");

            uploadTask = fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if (!task.isSuccessful()) {

                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).
                    addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {

                                Uri downloadUrl = task.getResult();

                                myUrl = downloadUrl.toString();

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(parentDbName);

                                HashMap<String, Object> userMap = new HashMap<>();
                                userMap.put(Util.userName, nameEt.getText().toString());
                                userMap.put(Util.userPassword, passwordEt.getText().toString());
                                userMap.put(Util.userPhone, phoneEt.getText().toString());
                                userMap.put(Util.userAddress, addressEt.getText().toString());
                                userMap.put(Util.userImage, myUrl);

                                ref.child(Util.currentOnlineUser.getPhone()).updateChildren(userMap);


                                progressDialog.dismiss();

                                startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
                                finish();

                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(SettingsActivity.this, "Error account is not updated", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });
        } else {
            Toast.makeText(SettingsActivity.this, "Image is not selected", Toast.LENGTH_SHORT).show();
        }
    }


    private void updateOnlyUserInfo() {


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Util.currentUserDbName);

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put(Util.userName, nameEt.getText().toString());
        userMap.put(Util.userPassword, passwordEt.getText().toString());
        userMap.put(Util.userPhone, phoneEt.getText().toString());
        userMap.put(Util.userAddress, addressEt.getText().toString());

        ref.child(Util.currentOnlineUser.getPhone()).updateChildren(userMap);

        startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
        finish();



    }










    // Displaying current online user information

    private void userInfoDisplay(final CircleImageView profileImage, final EditText nameEt,
                                 final EditText phoneEt, final EditText addressEt) {


        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                .child(Util.currentUserDbName).child(Util.currentOnlineUser.getPhone());

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        if(snapshot.child(Util.userImage).exists()){
                            String image=snapshot.child(Util.userImage).getValue().toString();
                            String name=snapshot.child(Util.userName).getValue().toString();
                            String password=snapshot.child(Util.userPassword).getValue().toString();
                            String address=snapshot.child(Util.userAddress).getValue().toString();
                            String phone=snapshot.child(Util.userPhone).getValue().toString();

                            Picasso.get().load(image).into(profileImage);
                            nameEt.setText(name);
                            phoneEt.setText(phone);
                            passwordEt.setText(password);
                            addressEt.setText(address);

                        }
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}