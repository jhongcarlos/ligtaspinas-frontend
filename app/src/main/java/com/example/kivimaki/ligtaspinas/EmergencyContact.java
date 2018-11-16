package com.example.kivimaki.ligtaspinas;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

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

public class EmergencyContact extends AppCompatActivity {
    public class emergencycontacts_activity extends AppCompatActivity {

        ListView lv_emg;
        EmergencyContactAdapter emer_adapt;

        ArrayList<String> id;
        ArrayList<String> cname;
        ArrayList<String> cno;
        ArrayList<String> category;

        RequestQueue requestQueue;
        Handler loadDB;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_emergency_contact);


            lv_emg = (ListView) findViewById(R.id.lv_emg);

            id = new ArrayList<String>();
            cname = new ArrayList<String>();
            cno = new ArrayList<String>();
            category = new ArrayList<String>();

//            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

            Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
            com.android.volley.Network network = new BasicNetwork(new HurlStack());
            requestQueue = new RequestQueue(cache, network);
            requestQueue.start();


            new JSONemc().execute();

            //Update dbmain after 2 mins
            loadDB = new Handler();
            loadDB.postDelayed(new Runnable() {
                public void run() {
                    LoadEMC();
                    loadDB.postDelayed(this, 15000); //
                }
            }, 15000); //

            lv_emg.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:"+cno.get(position)));
                    startActivity(intent);
                }
            });


        }

        ProgressDialog pd;
        public class JSONemc extends AsyncTask<Void,Void,Void> {

            @Override
            protected void onPreExecute()
            {
                pd = new ProgressDialog(emergencycontacts_activity.this);
                pd.setMessage("Loading...");
                pd.setCancelable(false);
                pd.show();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                LoadEMC();
                return null;
            }
        }

        public void LoadEMC(){
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.urlEmergencyContacts
                    , new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    if (response.equals("")) {
                        new JSONemc().execute();
                        pd.dismiss();
                    } else if (!response.equals("")) {
                        pd.dismiss();
                    }

                    id.clear();
                    cname.clear();
                    cno.clear();
                    category.clear();

                    try {
                        JSONArray JA = new JSONArray(response);
                        for (int i = 0; i < JA.length(); i++) {
                            JSONObject JO = JA.getJSONObject(i);
                            id.add(JO.getString("id"));
                            cname.add(JO.getString("contactname"));
                            cno.add(JO.getString("contactno"));
                            category.add(JO.getString("category"));
                        }
                    } catch (Exception e) {
//                    Toast.makeText(emergencycontacts_activity.this, ""+e, Toast.LENGTH_SHORT).show();
                    }

                    emer_adapt = new EmergencyContactAdapter(emergencycontacts_activity.this,
                            id, cname, cno, category);
                    lv_emg.setAdapter(emer_adapt);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    -1,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(stringRequest);
        }

    }

}
