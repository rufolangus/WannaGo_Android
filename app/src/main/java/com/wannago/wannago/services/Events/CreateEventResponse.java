package com.wannago.wannago.services.Events;

/**
 * Created by Rafael Valle on 3/27/2017.
 */

public interface CreateEventResponse
{
    void OnSuccess(int code, Integer id);
    void OnError(int code, String message);
}
