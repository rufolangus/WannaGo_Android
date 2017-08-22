package com.wannago.wannago.services.Account;

/**
 * Created by Rafael Valle on 3/22/2017.
 */

public interface RegisterCallback
{
    void OnSuccess(int code, String response);
    void OnError(int code, String error);

}
