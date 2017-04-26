package com.harbingerstudio.islamiclife.islamiclife.services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderApi;
import com.harbingerstudio.islamiclife.islamiclife.ui.MosqueLocatorMap;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class LocationUpdates extends IntentService {
    private String TAG = this.getClass().getSimpleName();
    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;
    public Bundle bundle;
    public static final String ACTION_RESP = "MESSAGE_PROCESSED";
    public static final String PARAM_IN_MSG = "imsg";
    public static final String PARAM_OUT_MSG = "omsg";


    public LocationUpdates() {
        super("Fused Location");
    }

    public LocationUpdates(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Location location = intent.getParcelableExtra(FusedLocationProviderApi.KEY_LOCATION_CHANGED);
        Log.i(TAG, "onHandleIntent");
        if(intent!=null){
            bundle = new Bundle();
            //final ResultReceiver resultReceiver = intent.getParcelableExtra("receiver");
            if(location !=null) {

                double[] resultData = new double[2];
                resultData[0] = location.getLatitude();
                resultData[1] = location.getLongitude();

                Log.i(TAG, "onHandleIntent " + location.getLatitude() + "," + location.getLongitude());

                Intent broadcastIntent = new Intent();
             //   broadcastIntent.setAction(MosqueLocatorMap.ResponeReceiver.ACTION_RESP);
                broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                broadcastIntent.putExtra(PARAM_OUT_MSG, resultData);
                sendBroadcast(broadcastIntent);

            }

        }
        this.stopSelf();
    }
}
