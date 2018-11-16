package com.example.kivimaki.ligtaspinas;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class Register extends AppCompatActivity {

        LinearLayout form1, form2;

        EditText rfname, rlname, remail, rpassword, getVCode;
        Button btnregister, btnconfirm, btnresendcode, btncancel;
        TextView txtBackToLogin;

        RequestQueue requestQueue;
        String sfname, slname, semail, spassword, saddress, verifCode;
        int randCode;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_register);

            form1 = (LinearLayout) findViewById(R.id.form1);
            form2 = (LinearLayout) findViewById(R.id.form2);

            rfname = (EditText) findViewById(R.id.rfname);
            rlname = (EditText) findViewById(R.id.rlname);
            remail = (EditText) findViewById(R.id.remail);
            rpassword = (EditText) findViewById(R.id.rpassword);
            getVCode = (EditText) findViewById(R.id.getVCode);

            txtBackToLogin = (TextView) findViewById(R.id.txtBackToLogin);

            btnregister = (Button) findViewById(R.id.btnregister);
            btnconfirm = (Button) findViewById(R.id.btnconfirm);
            btnresendcode = (Button) findViewById(R.id.btnresend);
            btncancel = (Button) findViewById(R.id.btncancel);

            final Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
            Network network = new BasicNetwork(new HurlStack());
            requestQueue = new RequestQueue(cache, network);
            requestQueue.start();

