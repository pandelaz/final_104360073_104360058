package com.example.hongyi.afinal;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;


public class Main1Activity extends AppCompatActivity {


    String myJson;
    int[] bound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);

        myJson = getIntent().getStringExtra("json");
        bound = getIntent().getIntArrayExtra("json2");

        ImageButton imgbtn1 = (ImageButton) findViewById(R.id.imageButton1);
        ImageButton imgbtn2 = (ImageButton) findViewById(R.id.imageButton2);
        ImageButton imgbtn3 = (ImageButton) findViewById(R.id.imageButton3);

        imgbtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangePage(1);
            }
        });

        imgbtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangePage(2);
            }
        });

        imgbtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangePage(3);
            }
        });

    }

    public void ChangePage(int i) {


        if(i == 1) {
            Intent intent2 = new Intent(Main1Activity.this, MainActivity.class);
            intent2.putExtra("json", myJson);
            intent2.putExtra("json2", bound);
            startActivity(intent2);
        } else if (i == 2) {
            Intent intent2 = new Intent(Main1Activity.this, Main3Activity.class);
            intent2.putExtra("json", myJson);
            intent2.putExtra("json2", bound);
            startActivity(intent2);
        } else if(i==3) {
            Uri gmmIntentUri = Uri.parse("google.navigation:q=25.044397,121.533338&mode=w");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        }




    }


}


