package com.olenderalex.nearbuy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.olenderalex.nearbuy.Model.SellerOrders;
import com.olenderalex.nearbuy.Utils.Util;
import com.olenderalex.nearbuy.ViewHolder.SellerOrdersViewHolder;

public class SellerNewOrdersActivity extends AppCompatActivity {

    private RecyclerView ordersList;
    private DatabaseReference  ordersRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_new_orders);

        ordersRef = FirebaseDatabase.getInstance().getReference().child(Util.confirmedOrders);

        ordersList=findViewById(R.id.list_new_orders);
        ordersList.setLayoutManager(new LinearLayoutManager(this));
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<SellerOrders> options=
                new FirebaseRecyclerOptions.Builder<SellerOrders>()
                .setQuery(ordersRef,SellerOrders.class)
                .build();

        FirebaseRecyclerAdapter<SellerOrders, SellerOrdersViewHolder> adapter =
                new FirebaseRecyclerAdapter<SellerOrders, SellerOrdersViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull SellerOrdersViewHolder holder, final int position, @NonNull final SellerOrders model) {

                        holder.userName.setText(model.getName());
                        holder.userPhone.setText("Phone : "+model.getPhone());
                        holder.userAddress.setText("Address : "+model.getCity()+" "+model.getAddress());
                        holder.userTotalPrice.setText("Total price : " +model.getTotal_price());
                        holder.userDateAndTime.setText("Ordered at : "+model.getUploaded_at_date()+"  "+model.getUploaded_at_time());

                        holder.showOrdersBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String userId =getRef(position).getKey();
                                Intent intent =new Intent(SellerNewOrdersActivity.this , SellerDisplayConfirmedOrderByUser.class);

                                //send phone number to display in  SellerDisplayConfirmedOrderByUser
                                //products that ordered
                                intent.putExtra(Util.userId,userId);
                                startActivity(intent);
                            }
                        });

                        /*
                        *       Delete delivered order
                         */
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence[] isDelivered =new CharSequence[]{
                                        "Yes",
                                        "No"
                                };

                                AlertDialog.Builder builder =new AlertDialog.Builder(SellerNewOrdersActivity.this);
                                final AlertDialog optionDialog = builder.create();

                                builder.setTitle("Is order is delivered?");

                                builder.setItems(isDelivered, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(which==0)
                                        {
                                            String userId =getRef(position).getKey();
                                            removeOrder(userId);
                                            Toast.makeText(SellerNewOrdersActivity.this,"Order removed",Toast.LENGTH_LONG).show();
                                        }
                                        else{
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
                    public SellerOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.new_orders_layout,parent,false);

                        return new SellerOrdersViewHolder(view);
                    }
                };
        ordersList.setAdapter(adapter);
        adapter.startListening();
    }

    private void removeOrder(String userId) {
        ordersRef.child(userId).removeValue();

    }


}