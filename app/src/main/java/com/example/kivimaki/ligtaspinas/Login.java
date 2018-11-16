package com.example.kivimaki.ligtaspinas;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.*;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;

public class Login extends AppCompatActivity {


        EditText email,pass;
        TextView txtRegister,txtForgotPass;
        Button btnlogin;

        String stemail,stpass;

        RequestQueue requestQueue;
        SharedPreferences.Editor editor;
        SharedPreferences sharedPreferences;

    ProgressDialog pd;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            email = (EditText)findViewById(R.id.email);
            pass = (EditText)findViewById(R.id.pass);
            btnlogin = (Button) findViewById(R.id.btnlogin);
            txtRegister = (TextView)findViewById(R.id.txtRegister);
            txtForgotPass = (TextView)findViewById(R.id.txtForgotPass);


            Cache cache = new DiskBasedCache(getCacheDir(),1024*1024);
            Network network = new BasicNetwork(new HurlStack());
            requestQueue = new RequestQueue(cache,network);
            requestQueue.start();



            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Login.this);
            editor = sharedPreferences.edit();

            email.setText(sharedPreferences.getString("email",""));
            pass.setText(sharedPreferences.getString("pass",""));



            btnlogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    stemail = email.getText().toString();
                    stpass = pass.getText().toString();


                    if(stemail.equals("") || stpass.equals("")){
                        pd = new ProgressDialog(Login.this);
                        pd.setTitle("Email or Password is empty!");
                        pd.show();

                    }else{
                        pd = new ProgressDialog(Login.this);
                        pd.setTitle("Loading");
                        pd.setCancelable(false);
                        pd.show();

                        login();
                    }


                }
            });

            txtRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(),Register.class);
                    startActivity(intent);
                    finish();
                }
            });
        }



        public void Connectivity(){
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

                pd = new ProgressDialog(Login.this);

            }
        }



        public void login(){
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.urlLogin,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.equals("OK")) {
                                URLs.userApp = stemail;
                                editor.putString("email", stemail);
                                editor.putString("pass", stpass);
                                editor.commit();
                                Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                                startActivity(intent);
                                finish();
                                pd.dismiss();
                            } else {
                                pd.dismiss();
                                Toasty.error(Login.this,"Invalid User!", Toast.LENGTH_LONG).show();
                            }

                            Toast.makeText(Login.this, ""+response, Toast.LENGTH_SHORT).show();

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){
                @Override
                protected java.util.Map<String, String> getParams() throws AuthFailureError {
                    java.util.Map<String,String> login = new HashMap<>();
                    login.put("login","1");
                    login.put("email",stemail);
                    login.put("password",stpass);
                    return login;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0,-1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(stringRequest);
        }

}
