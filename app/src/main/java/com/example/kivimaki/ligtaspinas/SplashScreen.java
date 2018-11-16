package com.example.kivimaki.ligtaspinas;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.appolica.flubber.Flubber;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class SplashScreen extends AppCompatActivity {

    public static int SPLASH_TIMEOUT = 2000;
    ImageView logoname;
    TextView txtConnectivity;

    RequestQueue requestQueue;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    android.os.Handler sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        txtConnectivity = (TextView) findViewById(R.id.txtConnectivity); //textview

        logoname = (ImageView) findViewById(R.id.logo1);

        Cache cache = new DiskBasedCache(getCacheDir(),1024*1024);
        Network network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache,network);
        requestQueue.start();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SplashScreen.this);
        editor = sharedPreferences.edit();

        stemail = sharedPreferences.getString("email","");
        stpass = sharedPreferences.getString("pass","");

        sp = new android.os.Handler();
        sp.postDelayed(new Runnable() {
            public void run() {
                Connectivity();
                sp.postDelayed(this, 500); //
            }
        }, SPLASH_TIMEOUT); //

    }

    String stemail = "",stpass = "";

    public void login(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.urlLogin,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("OK")) {
                            URLs.userApp = stemail;
                            editor.putString("email", stemail);
                            editor.commit();
                            Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(getApplicationContext(), Login.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> login = new HashMap<>();
                login.put("login","1");
                login.put("email",stemail);
                login.put("password",stpass);
                return login;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0,-1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }



    int load = 0;
    public void Connectivity() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        //For 3G check
        boolean is3g = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                .isConnectedOrConnecting();
        //For WiFi Check
        boolean isWifi = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .isConnectedOrConnecting();

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (!is3g && !isWifi) // DISABLED NETWORK
        {
            txtConnectivity.setVisibility(View.VISIBLE);
            txtConnectivity.setText("No Internet Connection");
            load = 0;

        } else //ENABLED NETWORK
        {
            txtConnectivity.setVisibility(View.GONE);

            if (load == 0) {
                load = 1;
            } else if (load == 1) {
                login();
                load = 2;
            }
        }
    }
}
