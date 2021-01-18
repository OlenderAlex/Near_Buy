package com.olenderalex.nearbuy;

import androidx.appcompat.app.AppCompatActivity;
import io.paperdb.Paper;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.olenderalex.nearbuy.Utils.Util;

public class AdminHomeActivity extends AppCompatActivity {

    private Button logoutBtn;
    private LinearLayout newOrdersLL,addNewProductLL;
    private LinearLayout manageProductsLL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        newOrdersLL=findViewById(R.id.layout_new_orders_admin);
        addNewProductLL=findViewById(R.id.layout_add_new_products_admin);
        manageProductsLL=findViewById(R.id.layout_manage_products_admin);


        newOrdersLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AdminHomeActivity.this, AdminNewOrdersActivity.class);
                startActivity(intent);
            }
        });

        addNewProductLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHomeActivity.this, AdminChooseByCategoryActivity.class);
                startActivity(intent);
            }
        });
        manageProductsLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AdminHomeActivity.this, AdminDisplayAllProductsActivity.class);
                startActivity(intent);
            }
        });



        //Logout-------------------------------------------------------------------
        logoutBtn=findViewById(R.id.logout_btn_admin_home);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Delete seller's password and phone number from phone memory
                Paper.book().destroy();
                Intent intent = new Intent(AdminHomeActivity.this, MainActivity.class); intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
//------------------------------------------------------------------------------------------------------
    }
}