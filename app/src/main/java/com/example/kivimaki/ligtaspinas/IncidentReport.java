package com.example.kivimaki.ligtaspinas;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;

public class IncidentReport extends AppCompatActivity {


        EditText eDescription,eName,eLoc;
        Button btnSend,btnVAI;
        ImageView imgShow;

        Bitmap bitmap;
        String name,description,location;
        static final int PICK_IMAGE_REQUEST = 1;
        RequestQueue requestQueue;
        GridView imgGrid;
        Handler checkWifi;

        int sendas = 0;
        final GridImages gridImg = new GridImages();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_incident_report);
            eDescription = (EditText)findViewById(R.id.txtDescription);
            eLoc = (EditText)findViewById(R.id.txtLoc);
            eName = (EditText)findViewById(R.id.txtName);
            btnSend = (Button) findViewById(R.id.btnsend);
            btnVAI = (Button) findViewById(R.id.btnVAI);
            imgShow = (ImageView) findViewById(R.id.showImg);
            imgGrid = (GridView) findViewById(R.id.gridimg);

            com.android.volley.Cache cache = new DiskBasedCache(getCacheDir(),1024*1024);
            com.android.volley.Network network = new BasicNetwork(new HurlStack());
            requestQueue = new RequestQueue(cache,network);
            requestQueue.start();

//            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

            eName.setText(URLs.userApp);
            gridImg.optImg = "ReportIssue";

            imgShow.setOnClickListener(new View.OnClickListener() {  // UPLOAD MORE IMAGE
                @Override
                public void onClick(View view) {
                    if(gridImg.numPhotos < 4) {
                        showFileChooser();
                    }
                    else{
                        Toasty.error(getApplicationContext(),"You can't select more photos!",Toast.LENGTH_LONG).show();
                    }
                }
            });

            btnVAI.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(!gridImg.IMGupload.isEmpty()) {
                        Intent intent = new Intent(getApplicationContext(), ViewImages.class);
                        startActivity(intent);
                    }else{
                        Toasty.warning(IncidentReport.this,"No Photos", Toast.LENGTH_LONG).show();
                    }
                }
            });

            btnSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    description = eDescription.getText().toString();
                    location = eLoc.getText().toString();
                    name = eName.getText().toString();

                    if(description.equals("")){
                        Toasty.warning(getApplicationContext(),"Description is empty!").show();}
                    else if(location.equals("")){
                        Toasty.warning(getApplicationContext(),"Your location is required!").show();
                    }
                    else{
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(IncidentReport.this);
                        alertDialog.setTitle("Report an Issue");
                        alertDialog.setMessage("Do you want to send this issue?");
                        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
//------------------
                                int arraySize = gridImg.IMGupload.size();
                                for(int u = 0; u < arraySize; u++){
                                    txtImg += getStringImage(gridImg.IMGupload.get(u))+","; // THIS STRING GO TO CLASS "gridImg"
                                }

                                new SendIssue().execute();
                            }
                        });
                        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

                        alertDialog.show();


                    }
                }
            });


            //Update dbmain after 2 mins
            checkWifi = new Handler();
            checkWifi.postDelayed(new Runnable() {
                public void run() {
                    Connectivity();
                    checkWifi.postDelayed(this, 1500); //
                }
            }, 1500); //
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
                pd.dismiss();
                Toasty.error(IncidentReport.this,"No Internet Connection").show();
            }
            else //ENABLED NETWORK
            {
                try {
                    pd.dismiss();
                }
                catch (Exception e){

                }
            }
        }


        //select image dah OK!
        private void showFileChooser() {
            Intent pickImageIntent = new Intent(Intent.ACTION_GET_CONTENT,
                    MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            pickImageIntent.setType("image/*");
            pickImageIntent.putExtra("crop", "false");
            pickImageIntent.putExtra("scale", true);
            pickImageIntent.putExtra("outputX", 1840);  // Mahalaga ang Convertion na ito para sa Image Insert quality
            pickImageIntent.putExtra("outputY", 1840);
            pickImageIntent.putExtra("aspectX", 1);
            pickImageIntent.putExtra("aspectY", 1);
            pickImageIntent.putExtra("outputFormat",
                    Bitmap.CompressFormat.JPEG.toString());
            startActivityForResult(pickImageIntent, PICK_IMAGE_REQUEST);

        }

        // Class for Image Gallery sa Adapter para maview
        // Star activity for result method to Set captured image on image view after click.
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {

            super.onActivityResult(requestCode, resultCode, data);

            // When an Image is picked
            if (resultCode == RESULT_OK) {
                Uri filePath = data.getData();
                if(requestCode == PICK_IMAGE_REQUEST){
                    try {
                        //Getting the Bitmap from Gallery
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                        gridImg.numPhotos++;
                        gridImg.IMGupload.add(bitmap);

                    }
                    catch (Exception e){

                    }
                }
            }else{
                Toasty.error(getApplicationContext(),"Cancel").show();
            }

        }

        private String getStringImage(Bitmap bmp) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp = Bitmap.createScaledBitmap(bmp, 850, 1296, false);
            bmp.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] imageBytes = baos.toByteArray();
            return android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT);


        }


        String txtImg = ""; // LAHAT NG PHOTO DITO INAADD THATS WHY MAY COMMA SA btnsend.onclicklistener sa txtIMG
        SweetAlertDialog pd;
        public class SendIssue extends AsyncTask<Void,Void,Void> {

            @Override
            protected void onPreExecute(){
                pd = new SweetAlertDialog(IncidentReport.this);
                pd.setTitleText("Sending your issue...");
                pd.setCancelable(false);
                pd.show();
            }

            @Override
            protected Void doInBackground(Void... voids) {

                SendReport();
                return null;
            }

        }

        //SEnd Images atbp..
        public void SendReport(){
            StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, URLs.urlSendIssue,
                    new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            if (response.equals("OK")) { // if ok stop the asynctask
                                eDescription.setText("");
                                eLoc.setText("");
                                sendas = 0;
                                txtImg = "";
                                gridImg.IMGupload.clear();
                                gridImg.numPhotos = 0;

                                pd.setTitleText("Sending Successfully!")
                                        .setContentText("We will fix issues you reported ASAP!")
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> send = new HashMap<>();
                    send.put("image",txtImg); //sending images
                    send.put("name",name);
                    send.put("description",description+"\n\n (LOCATION: "+location+")");
                    return send;
                }
            };



            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    -1,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)); // PINAKAMAHALAGA

            requestQueue.add(stringRequest);

//    Toasty.success(incidents_activity.this,"We will fix issues you reported ASAP!",Toast.LENGTH_LONG );

        }

        @Override
        public void onBackPressed() {
            new AlertDialog.Builder(this)
                    .setTitle("Exit")
                    .setMessage("Back to Dashboard?")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            IncidentReport.super.onBackPressed();
                        }
                    }).create().show();
        }


}
