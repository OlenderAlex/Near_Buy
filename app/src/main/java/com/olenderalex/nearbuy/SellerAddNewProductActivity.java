
package com.olenderalex.nearbuy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.paperdb.Paper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
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
import com.olenderalex.nearbuy.Utils.Util;

import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class SellerAddNewProductActivity extends AppCompatActivity {

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_add_new_product);

        /*
        receive name of chosen category
        */


        categoryName= getIntent().getExtras().get(Util.productCategory).toString();
        Toast.makeText(SellerAddNewProductActivity.this,categoryName,Toast.LENGTH_LONG).show();


        /* getting Reference to "Products" database table*/


        productsDataRef=FirebaseDatabase.getInstance().getReference().child(Util.productsDbName);


        /* getting Reference to "Product images" database table*/

        productsImagesRef= FirebaseStorage.getInstance().getReference().child(Util.productsImagesStorageName);

        addNewProductBtn=findViewById(R.id.btn_add_product);
        inputProductNameEt=findViewById(R.id.product_name);
        inputDescriptionEt=findViewById(R.id.product_description);
        inputPriceEt=findViewById(R.id.product_price);
        addProductImageIv=findViewById(R.id.select_product_image);

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
        else if (TextUtils.isEmpty(description))
        {
            Toast.makeText(this, "Please write product description...", Toast.LENGTH_SHORT).show();
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
            StoreProductInformation();
        }
    }



    private void StoreProductInformation()
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
                Toast.makeText(SellerAddNewProductActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
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
                            SaveProductInfoToDatabase();
                        }
                    }
                });
            }
        });
    }



    private void SaveProductInfoToDatabase()
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

        productsDataRef.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            Intent intent = new Intent(SellerAddNewProductActivity.this, SellerCategoryActivity.class);
                            startActivity(intent);

                            loadingBar.dismiss();
                            Toast.makeText(SellerAddNewProductActivity.this, "Product is added successfully..", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            loadingBar.dismiss();
                            String message = Objects.requireNonNull(task.getException()).toString();
                            Toast.makeText(SellerAddNewProductActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
