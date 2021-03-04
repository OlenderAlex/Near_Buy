package com.olenderalex.nearbuy.Admin;

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
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.olenderalex.nearbuy.Model.Admin;
import com.olenderalex.nearbuy.Model.Products;
import com.olenderalex.nearbuy.R;
import com.olenderalex.nearbuy.Utils.Util;
import com.olenderalex.nearbuy.ViewHolder.AdminsListViewHolder;
import com.olenderalex.nearbuy.ViewHolder.ProductViewHolder;
import com.squareup.picasso.Picasso;

public class DeleteAdminAccount extends AppCompatActivity {

    private RecyclerView adminsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_admin_account);
        adminsList = findViewById(R.id.admin_delete_admin_recV);
        adminsList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child(Util.adminDbName);
        FirebaseRecyclerOptions<Admin> options;
        options = new FirebaseRecyclerOptions.Builder<Admin>()
                .setQuery(databaseRef, Admin.class).build();


        FirebaseRecyclerAdapter<Admin, AdminsListViewHolder> adapter =
                new FirebaseRecyclerAdapter<Admin, AdminsListViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AdminsListViewHolder holder, int position, @NonNull final Admin model) {


                        holder.adminName.setText(model.getName());

                        //Option to delete admin account except Business Owner account
                        if (!model.getLogin().equals(Util.ownerLoginKey)) {
                            holder.adminName.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    CharSequence[] isShipped = new CharSequence[]{
                                            "Yes",
                                            "No"
                                    };
                                    AlertDialog.Builder builder = new AlertDialog.Builder(DeleteAdminAccount.this);
                                    final AlertDialog optionDialog = builder.create();

                                    builder.setTitle("Delete admin?");

                                    builder.setItems(isShipped, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (which == 0) {
                                                deleteAccount(model.getLogin());
                                            } else {
                                                optionDialog.dismiss();

                                            }
                                        }
                                    });
                                    builder.show();
                                }
                            });
                        }
                    }
                    @NonNull
                    @Override
                    public AdminsListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).
                                inflate(R.layout.admins_list_layout, parent, false);
                        return new AdminsListViewHolder(view);
                    }
                };
        adminsList.setAdapter(adapter);
        adapter.startListening();
    }

    private void deleteAccount(String login) {
        DatabaseReference databaseRef = FirebaseDatabase
                .getInstance()
                .getReference().child(Util.adminDbName).child(login);
        databaseRef.removeValue();
        Toast.makeText(DeleteAdminAccount.this
                , "Admin deleted", Toast.LENGTH_LONG).show();
    }
}
