package com.harbingerstudio.islamiclife.islamiclife;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.harbingerstudio.islamiclife.islamiclife.adapters.MainLayoutRecyclerAdapter;
import com.harbingerstudio.islamiclife.islamiclife.model.LayoutListItmes;
import com.harbingerstudio.islamiclife.islamiclife.model.LayoutListModel;
import com.harbingerstudio.islamiclife.islamiclife.ui.LauncherFragment;
import com.harbingerstudio.islamiclife.islamiclife.ui.RamadanFragment;
import com.harbingerstudio.islamiclife.islamiclife.utils.RecyclerItemClickListener;

import static com.harbingerstudio.islamiclife.islamiclife.Constants.APPTAG;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    public RecyclerView recyclerView;
    private GoogleApiClient mGoogleApiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.layourrecycler);
       // layoutListItmes = new LayoutListItmes();

        setSupportActionBar(toolbar);
        isGooglePlayServicesAvailable(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LauncherFragment launcherFragment = new LauncherFragment();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        fragmentTransaction.replace(R.id.container_id,launcherFragment);
        fragmentTransaction.commit();

    }

    @Override
    protected void onResume() {
        super.onResume();
        isGooglePlayServicesAvailable(this);
        //testDB();
        if(isGooglePlayServicesAvailable(this)){
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(AppInvite.API)
                    .enableAutoManage(this,this).build();
        }
        else{
            return;
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.stopAutoManage(this);
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGoogleApiClient.stopAutoManage(this);
        mGoogleApiClient.disconnect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(APPTAG,"onConnectionFailed:" + connectionResult);
        //showMessage(getString(R.string.google_play_services_error));
        Toast.makeText(this,getString(R.string.google_play_services_error),Toast.LENGTH_LONG).show();
    }
}
