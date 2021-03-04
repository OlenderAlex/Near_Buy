package com.olenderalex.nearbuy.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import io.paperdb.Paper;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
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
import com.olenderalex.nearbuy.MainActivity;
import com.olenderalex.nearbuy.Model.Products;
import com.olenderalex.nearbuy.Model.Users;
import com.olenderalex.nearbuy.R;
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
    private String imageUri="";
    private String productID="";
    private ImageView favoriteEmptyIv,favoriteFilledIv;
    private FloatingActionButton goToCartIv;

    private Users currentOnlineUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        //Retrieve data of current user from local DB
        Paper.init(this);
        currentOnlineUser=new Users();
        currentOnlineUser= Paper.book().read(Util.currentOnlineUser);

        productID=getIntent().getStringExtra(Util.productId);

        addToCartBtn=findViewById(R.id.add_to_cart_btn_details);
        productImage=findViewById(R.id.product_image_details);
        productName=findViewById(R.id.product_name_details);
        productDescription=findViewById(R.id.product_description_details);
        productPrice=findViewById(R.id.product_price_details);
        favoriteEmptyIv=findViewById(R.id.favorite_empty_details);
        favoriteFilledIv=findViewById(R.id.favorite_filled_details);
        numberOfProductsBtn=findViewById(R.id.number_of_products_button_details);
        goToCartIv=findViewById(R.id.go_to_cart_details);

        getProductDetails(productID);
        isAlreadyInFavorite(productID);
        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addingToCartList();
            }
        });

        //add to favorites by clicking on heart icon
        favoriteEmptyIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToFavoriteList();
                favoriteEmptyIv.setClickable(false);
                favoriteEmptyIv.setVisibility(View.GONE);

                favoriteFilledIv.setClickable(true);
                favoriteFilledIv.setVisibility(View.VISIBLE);

            }
        });
        //Delete from favorites by clicking on heart icon
        favoriteFilledIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFromFavoriteList();
                favoriteFilledIv.setClickable(false);
                favoriteFilledIv.setVisibility(View.GONE);

                favoriteEmptyIv.setVisibility(View.VISIBLE);
                favoriteEmptyIv.setClickable(true);
            }
        });

        goToCartIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(ProductDetailsActivity.this
                        ,CartActivity.class);
                startActivity(intent);
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
                    imageUri=product.getImage();
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

        final DatabaseReference carListRef = FirebaseDatabase.getInstance()
                .getReference()
                .child(Util.cartListStDbName)
                .child(currentOnlineUser.getPhone());

        //Storing the data

        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put(Util.productId, productID);
        cartMap.put(Util.productName, productName.getText().toString());
        cartMap.put(Util.productPrice, productPrice.getText().toString());
        cartMap.put(Util.uploadedDate, saveCurrentDate);
        cartMap.put(Util.uploadedTime, saveCurrentTime);
        cartMap.put(Util.productsQuantity, numberOfProductsBtn.getNumber());
        cartMap.put(Util.productImage, imageUri);

        carListRef.child(productID)
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast toast=Toast.makeText(
                                                        ProductDetailsActivity.this
                                                        ,"Added to cart"
                                                        , Toast.LENGTH_LONG);
                                                toast.show();
                                            }

                                        }
                                    });
                        }

    //------------------------------------------------------------------------------------------------------------------

    private void addToFavoriteList() {
        final DatabaseReference favoritesListRef = FirebaseDatabase.getInstance()
                .getReference()
                .child(Util.favoriteProductsDbName)
                .child(currentOnlineUser.getPhone())
                .child(productID);

        //Storing the data

        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put(Util.productId, productID);
        cartMap.put(Util.productName, productName.getText().toString());
        cartMap.put(Util.productPrice, productPrice.getText().toString());
        cartMap.put(Util.productImage, imageUri);

        favoritesListRef.updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast toast=Toast.makeText(
                                    ProductDetailsActivity.this,"Added to favorites"
                                    , Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }});
    }


    //Deleting product from favorites by clicking on filled black heart
    private void deleteFromFavoriteList() {
        final DatabaseReference favoritesListRef = FirebaseDatabase.getInstance()
                .getReference()
                .child(Util.favoriteProductsDbName)
                .child(currentOnlineUser.getPhone());

        favoritesListRef.child(productID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(ProductDetailsActivity.this, "Product deleted"
                        , Toast.LENGTH_SHORT).show();
            }
        });
    }

//-------------  Checking if product already added to favorites
//-------------  and displaying filled heart icon if added

    private void isAlreadyInFavorite(final String productID) {
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();

       rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               if (snapshot.child(Util.favoriteProductsDbName)
                       .child(currentOnlineUser.getPhone())
                       .child(productID)
                       .exists())
               {
                   favoriteEmptyIv.setVisibility(View.GONE);
                   favoriteFilledIv.setVisibility(View.VISIBLE);
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });
    }
}

