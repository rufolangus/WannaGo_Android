package com.wannago.wannago.services.Account;

import android.util.Log;

import com.wannago.wannago.API.WEBAPI;
import com.wannago.wannago.API.WannaGoAPI;
import com.wannago.wannago.models.ImageUploadResponse;
import com.wannago.wannago.models.LoginModel;
import com.wannago.wannago.models.LoginResponse;
import com.wannago.wannago.models.RegisterModel;
import com.wannago.wannago.services.IService;
import com.wannago.wannago.services.Services;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.internal.schedulers.ScheduledRunnable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Rafael Valle on 3/8/2017.
 */

public class Account implements IService
{
    public String accountID;
    public String username;
    public String firstName;
    public String lastName;
    public String email;
    public String imageUrl;
    public Integer Gender;
    public String birthDate;
    public String access_token;
    public String refresh_token;
    public int interval;
    private WannaGoAPI api;
    private TimerTask refresh_task;
    public void Init()
    {
        if(WEBAPI.API != null)
            api = WEBAPI.API;

        refresh_task = new TimerTask() {
            @Override
            public void run() {
                final Call<LoginResponse> refeshToken = api.RefreshToken("refresh_token", "offline_access", refresh_token);
                refeshToken.enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        if (response.isSuccessful()) {
                            String token = response.body().access_token;
                            access_token = token;
                            refresh_token = response.body().refresh_token;
                            interval = response.body().expires_in;
                        } else {
                            Log.d("Error", response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        Log.d("Error", t.getMessage());
                    }
                });
            }
        };
    }

    public void StartTask()
    {
        Timer timer = new Timer();
        timer.schedule(refresh_task,interval*1000,interval*1000);
    }

    public void Register(RegisterModel model, final RegisterCallback wCallBack)
    {
        if(model != null)
        {
            Call<Void> registerCall = api.Register(model);
            registerCall.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if(response.isSuccessful() && wCallBack != null)
                        wCallBack.OnSuccess(response.code(), response.message());
                    else if (wCallBack != null)
                        wCallBack.OnError(response.code(), response.message());
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t)
                {
                    if (wCallBack != null)
                        wCallBack.OnError(-1, t.getLocalizedMessage());
                }
            });

            }
    }
    public void Login(LoginModel model, final LoginCallback wCallBack)
    {
        if(model != null){
            Call<LoginResponse> loginCall = api.Login(model.username, model.password, model.grant_type, model.scope);
            loginCall.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response)
                {
                    if(wCallBack == null)
                        return;
                    if(response.isSuccessful())
                    {
                        String token = response.body().access_token;
                        access_token = token;
                        refresh_token = response.body().refresh_token;
                        interval = response.body().expires_in;
                        StartTask();
                        wCallBack.OnSuccess(response.code(), token);
                    }else
                        wCallBack.OnError(response.code(),response.message());
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    if(wCallBack != null)
                        wCallBack.OnError(-1, t.getLocalizedMessage());
                }
            });
        }
    }

    public void GetData(final GetDataCallback wCallback)
    {
        if(access_token != null || !access_token.equals(""))
        {
            final Call<RegisterModel> getData = api.GetUserData("Bearer " + access_token);
            getData.enqueue(new Callback<RegisterModel>() {
                @Override
                public void onResponse(Call<RegisterModel> call, Response<RegisterModel> response)
                {
                    if(response.isSuccessful())
                    {
                        RegisterModel model = response.body();
                        CopyModel(model);
                        if(wCallback != null)
                            wCallback.OnSuccess(response.message());
                    }else
                    {
                        if(wCallback != null)
                            wCallback.OnFail(response.message());
                    }
                }

                @Override
                public void onFailure(Call<RegisterModel> call, Throwable t) {
                    if(wCallback != null)
                        wCallback.OnFail(t.getMessage());
                }
            });
        }
    }

    void CopyModel(RegisterModel model)
    {
        username = model.username;
        firstName = model.firstName;
        lastName = model.lastName;
        imageUrl = model.image;
        email = model.email;
        birthDate = model.dateOfBirth;
        Gender = model.gender;

    }

    public void UploadImage(File file, final ImageUploadResponse responsecallback)
    {
        if(access_token == null || access_token.equals(""))
            return;
        final RequestBody requestFile = RequestBody.create(MediaType.parse("Image/*"),file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file",file.getName(),requestFile);
        Call<Void> uploadImageCall = api.UploadProfileImage("Bearer " + access_token, body);
        uploadImageCall.enqueue(new Callback<Void>()
        {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response)
            {
                if(response.isSuccessful())
                {
                    if(responsecallback != null)
                        responsecallback.OnSuccess("Success");
                }
                else
                {
                    if(responsecallback != null)
                        responsecallback.OnError(response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t)
            {
                if(responsecallback != null)
                    responsecallback.OnError(t.getMessage());
            }
        });
    }

    public void Edit(final RegisterModel model, final LoginCallback callback){

        if(model != null && access_token != null && !access_token.equals(""))
        {
            Call<Void> editCall = api.EditAccount("Bearer " + access_token, model);
            editCall.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if(response.isSuccessful())
                    {
                        CopyModel(model);
                        if(callback != null)
                            callback.OnSuccess(response.code(),response.message());
                    }else
                        {
                            if(callback != null)
                                callback.OnError(response.code(),response.message());
                        }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    if(callback != null)
                        callback.OnError(0, t.getMessage());
                }
            });
        }
    }

}
