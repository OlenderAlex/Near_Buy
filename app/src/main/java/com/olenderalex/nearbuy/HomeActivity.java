
package com.olenderalex.nearbuy;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.olenderalex.nearbuy.ViewHolder.ProductViewHolder;
import com.olenderalex.nearbuy.Utils.Util;
import com.olenderalex.nearbuy.Model.Products;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference productsRef;
    private RecyclerView recyclerMenu;
    public RecyclerView.LayoutManager layoutManager;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        this.configureToolBar();
        this.configureDrawerLayout();
        this.configureNavigationView();

        productsRef = FirebaseDatabase.getInstance().getReference().child(Util.productsDbName);


        recyclerMenu = findViewById(R.id.recycler_menu);
        recyclerMenu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerMenu.setLayoutManager(layoutManager);


        Paper.init(this);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(HomeActivity.this,CartActivity.class);
                startActivity(intent);
            }
        });

        View headerView = navigationView.getHeaderView(0);
        TextView userName = headerView.findViewById(R.id.user_name_tv);
        ImageView profileIv = headerView.findViewById(R.id.profile_image);

        userName.setText(Util.currentOnlineUser.getName());
        Picasso.get().load(Util.currentOnlineUser.getImage()).placeholder(R.drawable.profile_img).into(profileIv);

    }


    //Displaying products on home page

    @Override
    protected void onStart() {
        super.onStart();

        /*
        retrieve all products data from realtime database
         */
        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>().
                setQuery(productsRef, Products.class).build();



        /*
        display the product data in recycler view on HomeActivity
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
                                Intent intent =new Intent(HomeActivity.this,ProductDetailsActivity.class);
                                intent.putExtra(Util.productId ,model.getId());
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext()).
                                inflate(R.layout.product_items_layout, parent, false);

                        return new ProductViewHolder(view);
                    }
                };


        recyclerMenu.setAdapter(adapter);
        adapter.startListening();
    }

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
                Intent intent =new Intent(HomeActivity.this,SearchProductsActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_favorite:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
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

            Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
        if (id == R.id.nav_category) {
            Intent intent =new Intent(HomeActivity.this,SearchByCategoryActivity.class);
            startActivity(intent);
        }
        if (id == R.id.nav_orders) {
            Intent intent =new Intent(HomeActivity.this,UserOrdersActivity.class);
            startActivity(intent);
        }
        if (id == R.id.nav_search) {
            Intent intent =new Intent(HomeActivity.this,SearchProductsActivity.class);
            Bundle data =new Bundle();
            data.putString(Util.productCategory, "no category");
            intent.putExtras(data);
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
        toolbar.setTitle("Home");
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

}

