package com.example.lab_5_ph36760;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lab_5_ph36760.Model.Response_Model;
import com.example.lab_5_ph36760.Model.User;
import com.example.lab_5_ph36760.Services.HttpRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {
    EditText edtUsername, edtPassword;
    Button btnLogin;
    private HttpRequest httpRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        edtUsername = findViewById(R.id.edt_userName_Login);
        edtPassword = findViewById(R.id.edt_password_login);
        btnLogin = findViewById(R.id.btn_login);

        httpRequest = new HttpRequest();
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User();
                String _username = edtUsername.getText().toString();
                String _password = edtPassword.getText().toString();
                user.setUsername(_username);
                user.setPassword(_password);
                httpRequest.callApi().login(user).enqueue(responseUser);
            }
        });
    }

    Callback<Response_Model<User>> responseUser = new Callback<Response_Model<User>>() {
        @Override
        public void onResponse(Call<Response_Model<User>> call, Response<Response_Model<User>> response) {
            if (response.isSuccessful()) {
                if (response.body().getStatus() == 200) {
                    Toast.makeText(Login.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                    //lưu token, lưu device token, id
                    SharedPreferences sharedPreferences = getSharedPreferences("INFO", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("token", response.body().getToken());
                    editor.putString("refreshToken", response.body().getRefreshToken());
                    editor.putString("id", response.body().getData().get_id());
                    editor.apply();
                    Log.d("Token", "onResponse: " + response.body().getToken());
                    //sau khi chuyển màn hình chính
                    startActivity(new Intent(Login.this, ListFruit.class));
                }
            }
        }

        @Override
        public void onFailure(Call<Response_Model<User>> call, Throwable t) {
            Log.d("Lỗi Callback", "onFailure: " + t.getMessage());
        }
    };
}