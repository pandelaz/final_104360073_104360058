package com.example.hongyi.afinal;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;


public class Main1Activity extends AppCompatActivity {


    String myJson;
    int[] bound;

    String[] BuildingName = new String[] {
            "第一教學大樓",
            "第二教學大樓",
            "第三教學大樓",
            "第四教學大樓",
            "第五教學大樓",
            "第六教學大樓",
            "綜合科館",
            "共同科館",
            "設計館",
            "土木館",
            "光華館",
            "科研大樓",
            "國百館",
            "紡織大樓",
            "億光大樓",
            "化工館"
    };


    String[] blatlng = new String[] {
            "25.043377,121.533875",
            "25.043367, 121.534492",
            "25.042956, 121.534484",
            "25.042963, 121.533907",
            "25.044042, 121.534398",
            "25.043714, 121.533862",
            "25.042728, 121.535827",
            "25.042515, 121.534428",
            "25.042542, 121.533562",
            "25.043427, 121.533205",
            "25.044397, 121.533338",
            "25.044168, 121.533824",
            "25.043768, 121.533434",
            "25.042126, 121.538276", //紡織大樓經緯度待查詢
            "25.042126, 121.538276",
            "25.043382, 121.535119"
    };

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
            etBuildingFunction();
        }




    }

    public void etBuildingFunction() {
        final String[] list_item = BuildingName;
        AlertDialog.Builder dialog_list = new AlertDialog.Builder(Main1Activity.this);
        dialog_list.setTitle("請選擇大樓");
        dialog_list.setItems(list_item, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                for (int j = 0; j < BuildingName.length; j++) {
                    if (list_item[i].equals(BuildingName[j])) {

                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + blatlng[j] +  "&mode=w");
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);

                        Toast.makeText(Main1Activity.this,String.valueOf(j),Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
        });
        dialog_list.show();
    }


}


