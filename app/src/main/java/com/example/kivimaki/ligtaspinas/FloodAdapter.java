package com.example.kivimaki.ligtaspinas;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Kivimaki on 11/15/2018.
 */

public class FloodAdapter extends BaseAdapter{

    Context context;
    ArrayList<String> ID;
    ArrayList<String> STATION;
    ArrayList<String> WATERLVL;
    ArrayList<String> CRITICALLVL;


    public FloodAdapter(
            Context context1,
            ArrayList<String> id,
            ArrayList<String> station,
            ArrayList<String> waterlvl,
            ArrayList<String> criticallvl
    ){
        this.context = context1;
        this.ID = id;
        this.STATION = station;
        this.WATERLVL = waterlvl;
        this.CRITICALLVL = criticallvl;
    }

    @Override
    public int getCount() {
        return ID.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View child, ViewGroup parent) {
        Holder holder;
        LayoutInflater layoutInflater;

        if(child == null){
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            child = layoutInflater.inflate(R.layout.flooditem,null);
            holder = new Holder();
            holder.txtstation = (TextView) child.findViewById(R.id.txtStation);
            holder.txtwtrlvl = (TextView)child.findViewById(R.id.txtWaterLvl);
            holder.txtcriticallvl = (TextView)child.findViewById(R.id.txtCriticallvl);
            holder.imgicon = (ImageView) child.findViewById(R.id.imgicon);

            child.setTag(holder);
        }
        else {

            holder = (Holder)child.getTag();

        }

        holder.txtstation.setText(STATION.get(position));

        DecimalFormat df = new DecimalFormat("###.##");

        float getWL = 0 + Float.parseFloat(WATERLVL.get(position));
        float getCL = 0 + Float.parseFloat(CRITICALLVL.get(position));

        String wtrlvl = "",clvl = "";

        if(getWL == 0){
            wtrlvl = "0 ft and 0 inches";
        }
        else if(getWL > 0 && getWL < 12){
            wtrlvl = "0 ft and "+getWL +" inches";
        }

        else if(getWL == 12){
            wtrlvl = "1 ft and 0 inches";
        }
        else if(getWL > 12 && getWL < 24){
            getWL = getWL - 12;
            wtrlvl = "1 ft and "+getWL+" inches";
        }

        else if(getWL == 24){
            wtrlvl = "2 ft and 0 inches";
        }
        else if(getWL > 24 && getWL < 36){
            getWL = getWL - 24;
            wtrlvl = "2 ft and "+getWL+" inches";
        }

        else if(getWL == 36){
            wtrlvl = "3 ft and 0 inches";
        }
        else if(getWL > 36 && getWL < 48){
            getWL = getWL - 36;
            wtrlvl = "3 ft and "+getWL+" inches";
        }

        else if(getWL == 48){
            wtrlvl = "4 ft and 0 inches";
        }
        else if(getWL > 48 && getWL < 60){
            getWL = getWL - 48;
            wtrlvl = "4 ft and "+getWL+" inches";
        }

        else if(getWL == 60){
            wtrlvl = "5 ft and 0 inches";
        }
        else if(getWL > 60 && getWL < 72){
            getWL = getWL - 60;
            wtrlvl = "5 ft and "+getWL+" inches";
        }

        // end of Water level

        //start of Critical Level

        if(getCL == 0){
            clvl = "0 ft' 0 inch";
        }
        else if(getCL > 0 && getCL < 12){
            clvl = "0 ft' "+getWL+" inch";
        }

        else if(getCL == 12){
            clvl = "1 ft' 0 inch";
        }
        else if(getCL > 12 && getCL < 24){
            getCL = getCL - 12;
            clvl = "1 ft' "+getWL+" inch";
        }

        else if(getCL == 24){
            clvl = "2 ft' 0 inch";
        }
        else if(getCL > 24 && getCL < 36){
            getCL = getCL - 24;
            clvl = "2 ft' "+getWL+" inch";
        }

        else if(getCL == 36){
            clvl = "3 ft' 0 inch";
        }
        else if(getCL > 36 && getCL < 48){
            getCL = getCL - 36;
            clvl = "3 ft' "+getWL+" inch";
        }

        else if(getCL == 48){
            clvl = "4 ft' 0 inch";
        }
        else if(getCL > 48 && getCL < 60){
            getCL = getCL - 48;
            clvl = "4 ft' "+getWL+" inch";
        }

        else if(getCL == 60){
            clvl = "5 ft' 0 inch";
        }
        else if(getCL > 60 && getCL < 72){
            getCL = getCL - 60;
            clvl = "5 ft' "+getWL+" inch";
        }

        //end of critical Level


        if(Float.parseFloat(CRITICALLVL.get(position)) > Float.parseFloat(WATERLVL.get(position))){

            holder.txtwtrlvl.setText(wtrlvl);
            holder.txtwtrlvl.setTextColor(Color.WHITE);
            holder.txtwtrlvl.setBackgroundColor(Color.rgb(255,140,17));
            holder.txtcriticallvl.setText("Critical Level : " + clvl);
            holder.txtcriticallvl.setTextColor(Color.rgb(255,140,17));
            holder.imgicon.setImageResource(R.drawable.pt);
        }

        else if (Float.parseFloat(CRITICALLVL.get(position)) <= Float.parseFloat(WATERLVL.get(position))){
            holder.txtwtrlvl.setText(wtrlvl);
            holder.txtwtrlvl.setTextColor(Color.WHITE);
            holder.txtwtrlvl.setBackgroundColor(Color.RED);
            holder.txtcriticallvl.setText("Critical Level : " + clvl);
            holder.txtcriticallvl.setTextColor(Color.RED);
            holder.imgicon.setImageResource(R.drawable.pw);
        }

        return child;
    }

    public class Holder{
        TextView txtstation;
        TextView txtwtrlvl;
        TextView txtcriticallvl;

        ImageView imgicon;
    }
}

