package com.olenderalex.nearbuy.User;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.olenderalex.nearbuy.R;
import com.olenderalex.nearbuy.Utils.Util;

public class SearchByCategoryActivity extends AppCompatActivity {
    private ImageView vegetables;
    private ImageView cereals;
    private ImageView spices;
    private ImageView bakery;
    private ImageView cannedFood;
    private ImageView softDrinks;
    private ImageView dairy;
    private ImageView fruits;
    private ImageView eggs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_category);

        vegetables = findViewById(R.id.search_by_category_vegetables);
        cereals = findViewById(R.id.search_by_category_cereals);
        spices = findViewById(R.id.search_by_category_spices);
        bakery = findViewById(R.id.search_by_category_bakery);
        cannedFood = findViewById(R.id.search_by_category_canned_food);
        softDrinks = findViewById(R.id.search_by_category_soft_drinks);
        dairy = findViewById(R.id.search_by_category_dairy);
        fruits = findViewById(R.id.search_by_category_fruits);
        eggs = findViewById(R.id.search_by_category_eggs);

        vegetables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchByCategoryActivity.this, SearchProductsActivity.class);
                intent.putExtra(Util.productCategory,"vegetables");
                startActivity(intent);
            }
        });

        cereals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchByCategoryActivity.this, SearchProductsActivity.class);
                intent.putExtra(Util.productCategory,  "cereals");
                startActivity(intent);
            }
        });
        spices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchByCategoryActivity.this, SearchProductsActivity.class);
                intent.putExtra(Util.productCategory,  "spices");
                startActivity(intent);
            }
        });
        bakery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchByCategoryActivity.this, SearchProductsActivity.class);
                intent.putExtra(Util.productCategory,  "bakery");
                startActivity(intent);
            }
        });
        cannedFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchByCategoryActivity.this, SearchProductsActivity.class);
                intent.putExtra(Util.productCategory,  "canned food");
                startActivity(intent);
            }
        });
        softDrinks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchByCategoryActivity.this, SearchProductsActivity.class);
                intent.putExtra(Util.productCategory,  "soft drinks");
                startActivity(intent);
            }
        });
        dairy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchByCategoryActivity.this, SearchProductsActivity.class);
                intent.putExtra(Util.productCategory, "dairy");
                startActivity(intent);
            }
        });
        fruits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchByCategoryActivity.this, SearchProductsActivity.class);
                intent.putExtra(Util.productCategory,  "fruits");
                startActivity(intent);
            }
        });
        eggs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchByCategoryActivity.this, SearchProductsActivity.class);
                intent.putExtra(Util.productCategory,  "eggs");
                startActivity(intent);
            }
        });
    }

}