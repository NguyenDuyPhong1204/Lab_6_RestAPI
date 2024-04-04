package com.example.lab_5_ph36760;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab_5_ph36760.Adapter.AdapterDistributor;
import com.example.lab_5_ph36760.Handle.Item_Distributor_Handle;
import com.example.lab_5_ph36760.Model.Distributor;
import com.example.lab_5_ph36760.Services.HttpRequest;
import com.example.lab_5_ph36760.Model.Response_Model;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements Item_Distributor_Handle {
    private HttpRequest httpRequest;
    private AdapterDistributor adapterDistributor;
    private RecyclerView rcvDistributor;
    private ArrayList<Distributor> originList;
    private ArrayList<Distributor> displayList;
    EditText edtSearch;
    FloatingActionButton btn_add;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rcvDistributor = findViewById(R.id.rcv_distributor);
        btn_add = findViewById(R.id.btn_add);
        //khởi tạo services request
        httpRequest = new HttpRequest();
        //thực thi call api
        httpRequest.callApi().getListDistributos().enqueue(getDistributorAPI);

        edtSearch = findViewById(R.id.edt_search);
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //lấy từ khoá từ ô tìm kiếm
                    String key = edtSearch.getText().toString();

                    httpRequest.callApi().searchDistributor(key)//phương thức api cần thực thi
                            .enqueue(getDistributorAPI);//xử lý bất đồng bộ
                    //vì giá trị trả về vẫ là một list distributor
                    //nên có thể sử dụng lại callback của getListDistributor
                    return true;
                }

                return false;
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogAdd();
            }
        });

    }

    private void getData(ArrayList<Distributor> list) {
        adapterDistributor = new AdapterDistributor(this, list, this);
        rcvDistributor.setLayoutManager(new LinearLayoutManager(this));
        rcvDistributor.setAdapter(adapterDistributor);

    }

    private void openDialogAdd() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();

        View diaglogAdd = inflater.inflate(R.layout.dialog_add, null);
        builder.setView(diaglogAdd);
        Dialog dialog = builder.create();
        dialog.show();

        // Khởi tạo đối tượng btnAdd từ layout dialog_add.xml
        Button btnAdd = diaglogAdd.findViewById(R.id.btn_add_dialog);
        EditText edt_name = diaglogAdd.findViewById(R.id.edt_name);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edt_name.getText().toString();
                Distributor distributor = new Distributor();

                if (name.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Vui lòng nhập tên nhà phân phối", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    distributor.setName(name);
                    httpRequest.callApi().addDistributor(distributor).enqueue(responseDistributorAPI);
                    Toast.makeText(MainActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });

    }


    Callback<Response_Model<ArrayList<Distributor>>> getDistributorAPI = new Callback<Response_Model<ArrayList<Distributor>>>() {
        @Override
        public void onResponse(Call<Response_Model<ArrayList<Distributor>>> call, Response<Response_Model<ArrayList<Distributor>>> response) {
            // khi call thành công sẽ chạy vào hàm này
            if (response.isSuccessful()) {
                // check status
                if (response.body().getStatus() == 200) {
                    // lấy data
                    ArrayList<Distributor> list = response.body().getData();
                    Log.d("List", "onResponse: " + list);
                    // set dữ liệu lên rcv
                    getData(list);
                    // Thông báo
//                    Toast.makeText(MainActivity.this, response.body().getMessenger(), Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onFailure(Call<Response_Model<ArrayList<Distributor>>> call, Throwable t) {
            Log.d(">>>GetListDistributor", "onFailure: " + t.getMessage());
        }
    };


    Callback<Response_Model<Distributor>> responseDistributorAPI = new Callback<Response_Model<Distributor>>() {
        @Override
        public void onResponse(Call<Response_Model<Distributor>> call, Response<Response_Model<Distributor>> response) {
            if (response.isSuccessful()) {
                if (response.body().getStatus() == 200) {
                    //gọi callback load lại dữ liệu
                    httpRequest.callApi().getListDistributos().enqueue(getDistributorAPI);
                }
            }
        }

        @Override
        public void onFailure(Call<Response_Model<Distributor>> call, Throwable t) {
            Log.d(">>>GetListDistributor", "onFailure: " + t.getMessage());
        }
    };

    @Override
    public void Delete(String id) {
        httpRequest.callApi().deleteDistributorById(id).enqueue(responseDistributorAPI);
    }

    @Override
    public void Update(String id, Distributor distributor) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_update, null);
        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.show();

        EditText edtName = view.findViewById(R.id.edt_name_update);
        Button btnUpdate = view.findViewById(R.id.btn_update_dialog);

        edtName.setText(distributor.getName());

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtName.getText().toString();
                String id = distributor.getId();
                if(name.isEmpty()){
                    Toast.makeText(MainActivity.this, "Vui lòng nhập tên nhà phân phối", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    Distributor distributor1 = new Distributor();
                    distributor1.setName(name);
                    httpRequest.callApi().updateDistributorById(id, distributor1).enqueue(responseDistributorAPI);
                    dialog.dismiss();
                    Toast.makeText(MainActivity.this, "Chỉnh sửa thành công", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}