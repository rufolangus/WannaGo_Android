package com.wannago.wannago;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.wannago.wannago.API.WEBAPI;
import com.wannago.wannago.API.WannaGoAPI;
import com.wannago.wannago.models.LoginModel;
import com.wannago.wannago.models.RegisterModel;
import com.wannago.wannago.services.Account.GetDataCallback;
import com.wannago.wannago.services.Account.LoginCallback;
import com.wannago.wannago.services.Account.RegisterCallback;
import com.wannago.wannago.services.Services;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Splash extends AppCompatActivity {

    private WannaGoAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        WEBAPI.Init();
        Services.Init();
        api = WEBAPI.API;

        //Registration call will fail, because user already exists, change username & e-mail to test.

        CheckStoragePermision();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        AttemptLogin();
    }


    void AttemptLogin(){
        SharedPreferences prefs = this.getSharedPreferences(
                "com.wannago.wannago", Context.MODE_PRIVATE);
        String pass = prefs.getString("pass","none");
        String user = prefs.getString("user","none");
        if(user == "none" || pass == "none") {
            RunIntent();
        }
        else
            RequestLogin(user,pass);
    }

     void CheckStoragePermision(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }
        else
            AttemptLogin();
    }

    void RequestLogin(final String userName, final String password){
        LoginModel model = new LoginModel(userName, password);
        Services.Account.Login(model, new LoginCallback() {
            @Override
            public void OnSuccess(int code, String rawResponse)
            {
                Toast("Login Successful");
                SharedPreferences prefs = getApplicationContext().getSharedPreferences(
                        "com.wannago.wannago",Context.MODE_PRIVATE);
                prefs.edit().putString("user",userName).putString("pass",password).apply();
                GetData();


            }

            @Override
            public void OnError(int code, String error)
            {
                Toast("Login Failed: " + code + " " + error);
            }
        });
    }
    void GetData(){

        Services.Account.GetData(new GetDataCallback() {
            @Override
            public void OnSuccess(String success) {
                Toast("GOT DATA!");
                Intent intent = new Intent(getApplicationContext(),MainMenuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getApplicationContext().startActivity(intent);
                finish();
            }

            @Override
            public void OnFail(String fail) {
                Toast("Didnt get data.");
            }
        });

    }

    void Toast(String text){
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();
    }
    void RunIntent(){
        Intent intent = new Intent(this,LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
