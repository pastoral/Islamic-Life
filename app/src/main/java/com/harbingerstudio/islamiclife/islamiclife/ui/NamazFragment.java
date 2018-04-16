package com.harbingerstudio.islamiclife.islamiclife.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
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
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.harbingerstudio.islamiclife.islamiclife.R;
import com.harbingerstudio.islamiclife.islamiclife.adapters.DailyPrayerTimeAdapter;
import com.harbingerstudio.islamiclife.islamiclife.adapters.MainLayoutRecyclerAdapter;
import com.harbingerstudio.islamiclife.islamiclife.pojo.prayertime.Example;
import com.harbingerstudio.islamiclife.islamiclife.pojo.prayertime.Timings;
import com.harbingerstudio.islamiclife.islamiclife.retrofit.ApiArabicDate;
import com.harbingerstudio.islamiclife.islamiclife.retrofit.EndPointArabicDateInterface;
import com.harbingerstudio.islamiclife.islamiclife.retrofit.EndpointPrayerTimeInterface;
import com.harbingerstudio.islamiclife.islamiclife.retrofit.PrayerTimeApiClient;
import com.harbingerstudio.islamiclife.islamiclife.utils.ArabicUtils;
import com.harbingerstudio.islamiclife.islamiclife.utils.CheckInternet;
import com.harbingerstudio.islamiclife.islamiclife.utils.ProgressBarHandler;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import static android.R.attr.fragment;
import static com.harbingerstudio.islamiclife.islamiclife.Constants.FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS;
import static com.harbingerstudio.islamiclife.islamiclife.Constants.PRAYER_NAME_BAN;
import static com.harbingerstudio.islamiclife.islamiclife.Constants.PRAYER_NAME_ENG;
import static com.harbingerstudio.islamiclife.islamiclife.Constants.REQUEST_CHECK_SETTINGS;
import static com.harbingerstudio.islamiclife.islamiclife.Constants.UPDATE_INTERVAL_IN_MILLISECONDS;


/**
 * Created by User on 5/1/2017.
 */

