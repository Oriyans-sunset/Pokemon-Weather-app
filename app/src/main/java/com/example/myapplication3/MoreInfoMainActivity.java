package com.example.myapplication3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MoreInfoMainActivity extends AppCompatActivity {

    String url = "";
    Double lati = 0.0;
    Double longi = 0.0;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info_main2);

        RequestQueue mQueue;
        mQueue = Volley.newRequestQueue(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        String s = getIntent().getExtras().getString("arg");
        String p = "";
        s = s.trim();
        s = s + " ";
        char ch = 'e';
        String p1 = "";
        for(int i = 0; i < s.length(); i++) {
            ch = s.charAt(i);
            if (ch != ' ')
                p = p + (char)ch;
            else {
                p1 = p1 + p;
                p = "";
            }
        }
        String city = p1;


        TextView txt_description = findViewById(R.id.WeatherDesccriptionTextView);
        TextView txt_temp = findViewById(R.id.TempTextView);
        ImageView img_weather_description = findViewById(R.id.WeatherDescriptionImageView);
        TextView txt_humidity = findViewById(R.id.HumididtyTextView);
        TextView txt_precip = findViewById(R.id.PrecipitationTextView);
        TextView txt_sunrise = findViewById(R.id.SunRiseTextView);
        TextView txt_sunset = findViewById(R.id.SunSetTextView);
        Button current_weather_location = findViewById(R.id.CurrentLocationWeatherButton);
        Button reset_button = findViewById(R.id.reset_activity2_button);
        Button go_back_button = findViewById(R.id.goBack_button);
        RelativeLayout rl = findViewById(R.id.relative_layout);

        final Vibrator vibe = (Vibrator) MoreInfoMainActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
        rl.setBackgroundColor(Color.WHITE);
        go_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(MoreInfoMainActivity.this, MainActivity.class);
                startActivity(newIntent);

            }
        });
        reset_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_temp.setText("TEMPERATURE:");
                txt_temp.setBackgroundResource(R.color.purple_200);
                txt_description.setText("DESCRIPTION:");
                txt_humidity.setText("HUMIDITY:");
                txt_precip.setText("PRECIPIATTION:");
                txt_sunrise.setText("SUNRISE:");
                txt_sunset.setText("SUNSET:");
                rl.setBackgroundColor(Color.WHITE);
                vibe.vibrate(80);
            }
        });


     current_weather_location.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {

             vibe.vibrate(80);

             if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                 if(getApplicationContext().checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                 {

                     fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                         @Override
                         public void onSuccess(Location location) {


                             lati = location.getLatitude();
                             longi = location.getLongitude();

                         }
                     }).addOnFailureListener(new OnFailureListener() {
                         @Override
                         public void onFailure(@NonNull Exception e) {
                             Toast.makeText(MoreInfoMainActivity.this, "Couldn't fetch location", Toast.LENGTH_LONG).show();
                         }
                     });

                 }else
                 {
                     requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                 }


             }
             else
             {
                 Toast.makeText(MoreInfoMainActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();
             }
             String latio = "lat="+String.valueOf(lati);
             String longio = "&lon="+String.valueOf(longi);
             String location = latio+longio;
             String apiKey = "&key=e347dbc91f12441eb152f766b00b17be";

             url = "https://api.weatherbit.io/v2.0/current?"+location+apiKey;

             JsonObjectRequest Jrequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                 @Override
                 public void onResponse(JSONObject response) {
                     try {

                         JSONArray objm = response.getJSONArray("data");

                         JSONObject obj = objm.getJSONObject(0);

                         Toast.makeText(MoreInfoMainActivity.this,"Weather for "+obj.getString("city_name").toUpperCase(), Toast.LENGTH_LONG ).show();

                         Double temp = obj.getDouble("temp");
                         if(temp <= 20.0) {
                             txt_temp.setBackgroundResource(R.color.blue);
                         }
                         if(temp > 20.0 && temp < 35.0) {
                             txt_temp.setBackgroundResource(R.color.green);
                         }
                         if(temp >= 35.0) {
                             txt_temp.setBackgroundResource(R.color.orange);
                         }

                         txt_temp.setText(Double.toString(temp)+"℃");

                         JSONObject objw =  obj.getJSONObject("weather");

                         int code = objw.getInt("code");
                         String description = objw.getString("description");
                         txt_description.setText(description);
                         if( code == 200 || code == 201|| code == 230|| code == 231){
                             img_weather_description.setImageDrawable(getResources().getDrawable(R.drawable.flash_cloud_128));
                         }
                         if(code == 202 || code == 232 || code == 233){
                             img_weather_description.setImageDrawable(getResources().getDrawable(R.drawable.flash_128));

                         }
                         if(code == 300 || code == 301 || code == 500 || code == 520){
                             img_weather_description.setImageDrawable(getResources().getDrawable(R.drawable.rain_cloud_128));

                         }
                         if(code == 302 || code == 501 || code == 502 || code == 521 || code == 522){
                             img_weather_description.setImageDrawable(getResources().getDrawable(R.drawable.rain_128));

                         }
                         if(code == 511){
                             img_weather_description.setImageDrawable(getResources().getDrawable(R.drawable.snow_cloud_128));

                         }
                         if(code == 600 || code == 601 || code == 610 || code == 611){
                             img_weather_description.setImageDrawable(getResources().getDrawable(R.drawable.snow_cloud_128));

                         }
                         if(code == 602 ||code == 612 ||code == 621 ||code == 622||code == 623){
                             img_weather_description.setImageDrawable(getResources().getDrawable(R.drawable.snow_128));

                         }
                         if(code >= 700 && code <= 755){
                             img_weather_description.setImageDrawable(getResources().getDrawable(R.drawable.cloud_128));

                         }
                         if(code == 800 ){
                             img_weather_description.setImageDrawable(getResources().getDrawable(R.drawable.sun_128));

                         }
                         if( code == 801 || code == 802 ){
                             img_weather_description.setImageDrawable(getResources().getDrawable(R.drawable.cloudy_128));

                         }
                         if(code == 803 || code == 804){
                             img_weather_description.setImageDrawable(getResources().getDrawable(R.drawable.cloud_128));

                         }
                         if(code == 900){
                             img_weather_description.setImageDrawable(getResources().getDrawable(R.drawable.rain_128));

                         }
                         String relative_humidity = obj.getString("rh");
                         txt_humidity.setText(relative_humidity+"%"+" humid");

                         String precipitation = obj.getString("precip");
                         txt_precip.setText(precipitation+" mm/hr");

                         String sunrise = obj.getString("sunrise");
                         txt_sunrise.setText(sunrise);

                         String sunset = obj.getString("sunset");

                         txt_sunset.setText(sunset);


                     } catch (JSONException e) {
                         e.printStackTrace();
                     }
                 }
             }, new Response.ErrorListener() {
                 @Override
                 public void onErrorResponse(VolleyError error) {
                     Log.d("myapp", "error has occured");
                     Toast.makeText(getApplicationContext(), "Error! Please try again later.",
                             Toast.LENGTH_LONG).show();
                 }
             });

             mQueue.add(Jrequest);

         }
     });

         url = "https://api.weatherbit.io/v2.0/current?&city="+city+"&key=e347dbc91f12441eb152f766b00b17be";


        JsonObjectRequest Jrequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONArray objm = response.getJSONArray("data");

                    JSONObject obj = objm.getJSONObject(0);

                    Toast.makeText(MoreInfoMainActivity.this,"Weather for "+obj.getString("city_name").toUpperCase(), Toast.LENGTH_LONG ).show();

                    Double temp = obj.getDouble("temp");
                    if(temp <= 20.0) {
                       txt_temp.setBackgroundResource(R.color.blue);
                    }
                    if(temp > 20.0 && temp < 35.0) {
                        txt_temp.setBackgroundResource(R.color.green);
                    }
                    if(temp >= 35.0) {
                        txt_temp.setBackgroundResource(R.color.orange);
                    }

                    txt_temp.setText(Double.toString(temp)+"℃");

                    JSONObject objw =  obj.getJSONObject("weather");

                    int code = objw.getInt("code");
                    String description = objw.getString("description");
                    txt_description.setText(description);
                    if( code == 200 || code == 201|| code == 230|| code == 231){
                        img_weather_description.setImageDrawable(getResources().getDrawable(R.drawable.flash_cloud_128));
                    }
                    if(code == 202 || code == 232 || code == 233){
                        img_weather_description.setImageDrawable(getResources().getDrawable(R.drawable.flash_128));

                    }
                    if(code == 300 || code == 301 || code == 500 || code == 520){
                        img_weather_description.setImageDrawable(getResources().getDrawable(R.drawable.rain_cloud_128));

                    }
                    if(code == 302 || code == 501 || code == 502 || code == 521 || code == 522){
                        img_weather_description.setImageDrawable(getResources().getDrawable(R.drawable.rain_128));

                    }
                    if(code == 511){
                        img_weather_description.setImageDrawable(getResources().getDrawable(R.drawable.snow_cloud_128));

                    }
                    if(code == 600 || code == 601 || code == 610 || code == 611){
                        img_weather_description.setImageDrawable(getResources().getDrawable(R.drawable.snow_cloud_128));

                    }
                    if(code == 602 ||code == 612 ||code == 621 ||code == 622||code == 623){
                        img_weather_description.setImageDrawable(getResources().getDrawable(R.drawable.snow_128));

                    }
                    if(code >= 700 && code <= 755){
                        img_weather_description.setImageDrawable(getResources().getDrawable(R.drawable.cloud_128));

                    }
                    if(code == 800 ){
                        img_weather_description.setImageDrawable(getResources().getDrawable(R.drawable.sun_128));

                    }
                    if( code == 801 || code == 802 ){
                        img_weather_description.setImageDrawable(getResources().getDrawable(R.drawable.cloudy_128));

                    }
                    if(code == 803 || code == 804){
                        img_weather_description.setImageDrawable(getResources().getDrawable(R.drawable.cloud_128));

                    }
                    if(code == 900){
                        img_weather_description.setImageDrawable(getResources().getDrawable(R.drawable.rain_128));

                    }
                    String relative_humidity = obj.getString("rh");
                    txt_humidity.setText(relative_humidity+"%"+" humid");

                    String precipitation = obj.getString("precip");
                    txt_precip.setText(precipitation+" mm/hr");

                    String sunrise = obj.getString("sunrise");
                    txt_sunrise.setText(sunrise);

                    String sunset = obj.getString("sunset");
                    txt_sunset.setText(sunset);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("myapp", "error has occured");
                Toast.makeText(getApplicationContext(), "Error! Please try again.",
                        Toast.LENGTH_LONG).show();
            }
        });

        mQueue.add(Jrequest);

    }
}