package com.example.mob403_asm.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText userNameLogin, passwordLogin;
    private TextView tvForgotPassword, tvRegister;
    private Button btnLogin;
    private CheckBox chkRemember;
    private ApiInterface apiInterface;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Ánh xạ
        userNameLogin = findViewById(R.id.usernameLogin);
        passwordLogin = findViewById(R.id.passwordLogin);
        chkRemember = findViewById(R.id.chkRemember);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvRegister = findViewById(R.id.tvRegister);

        //Bắt sự kiện cho tvRegister
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        //Khởi tạo Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2/logintest/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiInterface = retrofit.create(ApiInterface.class);
        //Xử lý sự kiện "Remember me" bằng cách lưu trạng thái checkbox vào SharePreferences
        //và khôi phục nó khi LoginActivity được khởi động lại
        sharedPreferences = getSharedPreferences("login_prefs",MODE_PRIVATE);

        boolean rememberMe = sharedPreferences.getBoolean("remember_me",false);
        chkRemember.setChecked(rememberMe);

        //Bắt sự kiện cho btnLogin
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();

                //Lưu trạng thái "Remember me" vào SharedPreferences
                boolean isChecked = chkRemember.isChecked();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("remember_me",isChecked);
                editor.apply();
            }
        });

        //Bắt sự kiện cho tvForgotPassword
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgotPasswordDialog();
            }
        });
        if (rememberMe){
            //Khôi phục tên tên đăng nhập và mật khẩu từ SharedPreferences
            String savedUserName = sharedPreferences.getString("user_name","");
            String savedPassword = sharedPreferences.getString("password","");
            userNameLogin.setText(savedUserName);
            passwordLogin.setText(savedPassword);
        }
    }

    private void showForgotPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_reset,null);
        builder.setView(view);
        
        final TextInputEditText edtEmail = view.findViewById(R.id.edtEmail);
        Button btnReset = view.findViewById(R.id.btnReset);
        
        final AlertDialog dialog = builder.create();
        dialog.show();
        
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString().trim();
                
                if (!email.isEmpty()){
                    resetPassword(email);
                    dialog.dismiss();
                }else {
                    Toast.makeText(LoginActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void resetPassword(String email) {
        Call<ApiResponse> call = apiInterface.performResetPassword(email);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()){
                    ApiResponse apiResponse = response.body();
                    if (apiResponse != null && apiResponse.getStatus().equals("ok")){
                        Toast.makeText(LoginActivity.this, "Reset password email sent", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(LoginActivity.this, "Failed to reset password", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(LoginActivity.this, "Server error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginUser() {
        String userName = userNameLogin.getText().toString().trim();
        String password = passwordLogin.getText().toString().trim();
        
        if (userName.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Please Fill All Details", Toast.LENGTH_SHORT).show();
        }else {
            Call<ApiResponse> call = apiInterface.performUserLogin(userName,password);
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful()){
                        ApiResponse apiResponse = response.body();
                        if (apiResponse != null){
                            String status = apiResponse.getStatus();
                            int resultCode = apiResponse.getResultCode();

                            if (status.equals("ok")){
                                if (resultCode == 1){
                                    String name = apiResponse.getName();
                                    Toast.makeText(LoginActivity.this, "Login Successful! Welcome, "
                                            + name + "!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                }else {
                                    Toast.makeText(LoginActivity.this, "Invalid Username or Password!", Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                Toast.makeText(LoginActivity.this, "Login Failed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }else {
                        Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            //Lưu trạng thái tên đăng nhập và mật khẩu vào SharePreferences nếu "Remember me" được chọn
            if (chkRemember.isChecked()){
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("user_name",userName);
                editor.putString("password",password);
                editor.apply();
            }
        }
    }
}