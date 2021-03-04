package com.olenderalex.nearbuy.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.paperdb.Paper;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.olenderalex.nearbuy.MainActivity;
import com.olenderalex.nearbuy.Model.Admin;
import com.olenderalex.nearbuy.Model.Cart;
import com.olenderalex.nearbuy.Model.Users;
import com.olenderalex.nearbuy.R;
import com.olenderalex.nearbuy.Utils.Util;
import com.olenderalex.nearbuy.ViewHolder.CartViewHolder;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class CartActivity extends AppCompatActivity {

    private static final String TAG ="msg" ;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button checkoutBtn;
    private float totalPrice=0;
    private DatabaseReference cartListRef;

    private String orderNumber="";
    private Users currentOnlineUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //Retrieve data of current user from local DB
        Paper.init(this);
        currentOnlineUser=new Users();
        currentOnlineUser= Paper.book().read(Util.currentOnlineUser);

        cartListRef = FirebaseDatabase.getInstance()
                .getReference()
                .child(Util.cartListStDbName)
                .child(currentOnlineUser.getPhone());


        recyclerView=findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        checkoutBtn = findViewById(R.id.next_process_btn);
        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createOrder();
                Intent intent =new Intent(CartActivity.this,ConfirmOrderActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString(Util.orderNumber,orderNumber);
                bundle.putString(Util.totalPrice,String.valueOf(totalPrice));
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions <Cart> options = new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartListRef,Cart.class)
                 .build();
        FirebaseRecyclerAdapter <Cart , CartViewHolder>  adapter= new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model) {

                holder.productQuantityTxt.setText("Amount: "+model.getQuantity());
                holder.productNameTxt.setText(model.getProductName());
                holder.productPriceTxt.setText("Price: "+model.getPrice());
                Picasso.get().load(model.getImage()).into(holder.productIv);

                //Calculating total price
                final float singleProductPrice=Float.parseFloat(model.getPrice())*Float.parseFloat(model.getQuantity());
                totalPrice+=singleProductPrice;
                if(totalPrice!=0){
                    checkoutBtn.setVisibility(View.VISIBLE);
                }
                //Delete product from cart
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence[] editProduct = new CharSequence[] {
                                "Edit",
                                "Delete"
                        };
                        final AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Edit product:");

                        builder.setItems(editProduct, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, int which) {
                                if(which==0){
                                    Intent intent =new Intent(CartActivity.this,ProductDetailsActivity.class);
                                    intent.putExtra(Util.productId,model.getId());
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                                if(which==1){

                                    //reduce deleted product price from total price in cart
                                    totalPrice-=singleProductPrice;

                                    //Remove product from cart List
                                    cartListRef.child(model.getId()).removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(CartActivity.this
                                                                ,"Product removed from cart",Toast.LENGTH_LONG).show();

                                                        dialog.dismiss();
                                                    } }}); }
                            }
                        });builder.show();
                    }
                }); }
            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout,parent,false);
                CartViewHolder viewHolder = new CartViewHolder(view);
                return viewHolder ;
            }};
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }



    private void createOrder() {
// firebase refs
        final DatabaseReference ordersListRef = FirebaseDatabase.getInstance()
                .getReference().child(Util.orders);

        //create unique order number
        final String saveCurrentDate,saveCurrentTime;
        Calendar calFroDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("ddMMyyyy");
        saveCurrentDate = currentDate.format(calFroDate.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HHmmss");
        saveCurrentTime = currentTime.format(calFroDate.getTime());

        orderNumber=currentOnlineUser.getPhone()+saveCurrentDate+saveCurrentTime;

        // copy products data from shopping cart (Cart List table in Firebase)
        // to Order db table in Users view
            ValueEventListener valueEventListenerUsers = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ordersListRef.child(Util.usersView)
                            .child(currentOnlineUser.getPhone())
                            .child(orderNumber)
                            .setValue(dataSnapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isComplete()) {
                                Log.d(TAG, "Success!");
                            } else {
                                Log.d(TAG, "Copy failed!");
                            }
                        }
                    });
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            };
            cartListRef.addListenerForSingleValueEvent(valueEventListenerUsers);

    // copy products data from shopping cart (Cart List table in Firebase)
    // to Order db table in Admin view
    ValueEventListener valueEventListenerAdmin = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            ordersListRef.child(Util.adminView)
                    .child(orderNumber)
                    .setValue(dataSnapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isComplete()) {
                        Log.d(TAG, "Success!");
                    } else {
                        Log.d(TAG, "Copy failed!");
                    }
                }
            });
        }
        @Override
        public void onCancelled(DatabaseError databaseError) {}
    };
            cartListRef.addListenerForSingleValueEvent(valueEventListenerAdmin);
}

}