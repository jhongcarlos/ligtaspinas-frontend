package com.example.kivimaki.ligtaspinas;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class MyBrgy extends AppCompatActivity {

    LinearLayout lnflood,lnnews,lnreport,lnemcontact,lnevac,lnother;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_brgy);

        lnflood = (LinearLayout)findViewById(R.id.ln_floodlevel);
        lnnews = (LinearLayout)findViewById(R.id.ln_warning);
        lnreport = (LinearLayout)findViewById(R.id.ln_report);
        lnemcontact = (LinearLayout)findViewById(R.id.ln_EContacts);
        lnevac = (LinearLayout)findViewById(R.id.ln_EVac);
        lnother = (LinearLayout)findViewById(R.id.ln_viewother);

        lnflood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Flood.class);
                startActivity(intent);
            }
        });
        lnnews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Announcements.class);
                startActivity(intent);
            }
        });
        lnreport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),IncidentReport.class);
                startActivity(intent);
            }
        });
        lnemcontact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),EmergencyContact.class);
                startActivity(intent);
            }
        });
        lnevac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),EvacuationArea.class);
                startActivity(intent);
            }
        });
        lnother.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Flood.class);
                startActivity(intent);
            }
        });




    }
}
