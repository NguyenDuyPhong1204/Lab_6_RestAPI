package com.example.lab_5_ph36760;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.lab_5_ph36760.Adapter.AdapterSpinner;
import com.example.lab_5_ph36760.Model.Distributor;
import com.example.lab_5_ph36760.Model.Fruit;
import com.example.lab_5_ph36760.Model.Response_Model;
import com.example.lab_5_ph36760.Services.HttpRequest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Add_Fruit extends AppCompatActivity {
    private ImageView imgAdd;
    private EditText edtName, edtQuantity, edtPrice, edtStatus, edtDescript;
    private Button btnAdd;
    private Spinner spinner;
    private File file;
    private String id_distributor;
    private HttpRequest httpRequest;
    private ArrayList<Distributor> listDistributor;
    private AdapterSpinner adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_fruit);
        imgAdd = findViewById(R.id.img_add_fruit);
        edtName = findViewById(R.id.edt_name_fruit);
        edtQuantity = findViewById(R.id.edt_quantity);
        edtPrice = findViewById(R.id.edt_price);
        edtStatus = findViewById(R.id.edt_status);
        edtDescript = findViewById(R.id.edt_description);
        btnAdd = findViewById(R.id.btn_add_fruit);
        spinner = findViewById(R.id.spinner_distributor);
        listDistributor = new ArrayList<>();
        httpRequest = new HttpRequest();

        httpRequest.callApi().getListDistributos().enqueue(getDistributorAPI);



        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, RequestBody> bodyMap = new HashMap<>();
                String _name = edtName.getText().toString();
                int _quantity = Integer.parseInt(edtQuantity.getText().toString());
                double _price = Double.parseDouble(edtPrice.getText().toString());
                int _status = Integer.parseInt(edtStatus.getText().toString());
                String _description = edtDescript.getText().toString();
                //Put request body
                bodyMap.put("name", getRequestBody(_name));
                bodyMap.put("quantity", getRequestBody(String.valueOf(_quantity)));
                bodyMap.put("price", getRequestBody(String.valueOf(_price)));
                bodyMap.put("status", getRequestBody(String.valueOf(_status)));
                bodyMap.put("description", getRequestBody(_description));
                bodyMap.put("id_distributor", getRequestBody(id_distributor));
                MultipartBody.Part muPart;
                if (file != null) {
                    RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                    muPart = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
                    //"avatar" là cùng với key trong mutipart

                } else {
                    muPart = null;
                }
                httpRequest.callApi().addFruit(bodyMap, muPart).enqueue(responseFruit);
            }
        });
    }

    private void chooseImage() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            getImage.launch(intent);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    ActivityResultLauncher<Intent> getImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult o) {
            if (o.getResultCode() == Activity.RESULT_OK) {
                Intent data = o.getData();
                Uri imagePath = data.getData();

                file = createFileFromUri(imagePath, "avatar");
                //gilde để load hinh
                Glide.with(Add_Fruit.this).load(file)
                        .thumbnail(Glide.with(Add_Fruit.this).load(R.mipmap.ic_launcher))
                        .centerCrop()
                        .circleCrop()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(imgAdd);
            }
        }
    });

    private File createFileFromUri(Uri path, String name) {
        File _file = new File(Add_Fruit.this.getCacheDir(), name + ".png");
        try {
            InputStream in = Add_Fruit.this.getContentResolver().openInputStream(path);
            OutputStream out = new FileOutputStream(_file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
            return _file;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private RequestBody getRequestBody(String value) {
        return RequestBody.create(MediaType.parse("multipart/form-data"), value);
    }

    Callback<Response_Model<Fruit>> responseFruit = new Callback<Response_Model<Fruit>>() {
        @Override
        public void onResponse(Call<Response_Model<Fruit>> call, Response<Response_Model<Fruit>> response) {
            if (response.isSuccessful()) {
                if (response.body().getStatus() == 200) {
                    Toast.makeText(Add_Fruit.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Add_Fruit.this, ListFruit.class));
                    finish();
                }
            }
        }

        @Override
        public void onFailure(Call<Response_Model<Fruit>> call, Throwable t) {
            Toast.makeText(Add_Fruit.this, "Thêm không thành công", Toast.LENGTH_SHORT).show();
            Log.d("Lỗi thêm", "onFailure: " + t.getMessage());
        }
    };

    private void setDataSpinner(ArrayList<Distributor> list){
        adapter = new AdapterSpinner(this, list);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Distributor distributor = (Distributor) parent.getAdapter().getItem(position);
                id_distributor = distributor.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner.setSelection(0);
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
                    setDataSpinner(list);

                    // set dữ liệu lên rcv
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

}