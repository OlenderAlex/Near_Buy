 package com.olenderalex.nearbuy.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.paperdb.Paper;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.olenderalex.nearbuy.HomeActivity;
import com.olenderalex.nearbuy.Model.Products;
import com.olenderalex.nearbuy.Model.Users;
import com.olenderalex.nearbuy.R;
import com.olenderalex.nearbuy.Utils.Util;
import com.olenderalex.nearbuy.ViewHolder.ProductViewHolder;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

 public class SearchProductsActivity extends AppCompatActivity {

    private Button searchBtn;
    private EditText inputText;
    private RecyclerView searchList;
    private String searchInput,categoryName="";
     private Users currentOnlineUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_products);

        //Retrieve data of current user from local DB
        Paper.init(this);
        currentOnlineUser=new Users();
        currentOnlineUser= Paper.book().read(Util.currentOnlineUser);
        /*
        receive name of chosen category
        */
        Bundle data = getIntent().getExtras();
        if(data != null) {
            categoryName = data.getString(Util.productCategory);

            Toast.makeText(SearchProductsActivity.this, categoryName, Toast.LENGTH_LONG).show();

        }
        inputText=findViewById(R.id.et_input_text);
        searchBtn=findViewById(R.id.btn_search);
        searchList=findViewById(R.id.list_search);

        searchList.setLayoutManager(new LinearLayoutManager(SearchProductsActivity.this));

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchInput = inputText.getText().toString().toUpperCase();
                categoryName="";
                startSearching();
            }
        });
        // if category is  defined
       if(!categoryName.equals("")) {
           startSearching();
       }

    }

  public void startSearching()
  {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child(Util.productsDbName);
      FirebaseRecyclerOptions<Products> options;
        if(categoryName.equals("")) {
        /*
        retrieve data of products by input word in search bar
         */
         options = new FirebaseRecyclerOptions.Builder<Products>()
                 .setQuery(databaseRef.orderByChild(Util.productName)
                         .startAt(searchInput), Products.class)
                            .build();
        }else
            {
            /*
        retrieve data of all products in particular category
         */
                options = new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(databaseRef.orderByChild(Util.productCategory)
                                .startAt(categoryName), Products.class)
                        .build();
        }


        /*
        show the product data in recycler view
         */
        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ProductViewHolder holder, int position, @NonNull final Products model) {
                        holder.productNameTV.setText(model.getProductName());
                        holder.productPriceTV.setText("Price : " + model.getPrice());
                        Picasso.get().load(model.getImage()).into(holder.productImage);
                        //add to favorites by clicking on heart icon
                        holder.favoriteEmptyIv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                addToFavoriteList(model);

//                                //Icon will appears as a filled heart for a 0.1 sec
                                new CountDownTimer(200, 1000) {
                                    public void onTick(long millisUntilFinished) {
                                        holder.favoriteEmptyIv.setVisibility(View.GONE);
                                        holder.favoriteFilledIv.setVisibility(View.VISIBLE);
                                    }

                                    public void onFinish() {
                                        holder.favoriteEmptyIv.setVisibility(View.VISIBLE);
                                        holder.favoriteFilledIv.setVisibility(View.GONE);
                                    }
                                }.start();
                            }
                        });

                        // Go to product detail page when clicked
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent =new Intent(SearchProductsActivity.this,ProductDetailsActivity.class);
                                intent.putExtra(Util.productId ,model.getId());
                                startActivity(intent);
                            }
                        });


                    }
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

     //-----------   Adding  product to favorites
     private void addToFavoriteList(Products model) {
         final DatabaseReference favoritesListRef = FirebaseDatabase.getInstance()
                 .getReference()
                 .child(Util.favoriteProductsDbName)
                 .child(currentOnlineUser.getPhone())
                 .child(model.getId());

         //Storing the data

         final HashMap<String, Object> cartMap = new HashMap<>();
         cartMap.put(Util.productId, model.getId());
         cartMap.put(Util.productName, model.getProductName());
         cartMap.put(Util.productPrice, model.getPrice());
         cartMap.put(Util.productImage, model.getImage());

         favoritesListRef.updateChildren(cartMap)
                 .addOnCompleteListener(new OnCompleteListener<Void>() {
                     @Override
                     public void onComplete(@NonNull Task<Void> task) {
                         if (task.isSuccessful()) {
                             Toast toast=Toast.makeText(
                                     SearchProductsActivity.this
                                     ,"Added to favorites"
                                     , Toast.LENGTH_LONG);
                             toast.setGravity(Gravity.TOP| Gravity.CENTER_HORIZONTAL, 0, 570);
                             toast.show();
                         }
                     }});
     }

}