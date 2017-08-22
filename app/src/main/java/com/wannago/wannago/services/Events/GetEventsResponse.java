package com.wannago.wannago.services.Events;

import com.wannago.wannago.models.EventModel;

/**
 * Created by Rafael Valle on 3/27/2017.
 */

public interface GetEventsResponse
{
    void OnSuccess(int code, EventModel[] models);
    void OnError(int code, String message);
}
