package com.example.android.myplaces;

import android.*;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by micha on 09.06.2017.
 */

public class HotActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    private GoogleMap mMap;
    private TextView tvData;
    private static final String EXPANDABLE_LIST_KEY = "Zwiń i rozwiń aby odświeżyć";
    private static final String TAG = "HOTY";
    LocationManager locationManager;
    LocListener locationListener;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    double lon, lat;
    public List<String> hotList;



    private ExpandableListView listView;
    private ExpandableListAdapter listAdapter;
    private List<String> listDataHeader;
    private HashMap<String,List<String >> listHash;




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot);

        TextView sendsms = (TextView) findViewById(R.id.send_sms);
        final EditText phoneNumber = (EditText) findViewById(R.id.phone_number);


        // Set a click listener on that View
        sendsms.setOnClickListener(new View.OnClickListener() {
            // The code in this method will be executed when the bike_stations category is clicked on.
            @Override
            public void onClick(View view) {
                String number = phoneNumber.getText().toString();

                new SMSActivity().execute(number);
                // Create a new intent to open the {@link BikeStationsActivity}
                //Intent numbersIntent4 = new Intent(HotActivity.this, MySMSActivity.class);

                // Start the new activity
                //startActivity(numbersIntent4);
            }
        });



        MapsInitializer.initialize(getApplicationContext());
        getLocation();
        Log.d(TAG, "After getLocation");

        listView = (ExpandableListView)findViewById(R.id.lvExp);
        listHash = new HashMap<>();


        listHash.put(EXPANDABLE_LIST_KEY, new ArrayList<String>());
        listDataHeader = new ArrayList<>((listHash.keySet()));
        listAdapter = new ExpandableListAdapter(this,listDataHeader,listHash);
        listView.setAdapter(listAdapter);

        listView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int grpPos) {
                getHotAsyncTask();
            }
        });
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            //double longitude = Double.parseDouble(hotArray.getJSONObject(i).getString("longitude"));
            public boolean onChildClick(ExpandableListView parent,
                                        View v,
                                        int groupPosition,
                                        int childPosition,
                                        long id) {
                Intent intent = new Intent(HotActivity.this, MapsActivity.class);
                startActivity(intent);
                return false;
            }


        });



        //listView.setOnChildClickListener();
        enableMyLocation();
    }
    public void getHot(String myNumber) {
        Log.e(TAG, "getHot in AsyncTask");
        String apiID = "53ef6c4b-8025-4008-a268-916a66de4cfc";
        Log.i(TAG, "latitude" + lat
                + "long " + lon);
        String urlToGetHot = Helper.getConfigValue(this, "um_url") + "datastore_search/?resource_id=" +
                apiID + "&limit=5";

        Log.e(TAG, urlToGetHot);
        JSONFromApiGetter umApiGetter = new JSONFromApiGetter();
        umApiGetter.execute(urlToGetHot);
    }


    public void getLocation(){

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocListener();
        if (ActivityCompat.checkSelfPermission(HotActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(HotActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        List<String> providers = locationManager.getAllProviders();
        if(providers.size()==0){
            Log.wtf(TAG, "No providers available");
            return;
        }
        lat = locationManager.getLastKnownLocation(providers.get(0)).getLatitude();
        lon = locationManager.getLastKnownLocation(providers.get(0)).getLongitude();
        getHotAsyncTask();
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5000, 10, new LocListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        lon = location.getLatitude();
                        lat = location.getLongitude();
                       // Toast.makeText(HotActivity.this, "Current location: " + location.getLatitude() + " " + location.getLongitude(), Toast.LENGTH_SHORT).show();
                        getHotAsyncTask();
                    }
                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                    }
                    @Override
                    public void onProviderEnabled(String provider) {
                    }
                    @Override
                    public void onProviderDisabled(String provider) {
                    }
                });
    }
    void getHotAsyncTask(){
        HotActivity.LocalizationGetter localizationGetter = new HotActivity.LocalizationGetter();
        localizationGetter.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    private class LocalizationGetter extends AsyncTask<String, Integer, Void> {
        protected void onPreExecute(){super.onPreExecute();}
        @Override
        protected Void doInBackground(String... strings) {
            getHot("");
            return null;
        }
        protected void onPostExecute(Double lon,double lat) {
            Toast.makeText(HotActivity.this, "Current location: " + lon + " " + lat, Toast.LENGTH_SHORT).show();
        }
    }
    public class JSONFromApiGetter extends AsyncTask<String,Integer, JSONObject>implements Serializable{
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected  JSONObject doInBackground(String... arg0) {
            URL apiUrlObject = null;
            JSONObject jsonObject = null;
            try {
                apiUrlObject = new URL(arg0[0]);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                apiUrlObject.openStream()));
                String inputLine = "";
                StringBuilder stringJson = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    stringJson.append(inputLine);
                }
                jsonObject = new JSONObject(stringJson.toString());
                in.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject;
        }
        protected void onPostExecute(JSONObject result) {
            try {
                JSONArray hotArray = result.getJSONObject("result").
                        getJSONArray("records");
                //JSONArray coordinates = result.getJSONObject("result").
                  //      getJSONArray("featureMemberCoordinates");
                List<String> hotList = new ArrayList<>();
                ArrayList<String> posHotSpot = new ArrayList<>();
                ArrayList<String> nazwHotSpot = new ArrayList<>();
                for (int i = 0; i < hotArray.length(); i++) {
/*
                    String latBike = coordinates.getJSONObject(i).getString("latitude");
                    String lonBike= coordinates.getJSONObject(i).getString("longitude");
                    String STOJAKI= bikeArray.getJSONObject(i).getString("STOJAKI");
                    String AKTU_DAN = bikeArray.getJSONObject(i).getString("AKTU_DAN");
                    String OBJECTID = bikeArray.getJSONObject(i).getString("OBJECTID");
                    String LOKALIZACJA = bikeArray.getJSONObject(i).getString("LOKALIZACJA");
                    String ROWERY = bikeArray.getJSONObject(i).getString("ROWERY");
                    String NR_STACJI = bikeArray.getJSONObject(i).getString("NR_STACJI");
*/
                    String x_wgs84 =hotArray.getJSONObject(i).getString("x_wgs84");
                    String _id=hotArray.getJSONObject(i).getString("_id");
                    String y_wgs84 =hotArray.getJSONObject(i).getString("y_wgs84");
                    String nazwa=hotArray.getJSONObject(i).getString("nazwa");
                    hotList.add("Hot spot nr: "+ _id +"\n"+"Nazwa: "+ nazwa+"\n"+"Lokalizacja: "+"\n"+ Double.valueOf(x_wgs84) +", "+ Double.valueOf(y_wgs84));
                    posHotSpot.add(x_wgs84+","+y_wgs84);
                    nazwHotSpot.add(nazwa);



                }
                listHash.put(EXPANDABLE_LIST_KEY, hotList);
                listDataHeader = new ArrayList<>(listHash.keySet());
                Log.e(TAG," - getting hotStations: " +hotList);
                listAdapter.setExpandableLists(listHash, listDataHeader);
                listAdapter.notifyDataSetChanged();

                Intent intentMarker = new Intent(HotActivity.this,MapsActivity.class);
                intentMarker.putStringArrayListExtra("posHotSpot", posHotSpot);
                intentMarker.putExtra("nazwHotSpot", nazwHotSpot);
                Log.e(TAG," - test intentow: " +posHotSpot);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }
        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            enableMyLocation();
        }
    }


    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    android.Manifest.permission.ACCESS_FINE_LOCATION, true);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            //new JSONTask().execute("https://api.um.warszawa.pl/api/action/wfsstore_get/?id=a08136ec-1037-4029-9aa5-b0d0ee0b9d88&circle=21.02,52.21,1000&apikey=d64160f0-f237-4d84-9f82-823b44a6102f");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    public class SMSActivity extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(HotActivity.this, "Wysyłanie sms", Toast.LENGTH_LONG).show();
        }

        @Override
        protected String doInBackground(String... arg0) {
            StringBuilder result = new StringBuilder();;
            try {
                URL apiUrlObject = null;
                //!!!!!!!!!!!!!!!!!
                 //TUUUUUUU, zrobic tak zeby do atrubutu API pod nazwa @msg byly wczytywane hotpsoty
                //!111one
                //String msg = ?
                String urlToSend = "";
                String number = arg0[0];
                String senderNumber = "507362427";
                System.out.println(number);
                    if (senderNumber.length() > 11) {
                        senderNumber = senderNumber.substring(senderNumber.length() - 11);
                    }else if(senderNumber.length() == 9){
                        senderNumber = "48" + senderNumber;
                    }
                    if (number.length() > 11) {
                        number = number.substring(number.length() - 11);
                    }else if(number.length() == 9){
                        number = "48" + number;
                    }
                    urlToSend = "https://apitest.orange.pl/Messaging/v1/SMSOnnet?from=" + senderNumber + "&to=" + number
                            + "&msg=testtesttest&deliverystatus=true&apikey" +
                            "=" + Helper.getConfigValue(HotActivity.this, "api_orange_key");

                    apiUrlObject = new URL(urlToSend);
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(
                                    apiUrlObject.openStream()));
                    String inputLine = "";

                    while ((inputLine = in.readLine()) != null) {
                        result.append(inputLine + "  " + number + "\n");
                    }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result.toString();
        }

        protected void onPostExecute(String result) {
            Toast.makeText(HotActivity.this, result, Toast.LENGTH_LONG).show();
        }

    }
}
