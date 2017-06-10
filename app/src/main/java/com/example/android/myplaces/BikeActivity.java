package com.example.android.myplaces;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by micha on 03.06.2017.
 */

public class BikeActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private TextView tvData;
    private static final String EXPANDABLE_LIST_KEY = "Stacje rowerowe";
    private static final String TAG = "Rowery";
    LocationManager locationManager;
    LocListener locationListener;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    double lon, lat;

    private ExpandableListView listView;
    private ExpandableListAdapter listAdapter;
    private List<String> listDataHeader;
    private HashMap<String,List<String >> listHash;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike);

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
                getBikeAsyncTask();
            }
        });
        enableMyLocation();
    }
    public void getBike(String myNumber) {
        Log.e(TAG, "getBike in AsyncTask");
        String apiID = "e26218cb-61ec-4ccb-81cc-fd19a6fee0f8";
        Log.i(TAG, "latitude" + lat
                + "long " + lon);
        String urlToGetBikeStations = Helper.getConfigValue(this, "um_url") + "wfsstore_get?id=" +
                apiID + "&circle=" + lon + "," + lat + ",1000&" +
                "apikey=" + Helper.getConfigValue(this, "api_key");

        Log.e(TAG, urlToGetBikeStations);
        JSONFromApiGetter umApiGetter = new JSONFromApiGetter();
        umApiGetter.execute(urlToGetBikeStations);
    }
    public void getLocation(){
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocListener();
        if (ActivityCompat.checkSelfPermission(BikeActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(BikeActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        List<String> providers = locationManager.getAllProviders();
        if(providers.size()==0){
            Log.wtf(TAG, "No providers available");
            return;
        }
        lat = locationManager.getLastKnownLocation(providers.get(0)).getLatitude();
        lon = locationManager.getLastKnownLocation(providers.get(0)).getLongitude();
        getBikeAsyncTask();
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5000, 10, new LocListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        lon = location.getLatitude();
                        lat = location.getLongitude();
                        //Toast.makeText(BikeActivity.this, "Current location: " + location.getLatitude() + " " + location.getLongitude(), Toast.LENGTH_SHORT).show();
                        getBikeAsyncTask();
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
    void getBikeAsyncTask(){
        LocalizationGetter localizationGetter = new LocalizationGetter();
        localizationGetter.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    private class LocalizationGetter extends AsyncTask<String, Integer, Void> {
    protected void onPreExecute(){
        super.onPreExecute();
    }
        @Override
        protected Void doInBackground(String... strings) {
            getBike("");
            return null;
        }
    protected void onPostExecute(Double lon,double lat) {
        Toast.makeText(BikeActivity.this, "Current location: " + lon + " " + lat, Toast.LENGTH_SHORT).show();
    }
    }
    public class JSONFromApiGetter extends AsyncTask<String,Integer, JSONObject>{
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
                JSONArray bikeArray = result.getJSONObject("result").
                        getJSONArray("featureMemberProperties");
                JSONArray coordinates = result.getJSONObject("result").
                        getJSONArray("featureMemberCoordinates");
                List<String> bikeList = new ArrayList<>();
                for (int i = 0; i < bikeArray.length(); i++) {

                    String latBike = coordinates.getJSONObject(i).getString("latitude");
                    String lonBike= coordinates.getJSONObject(i).getString("longitude");
                    String STOJAKI= bikeArray.getJSONObject(i).getString("STOJAKI");
                    String AKTU_DAN = bikeArray.getJSONObject(i).getString("AKTU_DAN");
                    String OBJECTID = bikeArray.getJSONObject(i).getString("OBJECTID");
                    String LOKALIZACJA = bikeArray.getJSONObject(i).getString("LOKALIZACJA");
                    String ROWERY = bikeArray.getJSONObject(i).getString("ROWERY");
                    String NR_STACJI = bikeArray.getJSONObject(i).getString("NR_STACJI");

                    bikeList.add(latBike +"\n"+ OBJECTID +"\n"+ LOKALIZACJA +"\n"+
                            NR_STACJI +"\n"+ ROWERY+"\n"+ STOJAKI +"\n"+AKTU_DAN +"\n"+ Double.valueOf(latBike) +"\n"+ Double.valueOf(lonBike));
                }
                listHash.put(EXPANDABLE_LIST_KEY, bikeList);
                listDataHeader = new ArrayList<>(listHash.keySet());
                Log.e(TAG," - getting bikeStations: " +bikeList);
                listAdapter.setExpandableLists(listHash, listDataHeader);
                listAdapter.notifyDataSetChanged();

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
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
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
}