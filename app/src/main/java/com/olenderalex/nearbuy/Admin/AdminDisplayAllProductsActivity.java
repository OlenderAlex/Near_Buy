package com.olenderalex.nearbuy.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.olenderalex.nearbuy.Model.Products;
import com.olenderalex.nearbuy.R;
import com.olenderalex.nearbuy.Utils.Util;
import com.olenderalex.nearbuy.ViewHolder.ProductViewHolder;
import com.squareup.picasso.Picasso;

public class AdminDisplayAllProductsActivity extends AppCompatActivity {
    private Button searchBtn;
    private EditText inputText;
    private RecyclerView searchList;

    private String searchInput="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_display_all_products);

        inputText=findViewById(R.id.et_input_text_admin);
        searchBtn=findViewById(R.id.btn_search_admin);
        searchList=findViewById(R.id.list_search);

        searchList.setLayoutManager(new LinearLayoutManager(AdminDisplayAllProductsActivity.this));
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchInput = inputText.getText().toString().toUpperCase();
                startSearching();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child(Util.productsDbName);
        FirebaseRecyclerOptions<Products> options;
        options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(databaseRef.orderByChild(Util.uploadedDate)
                        , Products.class).build();


    /*
    show the product data in recycler view
     */
    FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
            new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model) {
                    holder.productNameTV.setText(model.getProductName());
                    holder.productPriceTV.setText("Price : " + model.getPrice());
                    Picasso.get().load(model.getImage()).into(holder.productImage);


                    // Go to product detail page when clicked
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent =new Intent(AdminDisplayAllProductsActivity.this, AdminEditProductsActivity.class);
                            intent.putExtra(Util.productId ,model.getId());
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }); }

                @NonNull
                @Override
                public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).
                            inflate(R.layout.product_items_layout2, parent, false);
                    return new ProductViewHolder(view);
                }
            };
        searchList.setAdapter(adapter);
        adapter.startListening();
}


    public void startSearching()
    {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child(Util.productsDbName);
        FirebaseRecyclerOptions<Products> options;

        if(!searchInput.equals("")) {
        /*
        retrieve data of products by input word in search bar
         */
            options = new FirebaseRecyclerOptions.Builder<Products>()
                    .setQuery(databaseRef.orderByChild(Util.productName)
                          .startAt(searchInput), Products.class).build();
        }else
        {
            options = new FirebaseRecyclerOptions.Builder<Products>()
                    .setQuery(databaseRef.orderByChild(Util.uploadedDate)
                            , Products.class).build();
        }

        /*
        show the product data in recycler view
         */
        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model) {
                        holder.productNameTV.setText(model.getProductName());
                        holder.productPriceTV.setText("Price : " + model.getPrice());
                        Picasso.get().load(model.getImage()).into(holder.productImage);


                        // Go to product detail page when clicked
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent =new Intent(AdminDisplayAllProductsActivity.this, AdminEditProductsActivity.class);
                                intent.putExtra(Util.productId ,model.getId());
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }); }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).
                                inflate(R.layout.product_items_layout2, parent, false);
                        return new ProductViewHolder(view);
                    }
                };
        searchList.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent =new Intent(AdminDisplayAllProductsActivity.this, AdminHomeActivity.class);
        startActivity(intent);
    }
}