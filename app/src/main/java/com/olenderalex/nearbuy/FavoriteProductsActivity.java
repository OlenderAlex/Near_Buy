package com.olenderalex.nearbuy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.olenderalex.nearbuy.Model.Cart;
import com.olenderalex.nearbuy.Utils.Util;
import com.olenderalex.nearbuy.ViewHolder.CartViewHolder;
import com.squareup.picasso.Picasso;

public class FavoriteProductsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_products);


        recyclerView=findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        final DatabaseReference favoritesListRef = FirebaseDatabase.getInstance()
                .getReference().child(Util.favoriteProductsDbName);

        FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(favoritesListRef.child(MainActivity.currentOnlineUser.getPhone()),Cart.class)
                .build();


        FirebaseRecyclerAdapter<Cart , CartViewHolder> adapter= new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model) {

                holder.productNameTxt.setText(model.getProductName());
                holder.productPriceTxt.setText("Price: "+model.getPrice());
                Picasso.get().load(model.getImage()).into(holder.productIv);


                //Delete product from Favorites
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        CharSequence[] editProduct = new CharSequence[] {
                                "See product",
                                "Delete"
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(FavoriteProductsActivity.this);
                        builder.setTitle(model.getProductName());

                        builder.setItems(editProduct, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, int which) {
                                if(which==0){
                                    Intent intent =new Intent(
                                            FavoriteProductsActivity.this,ProductDetailsActivity.class);
                                    intent.putExtra(Util.productId,model.getId());
                                    startActivity(intent);
                                }
                                if(which==1){
                                    favoritesListRef.child(MainActivity.currentOnlineUser.getPhone())
                                            .child(model.getId())
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(FavoriteProductsActivity.this
                                                                ,"Product removed",Toast.LENGTH_LONG).show();

                                                       dialog.dismiss();
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

                View view= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.cart_items_layout,parent,false);
                CartViewHolder viewHolder = new CartViewHolder(view);
                return viewHolder ;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}