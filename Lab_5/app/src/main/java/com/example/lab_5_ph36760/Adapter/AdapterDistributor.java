package com.example.lab_5_ph36760.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab_5_ph36760.Handle.Item_Distributor_Handle;
import com.example.lab_5_ph36760.Model.Distributor;
import com.example.lab_5_ph36760.Model.Response_Model;
import com.example.lab_5_ph36760.R;
import com.example.lab_5_ph36760.Services.HttpRequest;
import com.example.lab_5_ph36760.MainActivity;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterDistributor extends RecyclerView.Adapter<AdapterDistributor.viewHolep> {
    private Context context;
    private ArrayList<Distributor> list;
    private Item_Distributor_Handle handle;
    private HttpRequest httpRequest;


    public AdapterDistributor(Context context, ArrayList<Distributor> list, Item_Distributor_Handle handle) {
        this.context = context;
        this.list = list;
        this.handle = handle;
    }

    @NonNull
    @Override
    public viewHolep onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_distributor, parent, false);
        return new viewHolep(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolep holder, int position) {
        httpRequest = new HttpRequest();
        Distributor distributor = list.get(position);
        holder.tvName.setText(distributor.getName());
        holder.tvStt.setText(String.valueOf(position + 1));
        holder.img_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Bạn có muốn xoá không")
                        .setCancelable(false)
                        .setPositiveButton("Xoá", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                handle.Delete(distributor.getId());
                                Toast.makeText(context, "Xoá thành công", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                builder.create().show();
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
               handle.Update(distributor.getId(), distributor);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class viewHolep extends RecyclerView.ViewHolder {
        TextView tvStt, tvName;
        ImageView img_Delete;

        public viewHolep(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvStt = itemView.findViewById(R.id.tv_stt);
            img_Delete = itemView.findViewById(R.id.img_delete);
        }
    }



}
