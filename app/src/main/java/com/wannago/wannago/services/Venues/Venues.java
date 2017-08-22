package com.wannago.wannago.services.Venues;

import com.wannago.wannago.API.WEBAPI;
import com.wannago.wannago.API.WannaGoAPI;
import com.wannago.wannago.models.CreateVenueModel;
import com.wannago.wannago.models.VenueModel;
import com.wannago.wannago.services.IService;
import com.wannago.wannago.services.Services;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Rafael Valle on 3/26/2017.
 */

public class Venues implements IService
{
        WannaGoAPI api;

        @Override
        public void Init() {
            api = WEBAPI.API;
        }

        public void CreateVenue(CreateVenueModel model, final GetVenueResponse responseCallback)
        {
            String token = Services.Account.access_token;
            if(model != null && token != null && token != "")
            {
                Call<VenueModel> createVenue = api.CreateVenue("Bearer " + token, model);
                createVenue.enqueue(new Callback<VenueModel>() {
                    @Override
                    public void onResponse(Call<VenueModel> call, Response<VenueModel> response) {
                        if(response.isSuccessful() && responseCallback != null)
                        {
                            responseCallback.OnSuccess(response.code(),response.body());
                        }else if(responseCallback != null)
                            responseCallback.OnError(response.code(),response.message());
                    }

                    @Override
                    public void onFailure(Call<VenueModel> call, Throwable t) {

                        if(responseCallback != null)
                            responseCallback.OnError(1,t.getMessage());
                    }
                });
            }

        }


        public void GetVenue(int id, final GetVenueResponse responseCallback)
        {
               Call<VenueModel> getVenue = api.GetVenue(id);
                getVenue.enqueue(new Callback<VenueModel>() {
                        @Override
                        public void onResponse(Call<VenueModel> call, Response<VenueModel> response)
                        {
                            if(response.isSuccessful() && responseCallback != null)
                            {
                                responseCallback.OnSuccess(response.code(),response.body());
                            }else if(responseCallback != null)
                                responseCallback.OnError(response.code(),response.message());
                        }

                        @Override
                        public void onFailure(Call<VenueModel> call, Throwable t) {
                            if(responseCallback != null)
                                responseCallback.OnError(1,t.getMessage());
                        }
                });
        }

        public void GetVenues(final GetVenuesResponse responseCallback)
        {
                Call<VenueModel[]> getVenues = api.GetVenues();
            getVenues.enqueue(new Callback<VenueModel[]>() {
                @Override
                public void onResponse(Call<VenueModel[]> call, Response<VenueModel[]> response) {
                    if(response.isSuccessful() && responseCallback != null)
                    {
                        responseCallback.OnSuccess(response.code(),response.body());
                    }else if(responseCallback != null)
                        responseCallback.OnError(response.code(),response.message());
                }

                @Override
                public void onFailure(Call<VenueModel[]> call, Throwable t) {
                    if(responseCallback != null)
                        responseCallback.OnError(1,t.getMessage());
                }
            });
        }
}


