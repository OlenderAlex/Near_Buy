package com.olenderalex.nearbuy.Admin;

import androidx.appcompat.app.AppCompatActivity;
import io.paperdb.Paper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.olenderalex.nearbuy.MainActivity;
import com.olenderalex.nearbuy.Model.Admin;
import com.olenderalex.nearbuy.R;
import com.olenderalex.nearbuy.Utils.Util;

public class AdminHomeActivity extends AppCompatActivity {

    private LinearLayout newOrdersLL,addNewProductLL;
    private LinearLayout manageProductsLL,shippedOrdersLL;
    private LinearLayout createNewAdminLL,deleteAdminAccountLL;
    private LinearLayout waitingForShipmentLL,deliveredOrdersLL;
    private TextView ownerTv;

    private Admin currentOnlineAdmin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        //Retrieve current admin data from inner storage
        Paper.init(this);
        currentOnlineAdmin=new Admin();
        currentOnlineAdmin=Paper.book().read(Util.currentOnlineAdmin);


        newOrdersLL=findViewById(R.id.layout_new_orders_admin);
        addNewProductLL=findViewById(R.id.layout_add_new_products_admin);
        manageProductsLL=findViewById(R.id.layout_manage_products_admin);
        shippedOrdersLL=findViewById(R.id.layout_shipped_orders_admin);
        createNewAdminLL=findViewById(R.id.layout_owner_register_new_admin);
        waitingForShipmentLL=findViewById(R.id.layout_waiting_for_shipping_admin);
        deliveredOrdersLL=findViewById(R.id.layout_delivered_orders_admin);
        deleteAdminAccountLL=findViewById(R.id.layout_owner_delete_admin);

        ownerTv=findViewById(R.id.owner_tv);

        // Go to AdminNewOrdersActivity if clicked
        newOrdersLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHomeActivity.this, AdminNewOrdersActivity.class);
                startActivity(intent);
            }
        });
        waitingForShipmentLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHomeActivity.this, AdminWaitingForShipmentActivity.class);
                startActivity(intent);
            }
        });
        shippedOrdersLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHomeActivity.this, AdminShippedOrdersActivity.class);
                startActivity(intent);
            }
        });
        deliveredOrdersLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHomeActivity.this, AdminDeliveredOrdersActivity.class);
                startActivity(intent);
            }
        });



        // Go to AdminChooseByCategoryActivity if clicked
        addNewProductLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHomeActivity.this, AdminChooseByCategoryActivity.class);
                startActivity(intent);
            }
        });

        // Go to AdminDisplayAllProductsActivity if clicked
        manageProductsLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHomeActivity.this, AdminDisplayAllProductsActivity.class);
                startActivity(intent);
            }
        });

        //Show additional options for store owner account
        if(currentOnlineAdmin.getLogin().equals(Util.ownerLoginKey)){
            createNewAdminLL.setVisibility(View.VISIBLE);
            ownerTv.setVisibility(View.VISIBLE);
            deleteAdminAccountLL.setVisibility(View.VISIBLE);
        }
        // Go to AdminRegistrationActivity if clicked
        createNewAdminLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHomeActivity.this, AdminRegistrationActivity.class);
                startActivity(intent);
            }
        });
        // Go to AdminRegistrationActivity if clicked
        deleteAdminAccountLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHomeActivity.this, DeleteAdminAccount.class);
                startActivity(intent);
            }
        });



        //Logout-------------------------------------------------------------------
        Button logoutBtn = findViewById(R.id.logout_btn_admin_home);
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