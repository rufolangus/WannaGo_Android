package com.wannago.wannago;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.wannago.wannago.adapters.EventInfoWindowAdapter;
import com.wannago.wannago.models.EventModel;
import com.wannago.wannago.models.EventType;
import com.wannago.wannago.models.BasicResponse;
import com.wannago.wannago.services.Events.GetEventsResponse;
import com.wannago.wannago.services.Services;

import java.text.DecimalFormat;

public class MainMenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,OnMapReadyCallback,
                    LocationListener,GoogleMap.OnMapLongClickListener, GoogleApiClient.ConnectionCallbacks,
                    GoogleApiClient.OnConnectionFailedListener,GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener {


    private GoogleMap gMap;
    private GoogleApiClient mGoogleApiClient;
    private MapView mapView;
    private Boolean mapReady = false;
    private Boolean connected = false;
    private View buttomSheet;
    private BottomSheetBehavior behavior;
    FloatingActionButton fab;
    FloatingActionButton storiesBtn;
    private EventInfoWindowAdapter eventAdapter;
    private Location currentLocation;


    EventModel[] eventModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        buttomSheet = findViewById(R.id.bottom_sheet1);
        behavior = BottomSheetBehavior.from(buttomSheet);
        behavior.setHideable(true);
        if(behavior.getState() != BottomSheetBehavior.STATE_HIDDEN)
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN);

         fab = (FloatingActionButton) findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CreateEventActivity.class);
                getApplicationContext().startActivity(intent);
            }
        });
         storiesBtn = (FloatingActionButton) findViewById(R.id.fab_stories);
        storiesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "View Stories: Not implemented.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        eventAdapter = new EventInfoWindowAdapter(getLayoutInflater());
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(newState == BottomSheetBehavior.STATE_COLLAPSED || newState == BottomSheetBehavior.STATE_HIDDEN)
                {
                    fab.setVisibility(View.VISIBLE);
                    storiesBtn.setVisibility(View.VISIBLE);
                }else if (newState == BottomSheetBehavior.STATE_EXPANDED)
                {
                    fab.setVisibility(View.INVISIBLE);
                    storiesBtn.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView userName = (TextView) headerView.findViewById(R.id.nav_username);
        ImageView profileImageView = (ImageView) headerView.findViewById(R.id.profileImageView);
        if(profileImageView != null)
        {
            String url = Services.Account.imageUrl == "" ? null : Services.Account.imageUrl;
            Picasso.with(getApplicationContext()).load(url)
                                                 .placeholder(R.drawable.ic_menu_camera)
                                                 .error(R.drawable.ic_menu_camera)
                                                 .into(profileImageView);
        }
        userName.setText(Services.Account.username);
        navigationView.setNavigationItemSelectedListener(this);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .enableAutoManage(this,this)
                    .build();
        }
        mapView = (MapView) findViewById(R.id.mapView2);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

    }

    void GetEvents(){
        Services.Events.GetEventsAuth(new GetEventsResponse() {
            @Override
            public void OnSuccess(int code, EventModel[] models) {
                eventModels = models;
                SetEventsOnMap();
            }

            @Override
            public void OnError(int code, String message) {
                Toast("Error: " + code + " Message: " + message);
            }
        });
    }

    void SetEventsOnMap(){

        if(!mapReady)
            return;
        gMap.clear();
        if(eventModels != null)
        {
            eventAdapter.SetModels(eventModels);
            for(int i = 0; i < eventModels.length; i++)
            {
                EventModel model = eventModels[i];

                Marker marker = gMap.addMarker(new MarkerOptions().visible(true)
                        .title(model.name)
                        .snippet(model.description)
                        .icon(GetMarkerColor(model.GetProgress()))
                        .position(new LatLng(model.venue.lat,
                                model.venue.lon)
                        ));
                marker.setTag(model.id);
                Integer progress = model.GetProgress();



            }
        }
    }

    public BitmapDescriptor GetMarkerColor(Integer progress)
    {
        if (progress <= 50)
            return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
        else if (progress < 80)
            return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
        else
            return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_account) {
            Intent intent = new Intent(getApplicationContext(), EditAccount.class);
            getApplicationContext().startActivity(intent);
            // Handle the camera action
        } else if (id == R.id.nav_event_list) {
            Snackbar.make(item.getActionView(), "View Stories: Not implemented.",
                            Snackbar.LENGTH_LONG).setAction("Action", null).show();
        } else if (id == R.id.nav_map) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_stories) {

        } else if (id == R.id.nav_sign_out) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            getApplicationContext().startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        GetEvents();
    }

    @Override
    protected void onPause() {

        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
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
     public void onRequestPermissionsResult(int requestCode, String[] permissions, int [] results){
        onMapReady(gMap);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        mapReady = true;
        gMap.setInfoWindowAdapter(eventAdapter);
        gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                return true;
            }
        });
        Boolean hasPermision = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;
        if (hasPermision) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},0);
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        gMap.setOnMarkerClickListener(this);
        gMap.setMyLocationEnabled(true);
        gMap.setOnInfoWindowClickListener(this);
        gMap.setOnMapClickListener(this);
        if(eventModels != null)
            SetEventsOnMap();
        else
            GetEvents();

        if (!connected)
            return;
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location != null)
        {   currentLocation = location;
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

        } else {
            LocationRequest request = new LocationRequest();
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, request, this);
        }


    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        connected = true;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission_group.LOCATION,Manifest.permission_group.STORAGE},0);


            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();

        //You can still do this if you like, you might get lucky:
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null)
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            currentLocation = location;
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

        } else {
            LocationRequest request = new LocationRequest();
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, request, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        connected = false;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        connected = false;
    }

    @Override
    public void onLocationChanged(Location location)
    {
        currentLocation = location;
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

    }


    @Override
    public void onMapClick(LatLng latLng) {
        if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            new Runnable() {
                @Override
                public void run() {
                    behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            }.run();
            storiesBtn.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);

        }
    }

    EventModel GetModelById(Integer id){
        if(eventModels != null){
         for(int i = 0; i < eventModels.length; i++){
             EventModel model = eventModels[i];
             if(model.id == id)
                 return model;
         }
        }
        return null;
    }

    public void Toast(String message)
    {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onInfoWindowClick(Marker marker)
    {

        if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED
                || behavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {

            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            ImageView image = (ImageView)buttomSheet.findViewById(R.id.event_item_image);
            final Integer id = (Integer)marker.getTag();
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(),EventView.class);
                    intent.putExtra("id",id);
                    if(currentLocation != null)
                    {
                            intent.putExtra("lat",currentLocation.getLatitude());
                            intent.putExtra("lon",currentLocation.getLongitude());
                    }
                    getApplicationContext().startActivity(intent);
                }
            });
            storiesBtn.setVisibility(View.INVISIBLE);
            fab.setVisibility(View.INVISIBLE);

        }
    }
     Float getMiles(Float meters) {
        return meters*0.000621371192f;
    }

    void SetSheetViews(final EventModel model){
        if(model != null) {
            TextView nameView = (TextView) buttomSheet.findViewById(R.id.event_name);
            TextView locationView = (TextView) buttomSheet.findViewById(R.id.event_location);
            TextView descriptionView = (TextView) buttomSheet.findViewById(R.id.event_description);
            TextView priceView = (TextView) buttomSheet.findViewById(R.id.event_price);
            ImageButton going = (ImageButton) buttomSheet.findViewById(R.id.button_going);
            ImageButton notGoing = (ImageButton) buttomSheet.findViewById(R.id.button_notinterested);
            ImageView image = (ImageView)buttomSheet.findViewById(R.id.event_item_image);
            ImageView hostImage = (ImageView)buttomSheet.findViewById(R.id.event_host_image);
            TextView eventTypeView = (TextView)buttomSheet.findViewById(R.id.event_type);
            TextView distance = (TextView)buttomSheet.findViewById(R.id.event_distance);
            TextView divider = (TextView)buttomSheet.findViewById(R.id.devider);
            TextView date = (TextView)buttomSheet.findViewById(R.id.event_date);
            final Button checkin = (Button)buttomSheet.findViewById(R.id.checkin_button);
            View checkinLayout = (View)buttomSheet.findViewById(R.id.checkin_layout);
            View rsvpLayout = (View)buttomSheet.findViewById(R.id.rsvp_layout);
            TextView checkinCount = (TextView)buttomSheet.findViewById(R.id.checking_count);
            checkinCount.setText(model.checkIns + " Check Ins");
            date.setVisibility(View.INVISIBLE);
            divider.setVisibility(View.INVISIBLE);

            if(distance != null && currentLocation != null){
                Location destinationloc = new Location("dest");
                destinationloc.setLongitude(model.venue.lon);
                destinationloc.setLatitude(model.venue.lat);

                Float distanceInMeters = currentLocation.distanceTo(destinationloc);
                Float distanceInMiles = getMiles(distanceInMeters);
                DecimalFormat df = new DecimalFormat();
                df.setMaximumFractionDigits(2);
                //Rock & Roll
                if(distanceInMiles <= 0.126136f) {
                    rsvpLayout.setVisibility(View.GONE);
                    checkinLayout.setVisibility(View.VISIBLE);
                }else {
                    rsvpLayout.setVisibility(View.VISIBLE);
                    checkinLayout.setVisibility(View.GONE);
                }

                if(model.checkIn) {
                    rsvpLayout.setVisibility(View.GONE);
                    checkinLayout.setVisibility(View.GONE);
                }
                distance.setText(df.format(distanceInMiles) + "mi");
            }
            if(eventTypeView != null) {
                String eventTypeString = EventType.values()[model.type].toString();
                eventTypeView.setText(eventTypeString);
            }
            if(hostImage != null) {
                Picasso.with(getApplicationContext())
                        .load(model.hostImage == "" ? null : model.hostImage)
                        .placeholder(R.drawable.ic_menu_camera)
                        .error(R.drawable.ic_menu_camera)
                        .into(hostImage);
            }
            Picasso.with(getApplicationContext())
                    .load(model.imageURL == "" ? null : model.imageURL)
                    .placeholder(R.drawable.ic_menu_camera)
                    .error(R.drawable.ic_menu_camera)
                    .into(image);
            ProgressBar bar = (ProgressBar)buttomSheet.findViewById(R.id.event_prediction);
            if(bar != null){

                bar.setProgress(model.GetProgress());
            }
            if(checkin != null){
                checkin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Services.Events.CheckIn(model.id, new BasicResponse()
                        {
                            @Override
                            public void OnSuccess(String success) {
                                Toast(success);
                                checkin.setVisibility(View.GONE);
                            }

                            @Override
                            public void OnError(String error) {
                                Toast(error);
                            }
                        });
                    }
                });
            }
            if(going != null) {
                final EventModel finalModel = model;
                going.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Services.Events.RSVP(finalModel.id, true, new BasicResponse() {
                            @Override
                            public void OnSuccess(String success) {
                                Toast(success);
                            }

                            @Override
                            public void OnError(String error) {
                                Toast(error);
                            }
                        });
                    }
                });
            }

            if(notGoing != null) {
                final EventModel finalModel1 = model;
                notGoing.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        Services.Events.RSVP(finalModel1.id, false, new BasicResponse() {
                            @Override
                            public void OnSuccess(String success) {
                                Toast(success);
                            }

                            @Override
                            public void OnError(String error) {
                                Toast(error);
                            }
                        });
                    }
                });
            }
            if (nameView != null) {
                nameView.setText(model.name);
            }
            if(locationView != null)
                locationView.setText(model.venue.name);
            if(descriptionView != null)
                descriptionView.setText(model.description);
            if(priceView != null)
            {
                String price = "";
                if(model.price <= 0)
                    price = "Free";
                else
                    price = "$" + model.price.toString();
                priceView.setText(price);
            }

        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        Integer integer = (Integer)marker.getTag();
        EventModel model = GetModelById(integer);
        SetSheetViews(model);
        return true;
    }
}

