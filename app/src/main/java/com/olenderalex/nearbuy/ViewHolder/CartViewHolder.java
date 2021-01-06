package com.olenderalex.nearbuy.ViewHolder;

import android.view.View;
import android.widget.TextView;

import com.olenderalex.nearbuy.Interfaces.ItemClickListener;
import com.olenderalex.nearbuy.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView  productNameTxt ,productPriceTxt , productQuantityTxt;
    private ItemClickListener itemClickListener;

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);

        productNameTxt = itemView.findViewById(R.id.cart_product_name_cv);
        productPriceTxt = itemView.findViewById(R.id.cart_price);
        productQuantityTxt= itemView.findViewById(R.id.cart_quantity);

    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(),false);

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
