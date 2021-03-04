package com.olenderalex.nearbuy.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.paperdb.Paper;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.olenderalex.nearbuy.Admin.DisplayOrderDetails;
import com.olenderalex.nearbuy.HomeActivity;
import com.olenderalex.nearbuy.MainActivity;
import com.olenderalex.nearbuy.Model.Orders;
import com.olenderalex.nearbuy.Model.Users;
import com.olenderalex.nearbuy.R;
import com.olenderalex.nearbuy.Utils.Util;
import com.olenderalex.nearbuy.ViewHolder.AdminOrdersViewHolder;

public class UserOrdersActivity extends AppCompatActivity {

    private RecyclerView ordersList;
    private DatabaseReference ordersRef;
    private Users currentOnlineUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_orders);

        //Retrieve data of current user from local DB
        Paper.init(this);
        currentOnlineUser=new Users();
        currentOnlineUser= Paper.book().read(Util.currentOnlineUser);

        ordersRef = FirebaseDatabase.getInstance()
                .getReference()
                .child(Util.confirmedOrders)
                .child(Util.usersView)
                .child(currentOnlineUser.getPhone());

        ordersList=findViewById(R.id.user_orders_list);
        ordersList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Create List of orders with status "Delivered" and display them
        FirebaseRecyclerOptions<Orders> options =
                new FirebaseRecyclerOptions.Builder<Orders>()
                        .setQuery(ordersRef.orderByChild(Util.uploadedDate), Orders.class)
                        .build();

        FirebaseRecyclerAdapter<Orders, AdminOrdersViewHolder> adapter =
                new FirebaseRecyclerAdapter<Orders, AdminOrdersViewHolder>(options) {
                    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
                    @Override
                    protected void onBindViewHolder(@NonNull AdminOrdersViewHolder holder,
                                                    final int position, @NonNull final Orders model) {

                        holder.userName.setText("Order status : "+model.getStatus());
                        holder.userName.setTextColor(Color.parseColor("#9F0303"));
                        holder.userPhone.setText("Phone : " + model.getPhone());
                        holder.userAddress.setText("Address : " + model.getCity() + " " + model.getAddress());
                        holder.userTotalPrice.setText("Total price : " + model.getTotal_price());
                        holder.userDateAndTime.setText("Ordered at : " + model.getUploaded_at_date() + "  " + model.getUploaded_at_time());
                        holder.orderNumber.setText(("Order : " + model.getOrderNumber()));

                        /*
                         *      show   order details if clicked
                         */
                        holder.showOrdersBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(UserOrdersActivity.this, DisplayOrderDetails.class);
                                //send order number to next activity
                                //products that ordered
                                intent.putExtra(Util.orderNumber, model.getOrderNumber());
                                intent.putExtra(Util.shipmentStatus,Util.userPhone);
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
        Intent intent = new Intent(UserOrdersActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}