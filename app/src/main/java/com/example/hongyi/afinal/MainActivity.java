package com.example.hongyi.afinal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    BroadcastReceiver myBroadcasReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);





        
        myBroadcasReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String myJson = intent.getExtras().getString("html");

            }
        };

        IntentFilter intentFilter = new IntentFilter("MyMessage");
        registerReceiver(myBroadcasReceiver,intentFilter);

        new Thread(new GetHTMLData()).start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadcasReceiver);
    }

    class GetHTMLData implements Runnable {
        public void run() {
            try {
                Document doc = Jsoup.connect("http://aps.ntut.edu.tw/course/tw/Croom.jsp?format=-2&year=106&sem=2").get();
                Elements row = doc.select("tr");
                Elements col = row.get(2).select("td");
                String title = col.select("a").text();

                Log.d("title",title);

                Intent i = new Intent("MyMessage");
                i.putExtra("html",title);

                sendBroadcast(i);

            } catch (IOException e) {

            }
        }
    }
}


