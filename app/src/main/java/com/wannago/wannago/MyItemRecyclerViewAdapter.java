package com.wannago.wannago;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wannago.wannago.ItemFragment.OnListFragmentInteractionListener;
import com.wannago.wannago.dummy.DummyContent.DummyItem;
import com.wannago.wannago.models.EventModel;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<EventModel> mValues;
    private final OnListFragmentInteractionListener mListener;
    private Context context;

    public MyItemRecyclerViewAdapter(List<EventModel> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_list_item, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        holder.eventName.setText(holder.mItem.name);
        holder.eventDescription.setText(holder.mItem.description);
        holder.eventLocation.setText(holder.mItem.venue.name);
        holder.bar.setProgress(holder.mItem.GetProgress());
        Picasso.with(context).load(holder.mItem.imageURL == "" ? null : holder.mItem.imageURL)
                             .placeholder(R.drawable.ic_menu_camera)
                             .error(R.drawable.ic_menu_camera);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView eventName;
        public final TextView eventDescription;
        public final TextView eventLocation;
        public final ImageView imageView;
        public final ProgressBar bar;
        public EventModel mItem;

        public ViewHolder(View view) {
            super(view);

            eventName = (TextView) view.findViewById(R.id.event_name);
            eventDescription = (TextView) view.findViewById(R.id.event_description);
             eventLocation = (TextView) view.findViewById(R.id.event_location);
             imageView = (ImageView) view.findViewById(R.id.event_item_image);

             bar = (ProgressBar) view.findViewById(R.id.event_prediction);
            // Populate the data into the template view using the data object
            mView = view;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + eventName.getText() + "'";
        }
    }
}
