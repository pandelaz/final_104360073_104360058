package com.example.hongyi.afinal;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.TimePicker;

import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class Main3Activity extends AppCompatActivity {


    BroadcastReceiver myBroadcasReceiver;

    ProgressDialog dialog;

    LoveDBHelper dbhelper;
    private ListView listview;

    class ClassObj extends Object {
        private String Building;
        private String name;
        private String uri;
        private Integer vol;

        public ClassObj() {
            Building = "";
            name = "";
            uri = "";
            vol = 0;
        }

        public void SetBuilding(String b) {
            Building = b;
        }

        public void SetName(String n) {
            name = n;
        }

        public void Seturi(String u) {
            uri = u;
        }

        public void SetVol(String v) {
            try {
                vol = Integer.parseInt(v);
            } catch (Exception e) {
                vol = -1;
            }

        }

        public String GetBuilding() {
            return Building;
        }

        public String GetName() {
            return name;
        }

        public String Geturi() {
            return uri;
        }

        public Integer GetVol() {
            return vol;
        }

    }


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

    //陣列中 每一棟的教室上下邊界
    int[] bound = new int[BuildingName.length + 1];

    //教室變數
    ClassObj[] Cobj;

    String GetMain0String;

    //網路變數
    final String Baseuri = "http://aps.ntut.edu.tw/course/tw/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        GetMain0String = getIntent().getStringExtra("json");
        bound = getIntent().getIntArrayExtra("json2");

        String[] handleMain0String = GetMain0String.split("!");

        Cobj = new ClassObj[handleMain0String.length];

        for(int i=0; i< handleMain0String.length; i++) {
            String[] tmp = handleMain0String[i].split("~");
            Cobj[i] = new ClassObj();
            Cobj[i].SetBuilding(tmp[0]);
            Cobj[i].SetName(tmp[1]);
            Cobj[i].Seturi(tmp[2]);
            Cobj[i].SetVol(tmp[3]);
        }

        dialog = new ProgressDialog(Main3Activity.this);

        myBroadcasReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                HandlemyBroadcastReceiver(context,intent);
            }
        };

        IntentFilter intentFilter = new IntentFilter("MyMessage");
        intentFilter.addAction("MyMessage2");
        intentFilter.addAction("MyMessage3");
        registerReceiver(myBroadcasReceiver, intentFilter);

        listview = (ListView) findViewById(R.id.listview1);



        dbhelper = new LoveDBHelper(Main3Activity.this);
        final Cursor cursor = dbhelper.getAllLoves();

        String [] columns = new String[] {
                LoveDBHelper.COLUMN_ID,
                LoveDBHelper.COLUMN_CLASSNAME,
                LoveDBHelper.COLUMN_DATE,
                LoveDBHelper.COLUMN_URL
        };
        int [] widgets = new int[] {
                R.id.idd,
                R.id.named,
                R.id.timed,
                R.id.urld
        };

        CustomSimpleCursorAdapter cursorAdapter = new CustomSimpleCursorAdapter(this, R.layout.loveclass,
                cursor, columns, widgets, 0);

        listview = (ListView)findViewById(R.id.lovelistview);
        listview.setAdapter(cursorAdapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view, int position, long id) {

                TextView cname = (TextView) view.findViewById(R.id.named);
                final String CName = cname.getText().toString();

                final String[] list_item = {"顯示教室詳細課表","帶我去教室"};
                AlertDialog.Builder dialog_list = new AlertDialog.Builder(Main3Activity.this);
                dialog_list.setTitle("請選擇功能");
                dialog_list.setItems(list_item, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        switch (list_item[i]) {

                            case "顯示教室詳細課表" :
                                dialog.setMessage("Please wait....");
                                dialog.setCanceledOnTouchOutside(false);
                                dialog.show();
                                new Thread(new GetHTMLData3(CName)).start();
                                break;
                            case "帶我去教室" :
                                int whatBuilding = 0;

                                for (int j = 0; j < BuildingName.length; j++) {
                                    if (list_item[i].equals(BuildingName[j])) {
                                        whatBuilding = j;
                                        break;
                                    }
                                }

                                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + blatlng[whatBuilding] +  "&mode=w");
                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                mapIntent.setPackage("com.google.android.apps.maps");
                                startActivity(mapIntent);
                                break;
                        }

                        //Toast.makeText(MainActivity.this,"你選的是" + list_item[i],Toast.LENGTH_SHORT).show();
                    }
                });

                dialog_list.show();


            }
        });

    }
    //網頁上個別教室課表顯示處理
    class GetHTMLData3 implements Runnable {
        String bud;
        String cls;
        String url;

        public GetHTMLData3(String whatClass) {

            String[] stmp = whatClass.split(" ");
            String BulidingName = stmp[0];
            String ClassRoom = stmp[1];

            for (int i = 0; i < Cobj.length; i++) {
                if (Cobj[i].GetBuilding().equals(BulidingName) && Cobj[i].GetName().equals(ClassRoom)) {
                    Log.d("r", Cobj[i].GetName());
                    bud = BulidingName;
                    cls = ClassRoom;
                    url = Baseuri + Cobj[i].Geturi();
                    break;
                }
            }

        }

        public void run() {
            try {

                Document doc = Jsoup.connect(url).get();
                Elements row = doc.select("tr");
                Log.d("","");

                String messageData = "[";

                String[][] msgData = new String[7][13];

                for(int i=4;i<row.size();i++) {
                    Elements col = row.get(i).select("td"); //row 4=第一節(0810-0900) 4~16(1~D)

                    for(int j = 1; j<col.size(); j++) {
                        Elements Ename = col.get(j).select("a");//col 1=禮拜天 2-7(禮拜一到禮拜六)
                        String ename = col.get(j).text();
                        String aname = ename.contains("(") ? ename.substring(ename.indexOf("(")+1,ename.indexOf(")")) : "";

                        int setime = i >= 8 ? 1 : 0;
                        msgData[j-1][i-4] = "{";
                        msgData[j-1][i-4] += "\"id\":" + String.valueOf((i - 4) * 7 + (j - 1)) + ",";
                        msgData[j-1][i-4] += "\"cid\":\"" + aname + "\",";
                        msgData[j-1][i-4] += "\"name\":\"" + ((Ename.size() != 0) ? Ename.get(0).childNode(0).toString() : "沒課") + "\",";
                        msgData[j-1][i-4] += "\"prof\":\"" + ((Ename.size() != 0) ? Ename.get(1).childNode(0).toString() : "") + "\",";
                        msgData[j-1][i-4] += "\"place\":\"" + bud + " " + cls + "\",";
                        msgData[j-1][i-4] += "\"day\":" + (j == 1 ? "7" : String.valueOf(j - 1)) + ",";
                        String st = String.valueOf(i + 4 + setime);
                        String et = String.valueOf(i + 5 + setime);
                        msgData[j-1][i-4] += "\"startTime\":" + String.valueOf(i + 4 + setime) + ",";
                        msgData[j-1][i-4] += "\"endTime\":" + String.valueOf(i + 5 + setime);
                        msgData[j-1][i-4] += "}";

                        Log.d("","");
                    }
                }

                for(int i = 0; i < 7; i++) {
                    int equal = 0;
                    for(int j = 1; j < 13; j++) {
                        int l1 = msgData[i][j].indexOf("\"cid\":\"");
                        int l2 = msgData[i][j].indexOf("\",\"name\"");

                        int l3 = msgData[i][j-1].indexOf("\"cid\":\"");
                        int l4 = msgData[i][j-1].indexOf("\",\"name\"");

                        String s1 = msgData[i][j].substring(l1,l2);
                        String s2 = msgData[i][j-1].substring(l3,l4);

                        if(s1.equals(s2)) {
                            if(equal == 0)
                                messageData += msgData[i][j-1] + ",";

                            if( j == 12) {
                                int l10 = messageData.lastIndexOf("\"endTime\":");
                                int l11 = messageData.lastIndexOf("}");
                                int l12 = msgData[i][j].indexOf("\"endTime\":");
                                int l13 = msgData[i][j].indexOf("}");
                                String s10 = String.valueOf(Integer.parseInt(msgData[i][j].substring(l12 + 10, l13)));
                                messageData = messageData.substring(0,l10+10) + s10 + messageData.substring(l11,messageData.length());
                            }

                            else {
                                int l10 = messageData.lastIndexOf("\"endTime\":");
                                int l11 = messageData.lastIndexOf("}");
                                String s10;
                                if(j != 12) {
                                    int l12 = msgData[i][j - 1].indexOf("\"endTime\":");
                                    int l13 = msgData[i][j - 1].indexOf("}");
                                    s10 = String.valueOf(Integer.parseInt(msgData[i][j - 1].substring(l12 + 10, l13)));
                                } else {
                                    int l12 = msgData[i][j].indexOf("\"endTime\":");
                                    int l13 = msgData[i][j].indexOf("}");
                                    s10 = String.valueOf(Integer.parseInt(msgData[i][j].substring(l12 + 10, l13)));
                                }
                                messageData = messageData.substring(0,l10+10) + s10 + messageData.substring(l11,messageData.length());
                            }
                            equal++;
                        } else {
                            if(equal == 0) {
                                messageData += msgData[i][j-1] + ",";
                            } else {
                                int l10 = messageData.lastIndexOf("\"endTime\":");
                                int l11 = messageData.lastIndexOf("}");
                                String s10 = String.valueOf(Integer.parseInt(messageData.substring(l10+10,l11))+1);

                                messageData = messageData.substring(0,l10+10) + s10 + messageData.substring(l11,messageData.length());
                            }
                            if(j == 12) {
                                messageData += msgData[i][j] + ",";
                            }

                            equal = 0;
                        }
                    }
                }

                if(messageData.substring(messageData.length()-1,messageData.length()).equals(",")) {
                    messageData = messageData.substring(0,messageData.length()-1);
                }

                messageData += "]";

                Intent i = new Intent("MyMessage3");
                i.putExtra("json", messageData);
                sendBroadcast(i);

                //Log.d("","");

            } catch (IOException e) {

            }
        }
    }

    //BroadcastReceiver 處理
    public void HandlemyBroadcastReceiver(Context context, Intent intent) {

        Intent intent2 = new Intent(getApplicationContext(), Main2Activity.class);
        String myJson = intent.getExtras().getString("json");
        intent2.putExtra("d",myJson);
        intent2.putExtra("json", GetMain0String);
        intent2.putExtra("json2",bound);
        startActivity(intent2);


        if(dialog.isShowing())
            dialog.dismiss();
    }


}


