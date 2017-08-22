package com.wannago.wannago.API;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Rafael Valle on 3/22/2017.
 */

public final class WEBAPI
{
    public static WannaGoAPI API;

    private WEBAPI()
    {

    }

    public static void Init()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://youraddress")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        API = retrofit.create(WannaGoAPI.class);
    }

}
