package com.example.lab_5_ph36760;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab_5_ph36760.Adapter.AdapterDistributor;
import com.example.lab_5_ph36760.Adapter.AdapterFruit;
import com.example.lab_5_ph36760.Handle.Item_Fruit_Handle;
import com.example.lab_5_ph36760.Model.Distributor;
import com.example.lab_5_ph36760.Model.Fruit;
import com.example.lab_5_ph36760.Model.Response_Model;
import com.example.lab_5_ph36760.Services.HttpRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListFruit extends AppCompatActivity implements Item_Fruit_Handle {
    private HttpRequest httpRequest;
    private RecyclerView recyclerView;
    private AdapterFruit adapterFruit;
    private String token;
    private SharedPreferences sharedPreferences;
    FloatingActionButton btn_add;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_fruit);
        recyclerView = findViewById(R.id.rcv_fruit);
        btn_add = findViewById(R.id.btn_add_fruit);
        sharedPreferences = getSharedPreferences("INFO", MODE_PRIVATE);
        token = sharedPreferences.getString("token","");
        httpRequest = new HttpRequest();
        httpRequest.callApi().getListFruitNoToken().enqueue(getFruitAPI);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ListFruit.this, Add_Fruit.class));
            }
        });
    }

    private void getData(ArrayList<Fruit> list) {
        adapterFruit = new AdapterFruit(this, list, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapterFruit);
    }
    Callback<Response_Model<ArrayList<Fruit>>> getFruitAPI = new Callback<Response_Model<ArrayList<Fruit>>>() {
        @Override
        public void onResponse(Call<Response_Model<ArrayList<Fruit>>> call, Response<Response_Model<ArrayList<Fruit>>> response) {
            // khi call thành công sẽ chạy vào hàm này
            if (response.isSuccessful()) {
                // check status
                if (response.body().getStatus() == 200) {
                    // lấy data
                    ArrayList<Fruit> list = response.body().getData();
                    Log.d("List", "onResponse: " + list);
                    // set dữ liệu lên rcv
                    getData(list);
                    // Thông báo
//                    Toast.makeText(MainActivity.this, response.body().getMessenger(), Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onFailure(Call<Response_Model<ArrayList<Fruit>>> call, Throwable t) {
            Log.d(">>>GetListDistributor", "onFailure: " + t.getMessage());
        }
    };


    @Override
    public void Delete(String id) {

    }

    @Override
    public void Update(String id, Fruit fruit) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        httpRequest.callApi().getListFruitNoToken().enqueue(getFruitAPI);
    }
}