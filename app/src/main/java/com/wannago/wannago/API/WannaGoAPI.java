package com.wannago.wannago.API;

import com.wannago.wannago.models.CreateEventModel;
import com.wannago.wannago.models.CreateVenueModel;
import com.wannago.wannago.models.EventModel;
import com.wannago.wannago.models.LoginModel;
import com.wannago.wannago.models.LoginResponse;
import com.wannago.wannago.models.RegisterModel;
import com.wannago.wannago.models.VenueModel;

import java.io.File;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Rafael Valle on 3/8/2017.
 */

public interface WannaGoAPI
{

    @FormUrlEncoded
    @POST("/connect/token")
    Call<LoginResponse> Login(@Field("username") String usern,
                              @Field("password") String pass,
                              @Field("grant_type")String grantType,
                              @Field("scope")String scope);
    @FormUrlEncoded
    @POST("/connect/token")
    Call<LoginResponse> RefreshToken(@Field("grant_type") String type, @Field("scope") String scope, @Field("refresh_token") String token);
    @GET("/api/Account/Get")
    Call<RegisterModel> GetUserData(@Header("Authorization") String token);
    @POST("/api/Account/Register")
    Call<Void> Register(@Body RegisterModel model);
    @POST("/api/Account/Edit")
    Call<Void> EditAccount(@Header("Authorization") String token, @Body RegisterModel model);
    @Multipart
    @POST("/api/Account/UploadImage")
    Call<Void> UploadProfileImage(@Header("Authorization") String token, @Part MultipartBody.Part file);
    @POST("/api/Event/Post")
    Call<Integer> CreateEvent(@Header("Authorization") String token, @Body CreateEventModel model);
    @GET("api/Event/Get/{id}")
    Call<EventModel> GetEvent(@Path("id") int id);
    @GET("api/Event/Get")
    Call<EventModel[]> GetEvents();
    @GET("api/Event/GetAll")
    Call<EventModel[]> GetAll(@Header("Authorization") String token);
    @GET("api/Event/GetAuth")
    Call<EventModel>GetEventAuth(@Header("Authorization") String token, @Query("id") int id);
    @GET("api/Event/CheckIn")
    Call<Void>CheckIn (@Header("Authorization") String token, @Query("id") int id);
    @GET("api/Event/RSVP")
    Call<Void> RSVP(@Header("Authorization") String token, @Query("id") int id, @Query("going") Boolean going);
    @Multipart
    @POST("api/Event/UploadImage")
    Call<Void>UploadImage(@Header("Authorization") String token, @Part("eventID") RequestBody Id, @Part MultipartBody.Part file);
    @POST("/api/Venues/Post")
    Call<VenueModel> CreateVenue(@Header("Authorization") String token, @Body CreateVenueModel model);

    @GET("/api/Venues/Get/{id}")
    Call<VenueModel> GetVenue(@Path("id") int id);

    @GET("/api/Venues/Get")
    Call<VenueModel[]> GetVenues();

}
