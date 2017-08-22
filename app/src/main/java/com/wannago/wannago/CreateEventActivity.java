package com.wannago.wannago;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.text.Html;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.wannago.wannago.models.CreateEventModel;
import com.wannago.wannago.models.EventType;
import com.wannago.wannago.models.ImageUploadResponse;
import com.wannago.wannago.models.VenueModel;
import com.wannago.wannago.services.Events.CreateEventResponse;
import com.wannago.wannago.services.Services;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

public class CreateEventActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback,GoogleMap.OnMapClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private GoogleMap gMap;
    private GoogleApiClient mGoogleApiClient;
    private MapView mapView;
    private VenueModel selectVenue;
    private Boolean connected = false;
    private Button createEventButton;
    private EditText eventNameEdit;
    private EditText eventDescriptionEdit;
    private TextView eventStartTimeEdit;
    private TextView eventEndTimeEdit;
    private Switch isPrivateSwitch;
    private TextView locationNameView;
    private EditText eventPriceEdit;
    private ImageView imageView;
    private Spinner typeSpinner;
    File file;
    private TextView eventEndDate;
    private TextView eventStartDate;
    private Date startDate;
    private Date endDate;
    private Boolean saving = false;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        SetActionbar();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        mapView = (MapView) findViewById(R.id.create_event_map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        createEventButton = (Button) findViewById(R.id.create_event_button);
        eventNameEdit = (EditText) findViewById(R.id.create_event_name);
        eventDescriptionEdit = (EditText) findViewById(R.id.create_event_description);
        eventStartTimeEdit = (TextView) findViewById(R.id.create_event_start_time);
        eventEndTimeEdit = (TextView) findViewById(R.id.create_event_end_time);
        eventStartDate = (TextView) findViewById(R.id.create_event_start_date);
        eventEndDate = (TextView) findViewById(R.id.create_event_end_date);
        eventPriceEdit = (EditText) findViewById(R.id.create_event_price);
        isPrivateSwitch = (Switch) findViewById(R.id.create_event_switch);
        locationNameView = (TextView) findViewById(R.id.textView_create_place);
        typeSpinner = (Spinner) findViewById(R.id.eventTypeSpinner);
        typeSpinner.setAdapter(new ArrayAdapter<EventType>(this,
                android.R.layout.simple_spinner_item,
                EventType.values()));
        imageView = (ImageView) findViewById(R.id.create_event_image_view);
        eventStartTimeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerFragment dialog = new TimePickerFragment();
                dialog.setOnTimeSetListener(new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String am_pm = "";
                        if(startDate == null)
                            startDate = new Date();
                        startDate.setHours(hourOfDay);
                        startDate.setMinutes(minute);
                        Calendar datetime = Calendar.getInstance();
                        datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        datetime.set(Calendar.MINUTE, minute);
                        if (datetime.get(Calendar.AM_PM) == Calendar.AM)
                            am_pm = "AM";
                        else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
                            am_pm = "PM";

                        String strHrsToShow = (datetime.get(Calendar.HOUR) == 0) ?"12":datetime.get(Calendar.HOUR)+"";
                        String min = minute < 10 ? "0" + minute : "" + minute;
                        eventStartTimeEdit.setText( strHrsToShow+":"+min+" "+am_pm );
                    }
                });
                dialog.show(getSupportFragmentManager(),"timePicker");
            }
        });
        eventEndTimeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerFragment dialog = new TimePickerFragment();
                dialog.setOnTimeSetListener(new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String am_pm = "";
                        if(endDate == null)
                            endDate = new Date();
                        endDate.setHours(hourOfDay);
                        endDate.setMinutes(minute);
                        Calendar datetime = Calendar.getInstance();
                        datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        datetime.set(Calendar.MINUTE, minute);
                        if (datetime.get(Calendar.AM_PM) == Calendar.AM)
                            am_pm = "AM";
                        else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
                            am_pm = "PM";

                        String strHrsToShow = (datetime.get(Calendar.HOUR) == 0) ?"12":datetime.get(Calendar.HOUR)+"";
                        String min = minute < 10 ? "0" + minute : "" + minute;
                        eventEndTimeEdit.setText( strHrsToShow+":"+min+" "+am_pm );                    }
                });
                dialog.show(getSupportFragmentManager(),"timePicker");
            }
        });
        eventStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment dialog = new DatePickerFragment();

                dialog.setOnTimeSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        eventStartDate.setText(dayOfMonth + "/" + month + "/" + (year));
                        if(startDate == null)
                            startDate = new Date();
                        startDate.setYear(year);
                        startDate.setMonth(month);
                        startDate.setDate(dayOfMonth);

                    }
                });
                dialog.show(getSupportFragmentManager(),"timePicker");

            }
        });
        eventEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment dialog = new DatePickerFragment();
                dialog.setOnTimeSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        eventEndDate.setText(dayOfMonth + "/" + month + "/" + (year));
                    }
                });
                dialog.show(getSupportFragmentManager(),"timePicker");
            }
        });


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 5);
            }
        });
        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateEventModel model = Verify();
                if (model != null && !saving) {
                    saving = true;
                    Services.Events.CreateEvent(model, new CreateEventResponse() {
                        @Override
                        public void OnSuccess(int code, Integer id) {
                            Toast("Created Event!");
                            if (file == null) {
                                saving = false;
                                finish();
                            }
                            else
                                UploadImage(id);
                        }

                        @Override
                        public void OnError(int code, String message) {
                            Toast("Error Code: " + code + " Error Message:" + message);
                        }
                    });
                }
            }
        });
    }

    void UploadImage(Integer eventID) {

        Services.Events.UploadImage(eventID, file, new ImageUploadResponse() {

            @Override
            public void OnSuccess(String message) {
                Toast("Uploaded Image!");
                saving = false;
                finish();
            }

            @Override
            public void OnError(String message) {
                Toast("message");
                saving = false;
                finish();
            }
        });
    }

    void SetActionbar() {
        ActionBar bar = getActionBar();
        if (bar == null) {
            SetSupportActionBar();
            return;
        }

        bar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + "Create Event" + "</font>"));
        int color = Color.parseColor("#4CAF50");
        ColorDrawable colorDrawable = new ColorDrawable(color);
        bar.setBackgroundDrawable(colorDrawable);
    }

    void SetSupportActionBar() {
        android.support.v7.app.ActionBar bar = getSupportActionBar();
        bar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + "Create Event" + "</font>"));
        int color = Color.parseColor("#4CAF50");
        ColorDrawable colorDrawable = new ColorDrawable(color);
        bar.setBackgroundDrawable(colorDrawable);
    }

    CreateEventModel Verify() {
        String name = eventNameEdit.getText().toString();
        String description = eventDescriptionEdit.getText().toString();
        String startTime = eventStartTimeEdit.getText().toString();
        String endTime = eventEndTimeEdit.getText().toString();
        Boolean isPrivate = isPrivateSwitch.isChecked();
        String priceStrng = eventPriceEdit.getText().toString();

        if (name == "") {
            Toast("Please enter a valid name");
            return null;
        }
        if (description == "") {
            Toast("Please enter a valid description");
            return null;
        }

        if (startTime == "") {
            Toast("Please enter a valid startTime");
            return null;
        }

        if (endTime == "") {
            Toast("Please enter a valid endTime");
        }

        if (priceStrng == "") {
            Toast("Please enter a valid price");
        }

        if (selectVenue == null) {
            Toast("Please select a venue.");
            return null;
        }
        Float price;
        try {
            price = Float.parseFloat(priceStrng);
        } catch (NumberFormatException e) {
            Toast("Please enter a valid price");
            return null;
        }

        Integer type = typeSpinner.getSelectedItemPosition();
        return new CreateEventModel(name, selectVenue.id, description, "", type,
                "2017-03-27T09:51:12.119Z",
                "2017-03-27T09:51:12.119Z",
                price, isPrivate);

    }

    void Toast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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
        if (!connected)
            return;
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            Toast.makeText(getApplicationContext(), "Got Location: " + location.toString(), Toast.LENGTH_LONG).show();
            if (selectVenue == null)
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            else {
                LatLng venueLoc = new LatLng(selectVenue.lat, selectVenue.lon);
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(venueLoc, 15));
            }
        } else {
            LocationRequest request = new LocationRequest();
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, request, this);
        }


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
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
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();

        //You can still do this if you like, you might get lucky:
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null)
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            Toast.makeText(getApplicationContext(), "Got Location: " + location.toString(), Toast.LENGTH_LONG).show();
            if (selectVenue == null)
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            else {
                LatLng venueLoc = new LatLng(selectVenue.lat, selectVenue.lon);
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(venueLoc, 15));
            }
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
    public void onLocationChanged(Location location) {

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        Toast.makeText(getApplicationContext(), "Got Location: " + location.toString(), Toast.LENGTH_LONG).show();
        if (selectVenue == null)
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        else {
            LatLng venueLoc = new LatLng(selectVenue.lat, selectVenue.lon);
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(venueLoc, 15));
        }

    }

    @Override
    public void onMapClick(LatLng latLng) {

        Intent intent = new Intent(getApplicationContext(), VenuePickerActivity.class);
        startActivityForResult(intent, 1);
    }

    public void SetVenue(VenueModel venue) {
        LatLng venueLoc = new LatLng(venue.lat, venue.lon);
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(venueLoc, 15));
        gMap.addMarker(new MarkerOptions().position(venueLoc));
        selectVenue = venue;
        locationNameView.setText(venue.name);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 5) {
            if (resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                file = new File(picturePath);
                imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

            }
            return;
        }

        if (resultCode == RESULT_OK) {
            VenueModel venue = (VenueModel) data.getSerializableExtra("VenueModel");
            if (venue != null) {
                LatLng venueLoc = new LatLng(venue.lat, venue.lon);
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(venueLoc, 15));
                gMap.addMarker(new MarkerOptions().position(venueLoc));
                selectVenue = venue;
                locationNameView.setText(venue.name);

            }
        }
    }

    public static class TimePickerFragment extends DialogFragment {

        private TimePickerDialog.OnTimeSetListener listener;
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), R.style.TimePicker, listener, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void setOnTimeSetListener(TimePickerDialog.OnTimeSetListener listener){
            this.listener = listener;
        }


    }

    public static class DatePickerFragment extends DialogFragment {

        private DatePickerDialog.OnDateSetListener listener;
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int day = c.get(Calendar.DAY_OF_MONTH);
            int month = c.get(Calendar.MONTH);
            int year = c.get(Calendar.YEAR);


            // Create a new instance of TimePickerDialog and return it
            return new DatePickerDialog(getActivity(), R.style.TimePicker, listener,
                    year, month, day);
        }

        public void setOnTimeSetListener(DatePickerDialog.OnDateSetListener listener){
            this.listener = listener;
        }


    }
}
