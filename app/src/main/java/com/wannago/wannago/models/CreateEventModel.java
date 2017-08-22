package com.wannago.wannago.models;

/**
 * Created by Rafael Valle on 3/26/2017.
 */

public class CreateEventModel
{
    public String name;
    public Integer venueId;
    public String description;
    public String imageURL;
    public Integer type;
    public String startTime;
    public String endTime;
    public Float price;
    public Boolean isPrivate;

    public CreateEventModel(String name, Integer venueId, String description,
                            String imageURL, Integer type, String startTime,
                            String endTime, Float price, Boolean isPrivate)
    {
        this.name = name;
        this.venueId = venueId;
        this.description = description;
        this.imageURL = imageURL;
        this.type = type;
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
        this.isPrivate = isPrivate;
    }


}
