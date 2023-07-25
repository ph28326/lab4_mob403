package com.example.mob403_asm.retrofitutil;

import com.example.mob403_asm.model.ApiResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {
    @FormUrlEncoded
    @POST("register.php")
    Call<ApiResponse> performUserRegister(@Field("user_name") String userName,
                                          @Field("password") String password,
                                          @Field("name") String name,
                                          @Field("email") String email);


    @FormUrlEncoded
    @POST("login.php")
    Call<ApiResponse> performUserLogin(@Field("user_name") String userName,
                                          @Field("password") String password);

    @FormUrlEncoded
    @POST("reset_password.php")
    Call<ApiResponse> performResetPassword(@Field("email") String email);
}
