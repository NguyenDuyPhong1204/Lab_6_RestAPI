package com.example.lab_5_ph36760.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lab_5_ph36760.Handle.Item_Fruit_Handle;
import com.example.lab_5_ph36760.Model.Fruit;
import com.example.lab_5_ph36760.R;

import java.util.ArrayList;

public class AdapterFruit extends RecyclerView.Adapter<AdapterFruit.viewHolep>{

    private Context context;
    private ArrayList<Fruit> list;

    private Item_Fruit_Handle handle;

    public AdapterFruit(Context context, ArrayList<Fruit> list, Item_Fruit_Handle handle) {
        this.context = context;
        this.list = list;
        this.handle = handle;
    }

    @NonNull
    @Override
    public viewHolep onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fruit,parent,false);
        return new viewHolep(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolep holder, int position) {
        Fruit fruit = list.get(position);

        Glide.with(context).load(fruit.getImage()).thumbnail(Glide.with(context).load(R.drawable.loading)).into(holder.imgFruit);
        holder.tvName.setText(fruit.getName());
        holder.tvPrice.setText(String.valueOf(fruit.getPrice()));
        holder.tvQuantity.setText(String.valueOf(fruit.getQuantity()));
        Log.d("Image", "onBindViewHolder: " + fruit.getImage());
        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handle.Delete(fruit.get_id());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class viewHolep extends RecyclerView.ViewHolder{
        ImageView imgFruit, imgDelete;
        TextView tvName, tvPrice, tvQuantity;
        public viewHolep(@NonNull View itemView) {
            super(itemView);
            imgFruit = itemView.findViewById(R.id.img_fruit);
            imgDelete = itemView.findViewById(R.id.img_delete_fruit);
            tvName = itemView.findViewById(R.id.tv_name_fruit);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
        }
    }
}