//        Toast.makeText(this, ""+getPNUM1(), Toast.LENGTH_SHORT).show();


            btnregister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sfname = rfname.getText().toString();
                    slname = rlname.getText().toString();
                    semail = remail.getText().toString();
                    spassword = rpassword.getText().toString();

                    if (sfname.equals("")) {
                        Toasty.warning(getApplicationContext(), "Your first name is required!").show();
                    } else if (sfname.toString().length() < 2) {
                        Toasty.warning(getApplicationContext(), "Your first name is required!").show();
                    } else if (slname.toString().length() < 5) {
                        Toasty.warning(getApplicationContext(), "Password must be atleast 8 characters!").show();

                    } else if (slname.equals("")) {
                        Toasty.warning(getApplicationContext(), "Your last name is required!").show();

                    } else if (semail.equals("")) {
                        Toasty.warning(getApplicationContext(), "Your email is required!").show();

                    } else if (spassword.equals("")) {
                        Toasty.warning(getApplicationContext(), "Your password is required!").show();

                    } else if (spassword.toString().length() < 8) {
                        Toasty.warning(getApplicationContext(), "Password must be atleast 8 characters!").show();

                    } else {
                        CheckEmailExist();
                        getVCode.setEnabled(false);

                        pd = new ProgressDialog(Register.this);
                        pd.setMessage("Sending Verification Code...");
                        pd.setCancelable(false);
                        pd.show();
                    }

                }
            });

            txtBackToLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), Login.class);
                    startActivity(intent);
                    finish();
                }
            });

            btnconfirm.setOnClickListener(new View.OnClickListener() { // CONFIRM
                @Override
                public void onClick(View view) {
                    verifCode = getVCode.getText().toString();

                    if (verifCode.equals("" + randCode)) {
                        Toasty.success(getApplicationContext(), "Registration Complete!").show();
                        Register();

                        // Successfully Created!


                    } else {
                        Toasty.error(getApplicationContext(), "Invalid Code!").show();
                    }
                }
            });

            btnresendcode.setOnClickListener(new View.OnClickListener() { // RESEND CODE
                @Override
                public void onClick(View view) {
                    getVCode.setEnabled(false);
                    pd = new ProgressDialog(Register.this);
                    pd.setMessage("Resend Verification Code...");
                    pd.setCancelable(false);
                    pd.show();
                    EmailVerify();

                }
            });

            btncancel.setOnClickListener(new View.OnClickListener() { // CANCEL
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder cancel = new AlertDialog.Builder(Register.this);
                    cancel.setMessage("Back to login area?");
                    cancel.setCancelable(false);
                    cancel.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(getApplicationContext(), Login.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                    cancel.setNeutralButton("No", null);
                    cancel.show();
                }
            });

            getregion();
        }

        ProgressDialog pd;

        public void CheckEmailExist() {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.urlLogin,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.equals("EXISTS")) {
                                AlertDialog.Builder cancel = new AlertDialog.Builder(Register.this);
                                cancel.setMessage("Your Email already exists!");
                                cancel.setPositiveButton("Ok", null);
                                cancel.show();
                            } else if (response.equals("NO")) {
                                form1.setVisibility(View.GONE);
                                form2.setVisibility(View.VISIBLE);
                                Random r = new Random();
                                randCode = r.nextInt(999999 - 111111) + 111111;
                                EmailVerify();

                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Override
                protected java.util.Map<String, String> getParams() throws AuthFailureError {
                    java.util.Map<String, String> check = new HashMap<>();
                    check.put("EXISTSEMAIL", semail);
                    return check;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(stringRequest);
        }

        public void EmailVerify() {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.urlEmailVerify,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toasty.info(getApplicationContext(), "Verification Code was sent!").show();
                            getVCode.setEnabled(true);
                            pd.dismiss();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(Register.this, "" + error, Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected java.util.Map<String, String> getParams() throws AuthFailureError {
                    java.util.Map<String, String> register = new HashMap<>();
                    register.put("email", semail);
                    register.put("randCode", "" + randCode);
                    return register;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(stringRequest);
        }

//    private String getPNUM1() {
//        TelephonyManager telMgr = (TelephonyManager)
//                Register.this.getSystemService(Context.TELEPHONY_SERVICE);
//
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//
//        }
//
//        String deviceID = telMgr.getDeviceId();
//        String simSerialNumber = telMgr.getSimSerialNumber();
//        String simLineNumber = telMgr.getLine1Number();
//        return simLineNumber;
//    }



        public void Register(){
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.urlLogin,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.equals("OK")) {

                                AlertDialog.Builder regCom = new AlertDialog.Builder(Register.this);
                                regCom.setCancelable(false);
                                regCom.setTitle("Registration Complete!");
                                regCom.setMessage("Click \"Proceed\" to login.");
                                regCom.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(getApplicationContext(), Login.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                                regCom.show();

                            } else {
                                Toast.makeText(Register.this, "Something is wrong !", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){
                @Override
                protected java.util.Map<String, String> getParams() throws AuthFailureError {
                    java.util.Map<String,String> register = new HashMap<>();
                    register.put("register","1");
                    register.put("fullname",sfname+" "+slname);
                    register.put("email", semail);
                    register.put("password", spassword);
                    register.put("region", "2");
                    register.put("city", "2");
                    register.put("brgy", "2");
                    return register;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0,-1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(stringRequest);
        }

        public void getregion(){

            //select region

            final List<String> listregion = new ArrayList<String>();
             listregion.clear();
            final StringRequest getregion = new StringRequest(Request.Method.POST, URLs.getRegion,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONArray JR = new JSONArray(response);
                                for (int i = 0; i < JR.length(); i++) {
                                    JSONObject JO = JR.getJSONObject(i);
                                    listregion.add(JO.getString("regionname"));
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

            getregion.setRetryPolicy(new DefaultRetryPolicy(0,-1,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(getregion);

            final Spinner selregion = (Spinner) findViewById(R.id.selregion);

            ArrayAdapter<String> adp1 = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, listregion);
            adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            selregion.setAdapter(adp1);

            selregion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                    // TODO Auto-generated method stub
                    Toast.makeText(Register.this, ""+id+position, Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub
                }
            });

            // select city

            final List<String> listcity = new ArrayList<String>();
            listcity.clear();
            final StringRequest getcity = new StringRequest(Request.Method.POST, URLs.getCity,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONArray JR = new JSONArray(response);
                                for (int i = 0; i < JR.length(); i++) {
                                    JSONObject JO = JR.getJSONObject(i);
                                    listcity.add(JO.getString("cityname"));
                                }
                            }
                            catch (Exception e){

                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return super.getParams();
                }
            };

            getcity.setRetryPolicy(new DefaultRetryPolicy(0,-1,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(getcity);

            final Spinner selcity = (Spinner) findViewById(R.id.selbrgy);

            ArrayAdapter<String> adp2 = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, listcity);
            adp2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            selcity.setAdapter(adp2);

            selcity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                    // TODO Auto-generated method stub
                    Toast.makeText(Register.this, ""+id+position, Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub
                }
            });
            // select brgy

            final List<String> listbrgy = new ArrayList<String>();

            listbrgy.clear();

            final StringRequest getbrgy = new StringRequest(Request.Method.POST, URLs.getBrgy,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONArray JR = new JSONArray(response);
                                for (int i = 0; i < JR.length(); i++) {
                                    JSONObject JO = JR.getJSONObject(i);
                                    listbrgy.add(JO.getString("brgyname"));
                                }
                            }
                            catch (Exception e){

                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> brgy = new HashMap<>();
                    brgy.put("cityid","");

                    return brgy;
                }
            };

            getbrgy.setRetryPolicy(new DefaultRetryPolicy(0,-1,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(getbrgy);

            final Spinner selbrgy = (Spinner) findViewById(R.id.selbrgy);

            ArrayAdapter<String> adp3 = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, listbrgy);
            adp3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            selbrgy.setAdapter(adp3);

            selbrgy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                    // TODO Auto-generated method stub
                    Toast.makeText(Register.this, ""+id+position, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub
                }
            });

        }

}
