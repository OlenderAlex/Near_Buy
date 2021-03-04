
package com.olenderalex.nearbuy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.olenderalex.nearbuy.Model.Users;
import com.olenderalex.nearbuy.User.CartActivity;
import com.olenderalex.nearbuy.User.FavoriteProductsActivity;
import com.olenderalex.nearbuy.User.ProductDetailsActivity;
import com.olenderalex.nearbuy.User.SearchByCategoryActivity;
import com.olenderalex.nearbuy.User.SearchProductsActivity;
import com.olenderalex.nearbuy.User.UserAccountSettingsActivity;
import com.olenderalex.nearbuy.User.UserOrdersActivity;
import com.olenderalex.nearbuy.ViewHolder.ProductViewHolder;
import com.olenderalex.nearbuy.Utils.Util;
import com.olenderalex.nearbuy.Model.Products;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference productsRef;
    private RecyclerView recyclerMenu;
    public RecyclerView.LayoutManager layoutManager;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView shopAll;
    private TextView userName;
    private ImageView profileIv;
    private Users currentOnlineUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        this.configureToolBar();
        this.configureDrawerLayout();
        this.configureNavigationView();

        //Retrieve data of current user from local DB
        Paper.init(this);
        currentOnlineUser=new Users();
        currentOnlineUser= Paper.book().read(Util.currentOnlineUser);

        shopAll = findViewById(R.id.shop_all);
        productsRef = FirebaseDatabase.getInstance().getReference().child(Util.productsDbName);
        recyclerMenu = findViewById(R.id.recycler_menu);
        recyclerMenu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerMenu.setLayoutManager(layoutManager);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(HomeActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });
        shopAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(HomeActivity.this, SearchByCategoryActivity.class);
                startActivity(intent);
            }
        });

        View headerView = navigationView.getHeaderView(0);
        userName = headerView.findViewById(R.id.user_name_tv);
        profileIv = headerView.findViewById(R.id.profile_image);

        String name=currentOnlineUser.getName();
        if(!name.equals("")) {
            userName.setText(name);
        }
        if(!currentOnlineUser.getImage().equals(""))
        {
            Picasso.get().load(currentOnlineUser.getImage()).placeholder(R.drawable.profile_img).into(profileIv);
        }

    }


       /*
        retrieve all products data from realtime database
        display the product data in recycler view on HomeActivity
     */
    @Override
    protected void onStart() {
        super.onStart();

        final DatabaseReference favoriteRef;
        favoriteRef = FirebaseDatabase.getInstance().getReference();
        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>().
                setQuery(productsRef
                        .orderByChild(Util.dealPrice).equalTo(Util.deal), Products.class).build();

        //Get products data
        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    protected void onBindViewHolder(@NonNull final ProductViewHolder holder
                            , int position, @NonNull final Products model) {

                        //Set products data in View components
                        holder.productNameTV.setText(model.getProductName());
                        holder.productPriceTV.setText("Price : " + model.getPrice()+" NIS");
                        holder.productPriceTV.setTextColor(Color.parseColor("#9F0303"));
                        Picasso.get().load(model.getImage()).into(holder.productImage);

                        // Go to product detail page when clicked
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(HomeActivity.this
                                        , ProductDetailsActivity.class);
                                intent.putExtra(Util.productId, model.getId());
                                startActivity(intent);
                            }
                        });

                        //add to favorites by clicking on heart icon
                        holder.favoriteEmptyIv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                addToFavoriteList(model);

                               //Icon will appears as a filled heart for a 0.1 sec
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

                    }
                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).
                                inflate(R.layout.product_items_layout2, parent, false);
                        return new ProductViewHolder(view);
                    }
                };
        recyclerMenu.setAdapter(adapter);
        adapter.startListening();
    }

    //-------------------------------------------------------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Intent intentSearch =new Intent(HomeActivity.this, SearchProductsActivity.class);
                startActivity(intentSearch);
                return true;
            case R.id.action_favorite:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                Intent intentFavorites=new Intent(HomeActivity.this, FavoriteProductsActivity.class);
                startActivity(intentFavorites);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_cart) {
            Intent intent =new Intent(HomeActivity.this,CartActivity.class);
            startActivity(intent);
        }
        if (id == R.id.nav_my_account) {

            Intent intent = new Intent(HomeActivity.this, UserAccountSettingsActivity.class);
            startActivity(intent);
        }
        if (id == R.id.nav_category) {
            Intent intent =new Intent(HomeActivity.this, SearchByCategoryActivity.class);
            startActivity(intent);
        }
        if (id == R.id.nav_orders) {
            Intent intent =new Intent(HomeActivity.this, UserOrdersActivity.class);
            startActivity(intent);
        }
        if (id == R.id.nav_favorite) {
            Intent intent =new Intent(HomeActivity.this,FavoriteProductsActivity.class);
            startActivity(intent);
        }
        //Log out from current  account
        if (id == R.id.nav_logout) {
            Paper.book().destroy();
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        this.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    // 1 - Configure Toolbar
    private void configureToolBar(){
        this.toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("HOME");
        setSupportActionBar(toolbar);
    }

    // 2 - Configure Drawer Layout
    private void configureDrawerLayout(){
        this.drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    // 3 - Configure NavigationView
    private void configureNavigationView(){
        this.navigationView = findViewById(R.id.nav_view);
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//------------------------------------------------------------------------------------------------------------------
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
                                    HomeActivity.this
                                    ,"Added to favorites"
                                    , Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }});
    }


    //Deleting product from favorites by clicking on filled black heart
    private void deleteFromFavoriteList(Products model) {
        final DatabaseReference favoritesListRef = FirebaseDatabase.getInstance()
                .getReference()
                .child(Util.favoriteProductsDbName)
                .child(currentOnlineUser.getPhone());

        favoritesListRef.child(model.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(HomeActivity.this, "Product deleted successfully"
                        , Toast.LENGTH_SHORT).show();
            }
        });
    }


}

