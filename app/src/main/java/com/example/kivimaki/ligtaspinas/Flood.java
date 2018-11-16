package com.example.kivimaki.ligtaspinas;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class Flood extends AppCompatActivity {

        ListView lview;
        TextView lastUpdated;
        FloodAdapter waterLvlAdapter;

        ArrayList<String> ID;
        ArrayList<String> STATION;
        ArrayList<String> WATERLVL;
        ArrayList<String> CRITICALLVL;
        ArrayList<String> TIME;
        ArrayList<String> DATE;

        Handler loadDB;
        RequestQueue requestQueue;

        int WiFi = 0;

        @SuppressLint("RestrictedApi")
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_flood);

            lview = (ListView)findViewById(R.id.lview);
            lastUpdated = (TextView)findViewById(R.id.lastUpdated);


            ID = new ArrayList<String>();
            STATION = new ArrayList<String>();
            WATERLVL = new ArrayList<String>();
            CRITICALLVL = new ArrayList<String>();
            TIME = new ArrayList<String>();
            DATE = new ArrayList<String>();

            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

            Cache cache = new DiskBasedCache(getCacheDir(),1024*1024);
            com.android.volley.Network network = new BasicNetwork(new HurlStack());
            requestQueue = new RequestQueue(cache,network);
            requestQueue.start();

            new JSONmain().execute();

            //Update dbmain after 2 mins
            loadDB = new Handler();
            loadDB.postDelayed(new Runnable() {
                public void run() {
                    Connectivity();
                    loadDB.postDelayed(this, 500); //
                }
            }, 500); //

            lview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Intent selectedItem = new Intent(getApplicationContext(), FloodClicked.class);
                    selectedItem.putExtra("itemID", ID.get(position).toString());
                    startActivity(selectedItem);

                }
            });



        }

        public void Connectivity(){
            ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

            //For 3G check
            boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                    .isConnectedOrConnecting();
            //For WiFi Check
            boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                    .isConnectedOrConnecting();


            if (!is3g && !isWifi) // DISABLED NETWORK
            {
                Toasty.error(Flood.this,"No Internet Connection", Toast.LENGTH_LONG).show();
                WiFi = 0;

                Intent intent = new Intent(Flood.this,FloodClicked.class);
                startActivity(intent);
                finish();

            }
            else //ENABLED NETWORK
            {
                WiFi = 1;
                ShowMain();

            }
        }

        ProgressDialog pd;
        public class JSONmain extends AsyncTask<Void,Void,Void> {


            @Override
            protected void onPreExecute(){
                pd = new ProgressDialog(Flood.this);
                pd.setMessage("Loading...");
                pd.setCancelable(false);
                pd.show();
            }

            @Override
            protected Void doInBackground(Void... voids) {

                ShowMain();

                return null;
            }

        }

        public void ShowMain(){
            StringRequest getMain = new StringRequest(Request.Method.POST, URLs.urlMain,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            if (response.equals("")) {
                                new JSONmain().execute();
                                pd.dismiss();
                            } else if (!response.equals("")) {
                                pd.dismiss();
                            }

                            ID.clear();
                            STATION.clear();
                            WATERLVL.clear();
                            CRITICALLVL.clear();
                            TIME.clear();
                            DATE.clear();

                            String LastUpdate = "";
                            try {

                                JSONArray JA = new JSONArray(response);
                                for (int i = 0; i < JA.length(); i++) {
                                    JSONObject JO = JA.getJSONObject(i);

                                    ID.add(JO.getString("id"));
                                    STATION.add(JO.getString("name"));
                                    WATERLVL.add("" + JO.getString("water_lvl"));
                                    CRITICALLVL.add("" + JO.getString("critical_lvl"));
                                    TIME.add("" + JO.getString("time"));
                                    DATE.add("" + JO.getString("month") + " " + JO.getString("day") + " " + JO.getString("year"));

                                    LastUpdate = "Last Update : " + JO.getString("month") + "/" + JO.getString("day") + "/" + JO.getString("year") + " (" + JO.getString("time") + " " + JO.getString("ampm") + ")";

                                }
                                lastUpdated.setText("" + LastUpdate);

                            } catch (Exception e) {
//                               Toast.makeText(WaterLvlForecast.this, ""+e, Toast.LENGTH_SHORT).show();
                            }

                            int lastViewedPosition = lview.getFirstVisiblePosition();

                            //get offset of first visible view
                            View v = lview.getChildAt(0);
                            int topOffset = (v == null) ? 0 : v.getTop();

                            waterLvlAdapter = new FloodAdapter(Flood.this,
                                    ID, STATION, WATERLVL, CRITICALLVL);

                            lview.setAdapter(waterLvlAdapter);

                            lview.setSelectionFromTop(lastViewedPosition, topOffset);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }
        ){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> getMain = new HashMap<>();
                    getMain.put("getMain","1");

                    return getMain;
                }
            };

            getMain.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    -1,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(getMain);

        }


    }