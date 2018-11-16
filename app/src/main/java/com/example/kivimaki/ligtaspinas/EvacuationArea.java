package com.example.kivimaki.ligtaspinas;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

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
import com.google.android.gms.maps.GoogleMap;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EvacuationArea extends AppCompatActivity {

        ListView lv_eCenter;
        EvacuationAdapter eCenter_adapter;

        RequestQueue requestQueue;

        ArrayList<String> ID;
        ArrayList<String> LOCATION;
        ArrayList<String> DESCNAME;

        Handler loadDB;
        GoogleMap googleMap;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_evacuation_area);

            lv_eCenter = (ListView)findViewById(R.id.lv_ecenter);

            ID = new ArrayList<>();
            LOCATION = new ArrayList<>();
            DESCNAME = new ArrayList<>();

//            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

            Cache cache = new DiskBasedCache(getCacheDir(),1024*1024);
            Network network = new BasicNetwork(new HurlStack());
            requestQueue = new RequestQueue(cache,network);
            requestQueue.start();





            lv_eCenter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(getApplicationContext(), com.example.kivimaki.ligtaspinas.Map.class);
                    intent.putExtra("evac_id",ID.get(i));
                    startActivity(intent);
                }
            });

            new JSONEcenter().execute();
            loadDB = new Handler();
            loadDB.postDelayed(new Runnable() {
                public void run() {
                    ShowECEnter();
                    loadDB.postDelayed(this, 15000); //
                }
            }, 15000); //


        }



        ProgressDialog pd;
        public class JSONEcenter extends AsyncTask<Void,Void,Void> {

            @Override
            protected void onPreExecute(){
                pd = new ProgressDialog(EvacuationArea.this);
                pd.setMessage("Loading...");
                pd.setCancelable(false);
                pd.show();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                ShowECEnter();
                return null;
            }
        }
        public void ShowECEnter(){
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.urlECenterList,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            if (response.equals("")) {
                                new JSONEcenter().execute();
                                pd.dismiss();
                            } else if (!response.equals("")) {
                                pd.dismiss();
                            }

                            ID.clear();
                            LOCATION.clear();
                            DESCNAME.clear();

                            try {
                                JSONArray JA = new JSONArray(response);
                                for (int i = 0; i < JA.length(); i++) {
                                    JSONObject JO = JA.getJSONObject(i);

                                    ID.add(JO.getString("id"));
                                    LOCATION.add(JO.getString("description"));
                                    DESCNAME.add(JO.getString("location"));
                                }
                            } catch (Exception e) {
                                Toast.makeText(EvacuationArea.this, "" + e, Toast.LENGTH_SHORT).show();
                            }
                            eCenter_adapter = new EvacuationAdapter(EvacuationArea.this,
                                    ID, LOCATION, DESCNAME);

                            lv_eCenter.setAdapter(eCenter_adapter);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> getAllEvac = new HashMap<>();
                    getAllEvac.put("getAllEvac","getAllEvac");
                    return getAllEvac;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    -1,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(stringRequest);
        }

}
