package com.wannago.wannago;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wannago.wannago.models.CreateVenueModel;
import com.wannago.wannago.models.VenueModel;
import com.wannago.wannago.services.Services;
import com.wannago.wannago.services.Venues.GetVenueResponse;

public class VenuePickerActivity extends AppCompatActivity implements LocationListener, GoogleMap.OnMapClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {
    private GoogleMap gMap;
    private GoogleApiClient mGoogleApiClient;
    private MapView mapView;
    private Boolean connected = false;
    private EditText venueName;
    private Button createButton;
    private LatLng selectLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_picker);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        venueName = (EditText)findViewById(R.id.venue_picker_name);
        mapView = (MapView) findViewById(R.id.venue_picker_map);
        createButton = (Button)findViewById(R.id.venue_select);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //CREATE IT! GO BACK WITH RESULT!
                CreateVenueModel model = Verify();
                if(model != null){
                    Services.Venues.CreateVenue(model, new GetVenueResponse() {
                        @Override
                        public void OnSuccess(int code, VenueModel venue) {
                            Toast("Created Venue!");
                            Intent data = new Intent();
                            data.putExtra("VenueModel",venue);
                            setResult(RESULT_OK,data);
                            finish();
                        }

                        @Override
                        public void OnError(int code, String message) {
                            Toast("Failed to create venue. Code: " + code + " Message: " + message);
                        }
                    });
                }
            }
        });
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    protected void onStart() {
        super.onStart();
        mapView.onStart();
        mGoogleApiClient.connect();
    }

    CreateVenueModel Verify(){
        String name = venueName.getText().toString();
        if(name == "") {
            Toast("Please enter a name");
            return null;
        }

        if(selectLatLng == null) {
            Toast("Select a location.");
            return null;
        }

        CreateVenueModel model = new CreateVenueModel();
        model.lat = selectLatLng.latitude;
        model.lon = selectLatLng.longitude;
        model.name = name;
        return model;

    }

    void Toast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
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
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(location != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            Toast.makeText(getApplicationContext(), "Got Location: " + location.toString(), Toast.LENGTH_LONG).show();
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }
        else{
            LocationRequest request = new LocationRequest();
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,request,this);
        }


    }

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        connected = true;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationManager locationManager = (LocationManager)  this.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();

        //You can still do this if you like, you might get lucky:
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(location == null)
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            Toast.makeText(getApplicationContext(), "Got Location: " + location.toString(), Toast.LENGTH_LONG).show();
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }
        else{
            LocationRequest request = new LocationRequest();
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,request,this);
        }
    }

    @Override
    public void onConnectionSuspended(int i)
    {
        connected = false;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        connected = false;
    }

    @Override
    public void onLocationChanged(Location location) {

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        Toast.makeText(getApplicationContext(), "Got Location: " + location.toString(), Toast.LENGTH_LONG).show();
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

    }

    @Override
    public void onMapClick(LatLng latLng)
    {
        selectLatLng = null;
        gMap.clear();
        gMap.addMarker(new MarkerOptions()
                            .draggable(false)
                            .position(latLng)
                            .visible(true));
        selectLatLng = latLng;
    }
}
