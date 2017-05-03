package com.harbingerstudio.islamiclife.islamiclife.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.harbingerstudio.islamiclife.islamiclife.BaseActivity;
import com.harbingerstudio.islamiclife.islamiclife.R;
import com.harbingerstudio.islamiclife.islamiclife.pojo.mosque.Example;
import com.harbingerstudio.islamiclife.islamiclife.pojo.mosque.Result;
import com.harbingerstudio.islamiclife.islamiclife.retrofit.ApiClient;
import com.harbingerstudio.islamiclife.islamiclife.retrofit.ApiPlaceLocatorInterface;
import com.harbingerstudio.islamiclife.islamiclife.services.BackgroundLocationService;
import com.harbingerstudio.islamiclife.islamiclife.services.LocationUpdates;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.TwitterException;

import java.util.List;

import retrofit2.Call;

import static com.harbingerstudio.islamiclife.islamiclife.Constants.API_KEY;
import static com.harbingerstudio.islamiclife.islamiclife.Constants.FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS;
import static com.harbingerstudio.islamiclife.islamiclife.Constants.PLACE_MOSQUE;
import static com.harbingerstudio.islamiclife.islamiclife.Constants.REQUEST_CHECK_SETTINGS;
import static com.harbingerstudio.islamiclife.islamiclife.Constants.UPDATE_INTERVAL_IN_MILLISECONDS;
import static com.harbingerstudio.islamiclife.islamiclife.Constants.permisionList;
import static com.harbingerstudio.islamiclife.islamiclife.Constants.permsRequestCode;

import static com.harbingerstudio.islamiclife.islamiclife.R.id.map;
import static java.security.AccessController.getContext;

public class MosqueLocatorMap extends BaseActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ResultCallback<LocationSettingsResult>, LocationListener{

    private static final String MosqueLocatorTAG = MosqueLocatorMap.class.getSimpleName();

    private boolean mRequestingLocationUpdates;
    protected LocationRequest mLocationRequest;
    private SupportMapFragment mapFragment;
    public LocationSettingsRequest mLocationSettingsRequest;
    public PlaceAutocompleteFragment autocompleteFragment;
    //private ResponeReceiver receiver;
    private IntentFilter filter;

    private Location mLastLocation;
    private Marker marker;



    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    private GoogleApiClient mGoogleApiClient;

