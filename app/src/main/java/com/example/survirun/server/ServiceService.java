package com.example.survirun.server;

import com.example.survirun.Medel.ScoreModel;
import com.example.survirun.data.EmileCheck;
import com.example.survirun.data.ExerciseData;
import com.example.survirun.data.ExerciseRecordData;
import com.example.survirun.data.FindUserData;
import com.example.survirun.data.ImageData;
import com.example.survirun.data.InfoData;
import com.example.survirun.data.LoginData;
import com.example.survirun.data.NewUserData;
import com.example.survirun.data.ProfileImageData;
import com.example.survirun.data.ResultData;
import com.example.survirun.data.TokenData;
import com.example.survirun.data.getUserData;
import com.example.survirun.data.rankingData;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ServiceService {
    @POST("/api/v1/auth/new")
    Call<TokenData> signUp(@Body NewUserData user);

    @POST("/api/v1/auth/local")
    Call<TokenData> login(@Body LoginData loginData);

    @PATCH("/api/v1/auth/by-username/{username}")
    Call<ResultData> inputName(@Path("username") String name, @Header("x-access-token") String token);

    @GET("/api/v1/exercise")
    Call<ExerciseData> getExercise(@Header("x-access-token") String token);

    @Multipart
    @POST("/api/v1/auth/profile")
    Call<ResultData> postProfile(@Header("x-access-token") String token, @Part MultipartBody.Part file);

    @POST("/api/v1/auth/defaultProfile")
    Call<ResultData>postDefaultImage(@Header("x-access-token") String token);


    @GET("/api/v1/auth/by-email/{email}/exists")
    Call<EmileCheck> getEmileCheck(@Path("email") String email);

   // @GET("/api/v1/friend/list")
    //Call<FindUserData> getFriendList(@Header("x-access-token") String token);
    @PATCH("/api/v1/friend")
    Call<ResultData>deleteFriend(@Header("x-access-token") String token,@Query("reqType")String reqType,@Query("email") String email);

    @GET("api/v1/friend/roomList")
    Call<FindUserData>getFriendList(@Header("x-access-token") String token);


    @POST("/api/v1/friend")
    Call<ResultData> postAddFriend(@Header("x-access-token") String token, @Query("reqType")String reqType,@Query("email") String  email);

    @GET("/api/v1/auth/jwt-decode")
    Call<InfoData>getInfo(@Header("x-access-token")String token);

    @GET("/api/v1/auth/profile")
    Call<ImageData> getProfile(@Header("x-access-token") String token, @Query("reqType") String reqType, @Query("resType") String resType);
    @GET("/api/v1/auth/profile")
    Call<ImageData> getSuchProfile(@Header("x-access-token") String token, @Query("reqType") String reqType, @Query("resType") String resType, @Query("username")String username);
    @PATCH("/api/v1/exercise")
    Call<ExerciseData>patchUploadExercise(@Header("x-access-token") String token, @Body ScoreModel scoreModel);



    @GET("/api/v1/auth/by-email/{email}")
    Call<getUserData>getUser(@Header("x-access-token") String token,@Path("email")String email);

    @GET("/api/v1/exercise/list")
    Call<ExerciseRecordData>getExerciseRecordData(@Header("x-access-token")String token);

    @GET("/api/v1/exercise/score/global")
    Call<rankingData>getRanking(@Header("x-access-token")String token);


    @PATCH("/api/v1/auth/intro")
    Call<ResultData>patchEditIntro(@Header("x-access-token")String token ,@Body String intro);



}
