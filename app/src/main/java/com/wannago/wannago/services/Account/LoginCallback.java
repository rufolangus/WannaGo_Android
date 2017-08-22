package com.wannago.wannago.services.Account;

/**
 * Created by Rafael Valle on 3/22/2017.
 */

public interface LoginCallback
{
    void OnSuccess(int code, String rawResponse);
    void OnError(int code, String error);

}
