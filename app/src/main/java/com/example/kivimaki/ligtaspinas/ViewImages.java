package com.example.kivimaki.ligtaspinas;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

public class ViewImages extends AppCompatActivity {

        GridView imgGrid;
        final GridImages gimg = new GridImages();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_view_images);
            imgGrid = (GridView)findViewById(R.id.gridimg);

//            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

            ShowImages();

            imgGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                    new AlertDialog.Builder(ViewImages.this)
                            .setTitle("Delete a photo")
                            .setMessage("Do you want to delete selected photo?")
                            .setNegativeButton("No", null)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    gimg.IMGupload.remove(gimg.IMGupload.get(position));
                                    gimg.numPhotos--;
                                    ShowImages();
                                }
                            }).create().show();
                }
            });
        }


        public void ShowImages(){
            imgGrid.setAdapter(new ViewImagesAdapter(this, gimg.IMGupload,gimg.IMGECenter));
        }


}
