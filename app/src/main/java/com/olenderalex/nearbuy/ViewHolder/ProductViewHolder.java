package com.olenderalex.nearbuy.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.olenderalex.nearbuy.Interfaces.ItemClickListener;
import com.olenderalex.nearbuy.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView productNameTV,productPriceTV;
    public ImageView productImage;
    public ItemClickListener listener;
    public ImageView favoriteEmptyIv,favoriteFilledIv;


    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);

        productImage=itemView.findViewById(R.id.product_image_cardView2);
        productNameTV=itemView.findViewById(R.id.product_name_cardView2);
        productPriceTV=itemView.findViewById(R.id.product_price_cardView2);
        favoriteEmptyIv=itemView.findViewById(R.id.favorite_empty_cardView2);
        favoriteFilledIv=itemView.findViewById(R.id.favorite_filled_cardView2);

    }

    public void setItemClickListener (ItemClickListener listener){

        this.listener=listener;
    }

    @Override
    public void onClick(View view) {
        listener.onClick(view,getAdapterPosition(),false);
    }
}
