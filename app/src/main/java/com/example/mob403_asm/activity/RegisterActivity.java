package com.example.mob403_asm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mob403_asm.R;
import com.example.mob403_asm.model.ApiResponse;
import com.example.mob403_asm.retrofitutil.ApiInterface;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText usernameRegister, fullnameRegister, emailRegister, passwordRegister, confirmPasswordRegister;
    private Button btnRegister;
    private ApiInterface apiInterface;
    private TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Ánh xạ
        usernameRegister = findViewById(R.id.usernameRegister);
        fullnameRegister = findViewById(R.id.fullnameRegister);
        emailRegister = findViewById(R.id.emailRegister);
        passwordRegister = findViewById(R.id.passwordRegister);
        confirmPasswordRegister = findViewById(R.id.confirmPasswordRegister);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        //Bắt sự kiện cho tvLogin
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        //Khởi tạo Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2/logintest/")
                .addConverterFactory(GsonConverterFactory.create())//chuyển đổi dữ liệu thành đối tượng java
                .build();

        //Tạo ApiInterface từ Retrofit
        apiInterface = retrofit.create(ApiInterface.class);

        //Bắt sự kiện cho btnRegister
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

    }

    private void registerUser() {
        String userName = usernameRegister.getText().toString().trim();
        String name = fullnameRegister.getText().toString().trim();
        String email = emailRegister.getText().toString().trim();
        String password = passwordRegister.getText().toString().trim();
        String confirmPassword = confirmPasswordRegister.getText().toString().trim();

        if (userName.isEmpty() || name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()){
            Toast.makeText(this, "Please Fill All Details", Toast.LENGTH_SHORT).show();
        }else {
            if (confirmPassword.compareTo(password) == 0){
                //gửi yêu cầu đăng ký
                Call<ApiResponse> call = apiInterface.performUserRegister(userName,password,name,email);
                call.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        if (response.isSuccessful()){
                            ApiResponse apiResponse = response.body();
                            if (apiResponse != null){
                                String status = apiResponse.getStatus();
                                int resultCode = apiResponse.getResultCode();

                                if (status.equals("ok")){
                                    if (resultCode == 0){
                                        usernameRegister.setError("Username already exists!" );
                                        Toast.makeText(RegisterActivity.this, "Username already exists!", Toast.LENGTH_SHORT).show();
                                    } else if (resultCode == 1) {
                                        Toast.makeText(RegisterActivity.this, "Registration successfull!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                                    }
                                }else {
                                    Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }else {
                            Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        Toast.makeText(RegisterActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }else {
                Toast.makeText(this, "Confirm Password and Password didn't match", Toast.LENGTH_SHORT).show();
            }
        }
    }
}