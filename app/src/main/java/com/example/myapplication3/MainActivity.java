package com.example.myapplication3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;

import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    double lati = 0.0;
    double longi = 0.0;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    String newurl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        TextView txt = findViewById(R.id.weatherTextView);
        Button btn_get_temp = findViewById(R.id.getTermperatureButton);
        EditText edt = findViewById(R.id.getLocationEdtTxt);
        RelativeLayout rl = findViewById(R.id.rlVar1);
        ImageView img_pokemon = findViewById(R.id.img_view_pokemon);
        ImageView kill_music = findViewById(R.id.kill_music_imageView);
        Button reset_btn =  findViewById(R.id.reset_button);
        Button current_location_button = findViewById(R.id.current_location_btn);
        Button nextActvityButton = findViewById(R.id.nextActvityButton);

        final Vibrator vibe = (Vibrator) MainActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.pokemon_soundtrack);

        String url_poke = "https://pokeapi.co/api/v2/pokemon?limit=151";

        RequestQueue mQueue;
        mQueue = Volley.newRequestQueue(this);

        RequestQueue water_Queue;
        water_Queue  = Volley.newRequestQueue(this);

        RequestQueue water_1_Queue;
        water_1_Queue = Volley.newRequestQueue(this);

        RequestQueue grass_Queue;
        grass_Queue  = Volley.newRequestQueue(this);

        RequestQueue grass_1_Queue;
        grass_1_Queue = Volley.newRequestQueue(this);

        RequestQueue fire_Queue;
        fire_Queue  = Volley.newRequestQueue(this);

        RequestQueue fire_1_Queue;
        fire_1_Queue  = Volley.newRequestQueue(this);

        rl.setBackgroundColor(Color.WHITE);

        img_pokemon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://en.wikipedia.org/wiki/Pokémon_(video_game_series)"));
                startActivity(intent);
            }
        });

            edt.setBackgroundResource(R.color.grey);

        kill_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.pause();
                vibe.vibrate(80);
            }
        });


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
                        Toast.makeText(MainActivity.this, "Couldn't fetch location", Toast.LENGTH_LONG).show();
                    }
                });

            }else
            {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }


        }
        else
        {
            txt.setText("Permission Granted");
        }

        reset_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt.setText("0");
                rl.setBackgroundResource(R.color.white);
                btn_get_temp.setBackgroundResource(R.color.pink_button);
                current_location_button.setBackgroundResource(R.color.pink_button);
                btn_get_temp.setTextColor(getResources().getColor(R.color.black));
                current_location_button.setTextColor(getResources().getColor(R.color.black));
                edt.setTextColor(getResources().getColor(R.color.black));
                edt.setBackgroundResource(R.color.grey);
                txt.setTextColor(getResources().getColor(R.color.black));
                img_pokemon.setImageResource(R.drawable.harmoine_cartoon_character_pokemon_128);
                vibe.vibrate(80);
                mp.pause();
                edt.setText("");
            }
        });

        btn_get_temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTemp();
            }

            private void changeColor(double temp) {

                if((int)temp <= 20) {
                    rl.setBackgroundResource(R.color.blue);
                    getImage_blue();
                }
                if(temp > 20.0 && temp < 35) {
                    rl.setBackgroundResource(R.color.green);
                    getImage_green();
                }
                if(temp >= 35) {
                    getImage_red();
                    rl.setBackgroundResource(R.color.orange);
                }
                btn_get_temp.setBackgroundColor(Color.WHITE);
                current_location_button.setBackgroundColor(Color.WHITE);
                edt.setTextColor(getResources().getColor(R.color.white));
                txt.setTextColor(getResources().getColor(R.color.white));
            }

            private void getImage_red() {

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url_poke, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        final String[][] url_pokemon = new String[1][1];

                        try {

                            JSONArray objm = response.getJSONArray("results");

                            url_pokemon[0] = new String[151];

                            for(int i  = 0; i < 151; i++ ) {

                                JSONObject obj = objm.getJSONObject(i);

                                url_pokemon[0][i] = obj.getString("url");

                                // Log.d("poke_test", "pokemon url = "+obj.getString("url"));

                            }

                            for(int i = 0; i < 151 ; i++)
                            {
                                String s = url_pokemon[0][i];

                                JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(Request.Method.GET, s,null, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {



                                            JSONArray arr_obj = response.getJSONArray("types");

                                            JSONObject obj1 = arr_obj.getJSONObject(0);

                                            JSONObject obj2 = obj1.getJSONObject("type");

                                            String type = obj2.getString("name");


                                            if(type.equalsIgnoreCase("fire") == true ) {

                                                JSONObject obj_p = response.getJSONObject("sprites");

                                                String url_image = obj_p.getString("front_default");

                                                Picasso.get().load(url_image).resize(600,600).into(img_pokemon);

                                                //Log.d("alkazam12", "response = "+url_image);

                                            }



                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d("alkazam","lolla error ");

                                    }
                                });

                                fire_1_Queue.add(jsonObjectRequest1);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d("pokemon_tes", "error of pokemon");

                    }
                });



                fire_Queue.add(jsonObjectRequest);

            }

            private void getImage_green() {
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url_poke, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        final String[][] url_pokemon = new String[1][1];

                        try {

                            JSONArray objm = response.getJSONArray("results");

                            url_pokemon[0] = new String[151];

                            for(int i  = 0; i < 151; i++ ) {

                                JSONObject obj = objm.getJSONObject(i);

                                url_pokemon[0][i] = obj.getString("url");

                                // Log.d("poke_test", "pokemon url = "+obj.getString("url"));

                            }

                            for(int i = 0; i < 151 ; i++)
                            {
                                String s = url_pokemon[0][i];

                                JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(Request.Method.GET, s,null, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {



                                            JSONArray arr_obj = response.getJSONArray("types");

                                            JSONObject obj1 = arr_obj.getJSONObject(0);

                                            JSONObject obj2 = obj1.getJSONObject("type");

                                            String type = obj2.getString("name");


                                            if(type.equalsIgnoreCase("grass") == true ) {

                                                JSONObject obj_p = response.getJSONObject("sprites");

                                                String url_image = obj_p.getString("front_default");

                                                Picasso.get().load(url_image).resize(600,600).into(img_pokemon);

                                               // Log.d("alkazam12", "response = "+url_image);

                                            }



                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d("alkazam","lolla error ");

                                    }
                                });

                                grass_1_Queue.add(jsonObjectRequest1);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d("pokemon_tes", "error of pokemon");

                    }
                });



                grass_Queue.add(jsonObjectRequest);


            }

            private void getImage_blue() {
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url_poke, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        final String[][] url_pokemon = new String[1][1];

                        try {

                            JSONArray objm = response.getJSONArray("results");

                            url_pokemon[0] = new String[151];

                            for(int i  = 0; i < 151; i++ ) {

                                JSONObject obj = objm.getJSONObject(i);

                                url_pokemon[0][i] = obj.getString("url");

                                // Log.d("poke_test", "pokemon url = "+obj.getString("url"));

                            }

                            for(int i = 0; i < 151 ; i++)
                            {
                                String s = url_pokemon[0][i];

                                JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(Request.Method.GET, s,null, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {



                                            JSONArray arr_obj = response.getJSONArray("types");

                                            JSONObject obj1 = arr_obj.getJSONObject(0);

                                            JSONObject obj2 = obj1.getJSONObject("type");

                                            String type = obj2.getString("name");


                                            if(type.equalsIgnoreCase("water") == true ) {

                                                JSONObject obj_p = response.getJSONObject("sprites");

                                                String url_image = obj_p.getString("front_default");

                                                Picasso.get().load(url_image).resize(600,600).into(img_pokemon);

                                                //Log.d("water_pokemon", "water_pokemon_url = "+url_image);

                                            }



                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d("alkazam","lolla error ");

                                    }
                                });

                                water_1_Queue.add(jsonObjectRequest1);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d("pokemon_tes", "error of pokemon");

                    }
                });
                water_Queue.add(jsonObjectRequest);

            }




            private void getTemp() {

                String s = String.valueOf(edt.getText());
                if(s.isEmpty() == false) {
                    mp.start();
                    mp.setLooping(true);
                    vibe.vibrate(80);
                }
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
                    //Log.d("myapp", "this is the string"+p1);
                String s1 = p1;

                String newurl = "https://api.weatherbit.io/v2.0/current?&city=" + s1 + "&key=e347dbc91f12441eb152f766b00b17be";

                JsonObjectRequest Jrequest = new JsonObjectRequest(Request.Method.GET, newurl, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONArray objm = response.getJSONArray("data");

                            JSONObject obj = objm.getJSONObject(0);

                            //Log.d("myapp", "reposen is : " + obj.getDouble("temp"));

                            Double temp = obj.getDouble("temp");

                            txt.setText(Double.toString(temp)+"℃");

                            changeColor(temp);

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
                        mp.stop();
                    }
                });

                mQueue.add(Jrequest);

            }
        });
        current_location_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentTemp();
            }

            private void changeColor(double temp) {

                if((int)temp <= 20) {
                    rl.setBackgroundResource(R.color.blue);
                    getImage_blue();
                }
                if(temp > 20.0 && temp < 35) {
                    rl.setBackgroundResource(R.color.green);
                    getImage_green();
                }
                if(temp >= 35) {
                    getImage_red();
                    rl.setBackgroundResource(R.color.orange);
                }
                btn_get_temp.setBackgroundColor(Color.WHITE);
                current_location_button.setBackgroundColor(Color.WHITE);
                edt.setTextColor(getResources().getColor(R.color.white));
                txt.setTextColor(getResources().getColor(R.color.white));
            }

            private void getImage_red() {

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url_poke, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        final String[][] url_pokemon = new String[1][1];

                        try {

                            JSONArray objm = response.getJSONArray("results");

                            url_pokemon[0] = new String[151];

                            for(int i  = 0; i < 151; i++ ) {

                                JSONObject obj = objm.getJSONObject(i);

                                url_pokemon[0][i] = obj.getString("url");

                                // Log.d("poke_test", "pokemon url = "+obj.getString("url"));

                            }

                            for(int i = 0; i < 151 ; i++)
                            {
                                String s = url_pokemon[0][i];

                                JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(Request.Method.GET, s,null, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {



                                            JSONArray arr_obj = response.getJSONArray("types");

                                            JSONObject obj1 = arr_obj.getJSONObject(0);

                                            JSONObject obj2 = obj1.getJSONObject("type");

                                            String type = obj2.getString("name");


                                            if(type.equalsIgnoreCase("fire") == true ) {

                                                JSONObject obj_p = response.getJSONObject("sprites");

                                                String url_image = obj_p.getString("front_default");

                                                Picasso.get().load(url_image).resize(600,600).into(img_pokemon);

                                                //Log.d("alkazam12", "response = "+url_image);

                                            }



                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d("alkazam","lolla error ");

                                    }
                                });

                                fire_1_Queue.add(jsonObjectRequest1);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d("pokemon_tes", "error of pokemon");

                    }
                });



                fire_Queue.add(jsonObjectRequest);

            }

            private void getImage_green() {
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url_poke, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        final String[][] url_pokemon = new String[1][1];

                        try {

                            JSONArray objm = response.getJSONArray("results");

                            url_pokemon[0] = new String[151];

                            for(int i  = 0; i < 151; i++ ) {

                                JSONObject obj = objm.getJSONObject(i);

                                url_pokemon[0][i] = obj.getString("url");

                                // Log.d("poke_test", "pokemon url = "+obj.getString("url"));

                            }

                            for(int i = 0; i < 151 ; i++)
                            {
                                String s = url_pokemon[0][i];

                                JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(Request.Method.GET, s,null, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {



                                            JSONArray arr_obj = response.getJSONArray("types");

                                            JSONObject obj1 = arr_obj.getJSONObject(0);

                                            JSONObject obj2 = obj1.getJSONObject("type");

                                            String type = obj2.getString("name");


                                            if(type.equalsIgnoreCase("grass") == true ) {

                                                JSONObject obj_p = response.getJSONObject("sprites");

                                                String url_image = obj_p.getString("front_default");

                                                Picasso.get().load(url_image).resize(600,600).into(img_pokemon);

                                                // Log.d("alkazam12", "response = "+url_image);

                                            }



                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d("alkazam","lolla error ");

                                    }
                                });

                                grass_1_Queue.add(jsonObjectRequest1);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d("pokemon_tes", "error of pokemon");

                    }
                });



                grass_Queue.add(jsonObjectRequest);


            }

            private void getImage_blue() {
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url_poke, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        final String[][] url_pokemon = new String[1][1];

                        try {

                            JSONArray objm = response.getJSONArray("results");

                            url_pokemon[0] = new String[151];

                            for(int i  = 0; i < 151; i++ ) {

                                JSONObject obj = objm.getJSONObject(i);

                                url_pokemon[0][i] = obj.getString("url");

                                // Log.d("poke_test", "pokemon url = "+obj.getString("url"));

                            }

                            for(int i = 0; i < 151 ; i++)
                            {
                                String s = url_pokemon[0][i];

                                JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(Request.Method.GET, s,null, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {



                                            JSONArray arr_obj = response.getJSONArray("types");

                                            JSONObject obj1 = arr_obj.getJSONObject(0);

                                            JSONObject obj2 = obj1.getJSONObject("type");

                                            String type = obj2.getString("name");


                                            if(type.equalsIgnoreCase("water") == true ) {

                                                JSONObject obj_p = response.getJSONObject("sprites");

                                                String url_image = obj_p.getString("front_default");

                                                Picasso.get().load(url_image).resize(600,600).into(img_pokemon);

                                                //Log.d("water_pokemon", "water_pokemon_url = "+url_image);

                                            }



                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d("alkazam","lolla error ");

                                    }
                                });

                                water_1_Queue.add(jsonObjectRequest1);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d("pokemon_tes", "error of pokemon");

                    }
                });
                water_Queue.add(jsonObjectRequest);

            }




            private void getCurrentTemp() {

                vibe.vibrate(80);
                mp.start();
                String latio = "lat="+String.valueOf(lati);
                String longio = "&lon="+String.valueOf(longi);
                String location = latio+longio;
                String apiKey = "&key=e347dbc91f12441eb152f766b00b17be";

                 newurl = "https://api.weatherbit.io/v2.0/current?"+location+apiKey;

                JsonObjectRequest Jrequest = new JsonObjectRequest(Request.Method.GET, newurl, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONArray objm = response.getJSONArray("data");

                            JSONObject obj = objm.getJSONObject(0);

                            //Log.d("myapp", "reposen is : " + obj.getDouble("temp"));

                            Double temp = obj.getDouble("temp");

                            txt.setText(Double.toString(temp)+"℃");

                            Toast.makeText(MainActivity.this, "City: "+obj.getString("city_name").toUpperCase(), Toast.LENGTH_LONG).show();

                            changeColor(temp);

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
                        mp.stop();
                    }
                });

                mQueue.add(Jrequest);

            }
        });

        nextActvityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent newIntent = new  Intent(MainActivity.this, MoreInfoMainActivity.class);
                newIntent.putExtra("arg", String.valueOf(edt.getText()));
                startActivity(newIntent);
            }
        });


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(MainActivity.this, "Permission NOT GRANTED", Toast.LENGTH_LONG).show();
            }
        }



    }
}