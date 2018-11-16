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

public class EmergencyContactAdapter extends BaseAdapter {

        Context context;
        ArrayList<String> ID;
        ArrayList<String> CNAME;
        ArrayList<String> CNO;
        ArrayList<String> CATEGORY;


        public EmergencyContactAdapter(
                Context context1,
                ArrayList<String> id,
                ArrayList<String> cname,
                ArrayList<String> cno,
                ArrayList<String> category
        ){
            this.context = context1;
            this.ID = id;
            this.CNAME = cname;
            this.CNO = cno;
            this.CATEGORY = category;

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

                holder.txtContactName = (TextView)child.findViewById(R.id.txtCname);
                holder.txtContactNo = (TextView)child.findViewById(R.id.txtCno);
                holder.txtCategory = (TextView)child.findViewById(R.id.txtCategory);

                child.setTag(holder);
            }
            else{
                holder = (Holder)child.getTag();
            }

            holder.txtContactName.setText(CNAME.get(position));
            holder.txtContactNo.setText(CNO.get(position));
            holder.txtCategory.setText(CATEGORY.get(position));

            return child;
        }

        public class Holder{
            TextView txtContactName;
            TextView txtContactNo;
            TextView txtCategory;
        }



}
