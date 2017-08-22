package com.wannago.wannago;

import android.app.Service;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.wannago.wannago.adapters.EventListAdapter;
import com.wannago.wannago.models.EventModel;
import com.wannago.wannago.services.Events.Events;
import com.wannago.wannago.services.Events.GetEventsResponse;
import com.wannago.wannago.services.Services;

import java.util.ArrayList;

public class EventListActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

    }


}
