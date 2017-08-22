package com.wannago.wannago.models;

/**
 * Created by Rafael Valle on 3/22/2017.
 */

public class LoginModel
{
    public String username;
    public String password;
    public final String grant_type = "password";
    public final String scope = "openid name email profile offline_access role subject";

    public LoginModel(String username, String password)
    {
        this.username = username;
        this.password = password;
    }
}
