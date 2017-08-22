package com.wannago.wannago;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.wannago.wannago.models.EventModel;
import com.wannago.wannago.models.EventType;
import com.wannago.wannago.models.BasicResponse;
import com.wannago.wannago.models.VenueModel;
import com.wannago.wannago.services.Events.GetEventResponse;
import com.wannago.wannago.services.Services;

import java.text.DecimalFormat;
import java.util.Locale;

public class EventView extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    TextView eventName;
    TextView eventDate;
    TextView eventType;
    TextView distance;
    ImageView banner;
    ProgressBar progressBar;
    TextView description;
    TextView price;
    TextView going;
    TextView notInterested;
    ImageButton goingButton;
    ImageButton notGoingButton;
    Button storiesButton;
    private GoogleMap gMap;
    private GoogleApiClient mGoogleApiClient;
    private MapView mapView;
    Boolean connected = false;
    EventModel eventModel;
    Location currentLocation;
    View checkInLayout;
    View rsvpLayout;
    View rsvpCheckinLayout;
    Button checkin_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_view);
        eventName = (TextView)findViewById(R.id.event_name_view);
        eventType = (TextView)findViewById(R.id.event_type_view);
        eventDate = (TextView)findViewById(R.id.event_date_view);
        distance  = (TextView)findViewById(R.id.event_distance_view);
        banner = (ImageView)findViewById(R.id.event_banner_view);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar_view);
        description = (TextView)findViewById(R.id.event_decription_view);
        price = (TextView)findViewById(R.id.event_price_view);
        going = (TextView)findViewById(R.id.event_attendance_going);
        notInterested = (TextView)findViewById(R.id.event_attendance_notgoing);
        goingButton = (ImageButton)findViewById(R.id.event_button_go);
        notGoingButton = (ImageButton)findViewById(R.id.event_button_no);
        storiesButton = (Button)findViewById(R.id.event_button_stories);
        checkInLayout = (View)findViewById(R.id.layout_checkin);
        rsvpCheckinLayout = (View)findViewById(R.id.layout_rsvp_checkin);
        rsvpLayout = (View)findViewById(R.id.layout_rsvp);
        checkin_button = (Button)findViewById(R.id.event_view_checkin);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mapView = (MapView) findViewById(R.id.event_map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        storiesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v,"Comming soon...",Snackbar.LENGTH_SHORT).show();

            }
        });


        final Integer id = getIntent().getIntExtra("id",-1);
        Double lon = getIntent().getDoubleExtra("lon",0d);
        Double lat = getIntent().getDoubleExtra("lat",0d);
        if(lat != 0d && lon != 0d){
            currentLocation = new Location("currentLoc");
            currentLocation.setLatitude(lat);
            currentLocation.setLongitude(lon);
        }
        GetAndSet(id);
        notGoingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Services.Events.RSVP(id, false, new BasicResponse() {
                    @Override
                    public void OnSuccess(String success) {
                        GetAndSet(id);
                        Toast.makeText(getApplicationContext(),"Success!",Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void OnError(String error) {
                        Toast.makeText(getApplicationContext(),error,Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
        goingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Services.Events.RSVP(id, true, new BasicResponse() {
                    @Override
                    public void OnSuccess(String success) {
                        GetAndSet(id);
                        Toast.makeText(getApplicationContext(),"Success!",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void OnError(String error) {
                        Toast.makeText(getApplicationContext(),"error!",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }

    void GetAndSet(Integer id){
        Services.Events.GetEventAuthorized(id, new GetEventResponse() {
            @Override
            public void OnSuccess(int code, EventModel model) {
                SetData(model);
                eventModel = model;
            }

            @Override
            public void OnError(int code, String message) {
                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
            }
        });
    }

    void SetData(EventModel model)
    {
        eventName.setText(model.name);
        Picasso.with(getApplicationContext()).load(model.imageURL == "" ? null : model.imageURL)
                                             .error(R.drawable.ic_menu_camera)
                                             .placeholder(R.drawable.ic_menu_camera)
                                             .into(banner);
        description.setText(model.description);
        progressBar.setProgress(model.GetProgress());





            String priceTxt = model.price <= 0 ? "Free" : model.price.toString();
        price.setText(priceTxt);
        if(eventType != null) {
            String eventTypeString = EventType.values()[model.type].toString();
            eventType.setText(eventTypeString );
        }
        going.setText(model.going.toString());
        notInterested.setText(model.notGoing.toString());
        if(model != null) {
            LatLng loc = new LatLng(model.venue.lat, model.venue.lon);
            gMap.clear();
            gMap.addMarker(new MarkerOptions().position(loc));
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));
            if(currentLocation != null){
                Location destination = new Location("dest");
                destination.setLatitude(model.venue.lat);
                destination.setLongitude(model.venue.lon);
                Float distanceInMeters = currentLocation.distanceTo(destination);
                Float distanceInMiles = toMiles(distanceInMeters);
                if(distance != null)
                {
                    DecimalFormat df = new DecimalFormat();
                    df.setMaximumFractionDigits(2);
                    distance.setText(df.format(distanceInMiles) + "mi");

                    if(distanceInMiles <= 0.126136f) {
                        rsvpLayout.setVisibility(View.GONE);
                        checkInLayout.setVisibility(View.VISIBLE);
                    }else {
                        rsvpLayout.setVisibility(View.VISIBLE);
                        checkInLayout.setVisibility(View.GONE);
                    }

                    if(model.checkIn){
                        rsvpCheckinLayout.setVisibility(View.GONE);
                    }else{
                        rsvpCheckinLayout.setVisibility(View.VISIBLE);
                    }
                }
            }

        }


        }

        Float toMiles(Float meter){
            return meter * 0.000621371192f;
        }

    protected void onStart() {
        super.onStart();
        mapView.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {

        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        mapView.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.setOnMapClickListener(this);

        Boolean hasPermision = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;
        if (hasPermision) {

            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if(!connected)
            return;

        if(eventModel != null) {
            LatLng loc = new LatLng(eventModel.venue.lat,eventModel.venue.lon);
         gMap.addMarker(new MarkerOptions().position(loc));
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));

        }


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        connected = true;
        if (eventModel != null) {
            LatLng loc = new LatLng(eventModel.venue.lat, eventModel.venue.lon);
            gMap.addMarker(new MarkerOptions().position(loc));
        }
    }
        @Override
        public void onConnectionSuspended ( int i)
        {
            connected = false;
        }

        @Override
        public void onConnectionFailed (@NonNull ConnectionResult connectionResult){
            connected = false;
        }


        @Override
        public void onMapClick (LatLng latLng){

                if(eventModel != null) {
                    VenueModel venueModel = eventModel.venue;
                    String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", venueModel.lat, venueModel.lon, eventModel.name);

                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse(uri));
                    intent.setPackage("com.google.android.apps.maps");

                    startActivity(intent);
                }

        }
    }