    private List<Result> mosqueLocationList;
    private ApiPlaceLocatorInterface apiClient;
    private String placeName,vicinity = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve location and camera position from saved instance state.
     /*   if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }*/

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_mosque_locator_map);




       // filter = new IntentFilter(ResponeReceiver.ACTION_RESP);
        //filter.addCategory(Intent.CATEGORY_DEFAULT);

        buildGoogleApiClient();
       // mGoogleApiClient.connect();
        apiClient = ApiClient.getClient().create(ApiPlaceLocatorInterface.class);

      mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        createLocationRequest();
        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("Mosque Locator", "Place: " + place.getName());
                LatLng foundPlace = place.getLatLng();
                Intent i = new Intent(getApplicationContext(),PickLocationInMap.class);
                i.putExtra("Latitude" , String.valueOf(foundPlace.latitude));
                i.putExtra("Longitude" , String.valueOf(foundPlace.longitude));
                i.putExtra("Title", String.valueOf(place.getName()));
                i.putExtra("Snipet", String.valueOf(place.getAddress()));
                startActivity(i);
                //mMap.clear();

                //stopLocationUpdate();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("Mosque Locator", "An error occurred: " + status);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mLastLocation = new Location("");
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        //receiver = new ResponeReceiver();
        if(!mRequestingLocationUpdates){
            mRequestingLocationUpdates = true;
        }

    }

    /**
     * Saves the state of the map when the activity is paused.
     */
  /*   @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }*/


    @Override
    protected void onResume() {
        super.onResume();
        checkLocationSettings();
        if(mRequestingLocationUpdates && mGoogleApiClient.isConnected()){
            startLocationUpdate();
        }

      // LocationRequest locationRequest = checkLocationSettings();

        //MosqueLocatorMap.this.registerReceiver(receiver,filter);
        //displayMap();

    }

    @Override
    protected void onPause() {
        super.onPause();
       // MosqueLocatorMap.this.unregisterReceiver(receiver);
        stopLocationUpdate();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


    @Override
    public void onMapReady(GoogleMap map) {

        mMap = map;

        if(ContextCompat.checkSelfPermission(MosqueLocatorMap.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
            if(mLastLocation != null){
                displayMap();
            }
        }
    }


   /* public class ResponeReceiver extends BroadcastReceiver {
        public static final String ACTION_RESP = "MESSAGE_PROCESSED";
        //Log.d("MAP Size " , String.valueOf(locationMap.size()));
        Location lastLocation = new Location("");

        @Override
        public void onReceive(Context context, Intent intent) {
            double[] resultData = new double[2];
            resultData = intent.getDoubleArrayExtra(LocationUpdates.PARAM_OUT_MSG);

            if (resultData[0] != 0.0 || resultData[1] != 0.0) {


                lastLocation.setLatitude(resultData[0]);
                lastLocation.setLongitude(resultData[1]);

                Log.i("LAT LON", String.valueOf(resultData[0]) + " , " + String.valueOf(resultData[1]));
                mLastLocation = lastLocation;
                displayMap();

            }
        }
    }*/

    private void startLocationUpdate(){
        Log.d("STARTLOC update", "startLocationUpdate fired");
        if(ContextCompat.checkSelfPermission(MosqueLocatorMap.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
       /* Intent intent = new Intent(this, BackgroundLocationService.class);
        intent.putExtra("requestId", 101);
        startService(intent);*/

    }

    private void stopLocationUpdate(){
        /*Intent intent = new Intent(this, BackgroundLocationService.class);

        intent.putExtra("requestId", 101);
        stopService(intent);*/

        if(mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    private void displayMap(){
        if(mMap != null){

            if(mLastLocation == null) {
                if(ContextCompat.checkSelfPermission(MosqueLocatorMap.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mLastLocation = LocationServices.FusedLocationApi
                            .getLastLocation(mGoogleApiClient);
                }
            }

            if(mLastLocation != null){
               // getMosqueLocationList();
                String location = String.valueOf(mLastLocation.getLatitude()) + "," + String.valueOf(mLastLocation.getLongitude());
                Call<Example> call = apiClient.getNearbyPlaces(location,"1000",PLACE_MOSQUE,API_KEY);
                call.enqueue(new Callback<Example>() {
                    @Override
                    public void success(com.twitter.sdk.android.core.Result<Example> result) {
                       // mosqueLocationList = result.data.getResults();
                        //Log.d("TAAAAG" , result.data.toString());
                        try{
                            if(ContextCompat.checkSelfPermission(MosqueLocatorMap.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                mMap.clear();
                                mMap.setMyLocationEnabled(true);
                                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                                mMap.setBuildingsEnabled(true);
                            }
                            mosqueLocationList = result.data.getResults();
                            for(int i = 0; i<result.data.getResults().size(); i++){
                                Double lat = result.data.getResults().get(i).getGeometry().getLocation().getLat();
                                Double lng = result.data.getResults().get(i).getGeometry().getLocation().getLng();
                                 placeName = result.data.getResults().get(i).getName();
                                vicinity = result.data.getResults().get(i).getVicinity();
                                MarkerOptions markerOptions = new MarkerOptions();
                                LatLng latLng = new LatLng(lat, lng);
                                // Position of Marker on Map
                                markerOptions.position(latLng);
                                // Adding Title to the Marker
                                markerOptions.title(placeName + " : " + vicinity);
                                // Adding Marker to the Camera.
                                marker = mMap.addMarker(markerOptions);
                                // Adding colour to the marker
                                //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.mosque_icon));

                                // move map camera

                                //mMap.setOnMarkerClickListener(this);


                               /* mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastLocation.getLatitude(),
                                                mLastLocation.getLongitude()), 16));
                                Log.d("LAT_LAN", String.valueOf(mLastLocation.getLatitude()) + "  " + String.valueOf(mLastLocation.getLongitude()));
                                */

                            }
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastLocation.getLatitude(),
                                            mLastLocation.getLongitude()), 15));

                            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                            getMarkerDetails(mMap);



                        }
                        catch(Exception e){
                            Log.d("onResponse", "There is an error");
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        Log.d("TAAAAG" ," FAILED");
                    }
                });

               // Toast.makeText(this, String.valueOf(mLastLocation.getLatitude()) + "  " + String.valueOf(mLastLocation.getLongitude()), Toast.LENGTH_SHORT);
                // mMap.addMarker(new MarkerOptions().position(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude())));


            }

        }
    }



    @Override
    public void onConnected(Bundle connectionHint) {
        // Build the map.


        mapFragment.getMapAsync(this);
        if(mRequestingLocationUpdates){
            startLocationUpdate();
        }
    }

    /**
     * Handles failure to connect to the Google Play services client.
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        // Refer to the reference doc for ConnectionResult to see what error codes might
        // be returned in onConnectionFailed.
        Log.d(MosqueLocatorTAG, "Play services connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    /**
     * Handles suspension of the connection to the Google Play services client.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        mGoogleApiClient.connect();
        Log.d(MosqueLocatorTAG, "Play services connection suspended");
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case REQUEST_CHECK_SETTINGS :
                switch (resultCode){
                    case Activity.RESULT_OK :
                        mRequestingLocationUpdates = true;
                        mapFragment.getMapAsync(this);

                        // startLocationUpdate();
                        //stopLocationUpdate();
                       // displayMap();
                        break;
                    case Activity.RESULT_CANCELED :
                        Log.i("ThreeFragment", "User chose not to make required location settings changes.");
                        break;
                }
                break;
        }
    }




    /**
     * Check if the device's location settings are adequate for the app's needs using the
     * {@link com.google.android.gms.location.SettingsApi#checkLocationSettings(GoogleApiClient,
     * LocationSettingsRequest)} method, with the results provided through a {@code PendingResult}.
     */
    public void checkLocationSettings() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(createLocationRequest());

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,builder.build());
        result.setResultCallback(this);

    }

    /**
     * The callback invoked when
     * {@link com.google.android.gms.location.SettingsApi#checkLocationSettings(GoogleApiClient,
     * LocationSettingsRequest)} is called. Examines the
     * {@link com.google.android.gms.location.LocationSettingsResult} object and determines if
     * location settings are adequate. If they are not, begins the process of presenting a location
     * settings dialog to the user.
     */
    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch(status.getStatusCode()){
            case LocationSettingsStatusCodes.SUCCESS :
                Log.i(MosqueLocatorTAG, "All location settings are satisfied.");
                startLocationUpdate();
                //stopLocationUpdate();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED :
                Log.i(MosqueLocatorTAG, "Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");
                try{
                    status.startResolutionForResult(this, REQUEST_CHECK_SETTINGS);
                }
                catch(IntentSender.SendIntentException e){
                    Log.i(MosqueLocatorTAG, "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE :
                Log.i(MosqueLocatorTAG, "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
        }
    }

    protected LocationRequest createLocationRequest() {
        Log.i(MosqueLocatorTAG, "createLocationRequest()");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(10);
        return mLocationRequest;
    }

    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        displayMap();
    }

    public void getMarkerDetails(GoogleMap googleMap){
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                //Toast.makeText(getApplicationContext(), marker.getTitle(), Toast.LENGTH_SHORT).show();
                Snackbar.make(findViewById(map), marker.getTitle(), Snackbar.LENGTH_LONG).show();
            }
        });

    }


}
