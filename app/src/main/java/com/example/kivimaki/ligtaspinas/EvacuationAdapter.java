package com.example.kivimaki.ligtaspinas;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Kivimaki on 11/15/2018.
 */

public class EvacuationAdapter extends BaseAdapter{

        Context context;
        ArrayList<String> ID;
        ArrayList<String> LOCATION;
        ArrayList<String> DESCNAME;


        public EvacuationAdapter(Context context1,
                               ArrayList<String> id,
                               ArrayList<String> location,
                               ArrayList<String> descname)
        {
            this.context = context1;
            this.ID = id;
            this.LOCATION = location;
            this.DESCNAME = descname;
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
                layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                child = layoutInflater.inflate(R.layout.evacitem,null);

                holder = new Holder();
                holder.txtLocation = (TextView)child.findViewById(R.id.txtLocation);
                holder.txtDescname = (TextView)child.findViewById(R.id.txtDescname);

                child.setTag(holder);

            }
            else{
                holder = (Holder)child.getTag();
            }

            holder.txtLocation.setText(LOCATION.get(position));
            holder.txtDescname.setText(DESCNAME.get(position));

            return child;
        }

        public class Holder{
            TextView txtLocation;
            TextView txtDescname;
        }


}
