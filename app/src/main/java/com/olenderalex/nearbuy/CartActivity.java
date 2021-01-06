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
import android.widget.Button;
import android.widget.TextView;
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
import com.olenderalex.nearbuy.Model.Cart;
import com.olenderalex.nearbuy.Utils.Util;
import com.olenderalex.nearbuy.ViewHolder.CartViewHolder;


public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button nextProcessBtn;
    private int totalPrice=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView=findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        nextProcessBtn=(Button) findViewById(R.id.next_process_btn);
        nextProcessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent =new Intent(CartActivity.this,ConfirmOrderActivity.class);
                intent.putExtra(Util.totalPrice,String.valueOf(totalPrice));
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child(Util.cartListStDbName);
        FirebaseRecyclerOptions <Cart> options = new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartListRef.child(Util.usersCart)
                        .child(Util.currentOnlineUser.getPhone())
                        .child(Util.usersProductsInCart),Cart.class)
                 .build();



        FirebaseRecyclerAdapter <Cart , CartViewHolder>  adapter= new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model) {

                holder.productQuantityTxt.setText("Amount: "+model.getQuantity());
                holder.productNameTxt.setText(model.getProductName());
                holder.productPriceTxt.setText("Price: "+model.getPrice());


                //Calculating total price

                int singleProductPrice=( (Integer.valueOf(model.getPrice())))*Integer.valueOf(model.getQuantity());
                totalPrice+=singleProductPrice;

                if(totalPrice!=0){
                    nextProcessBtn.setVisibility(View.VISIBLE);
                }

                //Delete product from cart
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        CharSequence editProduct[] = new CharSequence[] {
                                "Edit",
                                "Delete"
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Edit product:");

                        builder.setItems(editProduct, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(which==0){
                                    Intent intent =new Intent(CartActivity.this,ProductDetailsActivity.class);
                                    intent.putExtra(Util.productId,model.getId());
                                    startActivity(intent);
                                }
                                if(which==1){
                                    cartListRef.child(Util.usersCart)
                                            .child(Util.currentOnlineUser.getPhone())
                                            .child(Util.usersProductsInCart)
                                            .child(model.getId())
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(CartActivity.this,"Product removed from cart",Toast.LENGTH_LONG).show();

                                                        Intent intent =new Intent(CartActivity.this,CartActivity.class);
                                                        startActivity(intent);
                                                    }
                                                }
                                            });
                                }
                            }
                        });
                        builder.show();
                    }
                });
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout,parent,false);
                CartViewHolder viewHolder = new CartViewHolder(view);
                return viewHolder ;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

}