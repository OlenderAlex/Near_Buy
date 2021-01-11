package com.olenderalex.nearbuy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.olenderalex.nearbuy.Utils.Util;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AdminManageProductsActivity extends AppCompatActivity {

    private  Button editBtn,deleteBtn;
    private  EditText name,price,description;
    private ImageView image;
    private String productID="";

    private DatabaseReference productsRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_products);
        //getting product id from previous activity
        productID=getIntent().getStringExtra(Util.productId);
            productsRef= FirebaseDatabase.getInstance().getReference()
                    .child(Util.productsDbName)
                     .child(productID);

        name=findViewById(R.id.edit_name);
        price=findViewById(R.id.edit_price);
        description=findViewById(R.id.edit_description);
        image=findViewById(R.id.edit_image);
        editBtn=findViewById(R.id.edit_apply_changes_btn);
        deleteBtn=findViewById(R.id.delete_product_btn11);

        getProductDetails();

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyChanges();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence[] editProduct = new CharSequence[] {
                       "Delete",
                       "Cancel"
                };
               AlertDialog.Builder builder = new AlertDialog.Builder(AdminManageProductsActivity.this);
               builder.setTitle("You sure you want to delete this product? :");

               builder.setItems(editProduct, new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                        if(which==0) {
                         deleteProduct();
                       }
                        if(which==1){
                            finish();
                       }
                   }
               });builder.show();
            }
            } );

    }

    private void deleteProduct() {
        productsRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(AdminManageProductsActivity.this, "Product deleted successfully"
                        , Toast.LENGTH_LONG).show();
            }
        });
        Intent intent = new Intent(AdminManageProductsActivity.this
                , AdminHomeActivity.class);
        startActivity(intent);
    }



    private void getProductDetails() {
        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String productName=  snapshot.child(Util.productName).getValue().toString();
                    String productPrice=  snapshot.child(Util.productPrice).getValue().toString();
                    String productDescription=  snapshot.child(Util.productDescription).getValue().toString();
                    String productImage=  snapshot.child(Util.productImage).getValue().toString();

                    name.setText(productName);
                    price.setText(productPrice);
                    description.setText(productDescription);
                    Picasso.get().load(productImage).into(image);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void applyChanges() {
        String productName=  name.getText().toString();
        String productPrice= price.getText().toString();
        String productDescription=  description.getText().toString();
        if(productName.equals("")){
            Toast.makeText(this,"Enter a name",Toast.LENGTH_LONG).show();
        }
        if(productPrice.equals("")){
            Toast.makeText(this,"Enter a price",Toast.LENGTH_LONG).show();
        }
        if(productDescription.equals("")){
            Toast.makeText(this,"Enter a description",Toast.LENGTH_LONG).show();
        }
        else{
            HashMap<String ,Object> productMap = new HashMap<>();
            productMap.put(Util.productId,productID);
            productMap.put(Util.productPrice,productPrice);
            productMap.put(Util.productName,productName);
            productMap.put(Util.productDescription,productDescription);

            productsRef.updateChildren(productMap).addOnCompleteListener(
                    new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(AdminManageProductsActivity.this,"Changes applied succesfully"
                                            ,Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(AdminManageProductsActivity.this
                                            , AdminHomeActivity.class);
                                    startActivity(intent);
                                }
                                }

                        }

            );
        }

    }
}