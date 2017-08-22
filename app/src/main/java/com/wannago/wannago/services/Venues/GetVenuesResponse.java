package com.wannago.wannago.services.Venues;

import com.wannago.wannago.models.VenueModel;

/**
 * Created by Rafael Valle on 3/27/2017.
 */

public interface GetVenuesResponse
{
    public void OnSuccess(int code, VenueModel[] venue);
    public void OnError(int code, String message);
}
