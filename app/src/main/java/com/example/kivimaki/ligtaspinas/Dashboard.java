package com.example.kivimaki.ligtaspinas;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    View mHeaderView;
    TextView txtName,txtEmail,txtConnectivity,wlocation,wtemp,whumidity,wwindspeed,wdesc;

    LinearLayout weatherinfo,mybrgy,mycity;

    int WiFi=0,LoadJSON=0;

    RequestQueue requestQueue;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    Handler checkWifi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        DrawerLayout drawerLayout = findViewById(R.id.drawerlayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView = findViewById(R.id.navview);

        mHeaderView = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);

        Cache cache = new DiskBasedCache(getCacheDir(), 1024*1024);
        com.android.volley.Network network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache,network);
        requestQueue.start();


        weatherinfo = (LinearLayout)findViewById(R.id.weatherinfo);
        mybrgy = (LinearLayout)findViewById(R.id.mybrgylinear);
        mycity = (LinearLayout)findViewById(R.id.mycitylinear);
        txtConnectivity = (TextView) findViewById(R.id.txtConnectivity);

        wlocation = (TextView) findViewById(R.id.wlocation);
        wtemp = (TextView) findViewById(R.id.wtemp);
        whumidity = (TextView) findViewById(R.id.whumidity);
        wwindspeed = (TextView) findViewById(R.id.wwindspeed);
        wdesc = (TextView) findViewById(R.id.wdesc);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Dashboard.this);
        editor = sharedPreferences.edit();

        mybrgy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MyBrgy.class);
                startActivity(intent);
            }
        });

        mycity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MyCity.class);
                startActivity(intent);
            }
        });

        new getWeatherAPI().execute();

        checkWifi = new Handler();
        checkWifi.postDelayed(new Runnable() {
            public void run() {
                Connectivity();
                WeatherAPI();
                checkWifi.postDelayed(this, 1500);
            }
        }, 1500);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.home){
            Toast.makeText(Dashboard.this, "You clicked Home!", Toast.LENGTH_SHORT).show();
        }

        if(id == R.id.out){
            logout();
        }
        return false;
    }

    ProgressDialog pd;
    public class getWeatherAPI extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onPreExecute(){
            pd = new ProgressDialog(Dashboard.this);
            pd.setCancelable(false);
            pd.setMessage("Loading...");
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            WeatherAPI();
            return null;
        }
    }

    public void Connectivity(){
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

//        For 3G check
        boolean is3g = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                .isConnectedOrConnecting();
//        For WiFi Check
        boolean isWifi = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .isConnectedOrConnecting();

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);


        if (!is3g && !isWifi) // DISABLED NETWORK
        {
            pd.dismiss();
            txtConnectivity.setVisibility(View.VISIBLE);
            weatherinfo.setVisibility(View.GONE);
            txtConnectivity.setText("No Internet Connection");
            WiFi = 0;
            LoadJSON = 0;


        }
        else // ENABLED NETWORK
        {
            weatherinfo.setVisibility(View.VISIBLE);
            txtConnectivity.setVisibility(View.GONE);
            pd.dismiss();
            WiFi = 1;


        }
    }

    public void WeatherAPI(){
        StringRequest getApi = new StringRequest(Request.Method.POST, URLs.urlGetAPI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(!response.equals("")){
                            pd.dismiss();

                        }else{
                            new getWeatherAPI().execute();
                            pd.dismiss();

                        }

                        String json = "["+response+"]";
                        try {
                            JSONArray JA = new JSONArray(json);
                            for (int ja = 0; ja < JA.length(); ja++) {
                                JSONObject JO = JA.getJSONObject(ja);

                                //main
                                JSONArray JA2 = new JSONArray("["+JO.getString("main") +"]");
                                for (int ja2 = 0; ja2 < JA2.length(); ja2++){
                                    JSONObject JO2 = JA2.getJSONObject(ja2);

                                    double temp = Double.parseDouble(JO2.getString("temp"));
                                    temp = temp - 273.15;
                                    int intTemp = (int) temp;
                                    wtemp.setText("Temp : "+intTemp +"°C");
                                }

                               //wind
                                JSONArray JA3 = new JSONArray("["+JO.getString("wind") +"]");
                                for (int ja3 = 0; ja3 < JA3.length(); ja3++){
                                    JSONObject JO3 = JA3.getJSONObject(ja3);

                                    double wind = Double.parseDouble(JO3.getString("speed"));
                                    wwindspeed.setText("Wind "+ wind +" ms");
                                }
                               // clouds
                                JSONArray JA4 = new JSONArray("["+JO.getString("clouds") +"]");
                                for (int ja4 = 0; ja4 < JA4.length();
                                ja4++){
                                    JSONObject JO4 = JA4.getJSONObject(ja4);
                                    whumidity.setText("Humidity " + JO4.getString("all") + "%");
                                }
                               // weather description
                                JSONArray JA5 = new JSONArray(JO.getString("weather"));
                                for (int ja5 = 0; ja5 < JA5.length();
                                ja5++){
                                    JSONObject JO5 = JA5.getJSONObject(ja5);
                                    wdesc.setText("Weather Status :\n\""+JO5.getString("description") +"\"");
                                }

                                // location

                                    wlocation.setText(JO.getString("name")+", PH");


                            }
                        }
                        catch (Exception e){

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        getApi.setRetryPolicy(new DefaultRetryPolicy( 0,-1,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(getApi);

    }

    @Override
    public void onBackPressed() {

        logout();

    }

    public void logout(){
        AlertDialog.Builder ab = new AlertDialog.Builder(Dashboard.this);
        ab.setCancelable(false);
        ab.setTitle("Logout?");
        ab.setMessage("Do you want to logout?");
        ab.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                editor.putString("email","");
                editor.putString("pass","");
                editor.commit();
                Intent intent = new Intent(getApplicationContext(),Login.class);
                startActivity(intent);
                finish();
            }
        });

        ab.setNegativeButton("No",null);
        ab.show();
    }
}

