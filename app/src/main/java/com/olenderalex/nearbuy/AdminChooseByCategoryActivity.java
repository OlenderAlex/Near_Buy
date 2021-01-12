package com.olenderalex.nearbuy;

import androidx.appcompat.app.AppCompatActivity;
import io.paperdb.Paper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.olenderalex.nearbuy.Utils.Util;

public class AdminChooseByCategoryActivity extends AppCompatActivity {

    private ImageView computers;
    private ImageView appliances;
    private ImageView smartphones;
    private ImageView clothes;
    private ImageView toys;
    private ImageView sportingGoods;
    private ImageView food;
    private ImageView fruitsAndVegetables;
    private ImageView forPets;

    private Button logoutBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_choose_by_category);

        computers=findViewById(R.id.category_comps);
        appliances=findViewById(R.id.category_appliances);
        smartphones=findViewById(R.id.category_phones);
        clothes=findViewById(R.id.category_clothes);
        toys=findViewById(R.id.category_toys);
        sportingGoods=findViewById(R.id.category_sport);
        food=findViewById(R.id.category_food);
        fruitsAndVegetables=findViewById(R.id.category_fruits);
        forPets=findViewById(R.id.category_pets);

        computers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(AdminChooseByCategoryActivity.this, AdminAddNewProductActivity.class);
                intent.putExtra(Util.productCategory,"Computers");
                startActivity(intent);
            }
        });

        appliances.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(AdminChooseByCategoryActivity.this, AdminAddNewProductActivity.class);
                intent.putExtra(Util.productCategory,"Appliances");
                startActivity(intent);
            }
        });
        smartphones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(AdminChooseByCategoryActivity.this, AdminAddNewProductActivity.class);
                intent.putExtra(Util.productCategory,"Smartphones");
                startActivity(intent);
            }
        });
        clothes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(AdminChooseByCategoryActivity.this, AdminAddNewProductActivity.class);
                intent.putExtra(Util.productCategory,"Watches");
                startActivity(intent);
            }
        });
        toys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(AdminChooseByCategoryActivity.this, AdminAddNewProductActivity.class);
                intent.putExtra(Util.productCategory,"Toys");
                startActivity(intent);
            }
        });
        sportingGoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(AdminChooseByCategoryActivity.this, AdminAddNewProductActivity.class);
                intent.putExtra(Util.productCategory,"Sporting Goods");
                startActivity(intent);
            }
        });
        food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(AdminChooseByCategoryActivity.this, AdminAddNewProductActivity.class);
                intent.putExtra(Util.productCategory,"Food");
                startActivity(intent);
            }
        });
        fruitsAndVegetables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(AdminChooseByCategoryActivity.this, AdminAddNewProductActivity.class);
                intent.putExtra(Util.productCategory,"Fruits And Vegetables");
                startActivity(intent);
            }
        });
        forPets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(AdminChooseByCategoryActivity.this, AdminAddNewProductActivity.class);
                intent.putExtra(Util.productCategory,"For Pets");
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
                startActivity(intent);
            }
        });
    }}