package com.example.kivimaki.ligtaspinas;

import android.content.ActivityNotFoundException;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
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
import com.john.waveview.WaveView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class FloodClicked extends AppCompatActivity {


        String itemId = "";

        TextView cStation, cWaterLvl, cCriticalLvl, txtCarLvl;

        WaveView wv1;

        RequestQueue requestQueue;
        Handler loadDB;

        Button btnhelpme;

        int WiFi = 0, show = 0;

        TextToSpeech tts;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_flood_clicked);

            itemId = getIntent().getStringExtra("itemID").toString();

            cStation = (TextView) findViewById(R.id.cStation);
            cWaterLvl = (TextView) findViewById(R.id.cWaterLvl);
            cCriticalLvl = (TextView) findViewById(R.id.cCriticalLvl);
            txtCarLvl = (TextView) findViewById(R.id.txtCarLvl);

            wv1 = (WaveView) findViewById(R.id.wv1);
            btnhelpme = (Button) findViewById(R.id.btnhelpme);


            Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
            Network network = new BasicNetwork(new HurlStack());
            requestQueue = new RequestQueue(cache, network);
            requestQueue.start();

            new JSONSelectedItem().execute();

            //Update dbmain after 2 mins
            loadDB = new Handler();
            loadDB.postDelayed(new Runnable() {
                public void run() {
                    Connectivity();
                    loadDB.postDelayed(this, 15000); //
                }
            }, 15000); //

            btnhelpme.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    promptSpeechInput();
                }
            });
        }

        public void Connectivity() {
            ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

            //For 3G check
            boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                    .isConnectedOrConnecting();
            //For WiFi Check
            boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                    .isConnectedOrConnecting();


            if (!is3g && !isWifi) // DISABLED NETWORK
            {
                Toasty.error(FloodClicked.this, "No Internet Connection", Toast.LENGTH_LONG).show();
                WiFi = 0;
                show = 0;

            } else //ENABLED NETWORK
            {
                WiFi = 1;
                if (WiFi == 1 && show == 0) {
                    show = 1;

                    ShowSelectedItem();
                }

            }
        }

        ProgressDialog pd;

    private final int REQ_CODE_SPEECH_INPUT = 100;
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Where do you want to go?");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), "Speech not supported",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
                }
                break;
            }

        }
    }


    public class JSONSelectedItem extends AsyncTask<Void, Void, Void> {

            @Override
            protected void onPreExecute() {
                pd = new ProgressDialog(FloodClicked.this);
                pd.setMessage("Loading...");
                pd.setCancelable(false);
                pd.show();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                ShowSelectedItem();
                return null;
            }
        }

        String waterlvl = "", criticallvl = "";

        public void ShowSelectedItem() {

            StringRequest getSelItem = new StringRequest(Request.Method.POST, URLs.urlMain,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            if (response.equals("")) {
                                new JSONSelectedItem().execute();
                                pd.dismiss();
                            } else if (!response.equals("")) {
                                pd.dismiss();
                            }

                            try {
                                JSONArray JA = new JSONArray(response);
                                for (int i = 0; i < JA.length(); i++) {
                                    JSONObject JO = JA.getJSONObject(i);
                                    cStation.setText(JO.getString("name"));

                                    waterlvl = JO.getString("water_lvl");
                                    criticallvl = JO.getString("critical_lvl");

                                }

                                getFinal();
                            } catch (Exception e) {
//                            Toast.makeText(clicked_forecast.this, ""+e, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> getMain = new HashMap<String, String>();

                    getMain.put("getSelItem", itemId);

                    return getMain;
                }
            };

            getSelItem.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    -1,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(getSelItem);

        }

        public void getFinal() {

            DecimalFormat df = new DecimalFormat("###.##");

            float getWL = 0 + Float.parseFloat(waterlvl);
            float getCL = 0 + Float.parseFloat(criticallvl);

            String wtrlvl = "", clvl = "";

            if (getWL == 0) {
                wtrlvl = "0 ft and 0 inches";
            } else if (getWL > 0 && getWL < 12) {
                wtrlvl = "0 ft and " + Float.parseFloat(df.format(getWL)) + " inches";
            } else if (getWL == 12) {
                wtrlvl = "1 ft and 0 inches";
            } else if (getWL > 12 && getWL < 24) {
                getWL = getWL - 12;
                wtrlvl = "1 ft and " + Float.parseFloat(df.format(getWL)) + " inches";
            } else if (getWL == 24) {
                wtrlvl = "2 ft and 0 inches";
            } else if (getWL > 24 && getWL < 36) {
                getWL = getWL - 24;
                wtrlvl = "2 ft and " + Float.parseFloat(df.format(getWL)) + " inches";
            } else if (getWL == 36) {
                wtrlvl = "3 ft and 0 inches";
            } else if (getWL > 36 && getWL < 48) {
                getWL = getWL - 36;
                wtrlvl = "3 ft and " + Float.parseFloat(df.format(getWL)) + " inches";
            } else if (getWL == 48) {
                wtrlvl = "4 ft and 0 inches";
            } else if (getWL > 48 && getWL < 60) {
                getWL = getWL - 48;
                wtrlvl = "4 ft and " + Float.parseFloat(df.format(getWL)) + " inches";
            } else if (getWL == 60) {
                wtrlvl = "5 ft and 0 inches";
            } else if (getWL > 60 && getWL < 72) {
                getWL = getWL - 60;
                wtrlvl = "5 ft and " + Float.parseFloat(df.format(getWL)) + " inches";
            }

            // end of Water level

            //start of Critical Level

            if (getCL == 0) {
                clvl = "0 ft' 0 inches";
            } else if (getCL > 0 && getCL < 12) {
                clvl = "0 ft' " + Float.parseFloat(df.format(getCL)) + " inches";
            } else if (getCL == 12) {
                clvl = "1 ft' 0 inches";
            } else if (getCL > 12 && getCL < 24) {
                getCL = getCL - 12;
                clvl = "1 ft' " + Float.parseFloat(df.format(getCL)) + " inches";
            } else if (getCL == 24) {
                clvl = "2 ft' 0 inches";
            } else if (getCL > 24 && getCL < 36) {
                getCL = getCL - 24;
                clvl = "2 ft' " + Float.parseFloat(df.format(getCL)) + " inches";
            } else if (getCL == 36) {
                clvl = "3 ft' 0 inches";
            } else if (getCL > 36 && getCL < 48) {
                getCL = getCL - 36;
                clvl = "3 ft' " + Float.parseFloat(df.format(getCL)) + " inches";
            } else if (getCL == 48) {
                clvl = "4 ft' 0 inches";
            } else if (getCL > 48 && getCL < 60) {
                getCL = getCL - 48;
                clvl = "4 ft' " + Float.parseFloat(df.format(getCL)) + " inches";
            } else if (getCL == 60) {
                clvl = "5 ft' 0 inches";
            } else if (getCL > 60 && getCL < 72) {
                getCL = getCL - 60;
                clvl = "5 ft' " + Float.parseFloat(df.format(getCL)) + " inches";
            }

            //end of critical Level

            cWaterLvl.setText("Current Water Level : " + wtrlvl);
            cCriticalLvl.setText("The Critical Level : " + clvl);

            Double getWaterLevel = Double.parseDouble(waterlvl);
            if (getWaterLevel == 0) {
                wv1.setProgress(-10);
                txtCarLvl.setText("- Measurement : " + wtrlvl + "\n- PASSABLE TO ALL TYPES OF VEHICLES (PATV)");
            } else if (getWaterLevel <= 1 || getWaterLevel <= 3) {
                wv1.setProgress(6);
                txtCarLvl.setText("- Measurement : " + wtrlvl + "\n- PASSABLE TO ALL TYPES OF VEHICLES (PATV)");
            } else if (getWaterLevel <= 5 || getWaterLevel <= 7) {
                wv1.setProgress(15);
                txtCarLvl.setText("- Measurement  : " + wtrlvl + "\n- PASSABLE TO ALL TYPES OF VEHICLES (PATV)");
            } else if (getWaterLevel <= 8 || getWaterLevel <= 9) {
                wv1.setProgress(18);
                txtCarLvl.setText("- Depth : Gutter \n- Measurement : " + wtrlvl + "\n- PASSABLE TO ALL TYPES OF VEHICLES (PATV)");
            } else if (getWaterLevel <= 10 || getWaterLevel <= 12) {
                wv1.setProgress(22);
                txtCarLvl.setText("- Depth : Half Knee \n- Measurement : " + wtrlvl + "\n- PASSABLE TO ALL TYPES OF VEHICLES (PATV)");
            } else if (getWaterLevel <= 13 || getWaterLevel <= 18) {
                wv1.setProgress(28);
                txtCarLvl.setText("- Depth : Half Tire \n- Measurement : " + wtrlvl + "\n- NOT PASSABLE TO LIGHT VEHICLES (NPLV)");
            } else if (getWaterLevel <= 19 || getWaterLevel <= 22) {
                wv1.setProgress(38);
                txtCarLvl.setText("- Depth : Knee \n- Measurement : " + wtrlvl + "\n- NOT PASSABLE TO LIGHT VEHICLES (NPLV)");
            } else if (getWaterLevel <= 23 || getWaterLevel <= 25) {
                wv1.setProgress(43);
                txtCarLvl.setText("- Depth : Knee \n- Measurement : " + wtrlvl + "\n- NOT PASSABLE TO LIGHT VEHICLES (NPLV)");
            } else if (getWaterLevel <= 26 || getWaterLevel <= 29) {
                wv1.setProgress(48);
                txtCarLvl.setText("- Depth : Tires \n- Measurement : " + wtrlvl + "\n- NOT PASSABLE TO ALL TYPES OF VEHICLES (NPATV)");
            } else if (getWaterLevel <= 30 || getWaterLevel <= 33) {
                wv1.setProgress(55);
                txtCarLvl.setText("- Depth : Tires \n- Measurement : " + wtrlvl + "\n- NOT PASSABLE TO ALL TYPES OF VEHICLES (NPATV)");
            } else if (getWaterLevel <= 34 || getWaterLevel <= 36) {
                wv1.setProgress(60);
                txtCarLvl.setText("- Depth : Tires \n- Measurement : " + wtrlvl + "\n- NOT PASSABLE TO ALL TYPES OF VEHICLES (NPATV)");
            } else if (getWaterLevel <= 37 || getWaterLevel <= 40) {
                wv1.setProgress(64);
                txtCarLvl.setText("- Depth : Waist \n- Measurement : " + wtrlvl + "\n- NOT PASSABLE TO ALL TYPES OF VEHICLES (NPATV)");
            } else if (getWaterLevel <= 41 || getWaterLevel <= 44) {
                wv1.setProgress(69);
                txtCarLvl.setText("- Depth : Waist \n- Measurement : " + wtrlvl + "\n- NOT PASSABLE TO ALL TYPES OF VEHICLES (NPATV)");
            } else if (getWaterLevel <= 45 || getWaterLevel > 45) {
                wv1.setProgress(72);
                txtCarLvl.setText("- Depth : Chest \n- Measurement : " + wtrlvl + "\n- NOT PASSABLE TO ALL TYPES OF VEHICLES (NPATV)");
            }

        }

}

