package com.example.lab_5_ph36760.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.lab_5_ph36760.Model.Distributor;
import com.example.lab_5_ph36760.R;

import java.util.ArrayList;

public class AdapterSpinner extends BaseAdapter {
    private Context context;
    private ArrayList<Distributor> list;

    public AdapterSpinner(Context context, ArrayList<Distributor> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.layout_spinner, parent, false);
        TextView tvName = convertView.findViewById(R.id.name_distributor);
        tvName.setText(list.get(position).getName());
        return convertView;
    }
}
