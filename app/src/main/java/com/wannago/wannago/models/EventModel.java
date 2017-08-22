package com.wannago.wannago.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Rafael Valle on 3/27/2017.
 */

public class EventModel
{
    public Integer id;
    public String name;
    public VenueModel venue;
    public String description;
    public String imageURL;
    public Integer type;
    public String startTime;
    public String endTime;
    public Float price;
    public Boolean isPrivate;
    public String hostID;
    public Integer going;
    public Integer notGoing;
    public Integer total;
    public Boolean rsvped;
    public Boolean goinOrNOt;
    public String hostImage;
    public boolean checkIn;
    public Integer checkIns;


    public Integer GetProgress(){
        Integer value = 0;
        if (total > 0)
            value = (going / total) * 100;

        return value;
    }
}
