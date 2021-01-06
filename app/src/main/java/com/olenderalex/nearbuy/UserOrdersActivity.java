package com.olenderalex.nearbuy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.olenderalex.nearbuy.Utils.Util;

import java.util.Objects;

public class UserOrdersActivity extends AppCompatActivity {
    private TextView msgOrderPlacedTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_orders);

        msgOrderPlacedTv = findViewById(R.id.userOrders_msg);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkOrderState();
    }

    private void checkOrderState() {
        DatabaseReference orderRef;
        orderRef = FirebaseDatabase.getInstance().getReference()
                .child(Util.confirmedOrders)
                .child(Util.currentOnlineUser.getPhone());
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String shippingState = Objects.requireNonNull(snapshot.child(Util.orderState).getValue()).toString();
                    String userName = Objects.requireNonNull(snapshot.child(Util.userName).getValue()).toString();

                    if (shippingState.equals(Util.shipped)) {
                        msgOrderPlacedTv.setText(R.string.you_order_was_chippd);
                        msgOrderPlacedTv.setVisibility(View.VISIBLE);


                    } else if (shippingState.equals(Util.notShipped)) {
                        msgOrderPlacedTv.setText(R.string.your_order_notshipped);
                        msgOrderPlacedTv.setVisibility(View.VISIBLE);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}