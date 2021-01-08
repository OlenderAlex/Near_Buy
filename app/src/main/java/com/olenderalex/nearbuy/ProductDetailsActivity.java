package com.olenderalex.nearbuy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.olenderalex.nearbuy.Model.Products;
import com.olenderalex.nearbuy.Utils.Util;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity {

    private Button addToCartBtn;
    private ImageView productImage;
    private ElegantNumberButton numberOfProductsBtn;
    private TextView productPrice,productDescription  ,productName;

    private String productID="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        productID=getIntent().getStringExtra(Util.productId);

        addToCartBtn=findViewById(R.id.add_to_cart_btn_details);
        productImage=findViewById(R.id.product_image_details);
        productName=findViewById(R.id.product_name_details);
        productDescription=findViewById(R.id.product_description_details);
        productPrice=findViewById(R.id.product_price_details);
        numberOfProductsBtn=findViewById(R.id.number_of_products_button_details);

        getProductDetails(productID);

        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addingToCartList();
            }
        });

    }



    private void getProductDetails(String productID) {

        DatabaseReference productsRef= FirebaseDatabase.getInstance().getReference().child(Util.productsDbName);

        productsRef.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    Products product =snapshot.getValue(Products.class);

                    assert product != null;
                    productName.setText(product.getProductName());
                    productDescription.setText(product.getDescription());
                    productPrice.setText(product.getPrice());
                    Picasso.get().load(product.getImage()).into(productImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addingToCartList() {
        String saveCurrentTime, saveCurrentDate;
        Calendar calFroDate = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy");
        saveCurrentDate = currentDate.format(calFroDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        saveCurrentTime = currentTime.format(calFroDate.getTime());

        final DatabaseReference carListRef = FirebaseDatabase.getInstance().getReference().child(Util.cartListStDbName);

        //Storing the data

        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put(Util.productId, productID);
        cartMap.put(Util.productName, productName.getText().toString());
        cartMap.put(Util.productPrice, productPrice.getText().toString());
        cartMap.put(Util.uploadedDate, saveCurrentDate);
        cartMap.put(Util.uploadedTime, saveCurrentTime);
        cartMap.put(Util.productsQuantity, numberOfProductsBtn.getNumber());
        cartMap.put(Util.productDiscount, "");

        carListRef.child(Util.usersView).child(Util.currentOnlineUser.getPhone())
                .child(Util.usersProductsInCart).child(productID)
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            carListRef.child(Util.adminView).child(Util.currentOnlineUser.getPhone())
                                    .child(Util.orders).child(productID)
                                    .updateChildren(cartMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(ProductDetailsActivity.this,"Added to cart", Toast.LENGTH_LONG).show();

                                                Intent intent = new Intent(ProductDetailsActivity.this, HomeActivity.class);
                                                startActivity(intent);
                                            }

                                        }
                                    });
                        }
                    }
                });

    }
}