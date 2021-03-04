package com.olenderalex.nearbuy.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.olenderalex.nearbuy.Model.Orders;
import com.olenderalex.nearbuy.R;
import com.olenderalex.nearbuy.Utils.Util;
import com.olenderalex.nearbuy.ViewHolder.AdminOrdersViewHolder;

public class AdminDeliveredOrdersActivity extends AppCompatActivity {

    private RecyclerView ordersList;
    private DatabaseReference ordersRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_delivered_orders);
        ordersRef = FirebaseDatabase.getInstance()
                .getReference()
                .child(Util.confirmedOrders)
                .child(Util.adminView);

        ordersList=findViewById(R.id.list_delivered_orders);
        ordersList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Create List of orders with status "Delivered" and display them
        FirebaseRecyclerOptions<Orders> options =
                new FirebaseRecyclerOptions.Builder<Orders>()
                        .setQuery(ordersRef.orderByChild(Util.shipmentStatus)
                                .equalTo(Util.delivered), Orders.class)
                        .build();

        FirebaseRecyclerAdapter<Orders, AdminOrdersViewHolder> adapter =
                new FirebaseRecyclerAdapter<Orders, AdminOrdersViewHolder>(options) {
                    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
                    @Override
                    protected void onBindViewHolder(@NonNull AdminOrdersViewHolder holder,
                                                    final int position, @NonNull final Orders model) {

                        holder.userName.setText(model.getName());
                        holder.userPhone.setText("Phone : " + model.getPhone());
                        holder.userAddress.setText("Address : " + model.getCity() + " " + model.getAddress());
                        holder.userTotalPrice.setText("Total price : " + model.getTotal_price());
                        holder.userDateAndTime.setText("Ordered at : " + model.getUploaded_at_date() + "  " + model.getUploaded_at_time());
                        holder.orderNumber.setText(("Order : " + model.getOrderNumber()));
                        holder.showOrdersBtn.setBackgroundColor(R.color.yellow);

                        /*
                         *      show   order details if clicked
                         */
                        holder.showOrdersBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(AdminDeliveredOrdersActivity.this, DisplayOrderDetails.class);
                                //send order number to next activity
                                //products that ordered
                                intent.putExtra(Util.orderNumber, model.getOrderNumber());
                                intent.putExtra(Util.shipmentStatus,Util.delivered);
                                startActivity(intent);
                            }
                        });
                    }
                    @NonNull
                    @Override
                    public AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.order_layout, parent, false);
                        return new AdminOrdersViewHolder(view);
                    }
                };
        ordersList.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AdminDeliveredOrdersActivity.this,AdminHomeActivity .class);
        startActivity(intent);
        finish();
    }
}