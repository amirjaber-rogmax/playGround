package com.rogmax.amirjaber.playground;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.rogmax.amirjaber.playground.adapters.CustomInfoWindowAdapter;
import com.rogmax.amirjaber.playground.adapters.PlaceAutoCompleteAdapter;
import com.rogmax.amirjaber.playground.facades.MainFacade;
import com.rogmax.amirjaber.playground.models.PlaceDto;
import com.rogmax.amirjaber.playground.models.Value;
import com.rogmax.amirjaber.playground.retrofit.APIUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: Map is Ready");
        mMap = googleMap;

        if (mLocationPermissionGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            init();
        }
    }

    private static final String TAG = "MapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 139));

    //retrofit
    private MainFacade userController;

    //widgets
    private AutoCompleteTextView mSearchText;
    private ImageView mGps, mInfo, mPlacePicker, mPlaceSender;

    //vars
    private Boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlaceAutoCompleteAdapter mPlaceAutoCompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    private PlaceDto mPlace;
    private Marker mMarker;
    private ProgressDialog progressDialog;
    String name, address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mSearchText = findViewById(R.id.input_search);
        mGps = findViewById(R.id.ic_gps);
        mInfo = findViewById(R.id.place_info);
        mPlacePicker = findViewById(R.id.place_picker);
        mPlaceSender = findViewById(R.id.place_sender);
        userController = APIUtils.getFacadeService();


        getLocationPermission();

    }

    private void init() {
        Log.d(TAG, "init: initializing");

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        mSearchText.setOnItemClickListener(mAutocompleteClickListener);

        mPlaceAutoCompleteAdapter = new PlaceAutoCompleteAdapter(this, mGoogleApiClient,
                LAT_LNG_BOUNDS, null);

        mSearchText.setAdapter(mPlaceAutoCompleteAdapter);

        mSearchText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || event.getAction() == event.ACTION_DOWN
                    || event.getAction() == event.KEYCODE_ENTER) {

                //execute method for searching
                geoLocate();
            }
            return false;
        });

        mGps.setOnClickListener(v -> {
            Log.d(TAG, "onClick: clicked gps icon");
            getDeviceLocation();
        });

        mInfo.setOnClickListener(v -> {
            Log.d(TAG, "init: clicked place info");
            try {
                if (mMarker.isInfoWindowShown()) {
                    mMarker.hideInfoWindow();
                } else {
                    Log.d(TAG, "init: place info: " + mPlace.toString());
                    mMarker.showInfoWindow();
                }
            } catch (NullPointerException e) {
                Log.e(TAG, "init: NullPointerException: " + e.getMessage());
            }
        });

        mPlacePicker.setOnClickListener(v -> {
            int PLACE_PICKET_REQUEST = 1;
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

            try {
                startActivityForResult(builder.build(MapActivity.this), PLACE_PICKET_REQUEST);
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        });

        mPlaceSender.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            final View sendDialogView = inflater.inflate(R.layout.custom_send_window, null);
            builder.setView(sendDialogView);
            builder.setCancelable(false);

            final EditText sendName = sendDialogView.findViewById(R.id.name_tv);
            final TextView sendCountry = sendDialogView.findViewById(R.id.country_tv);
            final TextView sendCity = sendDialogView.findViewById(R.id.city_tv);
            builder.setTitle("send to server?");
            sendCountry.setTextColor(Color.WHITE);
            sendCity.setTextColor(Color.WHITE);

            sendCountry.setText(mPlace.getName());
            sendCity.setText(mPlace.getAddress());

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            builder.setPositiveButton("OK", (dialog, which) -> {
                progressDialog = new ProgressDialog(this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Loading....");
                progressDialog.show();

                name = sendName.getText().toString();
                address = sendCountry.getText().toString();


                Call<Value> call1 = userController.list(name, address);
                call1.enqueue(new Callback<Value>() {
                    @Override
                    public void onResponse(@NonNull Call<Value> call, @NonNull Response<Value> response) {
                        String value = response.body().getValue();
                        String message = response.body().getMessage();
                        progressDialog.dismiss();

                        if (value.equals("1")) {
                            Toast.makeText(MapActivity.this, message, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MapActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MapActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Value> call, Throwable t) {
                        t.printStackTrace();
                        Toast.makeText(MapActivity.this, "an error occurred", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });


            });

            AlertDialog dialog = builder.create();
            dialog.show();

        });

        hideSoftKeyboard();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);

                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient, place.getId());
                placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

            }
        }
    }

    private void geoLocate() {
        Log.d(TAG, "geoLocate: geolocating");

        String searchString = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage());
        }

        if (list.size() > 0) {
            Address address = list.get(0);

            Log.d(TAG, "geoLocate: found a location: " + address.toString());

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));

            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_NEUTRAL:
                        break;
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
            builder.setMessage("Latitude: " + address.getLatitude() + " Longitude: " + address.getLongitude() + " Country: " + address.getCountryName() +
                    " City: " + address.getLocality() + " ZipCode: " + address.getPostalCode()).setNeutralButton("Okay", dialogClickListener).show();
        }
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: found location!");
                        Location currentLocation = (Location) task.getResult();

                        moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM, "My Location");
                    } else {
                        Log.d(TAG, "onComplete: current location null");
                        Toast.makeText(MapActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: Security Exception: " + e.getMessage());
        }

    }

    private void moveCamera(LatLng latLng, float zoom, PlaceDto placeDto) {
        Log.d(TAG, "moveCamera: movering the camera to: lat: " + latLng.latitude + "lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        mMap.clear();

        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(MapActivity.this));

        if (placeDto != null) {
            try {
                String snippet = "Address: " + placeDto.getAddress() + "\n" +
                        "Phone Number: " + placeDto.getPhoneNumber() + "\n" +
                        "Website: " + placeDto.getWebsiteUri() + "\n" +
                        "Price Rating: " + placeDto.getRating() + "\n";

                MarkerOptions options = new MarkerOptions()
                        .position(latLng)
                        .title(placeDto.getName())
                        .snippet(snippet);
                mMarker = mMap.addMarker(options);
                mMap.addMarker(options);
            } catch (NullPointerException e) {
                Log.e(TAG, "moveCamera: NullPointerException: " + e.getMessage());
            }
        } else {
            mMap.addMarker(new MarkerOptions().position(latLng));
        }

        hideSoftKeyboard();

    }

    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera: movering the camera to: lat: " + latLng.latitude + "lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if (!title.equals("My Locatopn")) {
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMap.addMarker(options);
        }

        hideSoftKeyboard();

    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);

    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed.");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }

    public void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /*
    -------------------------------------------------------- google places API autocomplete suggestions -------------------------
     */

    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            hideSoftKeyboard();

            final AutocompletePrediction item = mPlaceAutoCompleteAdapter.getItem(position);
            final String placeId = item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = places -> {
        if (!places.getStatus().isSuccess()) {
            Log.d(TAG, "onResult: place query did not complete successfully: " + places.getStatus().toString());
            places.release();
            return;
        }
        final Place place = places.get(0);
        try {
            mPlace = new PlaceDto();
            mPlace.setName(place.getName().toString());
            mPlace.setAddress(place.getAddress().toString());
            mPlace.setId(place.getId());
            mPlace.setLatLng(place.getLatLng());
            mPlace.setRating(place.getRating());
            mPlace.setPhoneNumber(place.getPhoneNumber().toString());
            mPlace.setWebsiteUri(place.getWebsiteUri());

            Log.d(TAG, "onResult: place: " + mPlace.toString());
        } catch (NullPointerException e) {
            Log.e(TAG, "onResult: NullPointerException: " + e.getMessage());
        }

        moveCamera(new LatLng(place.getViewport().getCenter().latitude,
                place.getViewport().getCenter().longitude), DEFAULT_ZOOM, mPlace);

        places.release();
    };

}
