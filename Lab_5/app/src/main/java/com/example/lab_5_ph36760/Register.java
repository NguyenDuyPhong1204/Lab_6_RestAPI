package com.example.lab_5_ph36760;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.lab_5_ph36760.Model.Response_Model;
import com.example.lab_5_ph36760.Model.User;
import com.example.lab_5_ph36760.Services.HttpRequest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeoutException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register extends AppCompatActivity {
    EditText edtUser, edtEmail, edtPass, edtFullName;
    Button btnRegister;
    ImageView imgAvatar;
    private File file;
    private HttpRequest httpRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        edtUser = findViewById(R.id.edt_userName);
        edtEmail = findViewById(R.id.edt_email);
        edtPass = findViewById(R.id.edt_password);
        edtFullName = findViewById(R.id.edt_fullname);
        imgAvatar = findViewById(R.id.img_profile);
        btnRegister = findViewById(R.id.btn_register);

        httpRequest = new HttpRequest();

        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sử dụng requestBody
                RequestBody _username = RequestBody.create(MediaType.parse("multipart/from-data"), edtUser.getText().toString());
                RequestBody _password = RequestBody.create(MediaType.parse("multipart/form-data"), edtPass.getText().toString());
                RequestBody _email = RequestBody.create(MediaType.parse("multipart/form-data"), edtEmail.getText().toString());
                RequestBody _name = RequestBody.create(MediaType.parse("multipart/form-data"), edtFullName.getText().toString());
                MultipartBody.Part muPart;
                if (file != null) {
                    RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                    muPart = MultipartBody.Part.createFormData("avatar", file.getName(), requestFile);
                    //"avatar" là cùng với key trong mutipart

                } else {
                    muPart = null;
                }
                httpRequest.callApi().register(_username, _password, _email, _name, muPart).enqueue(responseUser);
            }

        });
    }

    private void chooseImage() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
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
                Glide.with(Register.this).load(file)
                        .thumbnail(Glide.with(Register.this).load(R.mipmap.ic_launcher))
                        .centerCrop()
                        .circleCrop()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(imgAvatar);
            }
        }
    });

    private File createFileFromUri(Uri path, String name) {
        File _file = new File(Register.this.getCacheDir(), name + ".png");
        try {
            InputStream in = Register.this.getContentResolver().openInputStream(path);
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

    Callback<Response_Model<User>> responseUser = new Callback<Response_Model<User>>() {
        @Override
        public void onResponse(Call<Response_Model<User>> call, Response<Response_Model<User>> response) {
            if (response.isSuccessful()) {

                if (response.body().getStatus() == 200) {
                    Toast.makeText(Register.this, "Đăng kí thành công", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Register.this, Login.class));
                }
            }
        }

        @Override
        public void onFailure(Call<Response_Model<User>> call, Throwable t) {
            if (t instanceof TimeoutException) {
                Toast.makeText(Register.this, "Kết nối mạng không ổn định. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("Lỗi Callback", "onFailure: " + t.getMessage());
            }
        }
    };

}