package com.wannago.wannago.services.Events;

import com.wannago.wannago.API.WEBAPI;
import com.wannago.wannago.API.WannaGoAPI;
import com.wannago.wannago.models.CreateEventModel;
import com.wannago.wannago.models.EventModel;
import com.wannago.wannago.models.ImageUploadResponse;
import com.wannago.wannago.models.BasicResponse;
import com.wannago.wannago.services.IService;
import com.wannago.wannago.services.Services;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Rafael Valle on 3/26/2017.
 */

public class Events implements IService
{

    WannaGoAPI api;
    @Override
    public void Init() {
        api = WEBAPI.API;
    }

    public void CreateEvent(CreateEventModel model, final CreateEventResponse responseCallback)
    {
       String token = Services.Account.access_token;

        if(token != null && token != "" && model != null)
        {


            Call<Integer> createEventCall = api.CreateEvent("Bearer " + token,model);
            createEventCall.enqueue(new Callback<Integer>() {
                @Override
                public void onResponse(Call<Integer> call, Response<Integer> response) {
                    if(response.isSuccessful() && responseCallback != null)
                        responseCallback.OnSuccess(response.code(),response.body());
                    else if(responseCallback != null)
                        responseCallback.OnError(response.code(),response.message());
                }

                @Override
                public void onFailure(Call<Integer> call, Throwable t) {
                    if(responseCallback != null)
                        responseCallback.OnError(1,t.getMessage());
                }
            });
        }
    }

    public void CheckIn(Integer id, final BasicResponse basicResponse){
        String token = Services.Account.access_token;
        if(token == null || token == "")
            return;
        Call<Void> call = api.CheckIn("Bearer " + token, id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(basicResponse != null){
                    if(response.isSuccessful())
                        basicResponse.OnSuccess("Success");
                    else
                        basicResponse.OnError(response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if(basicResponse != null)
                    basicResponse.OnError(t.getMessage());
            }
        });
    }

    public void EditEvent(){

    }

    public void DeleteEvent(){

    }

    public void ShareEvent(){

    }

    public void RSVP(Integer id, Boolean going, final BasicResponse basicResponse){
        String token = Services.Account.access_token;
        if(token == null || token == "")
            return;
        Call<Void> call = api.RSVP("Bearer " + token, id, going);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(basicResponse != null)
                {
                    if(response.isSuccessful())
                        basicResponse.OnSuccess("Success!");
                    else
                        basicResponse.OnError(response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if(basicResponse != null)
                    basicResponse.OnError(t.getMessage());
            }
        });
    }

    public void GetEvent(Integer id, final GetEventResponse responseCallback)
    {
        Call<EventModel> call = api.GetEvent(id);
        call.enqueue(new Callback<EventModel>() {
            @Override
            public void onResponse(Call<EventModel> call, Response<EventModel> response) {
                if(response.isSuccessful() && responseCallback != null)
                {
                    responseCallback.OnSuccess(response.code(),response.body());
                }else if(responseCallback != null)
                    responseCallback.OnError(response.code(),response.message());
            }

            @Override
            public void onFailure(Call<EventModel> call, Throwable t) {
                if(responseCallback != null)
                    responseCallback.OnError(1,t.getMessage());
            }
        });
    }

    public void UploadImage(Integer eventID, File file, final ImageUploadResponse imageUploadResponse){

        RequestBody requestFile = RequestBody.create(MediaType.parse("Image/*"),file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file",file.getName(),requestFile);
        RequestBody id = RequestBody.create(MultipartBody.FORM,eventID.toString());
        String token = Services.Account.access_token;
        if(token == null || token == "")
            return;

        Call<Void> call = api.UploadImage("Bearer " + token, id, body);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                    if(response.isSuccessful())
                        if(imageUploadResponse != null)
                            imageUploadResponse.OnSuccess("YASS!");
                    else
                        if(imageUploadResponse != null)
                            imageUploadResponse.OnError(response.message());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                    if(imageUploadResponse != null)
                        imageUploadResponse.OnError(t.getMessage());
            }
        });

    }

    public void GetEvents(final GetEventsResponse responseCallback)
    {
            Call<EventModel[]> call = api.GetEvents();
        call.enqueue(new Callback<EventModel[]>() {
            @Override
            public void onResponse(Call<EventModel[]> call, Response<EventModel[]> response) {
                if(response.isSuccessful() && responseCallback != null)
                {
                    responseCallback.OnSuccess(response.code(),response.body());
                }else if(responseCallback != null)
                    responseCallback.OnError(response.code(),response.message());
            }

            @Override
            public void onFailure(Call<EventModel[]> call, Throwable t) {
                if(responseCallback != null)
                    responseCallback.OnError(1,t.getMessage());
            }
        });
    }
    public void GetEventAuthorized(int id, final GetEventResponse responseCallback){
        if(Services.Account.access_token == null || Services.Account.access_token == "")
            return;
        Call<EventModel> call = api.GetEventAuth("Bearer " + Services.Account.access_token,id);
        call.enqueue(new Callback<EventModel>() {
            @Override
            public void onResponse(Call<EventModel> call, Response<EventModel> response) {
                if(response.isSuccessful() && responseCallback != null)
                {
                    responseCallback.OnSuccess(response.code(),response.body());
                }else if(responseCallback != null)
                    responseCallback.OnError(response.code(),response.message());
            }

            @Override
            public void onFailure(Call<EventModel> call, Throwable t) {
                if(responseCallback != null)
                    responseCallback.OnError(1,t.getMessage());
            }
        });
    }
    public void GetEventsAuth(final GetEventsResponse responseCallback)
    {
        if(Services.Account.access_token == null || Services.Account.access_token == "")
            return;

        Call<EventModel[]> call = api.GetAll("Bearer " + Services.Account.access_token);
        call.enqueue(new Callback<EventModel[]>() {
            @Override
            public void onResponse(Call<EventModel[]> call, Response<EventModel[]> response) {
                if(response.isSuccessful() && responseCallback != null)
                {
                    responseCallback.OnSuccess(response.code(),response.body());
                }else if(responseCallback != null)
                    responseCallback.OnError(response.code(),response.message());
            }

            @Override
            public void onFailure(Call<EventModel[]> call, Throwable t) {
                if(responseCallback != null)
                    responseCallback.OnError(1,t.getMessage());
            }
        });
    }
}
