
package com.olenderalex.nearbuy.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.olenderalex.nearbuy.R;
import com.olenderalex.nearbuy.Utils.Util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class AdminAddNewProductActivity extends AppCompatActivity {

    private String categoryName,description,price,productName;
    private Button addNewProductBtn;
    private EditText inputProductNameEt,inputDescriptionEt,inputPriceEt;
    private ImageView addProductImageIv;

    private static final int GALLERY_IMG_PICKED=1;
    private Uri imageUri;

    private String productRandomKey,downloadImageUrl,saveCurrentDate,saveCurrentTime;
    private StorageReference productsImagesRef;

    private DatabaseReference productsDataRef;

    private ProgressDialog loadingBar;
    private CheckBox chkBoxIsDeal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);

        /*
        receive name of chosen category
        */


        categoryName= getIntent().getExtras().get(Util.productCategory).toString();
        Toast.makeText(AdminAddNewProductActivity.this,categoryName,Toast.LENGTH_LONG).show();


        /* getting Reference to "Products" database table*/


        productsDataRef=FirebaseDatabase.getInstance().getReference().child(Util.productsDbName);


        /* getting Reference to "Product images" database table*/

        productsImagesRef= FirebaseStorage.getInstance().getReference().child(Util.productsImagesStorageName);

        addNewProductBtn=findViewById(R.id.btn_add_product);
        inputProductNameEt=findViewById(R.id.product_name);
        inputDescriptionEt=findViewById(R.id.product_description);
        inputPriceEt=findViewById(R.id.product_price);
        addProductImageIv=findViewById(R.id.select_product_image);
        chkBoxIsDeal=findViewById(R.id.is_deal_chkbox);
        /* Choosing new product image*/
        addProductImageIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        /* Adding new product image
         */
        addNewProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateProductData();
            }
        });
        loadingBar = new ProgressDialog(this);
    }

    /*
    Method allow to user add images from phone gallery to app
     */
    private void openGallery(){
        Intent galleryIntent=new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,GALLERY_IMG_PICKED);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GALLERY_IMG_PICKED && resultCode==RESULT_OK && data!=null){
            imageUri=data.getData();
            addProductImageIv.setImageURI(imageUri);
        }
    }



    /*Validation for adding new product*/


    private void validateProductData()
    {
        description = inputDescriptionEt.getText().toString();
        price = inputPriceEt.getText().toString();
        productName = inputProductNameEt.getText().toString().toUpperCase();


        if (imageUri == null)
        {
            Toast.makeText(this, "Product image is mandatory...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(price))
        {
            Toast.makeText(this, "Please write product Price...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(productName))
        {
            Toast.makeText(this, "Please write product name...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            storeProductImage();
        }
    }



    private void storeProductImage()
    {
        loadingBar.setTitle("Add New Product");
        loadingBar.setMessage("Dear Admin, please wait while we are adding the new product.");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();


        //Creating a unique key for each product
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("ddMMyyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HHmmss");
        saveCurrentTime = currentTime.format(calendar.getTime());
        productRandomKey = productName+saveCurrentDate + saveCurrentTime;


        final StorageReference filePath = productsImagesRef.child(imageUri.getLastPathSegment() + productRandomKey + ".jpg");

        final UploadTask uploadTask = filePath.putFile(imageUri);


        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                String message = e.toString();
                Toast.makeText(AdminAddNewProductActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                    {
                        if (!task.isSuccessful())
                        {
                            throw Objects.requireNonNull(task.getException());
                        }

                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task)
                    {
                        if (task.isSuccessful())
                        {
                            downloadImageUrl = Objects.requireNonNull(task.getResult()).toString();
                            saveProductInfoToDatabase();
                        }
                    }
                });
            }
        });
    }



    private void saveProductInfoToDatabase()
    {
        HashMap <String ,Object> productMap = new HashMap<>();
        productMap.put(Util.productId,productRandomKey);
        productMap.put(Util.uploadedDate,saveCurrentDate);
        productMap.put(Util.uploadedTime,saveCurrentTime);
        productMap.put(Util.productImage,downloadImageUrl);
        productMap.put(Util.productCategory,categoryName);
        productMap.put(Util.productPrice,price);
        productMap.put(Util.productName,productName);
        productMap.put(Util.productDescription,description);

        //Checking if admin selected product as a Deal
        if(chkBoxIsDeal.isChecked())
        {
            productMap.put(Util.dealPrice,Util.deal);
        }
        else
        {
            productMap.put(Util.dealPrice,Util.notDeal);
        }

        //storing date to DB
        productsDataRef.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            Intent intent = new Intent(AdminAddNewProductActivity.this, AdminChooseByCategoryActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();

                            loadingBar.dismiss();
                            Toast.makeText(AdminAddNewProductActivity.this, "Product is added successfully..", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            loadingBar.dismiss();
                            String message = Objects.requireNonNull(task.getException()).toString();
                            Toast.makeText(AdminAddNewProductActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
