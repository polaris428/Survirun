package com.example.survirun.server;

import com.example.survirun.data.ExerciseData;
import com.example.survirun.data.LoginData;
import com.example.survirun.data.NewUserData;
import com.example.survirun.data.ResultData;
import com.example.survirun.data.TokenData;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ServiceService {
    @POST("/api/v1/auth/new")
    Call<TokenData> signUp(@Body NewUserData user);

    @POST("/api/v1/auth/local")
    Call<TokenData>login(@Body LoginData loginData);

    @PATCH("/api/v1/auth/by-username/:{username}")
    Call<ResultData>inputName(@Path("username")String name,@Header("x-access-token")String token);

    @GET("api/v1/exercise")
    Call<ExerciseData>getExercise(@Header("x-access-token")String token);
}
