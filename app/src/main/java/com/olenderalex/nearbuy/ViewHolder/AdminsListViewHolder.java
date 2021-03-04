package com.olenderalex.nearbuy.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.olenderalex.nearbuy.Interfaces.ItemClickListener;
import com.olenderalex.nearbuy.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdminsListViewHolder extends RecyclerView.ViewHolder {

    public TextView adminName;
    public ItemClickListener listener;


    public AdminsListViewHolder(@NonNull View itemView) {
        super(itemView);

        adminName = itemView.findViewById(R.id.admin_list_name);

    }


    public void setItemClickListener(ItemClickListener listener) {

        this.listener = listener;
    }

    public void onClick(View view) {
        listener.onClick(view, getAdapterPosition(), false);
    }
}