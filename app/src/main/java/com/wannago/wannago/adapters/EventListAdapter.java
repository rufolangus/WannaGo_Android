package com.wannago.wannago.adapters;

import android.content.Context;
import android.media.Image;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wannago.wannago.R;
import com.wannago.wannago.models.EventModel;

import java.util.ArrayList;

/**
 * Created by Rafael Valle on 3/27/2017.
 */

public class EventListAdapter extends ArrayAdapter<EventModel>
{

    public EventListAdapter(Context context, ArrayList<EventModel> models) {
        super(context, 0, models);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        EventModel model = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.event_list_item, parent,false);
        }
        // Lookup view for data population
        TextView eventName = (TextView) convertView.findViewById(R.id.event_name);
        TextView eventDescription = (TextView) convertView.findViewById(R.id.event_description);
        TextView eventLocation = (TextView) convertView.findViewById(R.id.event_location);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.event_item_image);
        if(imageView != null)
            Picasso.with(getContext())
                    .load(model.imageURL == "" ? null : model.imageURL)
                    .placeholder(R.drawable.ic_menu_camera)
                    .error(R.drawable.ic_menu_camera);
        ProgressBar bar = (ProgressBar) convertView.findViewById(R.id.event_prediction);
        // Populate the data into the template view using the data object
        eventName.setText(model.name);
        eventDescription.setText(model.description);
        eventLocation.setText(model.venue.name);
        bar.setProgress(model.GetProgress());
        // Return the completed view to render on screen
        return convertView;
    }

}
