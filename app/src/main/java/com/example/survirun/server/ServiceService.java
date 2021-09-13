package com.example.survirun.server;

import com.example.survirun.data.NewUserData;
import com.example.survirun.data.TokenData;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ServiceService {
    @POST("/api/v1/auth/new")
    Call<TokenData> signUp(@Body NewUserData user);

}
