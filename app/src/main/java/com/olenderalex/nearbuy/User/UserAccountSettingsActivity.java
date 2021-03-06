package com.olenderalex.nearbuy.User;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
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
import com.olenderalex.nearbuy.HomeActivity;
import com.olenderalex.nearbuy.MainActivity;
import com.olenderalex.nearbuy.Model.Users;
import com.olenderalex.nearbuy.R;
import com.olenderalex.nearbuy.Utils.Util;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.Objects;

public class UserAccountSettingsActivity extends AppCompatActivity {
    private CircleImageView profileImage;
    private EditText nameEt,phoneEt,addressEt,passwordEt,cityEt;
    private TextView updateAccountImageTextBtn , closeTextBtn;

    private Button saveBtn;
    private StorageTask uploadTask;

    private Uri imageUri;
    private String myUrl ="";
    private StorageReference storageImageReference;
    private DatabaseReference usersRef;

    private Users currentOnlineUser;
    private String parentDbName,userPhone;
    private boolean clicked = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        //Retrieve data of current user from local DB
        Paper.init(this);
        currentOnlineUser=new Users();
        currentOnlineUser= Paper.book().read(Util.currentOnlineUser);

        //Receive users personal information from Paper inner DB

        parentDbName = Paper.book().read(Util.currentDbName);
        userPhone=Paper.book().read(Util.currentLoginKey);

        storageImageReference= FirebaseStorage.getInstance().getReference()
                .child(Util.profileImagesStorageName);
        usersRef= FirebaseDatabase.getInstance().getReference()
                .child(parentDbName)
                .child(userPhone);

        profileImage=findViewById(R.id.setting_profile_image);
        nameEt=findViewById(R.id.settings_name);
        cityEt=findViewById(R.id.settings_city);
        passwordEt=findViewById(R.id.settings_password);
        phoneEt=findViewById(R.id.settings_phone_number);
        addressEt=findViewById(R.id.settings_address);
        updateAccountImageTextBtn=findViewById(R.id.settings_update_profile_pic);
        closeTextBtn=findViewById(R.id.settings_close);
        saveBtn=findViewById(R.id.settings_save);

        userInfoDisplay();
        closeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    fillUserInfo();

            }
        });
        updateAccountImageTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicked=true;
                CropImage.activity(imageUri).
                        setAspectRatio(1,1)
                        .start(UserAccountSettingsActivity.this);
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
            Toast.makeText(this,"Picture is not added",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(UserAccountSettingsActivity.this, UserAccountSettingsActivity.class));
            finish();
        }
    }





    private void fillUserInfo() {
        String phone=phoneEt.getText().toString().trim();
        String name=nameEt.getText().toString().trim();
        String  password=passwordEt.getText().toString().trim();
        if(TextUtils.isEmpty(phone) ){
            Toast.makeText(UserAccountSettingsActivity.this, "Enter your phone number", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(name)){
            Toast.makeText(UserAccountSettingsActivity.this,"Enter your name",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(UserAccountSettingsActivity.this,"Enter your password",Toast.LENGTH_SHORT).show();
        }
        else if(clicked){
            uploadImage();
        }else
        updateOnlyUserInfo();
    }




    //Update user personal information in Firebase

    private void updateOnlyUserInfo() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(parentDbName);

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put(Util.userName, nameEt.getText().toString());
        userMap.put(Util.userPassword, passwordEt.getText().toString());
        userMap.put(Util.userPhone, phoneEt.getText().toString());
        userMap.put(Util.userAddress, addressEt.getText().toString());
        userMap.put(Util.userCity, cityEt.getText().toString());

        ref.child(currentOnlineUser.getPhone()).updateChildren(userMap);
        Toast.makeText(this,"Your data will update soon",Toast.LENGTH_LONG).show();
        startActivity(new Intent(UserAccountSettingsActivity.this, HomeActivity.class));
        finish();
    }






    private void uploadImage() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update profile");
        progressDialog.setMessage("Account is updating");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        if (imageUri != null) {
            final StorageReference fileRef = storageImageReference.child(currentOnlineUser.getPhone() + ".jpg");
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

                                myUrl = Objects.requireNonNull(downloadUrl).toString();

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(parentDbName);

                                HashMap<String, Object> userMap = new HashMap<>();
                                userMap.put(Util.userName, nameEt.getText().toString());
                                userMap.put(Util.userPassword, passwordEt.getText().toString());
                                userMap.put(Util.userPhone, phoneEt.getText().toString());
                                userMap.put(Util.userAddress, addressEt.getText().toString());
                                userMap.put(Util.userImage, myUrl);

                                ref.child(currentOnlineUser.getPhone()).updateChildren(userMap);
                                progressDialog.dismiss();

                                startActivity(new Intent(UserAccountSettingsActivity.this, HomeActivity.class));
                                finish();

                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(UserAccountSettingsActivity.this, "Error account is not updated", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });
        } else {
            Toast.makeText(UserAccountSettingsActivity.this, "Image is not selected", Toast.LENGTH_SHORT).show();
        }
    }








    // Displaying updated online user information

    private void userInfoDisplay() {

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        if(snapshot.child(Util.userImage).exists()){
                            String image= (snapshot.child(Util.userImage).getValue()).toString();
                            String name= Objects.requireNonNull(snapshot.child(Util.userName).getValue()).toString();
                            String password= Objects.requireNonNull(snapshot.child(Util.userPassword).getValue()).toString();
                            String address= Objects.requireNonNull(snapshot.child(Util.userAddress).getValue()).toString();
                            String phone= Objects.requireNonNull(snapshot.child(Util.userPhone).getValue()).toString();

                           if(!image.equals(""))
                           {
                               Picasso.get().load(image).into(profileImage);
                           }

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