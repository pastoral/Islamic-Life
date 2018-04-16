package com.harbingerstudio.islamiclife.islamiclife.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.harbingerstudio.islamiclife.islamiclife.R;

public class NamazCalendar extends AppCompatActivity {
private Intent intent;
String lat,lon,month,year;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_namaz_calendar);
        intent = getIntent();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        lat = intent.getStringExtra("LAT");
        lon = intent.getStringExtra("LON");
        month = intent.getStringExtra("MONTH");
        year = intent.getStringExtra("YEAR");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(getApplicationContext(),month +"  "+year+" "+lat+"  "+lon, Toast.LENGTH_LONG).show();
    }
}
