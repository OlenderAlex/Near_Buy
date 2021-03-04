package com.olenderalex.nearbuy.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.olenderalex.nearbuy.Model.Cart;
import com.olenderalex.nearbuy.R;
import com.olenderalex.nearbuy.Utils.Util;
import com.olenderalex.nearbuy.ViewHolder.CartViewHolder;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

public class DisplayOrderDetails extends AppCompatActivity {

    private RecyclerView productsList;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference orderAdminRef;
    private String orderNumber = "";
    private String shipmentStatus = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_order_details);
        orderAdminRef = FirebaseDatabase.getInstance().getReference()
                .child(Util.orders)
                .child(Util.adminView);


        orderNumber = getIntent().getStringExtra(Util.orderNumber);
        shipmentStatus= getIntent().getStringExtra(Util.shipmentStatus);

        productsList = findViewById(R.id.admin_display_order_details);
        productsList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        productsList.setLayoutManager(layoutManager);
        if(shipmentStatus.equals(Util.notShipped)) {
            Button readyForShipmentBtn = findViewById(R.id.btn_set_ready_for_shipment);
            readyForShipmentBtn.setVisibility(View.VISIBLE);
            readyForShipmentBtn.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onClick(View view) {
                    orderPackedAndReadyForShipment();

                }
            });

            TextView cancelOrder = findViewById(R.id.btn_cancel_order);
            cancelOrder.setVisibility(View.VISIBLE);
            cancelOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelAndDeleteOrder();
                }
            });
        }

    }


    //Move order From "New Orders"to "Packed And Ready for shipment"


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                        .setQuery(orderAdminRef.child(orderNumber), Cart.class)
                        .build();
        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Cart model) {

                holder.productQuantityTxt.setText("Amount: " + model.getQuantity());
                holder.productNameTxt.setText(model.getProductName());
                holder.productPriceTxt.setText("Price: " + model.getPrice());
                Picasso.get().load(model.getImage()).into(holder.productIv);
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false);
                CartViewHolder viewHolder = new CartViewHolder(view);
                return viewHolder;
            }
        };
        productsList.setAdapter(adapter);
        adapter.startListening();
    }


    //Confirm that order is ready for shipment
    @SuppressLint("ResourceAsColor")
    private void orderPackedAndReadyForShipment() {
        CharSequence[] isDelivered = new CharSequence[]{
                "Yes",
                "No"
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(DisplayOrderDetails.this);
        final AlertDialog optionDialog = builder.create();

        builder.setTitle("Did you pack the order?");

        builder.setItems(isDelivered, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    updateShipmentStatus();
                    Toast.makeText(DisplayOrderDetails.this,
                            "Order is ready for shipment", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(DisplayOrderDetails.this,
                            AdminNewOrdersActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    optionDialog.dismiss();

                }
            }
        });
        builder.show();
    }

    //Update order shipment status in "Confirmed orders" in Firebase
    private void updateShipmentStatus() {

        final DatabaseReference userViewRef = FirebaseDatabase.getInstance().getReference()
                .child(Util.confirmedOrders)
                .child(Util.usersView);
        DatabaseReference adminViewRef = FirebaseDatabase.getInstance().getReference()
                .child(Util.confirmedOrders)
                .child(Util.adminView)
                .child(orderNumber);

        //Change order status from "Not shipped " to "Ready for shipment"
        final HashMap<String, Object> orderStatusMap = new HashMap<>();
        orderStatusMap.put(Util.shipmentStatus, Util.readyForShipment);

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


    private void cancelAndDeleteOrder() {
        final CharSequence[] isCanceled = new CharSequence[]{
                "Yes",
                "No"
        };

        final AlertDialog.Builder builder = new AlertDialog.Builder(DisplayOrderDetails.this);
        final AlertDialog optionDialog = builder.create();

        builder.setTitle("You sure you want to cancel this order?");

        builder.setItems(isCanceled, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    final DatabaseReference confirmedOrdersUserViewRef = FirebaseDatabase.getInstance().getReference()
                            .child(Util.confirmedOrders)
                            .child(Util.usersView);
                    final DatabaseReference ordersUserViewRef = FirebaseDatabase.getInstance().getReference()
                            .child(Util.orders)
                            .child(Util.usersView);

                    DatabaseReference confirmedOrdersAdminViewRef = FirebaseDatabase.getInstance().getReference()
                            .child(Util.confirmedOrders)
                            .child(Util.adminView)
                            .child(orderNumber);
                    DatabaseReference ordersAdminViewRef = FirebaseDatabase.getInstance().getReference()
                            .child(Util.orders)
                            .child(Util.adminView)
                            .child(orderNumber);

                    //Delete order for admin in two tables "Orders" & "Confirmed orders"
                    confirmedOrdersAdminViewRef.removeValue();
                    ordersAdminViewRef.removeValue();

                    //Get user phone number to update shipment status for particular user order
                    confirmedOrdersAdminViewRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.child(Util.userPhone).exists()) {

                                //Delete order for user in two tables "Orders" & "Confirmed orders"
                                String userPhone = Objects.requireNonNull(snapshot.child(Util.userPhone).getValue()).toString();
                                confirmedOrdersUserViewRef.child(userPhone).child(orderNumber).removeValue();
                                ordersUserViewRef.child(userPhone).child(orderNumber).removeValue();

                                Intent intent = new Intent(DisplayOrderDetails.this,AdminNewOrdersActivity .class);
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    optionDialog.dismiss();
                }
            }
        });
        builder.show();

    }


}