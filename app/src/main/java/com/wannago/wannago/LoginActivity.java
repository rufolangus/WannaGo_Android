package com.wannago.wannago;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.wannago.wannago.models.LoginModel;
import com.wannago.wannago.models.RegisterModel;
import com.wannago.wannago.services.Account.GetDataCallback;
import com.wannago.wannago.services.Account.LoginCallback;
import com.wannago.wannago.services.Services;

public class LoginActivity extends AppCompatActivity {

    LoginButton loginButton;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        TextView registerButton = (TextView)findViewById(R.id.textView4);
        Button button = (Button)findViewById(R.id.signin_button);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        CallbackManager callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                EditText username = (EditText)findViewById(R.id.login_username);
                EditText pass = (EditText)findViewById(R.id.login_password);
                String userName = username.getText().toString();
                String password = pass.getText().toString();
                if(userName == null || userName.isEmpty())
                {
                    Toast("Please enter a valid e-mail address.");
                    return;
                }
                if(password == null || password.isEmpty())
                {
                    Toast("Please enter a password");
                    return;
                }
                RequestLogin(userName,password);

            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent registerIntent = new Intent(getApplicationContext(),RegisterActivity.class);
                    registerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(registerIntent);
            }
        });
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


}
