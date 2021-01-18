package com.olenderalex.nearbuy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class ConfirmOrderActivity extends AppCompatActivity {

    private EditText nameEt,phoneEt,cityEt,addressEt;
    private Button confirmOrderBtn;
    private String orderNumber="";

    TextView totalPriceTv;
    String totalPriceString ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        Bundle extras = getIntent().getExtras();
        orderNumber = extras.getString(Util.orderNumber);
        totalPriceString = extras.getString(Util.totalPrice);

        confirmOrderBtn=findViewById(R.id.btn_confirmOrder);
        totalPriceTv =findViewById(R.id.txt_totalPrice_confirmOrder);
        nameEt=findViewById(R.id.name_confirmOrder);
        phoneEt=findViewById(R.id.phone_confirmOrder);
        cityEt=findViewById(R.id.city_address_confirmOrder);
        addressEt=findViewById(R.id.address_confirmOrder);

        totalPriceTv.setText("AMOUNT TO PAY : "+totalPriceString +"$");


        confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        userInfoDisplay();
    }

    private void check() {
        if (TextUtils.isEmpty(nameEt.getText().toString())){
            Toast.makeText(this,"Please enter your full name ",Toast.LENGTH_LONG).show();
        }else if(TextUtils.isEmpty(phoneEt.getText().toString())){
            Toast.makeText(this,"Please enter your phone number ",Toast.LENGTH_LONG).show();
        }else if(TextUtils.isEmpty(cityEt.getText().toString())){
            Toast.makeText(this,"Please enter your city ",Toast.LENGTH_LONG).show();
        }else if(TextUtils.isEmpty(addressEt.getText().toString())){
            Toast.makeText(this,"Please enter your address ",Toast.LENGTH_LONG).show();
        }else {
            confirmOrder();
        }
    }



    private void confirmOrder() {
        final String saveCurrentDate,saveCurrentTime;

        Calendar calFroDate = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy");
        saveCurrentDate = currentDate.format(calFroDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        saveCurrentTime = currentTime.format(calFroDate.getTime());



        final DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference()
                .child(Util.confirmedOrders);

        final HashMap<String,Object> confirmedOrdersMap= new HashMap<>();
        confirmedOrdersMap.put(Util.totalPrice, totalPriceString);
        confirmedOrdersMap.put(Util.userName, nameEt.getText().toString());
        confirmedOrdersMap.put(Util.userPhone, phoneEt.getText().toString());
        confirmedOrdersMap.put(Util.uploadedDate, saveCurrentDate);
        confirmedOrdersMap.put(Util.uploadedTime, saveCurrentTime);
        confirmedOrdersMap.put(Util.userAddress,addressEt.getText().toString());
        confirmedOrdersMap.put(Util.userCity, cityEt.getText().toString());
        confirmedOrdersMap.put(Util.orderNumber,orderNumber);
        confirmedOrdersMap.put(Util.orderState,Util.notShipped);

        ordersRef.child(Util.usersView).child(MainActivity.currentOnlineUser.getPhone())
                .child(orderNumber)
                .updateChildren(confirmedOrdersMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {

                 @Override
                    public void onComplete(@NonNull Task<Void> task) {
                      if(task.isSuccessful()){
                         ordersRef.child(Util.adminView)
                                 .child(orderNumber)
                            .updateChildren(confirmedOrdersMap)
                                 .addOnCompleteListener(new OnCompleteListener<Void>() {

                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                    /*
                     *Empty user's cart if order confirmed
                     */
                         FirebaseDatabase.getInstance().getReference().child(Util.cartListStDbName)
                            .child(MainActivity.currentOnlineUser.getPhone())
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                        {
                                            Toast.makeText(ConfirmOrderActivity.this
                                                    ,"Thank you for your order ",Toast.LENGTH_LONG)
                                                    .show();
                                            Intent intent = new Intent(ConfirmOrderActivity.this, HomeActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                        }
                                    else{
                                        Toast.makeText(ConfirmOrderActivity.this
                                                ," Can't confirme your order ",Toast.LENGTH_LONG)
                                                .show();
                                    }

                                }
                            });
                         }
                        }
                     });
                     }
                 }
                });
            }
// Displaying updated online user information

    private void userInfoDisplay() {

        final DatabaseReference userInfoRef = FirebaseDatabase.getInstance().getReference()
                .child(Util.usersDbName).child(MainActivity.currentOnlineUser.getPhone());

        userInfoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(snapshot.child(Util.userCity).exists())
                    {
                        String city= Objects.requireNonNull(snapshot.child(Util.userCity).getValue()).toString();
                        cityEt.setText(city);
                    }
                        String name= Objects.requireNonNull(snapshot.child(Util.userName).getValue()).toString();
                        String address= Objects.requireNonNull(snapshot.child(Util.userAddress).getValue()).toString();
                        String phone= Objects.requireNonNull(snapshot.child(Util.userPhone).getValue()).toString();

                        nameEt.setText(name);
                        phoneEt.setText(phone);
                        addressEt.setText(address);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
