package com.example.kivimaki.ligtaspinas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Kivimaki on 11/15/2018.
 */

public class AnnouncementADapter extends BaseAdapter{

    Context context;
    ArrayList<String> MSG;
    ArrayList<String> TIME;
    ArrayList<String> DATE;

    public AnnouncementADapter(
            Context context1,
            ArrayList<String> msg,
            ArrayList<String> time,
            ArrayList<String> date
    ){
        this.context = context1;
        this.MSG = msg;
        this.TIME = time;
        this.DATE = date;
    }

    @Override
    public int getCount() {
        return MSG.size();
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
            layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            child = layoutInflater.inflate(R.layout.announcementitem,null);

            holder = new Holder();
            holder.txtMSG = (TextView)child.findViewById(R.id.txtMsg);
            holder.txtLASTUPDATE = (TextView)child.findViewById(R.id.txtLastUpdate);

            child.setTag(holder);

        }else{
            holder = (Holder)child.getTag();
        }

        holder.txtMSG.setText(MSG.get(position));
        holder.txtLASTUPDATE.setText("Last Update : "+TIME.get(position)+" "+DATE.get(position));

        return child;
    }
    public class Holder{
        TextView txtMSG;
        TextView txtLASTUPDATE;
    }
}