public class NamazFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, ResultCallback<LocationSettingsResult> , LocationListener {
    protected GoogleApiClient mGoogleApiClient;
    private static final String TAG = "NamazFragment";
    private Location currentLocation;
    private int PLACE_PICKER_REQUEST = 1;
    private EndpointPrayerTimeInterface apiClient;
    private EndPointArabicDateInterface arabicDateInterface;
    private Timings timings;
    String strTimeZone, strTimeStamp, strSunrise, strSunset;
    public String [] namazTimes;
    private TabHost tabHost;
    private View view;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    public DailyPrayerTimeAdapter dailyPrayerTimeAdapter;
    public TextView txtarabichdate , txtenglishdate , txtsunrise , txtsunset;
    private String[] strDateSplit = new String[3];
    public String strArabicDate, strEnglishDate;
    private boolean mRequestingLocationUpdates;
    protected LocationRequest mLocationRequest;
    private ProgressBarHandler mProgressBarHandler;
    private CheckInternet checkInternet;
    private Snackbar snackbar;
    String month,year;
    private Button btnnamazcalender;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.namaz_fragment, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerprayertime);
        txtarabichdate = (TextView)view.findViewById(R.id.txtarabichdate);
        txtenglishdate = (TextView)view.findViewById(R.id.txtenglishdate);
        txtsunrise = (TextView)view.findViewById(R.id.txtsunrise);
        txtsunset = (TextView)view.findViewById(R.id.txtsunset);
        btnnamazcalender = view.findViewById(R.id.btnnamazcalender);
        mProgressBarHandler = new ProgressBarHandler(getActivity());
        //tabHost = (TabHost)view.findViewById(R.id.tabhost);
        layoutManager = new GridLayoutManager(getContext(),2);
        //tabHost.setup();

        //Tab 1
       /* TabHost.TabSpec spec = tabHost.newTabSpec("Tab One");
        spec.setContent(R.id.tab1);
        spec.setIndicator(getString(R.string.dailyprayertime));
        tabHost.addTab(spec);

        //Tab 2
        spec = tabHost.newTabSpec("Tab Two");
        spec.setContent(R.id.tab2);
        spec.setIndicator(getString(R.string.calenderprayertime));
        tabHost.addTab(spec);*/
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildGoogleApiClient();
        apiClient = PrayerTimeApiClient.getPrayerTimeApiClient().create(EndpointPrayerTimeInterface.class);
        arabicDateInterface = ApiArabicDate.getArabicDateApiClient().create(EndPointArabicDateInterface.class);
        namazTimes = new String[5];
        checkInternet = new CheckInternet(getContext());
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!mGoogleApiClient.isConnected()){
            mGoogleApiClient.connect();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = sdf.format(new Date());
         year = currentDateandTime.substring(0,4);
         month = currentDateandTime.substring(4,6);
         btnnamazcalender.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 loadNamajSchedule();
             }
         });
        //Toast.makeText(getActivity(),year + "   "+month,Toast.LENGTH_LONG).show();
        /*PlacePicker.IntentBuilder placePickerIntent = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(placePickerIntent.build(getActivity()), PLACE_PICKER_REQUEST);
        }
        catch (GooglePlayServicesRepairableException
                | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }*/
       // Calendar calendar = Calendar.getInstance();
        //java.sql.Date currentTimestamp = new java.sql.Timestamp(calendar.getTime().getTime());
        Long tsLong = System.currentTimeMillis()/1000;
        strTimeStamp = tsLong.toString();
        loadArabicDate(strTimeStamp);

        TimeZone timeZone = TimeZone.getDefault();
        strTimeZone = timeZone.getID();
        //Toast.makeText(getActivity(), strTimeZone, Toast.LENGTH_LONG).show();
        checkLocationSettings();
        if(mGoogleApiClient.isConnected()){
            startLocationUpdate();
        }
        boolean isConnected = checkInternet.haveNetworkConnection();
        showSnack(isConnected);

    }

    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if(ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
        loadPrayerSchedule();
    }

    /**
     * Handles failure to connect to the Google Play services client.
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        // Refer to the reference doc for ConnectionResult to see what error codes might
        // be returned in onConnectionFailed.
        Log.d(TAG, "Play services connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    /**
     * Handles suspension of the connection to the Google Play services client.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        mGoogleApiClient.connect();
        Log.d(TAG, "Play services connection suspended");
    }


    public boolean isGooglePlayServicesAvailable(Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                Dialog df = googleApiAvailability.getErrorDialog(activity, status, 2404);
                df.setCancelable(false);
                df.show();
                //googleApiAvailability.getErrorDialog(activity, status, 2404).show();
            }
            return false;
        }
        return true;
    }


   public void loadArabicDate(String timeStamp){
       String date = new ArabicUtils().getDateFromTimestamp(Long.parseLong(timeStamp));
       Call<com.harbingerstudio.islamiclife.islamiclife.pojo.arabicdate.Example> exampleCall = arabicDateInterface.getArabicDate(new ArabicUtils().splitDate(date));
       exampleCall.enqueue(new Callback<com.harbingerstudio.islamiclife.islamiclife.pojo.arabicdate.Example>() {
           @Override
           public void onResponse(Call<com.harbingerstudio.islamiclife.islamiclife.pojo.arabicdate.Example> call, Response<com.harbingerstudio.islamiclife.islamiclife.pojo.arabicdate.Example> response) {
               try{
                   String month = response.body().getData().getHijri().getMonth().getEn();
                   String day = response.body().getData().getHijri().getDay();
                   String year = response.body().getData().getHijri().getYear();

                   txtarabichdate.setText(day + " " + month + " " + year);
               }
               catch(Exception e){
                   Log.d("onResponse", "There is an error");
                   e.printStackTrace();
               }
           }

           @Override
           public void onFailure(Call<com.harbingerstudio.islamiclife.islamiclife.pojo.arabicdate.Example> call, Throwable t) {

           }


       });


   }

    protected LocationRequest createLocationRequest() {
        Log.i(TAG, "createLocationRequest()");
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(10);
        return mLocationRequest;
    }


   /* @Override
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
    }*/




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
                Log.i(TAG, "All location settings are satisfied.");
                if(ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                }
                else{
                    mGoogleApiClient.connect();
                }
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED :
                Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");
                try{
                    //status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                    this.startIntentSenderForResult(status.getResolution().getIntentSender(), REQUEST_CHECK_SETTINGS, null, 0, 0, 0, null);
                }
                catch(IntentSender.SendIntentException e){
                    Log.i(TAG, "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE :
                Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int temp = requestCode;
        if(requestCode == PLACE_PICKER_REQUEST && resultCode == Activity.RESULT_OK){
            final Place place = PlacePicker.getPlace(getContext(), data);
            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();
            String attributions = (String) place.getAttributions();
            if (attributions == null) {
                attributions = "";
            }

            Toast.makeText(getActivity(), name + " " + address + " " + attributions, Toast.LENGTH_SHORT).show();
        }
        else if(requestCode == REQUEST_CHECK_SETTINGS && resultCode == Activity.RESULT_OK){
            // buildGoogleApiClient();
            mGoogleApiClient.connect();
            if(mGoogleApiClient.isConnected()){
                startLocationUpdate();
                if(currentLocation != null){
                    loadArabicDate(strTimeStamp);
                    loadPrayerSchedule();
                }
            }
        }
        else {
            return;
        }
    }

    public void loadPrayerSchedule(){
        if(currentLocation != null){
            //Toast.makeText(getContext(),String.valueOf(currentLocation.getLatitude()) + "  " + String.valueOf(currentLocation.getLongitude()), Toast.LENGTH_SHORT).show();
            //Intent mIntent = new
            mProgressBarHandler.show();

            Call<Example> call = apiClient.getPrayerTime(String.valueOf(currentLocation.getLatitude()),String.valueOf(currentLocation.getLongitude()),strTimeZone,String.valueOf(3),String.valueOf(1));
            call.enqueue(new Callback<Example>() {
                @Override
                public void onResponse(Call<Example> call, Response<Example> response) {
                    try {
                        timings = response.body().getData().getTimings();
                        namazTimes[0] = timings.getFajr();
                        namazTimes[1] = timings.getDhuhr();
                        namazTimes[2] = timings.getAsr();
                        namazTimes[3] = timings.getMaghrib();
                        namazTimes[4] = timings.getIsha();
                        strSunrise = timeFormatting(timings.getSunrise());
                        strSunset = timeFormatting(timings.getSunset());
                        //String getDate = result.data.getData().getDate().getTimestamp();
                        mProgressBarHandler.hide();
                        txtenglishdate.setText(response.body().getData().getDate().getReadable());
                        txtsunrise.setText(getString(R.string.sunrise) +  strSunrise);
                        txtsunset.setText(getString(R.string.sunset) +  strSunset);
                        // strTimeZone = new ArabicUtils().splitDate(new ArabicUtils().getDateFromTimestamp(Long.parseLong(result.data.getData().getDate().getTimestamp())));
                        Log.d(TAG, "Yeeeeeeeee");
                        dailyPrayerTimeAdapter = new DailyPrayerTimeAdapter(getContext(),PRAYER_NAME_ENG,PRAYER_NAME_BAN,namazTimes);
                        recyclerView.setLayoutManager(layoutManager);

                        recyclerView.setAdapter(dailyPrayerTimeAdapter);
                        btnnamazcalender.setVisibility(View.VISIBLE);
                    }
                    catch(Exception e){
                        Log.d("onResponse", "There is an error");
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<Example> call, Throwable t) {

                }

            });

        }
    }

    private void startLocationUpdate(){
        mProgressBarHandler.show();
        Log.d("STARTLOC update", "startLocationUpdate fired");
        if(ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);
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

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        loadArabicDate(strTimeStamp);
        loadPrayerSchedule();
    }

    public void showSnack(boolean isConnected ){
        String message = "";
        int color = Color.TRANSPARENT;
        if(!isConnected){
            message = getString(R.string.nointernetconnection);
            color = Color.RED;
            snackbar = Snackbar.make(view.findViewById(R.id.namazfragmentroot), message, Snackbar.LENGTH_INDEFINITE);
            View sbView = snackbar.getView();
            TextView textView = (TextView)sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
        }
        else{
            snackbar = null;
        }


    }

    @Override
    public void onPause() {
        super.onPause();
       if(snackbar != null){
           snackbar.dismiss();
       }
        mProgressBarHandler.hide();
    }

    private String timeFormatting(String s){
        String time, getString, remainString;
        int intTime;
        getString = s.substring(0,2);
        remainString = s.substring(2);
        intTime = Integer.parseInt(getString);
        if(intTime > 12){
            intTime = intTime - 12;
            time = "0" + String.valueOf(intTime)+ remainString + "PM";
        }
        else{
            time = s + "AM";
        }
        return time;
    }
    public void loadNamajSchedule(){
        Intent i = new Intent(getContext(),NamazCalendar.class);
        i.putExtra("LAT",String.valueOf(currentLocation.getLatitude()));
        i.putExtra("LON",String.valueOf(currentLocation.getLongitude()));
        i.putExtra("MONTH", month);
        i.putExtra("YEAR",year);
        startActivity(i);
    }
}
