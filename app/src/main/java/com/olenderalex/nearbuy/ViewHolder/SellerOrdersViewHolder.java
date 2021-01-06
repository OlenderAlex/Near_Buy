package com.olenderalex.nearbuy.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.olenderalex.nearbuy.Interfaces.ItemClickListener;
import com.olenderalex.nearbuy.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SellerOrdersViewHolder extends RecyclerView.ViewHolder {

    public TextView userName , userPhone, userTotalPrice;
    public TextView userDateAndTime , userAddress;
    public ItemClickListener listener;
    public Button showOrdersBtn;


    public SellerOrdersViewHolder(@NonNull View itemView) {
        super(itemView);

        userName=itemView.findViewById(R.id.user_name_new_orders);
        userPhone=itemView.findViewById(R.id.user_phone_new_orders);
        userAddress=itemView.findViewById(R.id.user_address_new_orders);
        userTotalPrice=itemView.findViewById(R.id.total_price_new_orders);
        userDateAndTime=itemView.findViewById(R.id.order_time_new_orders);

        showOrdersBtn=itemView.findViewById(R.id.show_details_new_orders_btn);
    }


    public void setItemClickListener (ItemClickListener listener){

        this.listener=listener;
    }

    public void onClick(View view) {
        listener.onClick(view,getAdapterPosition(),false);
    }
}
