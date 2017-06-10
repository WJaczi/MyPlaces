package com.example.android.myplaces;


import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;

import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by weronika on 10.06.17.
 */
public class MySMSActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {





    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);


        new SMSActivity().execute();

    }




    public class SMSActivity extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MySMSActivity.this, "Wysyłanie sms", Toast.LENGTH_LONG).show();
        }

        @Override
        protected String doInBackground(String... arg0) {
            StringBuilder result = new StringBuilder();;
            try {

                URL apiUrlObject = null;
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
                        + "&msg=Czesc!Jestem w poblizu hospotow!&deliverystatus=true&apikey" +
                        "=" + Helper.getConfigValue(MySMSActivity.this, "api_orange_key");






                apiUrlObject = new URL(urlToSend);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                apiUrlObject.openStream()));
                String inputLine = "";

                while ((inputLine = in.readLine()) != null) {
                    result.append(inputLine + "  " + number + "\n");
                }



                ;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }



            return result.toString();
        }



        protected void onPostExecute(String result) {
            Toast.makeText(MySMSActivity.this, result, Toast.LENGTH_LONG).show();
        }



    }
}

