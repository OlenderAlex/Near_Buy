package com.olenderalex.nearbuy.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.olenderalex.nearbuy.Interfaces.ItemClickListener;
import com.olenderalex.nearbuy.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView  productNameTxt ,productPriceTxt , productQuantityTxt;
    public ImageView productIv;
    private ItemClickListener itemClickListener;

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);

        productNameTxt = itemView.findViewById(R.id.cart_product_name_cv_holder);
        productPriceTxt = itemView.findViewById(R.id.cart_price_holder);
        productQuantityTxt= itemView.findViewById(R.id.cart_quantity_holder);
        productIv= itemView.findViewById(R.id.cart_product_image_holder);

    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(),false);

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
