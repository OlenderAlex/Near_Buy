package com.olenderalex.nearbuy.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.olenderalex.nearbuy.Model.Orders;
import com.olenderalex.nearbuy.R;
import com.olenderalex.nearbuy.Utils.Util;
import com.olenderalex.nearbuy.ViewHolder.AdminOrdersViewHolder;

import java.util.HashMap;
import java.util.Objects;

public class AdminShippedOrdersActivity extends AppCompatActivity {

    private RecyclerView ordersList;
    private DatabaseReference ordersRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_shipped_orders);

        ordersRef = FirebaseDatabase.getInstance()
                .getReference()
                .child(Util.confirmedOrders)
                .child(Util.adminView);

        ordersList=findViewById(R.id.list_shipped_orders);
        ordersList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Create List of orders with status "Not Shipped" and display them
        FirebaseRecyclerOptions<Orders> options =
                new FirebaseRecyclerOptions.Builder<Orders>()
                        .setQuery(ordersRef.orderByChild(Util.shipmentStatus)
                                .equalTo(Util.shipped), Orders.class)
                        .build();

        FirebaseRecyclerAdapter<Orders, AdminOrdersViewHolder> adapter =
                new FirebaseRecyclerAdapter<Orders, AdminOrdersViewHolder>(options) {
                    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
                    @Override
                    protected void onBindViewHolder(@NonNull final AdminOrdersViewHolder holder, final int position, @NonNull final Orders model) {

                        holder.userName.setText(model.getName());
                        holder.userPhone.setText("Phone : " + model.getPhone());
                        holder.userAddress.setText("Address : " + model.getCity() + " " + model.getAddress());
                        holder.userTotalPrice.setText("Total price : " + model.getTotal_price());
                        holder.userDateAndTime.setText("Ordered at : " + model.getUploaded_at_date() + "  " + model.getUploaded_at_time());
                        holder.orderNumber.setText(("Order : "+model.getOrderNumber()));
                        /*
                         *       Delete delivered order
                         */
                        holder.showOrdersBtn.setText("confirm shipment");
                        holder.showOrdersBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence[] isShipped = new CharSequence[]{
                                        "Yes",
                                        "No"
                                };
                                AlertDialog.Builder builder = new AlertDialog.Builder(AdminShippedOrdersActivity.this);
                                final AlertDialog optionDialog = builder.create();

                                builder.setTitle("Is order delivered?");

                                builder.setItems(isShipped, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == 0) {
                                            updateShipmentStatus(model.getOrderNumber());
                                            Toast.makeText(AdminShippedOrdersActivity.this
                                                    , "Order moved to delivered", Toast.LENGTH_LONG).show();
                                        } else {
                                            optionDialog.dismiss();

                                        }
                                    }
                                });

                                builder.show();
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
    //Update order shipment status in "Confirmed orders" in Firebase
    private void updateShipmentStatus(final String orderNumber) {

        final DatabaseReference userViewRef = FirebaseDatabase.getInstance().getReference()
                .child(Util.confirmedOrders)
                .child(Util.usersView);
        DatabaseReference adminViewRef = FirebaseDatabase.getInstance().getReference()
                .child(Util.confirmedOrders)
                .child(Util.adminView)
                .child(orderNumber);

        //Change order status from "Not shipped " to "Ready for shipment"
        final HashMap<String, Object> orderStatusMap = new HashMap<>();
        orderStatusMap.put(Util.shipmentStatus, Util.delivered);

        //change for Admin view
        adminViewRef.updateChildren(orderStatusMap);

        //Get user phone number to update shipment status for order
        //Getting user phone from Db , not that attached to order
        adminViewRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(Util.userPhone).exists()) {
                    //change for user view
                    String userPhone = Objects.requireNonNull(snapshot.child(Util.userPhone).getValue()).toString();
                    userViewRef.child(userPhone).child(orderNumber).updateChildren(orderStatusMap);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}