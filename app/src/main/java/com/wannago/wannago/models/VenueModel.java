package com.wannago.wannago.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Rafael Valle on 3/26/2017.
 */

public class VenueModel implements Serializable
{
    public Integer id;
    public String name;
    public Double  lat;
    public Double  lon;

}
