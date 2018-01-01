package com.example.hongyi.afinal;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.IntentFilter;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class Main0Activity extends AppCompatActivity {

    BroadcastReceiver myBroadcasReceiver;

    //教室變數
    ClassObj[] Cobj;

    //網路變數
    final String Baseuri = "http://aps.ntut.edu.tw/course/tw/";

    String[] BuildingName = new String[]
            {"第一教學大樓",
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
                    "化工館"};

    //陣列中 每一棟的教室上下邊界
    int[] bound = new int[BuildingName.length + 1];

    int brange = 0, bcnt = 0;
    String testString = "";
    String CobjString = "";

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main0);



        myBroadcasReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                HandlemyBroadcastReceiver(context,intent);
            }
        };

        IntentFilter intentFilter = new IntentFilter("MyMessage");
        registerReceiver(myBroadcasReceiver, intentFilter);

        new Thread(new GetHTMLData()).start();

    }

    //網頁上的所有資訊抓下來處理
    class GetHTMLData implements Runnable {

        String Split2Num(String name) {
            int i;
            if (name.contains("二樓"))
                name = name.replace("二樓", "2F");
            else if (name.contains("一樓"))
                name = name.replace("一樓", "1F");

            for (i = 0; i < name.length(); i++) {

                char testchar = name.charAt(i);
                boolean checka = Character.isDigit(testchar);
                boolean checkb = ((testchar >= 65) && (testchar <= 90)) || ((testchar >= 97) && (testchar <= 122));
                if (checka || checkb) {
                    String returnstring = name.substring(i, name.length());
                    return returnstring;
                }
            }
            return name;
        }
        String RecognizeBuilding(String name) {
            if (name.contains("一教")) {
                return BuildingName[0];
            } else if (name.contains("二教")) {
                return BuildingName[1];
            } else if (name.contains("三教")) {
                return BuildingName[2];
            } else if (name.contains("四教")) {
                return BuildingName[3];
            } else if (name.contains("五教")) {
                return BuildingName[4];
            } else if (name.contains("六教")) {
                return BuildingName[5];
            } else if (name.contains("光華")) {
                return BuildingName[10];
            } else if (name.contains("科研")) {
                return BuildingName[11];
            } else if (name.contains("土木")) {
                return BuildingName[9];
            } else if (name.contains("設計")) {
                return BuildingName[8];
            } else if (name.contains("化")) { //化學 化工
                return BuildingName[15];
            } else if (name.contains("共同")) {
                return BuildingName[7];
            } else if (name.contains("紡織")) {
                return BuildingName[13];
            } else if (name.contains("國百")) {
                return BuildingName[12];
            } else if (name.contains("綜")) { //綜科 綜一 綜二 綜三
                return BuildingName[6];
            } else if (name.contains("億光")) {
                return BuildingName[14];
            } else if (name.contains("思源")) {
                return BuildingName[6];
            } else {
                return "UnKnown";
            }
        }

        //把從網頁取得下來的資料進行排序整理
        public void SortClassObj() {
            //int[] bound = new int[BuildingName.length+1];

            for (int k = 1; k <= BuildingName.length; k++) {
                int ifbreak = 0;
                for (int i = bound[k - 1] == 0 ? 0 : (bound[k - 1] + 1); i < Cobj.length; i++) {

                    String t1 = Cobj[i].GetBuilding();
                    if (!Cobj[i].GetBuilding().contains(BuildingName[k - 1])) {
                        for (int j = i + 1; j < Cobj.length; j++) {
                            String t2 = Cobj[j].GetBuilding();
                            if (Cobj[j].GetBuilding().contains(BuildingName[k - 1])) {
                                ClassObj tmp;
                                tmp = Cobj[i];
                                Cobj[i] = Cobj[j];
                                Cobj[j] = tmp;
                                bound[k] = i;
                                ifbreak = 1;
                                break;
                            }
                        }
                        if (ifbreak == 0) {
                            bound[k] = bound[k - 1];
                            break;
                        }
                    }
                }
            }

            bound[16] = Cobj.length - 1;

            for (int i = 0; i < bound.length; i++) {
                if (bound[i] == 0)
                    bound[i] = -1;
            }

            //=====sort=====
            //building Class , date , Course , Class
            for (int k = 0; k < (bound.length-1); k++) {
                for (int i = (bound[k] + 1); i <= bound[k + 1]; i++) {
                    for (int j = i + 1; j <= bound[k + 1]; j++) {

                        String s1 = Cobj[i].GetName();

                        int num1 = 0;
                        int notnum1 = 0;

                        try {
                            num1 = Integer.parseInt(s1) * 10;
                        } catch (Exception e) {

                            char c = s1.charAt(0);

                            if (c >= 48 && c <= 57) {
                                if (s1.contains("F")) {
                                    if (s1.contains("_")) {
                                        String[] s3 = s1.split("_");
                                        num1 = (c - 48) * 1000 + Integer.parseInt(s3[1]);
                                    } else {
                                        num1 = (c - 48) * 1000;
                                    }
                                } else if (s1.contains("_")) {

                                    String[] s3 = s1.split("_");
                                    num1 = Integer.parseInt(s3[0]) * 10 + Integer.parseInt(s3[1]);

                                } else if (s1.length() == 4) {

                                    char clast = s1.charAt(s1.length() - 1);

                                    if (clast >= 65 && clast <= 90) {

                                        String s3 = s1.substring(0, 3);
                                        num1 = Integer.parseInt(s3) * 10 + (clast - 65);

                                    } else {
                                        notnum1 = 1;
                                    }
                                }

                            } else {
                                notnum1 = 1;
                            }
                        }

                        String s2 = Cobj[j].GetName();

                        int num2 = 0;
                        int notnum2 = 0;

                        try {
                            num2 = Integer.parseInt(s2) * 10;
                        } catch (Exception e) {
                            char c = s2.charAt(0);
                            if (c >= 48 && c <= 57) {

                                if (s2.contains("F")) {

                                    if (s2.contains("_")) {

                                        String[] s3 = s2.split("_");
                                        num2 = (c - 48) * 1000 + Integer.parseInt(s3[1]);

                                    } else {
                                        num2 = (c - 48) * 1000;
                                    }

                                } else if (s2.contains("_")) {

                                    String[] s3 = s2.split("_");
                                    num2 = Integer.parseInt(s3[0]) * 10 + Integer.parseInt(s3[1]);

                                } else if (s2.length() == 4) {

                                    char clast = s2.charAt(s2.length() - 1);

                                    if (clast >= 65 && clast <= 90) {

                                        String s3 = s2.substring(0, 3);
                                        num2 = Integer.parseInt(s3) * 10 + (clast - 65);

                                    } else {
                                        notnum2 = 1;
                                    }
                                }

                            } else {
                                notnum2 = 1;
                            }
                        }

                        if (notnum1 == 0) { //純數字

                            if ((notnum2 == 0 && num2 < num1) || (notnum2 == 1)) {

                                ClassObj cobj;

                                cobj = Cobj[i];
                                Cobj[i] = Cobj[j];
                                Cobj[j] = cobj;

                            }

                        }
                    }
                }
            }


            //===============
            CobjString = "";
            for(int i=0;i<Cobj.length;i++) {
                Log.d("" + i, Cobj[i].GetBuilding() + "\t\t" + Cobj[i].GetName());

                CobjString += Cobj[i].GetBuilding() + "~" + Cobj[i].GetName() + "~" + Cobj[i].Geturi() + "~" + Cobj[i].GetVol();
                if(i!=(Cobj.length-1)) {
                    CobjString += "!";
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {

            }

            Intent i = new Intent("MyMessage");
            i.putExtra("json", CobjString);
            sendBroadcast(i);

            Log.d("","1234");

        }

        public void run() {
            try {
                Document doc = Jsoup.connect(Baseuri + "Croom.jsp?format=-2&year=106&sem=2").get();
                Elements row = doc.select("tr");

                Cobj = new ClassObj[row.size() - 2];

                for (int i = 2; i < row.size(); i++) {

                    Elements col = row.get(i).select("td");

                    Cobj[i - 2] = new ClassObj();
                    Cobj[i - 2].SetBuilding(RecognizeBuilding(col.get(0).select("a").get(0).childNode(0).toString()));
                    Cobj[i - 2].SetName(Split2Num(col.get(0).select("a").get(0).childNode(0).toString()));

                    Cobj[i - 2].Seturi(col.get(0).select("a").get(0).attr("href"));
                    Cobj[i - 2].SetVol(col.get(2).childNode(0).toString().trim());

                }

                SortClassObj();

            } catch (IOException e) {

            }

        }
    }

    //BroadcastReceiver 處理
    public void HandlemyBroadcastReceiver(Context context, Intent intent) {

        if(intent.getAction().equals("MyMessage")) {

            unregisterReceiver(myBroadcasReceiver);
            String myJson = intent.getExtras().getString("json");

            Intent intent2 = new Intent(Main0Activity.this,Main1Activity.class);
            intent2.putExtra("json",myJson);
            intent2.putExtra("json2",bound);
            startActivity(intent2);
            finish();

        }

    }


}


