package com.olenderalex.nearbuy;

import androidx.annotation.NonNull;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.olenderalex.nearbuy.Utils.Util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmOrderActivity extends AppCompatActivity {

    private EditText nameEt,phoneEt,cityEt,addressEt;
    private Button confirmOrderBtn;

    TextView totalPriceTv;
    String totalPriceString ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        totalPriceString=getIntent().getStringExtra(Util.totalPrice);

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
                .child(Util.confirmedOrders)
                .child(Util.currentOnlineUser.getPhone());

        HashMap<String,Object> confirmedOrdersMap= new HashMap<>();
        confirmedOrdersMap.put(Util.totalPrice, totalPriceString);
        confirmedOrdersMap.put(Util.userName, nameEt.getText().toString());
        confirmedOrdersMap.put(Util.userPhone, phoneEt.getText().toString());
        confirmedOrdersMap.put(Util.uploadedDate, saveCurrentDate);
        confirmedOrdersMap.put(Util.uploadedTime, saveCurrentTime);
        confirmedOrdersMap.put(Util.userAddress,addressEt.getText().toString());
        confirmedOrdersMap.put(Util.userCity, cityEt.getText().toString());

        confirmedOrdersMap.put(Util.orderState,Util.notShipped);


        /*
        *Empty user's cart if order confirmed
         */


        ordersRef.updateChildren(confirmedOrdersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    FirebaseDatabase.getInstance().getReference().child(Util.cartListStDbName)
                            .child(Util.usersCart).child(Util.currentOnlineUser.getPhone())
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                        {
                                            Toast.makeText(ConfirmOrderActivity.this,"Thank you for your order ",Toast.LENGTH_LONG)
                                                    .show();

                                            Intent intent = new Intent(ConfirmOrderActivity.this, HomeActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                        }

                                }
                            });
                }
            }
        });
    }
}