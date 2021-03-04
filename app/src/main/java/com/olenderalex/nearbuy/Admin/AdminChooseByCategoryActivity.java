package com.olenderalex.nearbuy.Admin;

import androidx.appcompat.app.AppCompatActivity;
import io.paperdb.Paper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.olenderalex.nearbuy.MainActivity;
import com.olenderalex.nearbuy.R;
import com.olenderalex.nearbuy.Utils.Util;

public class AdminChooseByCategoryActivity extends AppCompatActivity {

    private ImageView vegetables;
    private ImageView cereals;
    private ImageView spices;
    private ImageView bakery;
    private ImageView cannedFood;
    private ImageView softDrinks;
    private ImageView dairy;
    private ImageView fruits;
    private ImageView eggs;

    private Button logoutBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_choose_by_category);

        vegetables =findViewById(R.id.category_vegetables);
        cereals =findViewById(R.id.category_cereals);
        spices =findViewById(R.id.category_spices);
        bakery =findViewById(R.id.category_bakery);
        cannedFood=findViewById(R.id.category_canned_food);
        softDrinks =findViewById(R.id.category_soft_drinks);
        dairy =findViewById(R.id.category_dairy);
        fruits =findViewById(R.id.category_fruits);
        eggs =findViewById(R.id.category_eggs);

        vegetables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(AdminChooseByCategoryActivity.this, AdminAddNewProductActivity.class);
                intent.putExtra(Util.productCategory,"vegetables");
                startActivity(intent);
            }
        });

        cereals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(AdminChooseByCategoryActivity.this, AdminAddNewProductActivity.class);
                intent.putExtra(Util.productCategory,"cereals");
                startActivity(intent);
            }
        });
        spices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(AdminChooseByCategoryActivity.this, AdminAddNewProductActivity.class);
                intent.putExtra(Util.productCategory,"spices");
                startActivity(intent);
            }
        });
        bakery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(AdminChooseByCategoryActivity.this, AdminAddNewProductActivity.class);
                intent.putExtra(Util.productCategory,"bakery");
                startActivity(intent);
            }
        });
        cannedFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(AdminChooseByCategoryActivity.this, AdminAddNewProductActivity.class);
                intent.putExtra(Util.productCategory,"canned food");
                startActivity(intent);
            }
        });
        softDrinks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(AdminChooseByCategoryActivity.this, AdminAddNewProductActivity.class);
                intent.putExtra(Util.productCategory,"soft drinks");
                startActivity(intent);
            }
        });
        dairy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(AdminChooseByCategoryActivity.this, AdminAddNewProductActivity.class);
                intent.putExtra(Util.productCategory,"dairy");
                startActivity(intent);
            }
        });
        fruits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(AdminChooseByCategoryActivity.this, AdminAddNewProductActivity.class);
                intent.putExtra(Util.productCategory,"fruits");
                startActivity(intent);
            }
        });
        eggs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(AdminChooseByCategoryActivity.this, AdminAddNewProductActivity.class);
                intent.putExtra(Util.productCategory,"eggs");
                startActivity(intent);
            }
        });



        //Logout-------------------------------------------------------------------
        logoutBtn=findViewById(R.id.logout_btn_seller_category);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Delete seller's password and phone number from phone memory
                Paper.book().destroy();
                Intent intent = new Intent(AdminChooseByCategoryActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent =new Intent(AdminChooseByCategoryActivity.this, AdminHomeActivity.class);
        startActivity(intent);
    }
}