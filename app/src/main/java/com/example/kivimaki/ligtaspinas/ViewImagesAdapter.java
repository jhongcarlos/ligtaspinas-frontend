package com.example.kivimaki.ligtaspinas;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by Kivimaki on 11/15/2018.
 */

public class ViewImagesAdapter extends BaseAdapter{

        Context context;
        ArrayList<Bitmap> IMG;
        ArrayList<Bitmap> IMGECenter;

        GridImages gimg = new GridImages();

        public ViewImagesAdapter (
                Context context1,
                ArrayList<Bitmap> img,
                ArrayList<Bitmap> imgecenter
        ){
            this.context = context1;
            this.IMG = img;
            this.IMGECenter = imgecenter;
        }

        @Override
        public int getCount() {
            return IMG.size();
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
            ImageView imageView;
            if (child == null) {
                imageView = new ImageView(this.context);
                imageView.setLayoutParams(new GridView.LayoutParams(340, 340));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else {
                imageView = (ImageView) child;
            }


            if (gimg.optImg.equals("ReportIssue")) {
                imageView.setImageBitmap(this.IMG.get(position));
            }
            else if(gimg.optImg.equals("ECenter")){
                imageView.setImageBitmap(this.IMGECenter.get(position));
            }

            return imageView;
        }

}
