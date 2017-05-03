package com.harbingerstudio.islamiclife.islamiclife.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.TabHost;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.harbingerstudio.islamiclife.islamiclife.R;
import com.harbingerstudio.islamiclife.islamiclife.adapters.DailyPrayerTimeAdapter;
import com.harbingerstudio.islamiclife.islamiclife.adapters.MainLayoutRecyclerAdapter;
import com.harbingerstudio.islamiclife.islamiclife.pojo.prayertime.Example;
import com.harbingerstudio.islamiclife.islamiclife.pojo.prayertime.Timings;
import com.harbingerstudio.islamiclife.islamiclife.retrofit.EndpointPrayerTimeInterface;
import com.harbingerstudio.islamiclife.islamiclife.retrofit.PrayerTimeApiClient;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;

import java.util.TimeZone;

import retrofit2.Call;

import static com.harbingerstudio.islamiclife.islamiclife.Constants.PRAYER_NAME_BAN;
import static com.harbingerstudio.islamiclife.islamiclife.Constants.PRAYER_NAME_ENG;


/**
 * Created by User on 5/1/2017.
 */

public class NamazFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{
    protected GoogleApiClient mGoogleApiClient;
    private static final String TAG = "NamazFragment";
    private Location currentLocation;
    private int PLACE_PICKER_REQUEST = 1;
    private EndpointPrayerTimeInterface apiClient;
    private Timings timings;
    String strTimeZone, strTimeStamp, strSunrise, strSunset;
    public String [] namazTimes;
    private TabHost tabHost;
    private View view;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    public DailyPrayerTimeAdapter dailyPrayerTimeAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.namaz_fragment, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerprayertime);
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
        namazTimes = new String[5];
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

        TimeZone timeZone = TimeZone.getDefault();
        strTimeZone = timeZone.getID();
        Toast.makeText(getActivity(), strTimeZone, Toast.LENGTH_LONG).show();


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
        if(currentLocation != null){
            //Toast.makeText(getContext(),String.valueOf(currentLocation.getLatitude()) + "  " + String.valueOf(currentLocation.getLongitude()), Toast.LENGTH_SHORT).show();
            //Intent mIntent = new

            Call<Example> call = apiClient.getPrayerTime(String.valueOf(currentLocation.getLatitude()),String.valueOf(currentLocation.getLongitude()),strTimeZone,String.valueOf(1));
            call.enqueue(new Callback<Example>() {
                @Override
                public void success(Result<Example> result) {
                   // timings = result.data.getTimings();
                    //Log.d(TAG, "Yeeeeeeeee");
                    try {
                        timings = result.data.getData().getTimings();
                        namazTimes[0] = timings.getFajr();
                        namazTimes[1] = timings.getDhuhr();
                        namazTimes[2] = timings.getAsr();
                        namazTimes[3] = timings.getMaghrib();
                        namazTimes[4] = timings.getIsha();
                        strSunrise = timings.getSunrise();
                        strSunset = timings.getSunset();
                        Log.d(TAG, "Yeeeeeeeee");
                        dailyPrayerTimeAdapter = new DailyPrayerTimeAdapter(getContext(),PRAYER_NAME_ENG,PRAYER_NAME_BAN,namazTimes);
                        recyclerView.setLayoutManager(layoutManager);

                        recyclerView.setAdapter(dailyPrayerTimeAdapter);
                    }
                    catch(Exception e){
                        Log.d("onResponse", "There is an error");
                        e.printStackTrace();
                    }
                }

                @Override
                public void failure(TwitterException exception) {
                    Log.d("TAG" ," FAILED");
                }
            });

        }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
