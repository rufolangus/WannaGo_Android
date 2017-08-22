package com.wannago.wannago.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.wannago.wannago.R;
import com.wannago.wannago.models.EventModel;
import com.wannago.wannago.models.EventType;

/**
 * Created by Rafael Valle on 4/17/2017.
 */

public class EventInfoWindowAdapter implements GoogleMap.InfoWindowAdapter
{

    private final View view;
    private EventModel[] models;

    public EventInfoWindowAdapter(LayoutInflater inflator)
    {
        view =  inflator.inflate(R.layout.eventmarker, null);
    }

    public void SetModels(EventModel[] models){
        this.models = models;
    }

    @Override
    public View getInfoWindow(Marker marker)
    {

        TextView title = (TextView) view.findViewById(R.id.marker_title);
        ProgressBar bar = (ProgressBar)view.findViewById(R.id.event_progress_slider);
        TextView textView = (TextView)view.findViewById(R.id.marker_type);

        if(title != null)
            title.setText(marker.getTitle());
        Integer id = (Integer)marker.getTag();
        EventModel model = GetModel(id);
        if(model != null){
            if(textView != null)
            {
                String type = EventType.values()[model.type].toString();
                textView.setText(type);

            }
            TextView priceView = (TextView)view.findViewById(R.id.marker_price);
            if (priceView != null)
            {
                String price = model.price <= 0 ? "Free" : "$" + model.price;
                priceView.setText(price);
            }
            if(bar != null){
                bar.setProgress(model.GetProgress());
            }
        }
        return view;
    }

    EventModel GetModel(int id)
    {
        for(int i = 0; i < models.length; i++)
        {
            EventModel model = models[i];
            if(model.id == id)
                return model;
        }
        return null;

    }

    @Override
    public View getInfoContents(Marker marker)
    {
        return view;
    }
}
