package com.example.kivimaki.ligtaspinas;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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

public class Announcements extends AppCompatActivity {

        ListView lv_warning;

        AnnouncementADapter warningmsgAdapter;

        ArrayList<String> MSG;
        ArrayList<String> TIME;
        ArrayList<String> DATE;

        Handler LoadAlertMsg;
        RequestQueue requestQueue;

        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_announcements);

            lv_warning = (ListView)findViewById(R.id.lv_announcement);

            MSG = new ArrayList<String>();
            TIME = new ArrayList<String>();
            DATE = new ArrayList<String>();



            Cache cache = new DiskBasedCache(getCacheDir(),1024*1024);
            com.android.volley.Network network = new BasicNetwork(new HurlStack());
            requestQueue = new RequestQueue(cache,network);
            requestQueue.start();

//            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

            new JSONalertmsg().execute();
            LoadAlertMsg = new Handler();
            LoadAlertMsg.postDelayed(new Runnable() {
                @Override
                public void run() {
                    AlertMsg();
                    LoadAlertMsg.postDelayed(this,2500);
                }
            }, 2500);


            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Announcements.this);
            editor = sharedPreferences.edit();

        }

        ProgressDialog pd;
        public class JSONalertmsg extends AsyncTask<Void,Void,Void> {

            @Override
            protected void onPreExecute(){
                pd = new ProgressDialog(Announcements.this);
                pd.setMessage("Loading...");
                pd.setCancelable(false);
                pd.show();
            }

            @Override
            protected Void doInBackground(Void... voids) {

                AlertMsg();

                return null;
            }
        }



        public void AlertMsg(){

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.urlAlertMsg,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            if (response.equals("")) {
                                new JSONalertmsg().execute();
                                pd.dismiss();
                            } else if (!response.equals("")) {
                                pd.dismiss();
                            }

                            MSG.clear();
                            TIME.clear();
                            DATE.clear();
                            try {

                                JSONArray JA = new JSONArray(response);
                                editor.putInt("countAlertMsg", JA.length());
                                editor.commit();

                                for (int i = 0; i < JA.length(); i++) {
                                    JSONObject JO = JA.getJSONObject(i);

                                    MSG.add(JO.getString("msg"));
                                    TIME.add(JO.getString("time") + " " + JO.getString("ampm"));
                                    DATE.add(JO.getString("date"));

                                }

                            } catch (Exception e) {
//                            Toast.makeText(warningmessage_activity.this, ""+e, Toast.LENGTH_SHORT).show();
                            }

                            int lastViewedPosition = lv_warning.getFirstVisiblePosition();

                            //get offset of first visible view
                            View v = lv_warning.getChildAt(0);
                            int topOffset = (v == null) ? 0 : v.getTop();

                            warningmsgAdapter = new AnnouncementADapter(Announcements.this,
                                    MSG, TIME, DATE);

                            lv_warning.setAdapter(warningmsgAdapter);

                            lv_warning.setSelectionFromTop(lastViewedPosition, topOffset);


                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){
                //input hash
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    -1,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(stringRequest);

        }

}
