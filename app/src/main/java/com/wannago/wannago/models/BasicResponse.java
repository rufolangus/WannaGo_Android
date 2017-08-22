package com.wannago.wannago.models;

/**
 * Created by Rafael Valle on 4/18/2017.
 */

public interface BasicResponse
{
    void OnSuccess(String success);
    void OnError(String error);
}
