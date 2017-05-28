package com.example.android.myplaces;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by weronika on 28.05.17.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the content of the activity to use the activity_main.xml layout file
        setContentView(R.layout.activity_main);

        // Find the View that shows the numbers category
        TextView bikestations = (TextView) findViewById(R.id.bike_stations);

        // Set a click listener on that View
        bikestations.setOnClickListener(new View.OnClickListener() {
            // The code in this method will be executed when the bike_stations category is clicked on.
            @Override
            public void onClick(View view) {
                // Create a new intent to open the {@link BikeStationsActivity}
                Intent numbersIntent = new Intent(MainActivity.this, MapsActivity.class);

                // Start the new activity
                startActivity(numbersIntent);
            }
        });

        //Find the View that shows the metro_station category
        TextView metrostation = (TextView) findViewById(R.id.metro_station);

        // Find the View that shows the park_and_ride category
        TextView parkandride = (TextView) findViewById(R.id.park_and_ride);


        }
    }