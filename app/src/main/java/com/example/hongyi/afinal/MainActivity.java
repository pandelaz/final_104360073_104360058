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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    BroadcastReceiver myBroadcasReceiver;

    ProgressDialog dialog;

    LoveDBHelper dbhelper;

    //時間變數
    private int mYear, mMonth, mDay, mHour, mMinute;
    private ImageButton button;
    private TextView tvDate, tvTime;
    private EditText etClass, etBuilding;
    private ListView listview;
    String testString = "";
    String GetMain0String = "";
    int brange = 0, bcnt = 0;

    //網路變數
    final String Baseuri = "http://aps.ntut.edu.tw/course/tw/";

    //選擇哪一棟大樓
    int whatBuilding = 1;

    //教室變數
    ClassObj[] Cobj;

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
        setContentView(R.layout.activity_main);

        dbhelper = new LoveDBHelper(MainActivity.this);

        dialog = new ProgressDialog(MainActivity.this);

        tvDate = (TextView) findViewById(R.id.tvDate);
        tvTime = (TextView) findViewById(R.id.tvTime);
        etClass = (EditText) findViewById(R.id.editTextClass);
        etBuilding = (EditText) findViewById(R.id.editTextBuilding);
        button = (ImageButton) findViewById(R.id.button);
        listview = (ListView) findViewById(R.id.listview1);

        ShowNowTime();

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

        for (int j = 0; j < BuildingName.length; j++) {
            if (BuildingName[j].equals("第一教學大樓")) {
                etBuilding.setText("第一教學大樓");
                whatBuilding = j;
                brange = bound[whatBuilding + 1] - (bound[whatBuilding] + 1);
                etClass.setText("");
                break;
            }
        }


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

        //new Thread(new GetHTMLData()).start();

        tvDate.setOnClickListener(new EditText.OnClickListener() {
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        tvTime.setOnClickListener(new EditText.OnClickListener() {
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        etBuilding.setOnClickListener(new EditText.OnClickListener() {
            public void onClick(View v) { etBuildingFunction();}
        });

        etClass.setOnClickListener(new EditText.OnClickListener() {
            public void onClick(View v) { etClassFunction(); }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { buttonFunction();}
        });


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view, int position, long id) {

                TextView cname = (TextView) view.findViewById(R.id.textView5);
                TextView cdate = (TextView) view.findViewById(R.id.tv1);
                TextView curl = (TextView) view.findViewById(R.id.tv4);
                final String CName = cname.getText().toString();
                final String CDate = cdate.getText().toString();
                final String CUrl = curl.getText().toString();

                final String[] list_item = {"加入我最愛的空教室","顯示教室詳細課表","帶我去教室"};
                AlertDialog.Builder dialog_list = new AlertDialog.Builder(MainActivity.this);
                dialog_list.setTitle("請選擇功能");
                dialog_list.setItems(list_item, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        switch (list_item[i]) {

                            case "加入我最愛的空教室" :
                                dbhelper.insertLove(CName,CDate,CUrl);
                                Intent intent = new Intent(MainActivity.this,Main3Activity.class);
                                intent.putExtra("json", GetMain0String);
                                intent.putExtra("json2",bound);
                                startActivity(intent);
                                break;
                            case "顯示教室詳細課表" :
                                dialog.setMessage("Please wait....");
                                dialog.setCanceledOnTouchOutside(false);
                                dialog.show();
                                new Thread(new GetHTMLData3(CName)).start();
                                break;
                            case "帶我去教室" :
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

    public void showDatePickerDialog() {
        // 設定初始日期
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        // 跳出日期選擇器
        DatePickerDialog dpd = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // 完成選擇，顯示日期
                        GregorianCalendar GregorianCalendar = new GregorianCalendar(year, monthOfYear, dayOfMonth - 1);
                        String SdayOfWeek = "";
                        int dayOfWeek = GregorianCalendar.get(Calendar.DAY_OF_WEEK);
                        switch (dayOfWeek) {
                            case 1:
                                SdayOfWeek = "禮拜一";
                                break;
                            case 2:
                                SdayOfWeek = "禮拜二";
                                break;
                            case 3:
                                SdayOfWeek = "禮拜三";
                                break;
                            case 4:
                                SdayOfWeek = "禮拜四";
                                break;
                            case 5:
                                SdayOfWeek = "禮拜五";
                                break;
                            case 6:
                                SdayOfWeek = "禮拜六";
                                break;
                            case 7:
                                SdayOfWeek = "禮拜日";
                                break;
                        }

                        tvDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth + " " + SdayOfWeek);

                    }
                }, mYear, mMonth, mDay);
        dpd.show();
    }

    public void showTimePickerDialog() {
        // 設定初始時間
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // 跳出時間選擇器
        TimePickerDialog tpd = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // 完成選擇，顯示時間
                        if(hourOfDay<=8) hourOfDay = 8;
                        if(hourOfDay>=22) hourOfDay = 22;
                        String hd = (hourOfDay<10) ? ("0" + hourOfDay) : String.valueOf(hourOfDay);
                        String ht = (minute<10) ? ("0" + minute) : String.valueOf(minute);
                        tvTime.setText(hd + ":" + ht);
                    }
                }, mHour, mMinute, false);
        tpd.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadcasReceiver);
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

            for(int i=0;i<Cobj.length;i++) {
                Log.d("" + i, Cobj[i].GetBuilding() + "\t\t" + Cobj[i].GetName());
            }



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
                    //Log.d("data" + String.valueOf(i - 1), Cobj[i - 2].GetName() + "," + Cobj[i - 2].GetVol() + "," + Cobj[i - 2].Geturi());

                }

                SortClassObj();

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {

                }

                Intent i = new Intent("MyMessage2");
                //i.putExtra("json", testString);
                sendBroadcast(i);

            } catch (IOException e) {

            }

        }
    }

    //網頁上個別教室資訊處理
    class GetHTMLData2 implements Runnable {
        String bud;
        String cls;
        String url;
        String dt;

        public GetHTMLData2(String uri, String d, String Bud, String Cls) {
            url = Baseuri + uri;
            dt = d;
            bud = Bud;
            cls = Cls;
        }

        private int WeekChange2Num(String week) {
            int d1 = 0;
            switch (week) {
                case "禮拜一":
                    d1 = 2;
                    break;
                case "禮拜二":
                    d1 = 3;
                    break;
                case "禮拜三":
                    d1 = 4;
                    break;
                case "禮拜四":
                    d1 = 5;
                    break;
                case "禮拜五":
                    d1 = 6;
                    break;
                case "禮拜六":
                    d1 = 7;
                    break;
                case "禮拜日":
                    d1 = 1;
                    break;
            }
            return d1;
        }

        private int TimeChange2Num(String Time) {
            int d0 = 0;
            int dd = Integer.parseInt(Time);
            switch (dd) {
                case 8:
                    d0 = 4;
                    break;
                case 9:
                    d0 = 5;
                    break;
                case 10:
                    d0 = 6;
                    break;
                case 11:
                    d0 = 7;
                    break;
                case 13:
                    d0 = 8;
                    break;
                case 14:
                    d0 = 9;
                    break;
                case 15:
                    d0 = 10;
                    break;
                case 16:
                    d0 = 11;
                    break;
                case 17:
                    d0 = 12;
                    break;
                case 18:
                    d0 = 13;
                    break;
                case 19:
                    d0 = 14;
                    break;
                case 20:
                    d0 = 15;
                    break;
                case 21:
                    d0 = 16;
                    break;
            }
            return d0;
        }

        public void run() {
            try {

                String[] d = dt.split(",");
                int d0 = TimeChange2Num(d[0]);
                int d1 = WeekChange2Num(d[1]);

                Document doc = Jsoup.connect(url).get();
                Elements row = doc.select("tr");
                Elements col = row.get(d0).select("td"); //row 4=第一節(0810-0900) 4~16(1~D)
                Elements Ename = col.get(d1).select("a");//col 1=禮拜天 2-7(禮拜一到禮拜六)
                String messageData = bud + " " + cls + ",";
                //building Class , date , Course , Class
                switch (d0) {
                    case 4:
                        messageData += "08:10-09:00,";
                        break;
                    case 5:
                        messageData += "09:10-10:00,";
                        break;
                    case 6:
                        messageData += "10:10-11:00,";
                        break;
                    case 7:
                        messageData += "11:10-12:00,";
                        break;
                    case 8:
                        messageData += "13:10-14:00,";
                        break;
                    case 9:
                        messageData += "14:10-15:00,";
                        break;
                    case 10:
                        messageData += "15:10-16:00,";
                        break;
                    case 11:
                        messageData += "16:10-17:00,";
                        break;
                    case 12:
                        messageData += "17:10-18:00,";
                        break;
                    case 13:
                        messageData += "18:10-19:00,";
                        break;
                    case 14:
                        messageData += "19:10-20:00,";
                        break;
                    case 15:
                        messageData += "20:10-21:00,";
                        break;
                    case 16:
                        messageData += "21:10-22:00,";
                        break;
                }


                if (Ename.size() != 0) {
                    String SCourseName = Ename.get(0).childNode(0).toString();
                    String SClassName = Ename.get(1).childNode(0).toString();
                    messageData += SCourseName + "," + SClassName;
                } else {
                    messageData += ",沒課";
                }

                messageData += "," + url;

                Log.d("if", messageData);

                testString += messageData + "\n";
                bcnt++;

                Intent i = new Intent("MyMessage");
                i.putExtra("json", testString);
                sendBroadcast(i);

            } catch (IOException e) {

            }
        }
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

    //某時段的某間教室是否有人使用 某時段的所有空教室 完成
    public void SearchClassRoom(String BulidingName, String ClassRoom, String Date) {

        if(!ClassRoom.equals("")) {

            for (int i = 0; i < Cobj.length; i++) {
                if (Cobj[i].GetBuilding().equals(BulidingName) && Cobj[i].GetName().equals(ClassRoom)) {
                    Log.d("r", Cobj[i].GetName());
                    new Thread(
                            new GetHTMLData2(Cobj[i].Geturi(), Date, BulidingName, ClassRoom)
                    ).start();
                }
            }

        } else {

            for (int i = 0; i < Cobj.length; i++) {
                if (Cobj[i].GetBuilding().equals(BulidingName)) {
                    Log.d("r", Cobj[i].GetName());
                    new Thread(
                            new GetHTMLData2(Cobj[i].Geturi(), Date, BulidingName, Cobj[i].GetName())
                    ).start();
                }
            }

        }

    }

    //處理接收到的資料並加入listview之中（查詢按鈕）
    public void HandleMyMessage(String myJson) {
        if (bcnt == (brange + 1)) {
            Log.d("result", testString);
            String[] ohoh = myJson.split("\n");
            String[] messageData = ohoh;

            String[] msg2 = new String[messageData.length];
            String[] msg3 = new String[messageData.length];
            String[] msg4 = new String[messageData.length];
            String[] msg5 = new String[messageData.length];
            String[] msg6 = new String[messageData.length];

            for (int i = 0; i < messageData.length; i++) {
                String[] tmp = messageData[i].split(",");
                msg2[i] = tmp[0];
                msg3[i] = tmp[1];
                msg4[i] = tmp[2];
                msg5[i] = tmp[3];
                msg6[i] = tmp[4];
            }


            //=====sort=====
            //building Class , date , Course , Class

            for (int i = 0; i < messageData.length; i++) {
                for (int j = i + 1; j < messageData.length; j++) {

                    String[] s1 = msg2[i].split(" ");


                    int num1 = 0;
                    int notnum1 = 0;

                    try {
                        num1 = Integer.parseInt(s1[1]) * 10;
                    } catch (Exception e) {

                        char c = s1[1].charAt(0);

                        if (c >= 48 && c <= 57) {
                            if (s1[1].contains("F")) {
                                if (s1[1].contains("_")) {
                                    String[] s3 = s1[1].split("_");
                                    num1 = (c - 48) * 1000 + Integer.parseInt(s3[1]);
                                } else {
                                    num1 = (c - 48) * 1000;
                                }
                            } else if (s1[1].contains("_")) {

                                String[] s3 = s1[1].split("_");
                                num1 = Integer.parseInt(s3[0]) * 10 + Integer.parseInt(s3[1]);

                            } else if (s1[1].length() == 4) {

                                char clast = s1[1].charAt(s1[1].length() - 1);

                                if (clast >= 65 && clast <= 90) {

                                    String s3 = s1[1].substring(0, 3);
                                    num1 = Integer.parseInt(s3) * 10 + (clast - 65);

                                } else {
                                    notnum1 = 1;
                                }
                            }

                        } else {
                            notnum1 = 1;
                        }
                    }

                    String[] s2 = msg2[j].split(" ");

                    int num2 = 0;
                    int notnum2 = 0;

                    try {
                        num2 = Integer.parseInt(s2[1]) * 10;
                    } catch (Exception e) {
                        char c = s2[1].charAt(0);
                        if (c >= 48 && c <= 57) {

                            if (s2[1].contains("F")) {

                                if (s2[1].contains("_")) {

                                    String[] s3 = s2[1].split("_");
                                    num2 = (c - 48) * 1000 + Integer.parseInt(s3[1]);

                                } else {
                                    num2 = (c - 48) * 1000;
                                }

                            } else if (s2[1].contains("_")) {

                                String[] s3 = s2[1].split("_");
                                num2 = Integer.parseInt(s3[0]) * 10 + Integer.parseInt(s3[1]);

                            } else if (s2[1].length() == 4) {

                                char clast = s2[1].charAt(s2[1].length() - 1);

                                if (clast >= 65 && clast <= 90) {

                                    String s3 = s2[1].substring(0, 3);
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


                            String tmp2, tmp4, tmp5, tmp6;

                            tmp2 = msg2[i];
                            msg2[i] = msg2[j];
                            msg2[j] = tmp2;

                            tmp4 = msg4[i];
                            msg4[i] = msg4[j];
                            msg4[j] = tmp4;

                            tmp5 = msg5[i];
                            msg5[i] = msg5[j];
                            msg5[j] = tmp5;

                            tmp6 = msg6[i];
                            msg6[i] = msg6[j];
                            msg6[j] = tmp6;

                        }

                    }
                }
            }

            //===============

            final String ID_TITLE = "TITLE", ID_TITLE1 = "TITLE1", ID_TITLE2 = "TITLE2", ID_TITLE3 = "TITLE3", ID_TITLE4 = "TITLE4";

            ArrayList<HashMap<String, String>> myListData = new ArrayList<HashMap<String, String>>();

            ListView listView = (ListView) findViewById(R.id.listview1);

            for (int i = 0; i < messageData.length; ++i) {
                HashMap<String, String> item = new HashMap<String, String>();
                item.put(ID_TITLE, msg2[i]);
                item.put(ID_TITLE1, msg3[i]);
                item.put(ID_TITLE2, msg4[i]);
                item.put(ID_TITLE3, msg5[i]);
                item.put(ID_TITLE4, msg6[i]);
                myListData.add(item);
            }

            listView.setAdapter(new CustomListViewAdapter(
                    MainActivity.this,
                    myListData,
                    R.layout.showclass_listview,
                    new String[]{ID_TITLE, ID_TITLE1, ID_TITLE2, ID_TITLE3, ID_TITLE4},
                    new int[]{R.id.textView5, R.id.tv1, R.id.tv2, R.id.tv3, R.id.tv4})
            );
            Log.d("t", "t");

        }

    }

    //BroadcastReceiver 處理
    public void HandlemyBroadcastReceiver(Context context, Intent intent) {

        if(intent.getAction().equals("MyMessage")) {

            String myJson = intent.getExtras().getString("json");
            HandleMyMessage(myJson);
            //Log.d("result",myJson);
        } else if(intent.getAction().equals("MyMessage2")) {
            for (int j = 0; j < BuildingName.length; j++) {
                if (BuildingName[j].equals("第一教學大樓")) {
                    etBuilding.setText("第一教學大樓");
                    whatBuilding = j;
                    brange = bound[whatBuilding + 1] - (bound[whatBuilding] + 1);
                    etClass.setText("");
                    break;
                }
            }
        } else {
            Intent intent2 = new Intent(getApplicationContext(), Main2Activity.class);
            String myJson = intent.getExtras().getString("json");
            intent2.putExtra("d",myJson);
            intent2.putExtra("json", GetMain0String);
            intent2.putExtra("json2",bound);
            startActivity(intent2);
        }
        //ImageView imgview = (ImageView) findViewById(R.id.imageView4);
        //imgview.setVisibility(View.GONE);

        if(dialog.isShowing())
            dialog.dismiss();
    }

    //顯示目前時間
    public void ShowNowTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) +1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        //System.out.println(year + "-" + month + "-" + day);

        GregorianCalendar GregorianCalendar = new GregorianCalendar(year, month - 1, day - 1);
        String SdayOfWeek = "";
        int dayOfWeek = GregorianCalendar.get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeek) {
            case 1:
                SdayOfWeek = "禮拜一";
                break;
            case 2:
                SdayOfWeek = "禮拜二";
                break;
            case 3:
                SdayOfWeek = "禮拜三";
                break;
            case 4:
                SdayOfWeek = "禮拜四";
                break;
            case 5:
                SdayOfWeek = "禮拜五";
                break;
            case 6:
                SdayOfWeek = "禮拜六";
                break;
            case 7:
                SdayOfWeek = "禮拜日";
                break;
        }

        tvDate.setText(year + "-" + (month) + "-" + day + " " + SdayOfWeek);
        tvTime.setText(hour + ":" + min);
    }

    public void etClassFunction() {
        String ClassArray = "無,";

        for (int i = (bound[whatBuilding] + 1); i <= bound[whatBuilding + 1]; i++) {
            ClassArray += Cobj[i].GetName() + ",";
        }

        if (!ClassArray.equals(""))
            ClassArray = ClassArray.substring(0, ClassArray.length() - 1);

        final String[] list_item = ClassArray.split(",");
        AlertDialog.Builder dialog_list = new AlertDialog.Builder(MainActivity.this);
        dialog_list.setTitle("請選擇教室");
        dialog_list.setItems(list_item, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!list_item[i].equals("無")) {
                    etClass.setText(list_item[i]);
                    brange = 0;
                } else {
                    brange = bound[whatBuilding + 1] - (bound[whatBuilding] + 1);
                    etClass.setText("");
                }
                Toast.makeText(MainActivity.this, "你選的是" + list_item[i], Toast.LENGTH_SHORT).show();
            }
        });
        dialog_list.show();
    }

    public void etBuildingFunction() {
        final String[] list_item = BuildingName;
        AlertDialog.Builder dialog_list = new AlertDialog.Builder(MainActivity.this);
        dialog_list.setTitle("請選擇大樓");
        dialog_list.setItems(list_item, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                for (int j = 0; j < BuildingName.length; j++) {
                    if (list_item[i].equals(BuildingName[j])) {
                        etBuilding.setText(list_item[i]);
                        whatBuilding = j;
                        brange = bound[whatBuilding + 1] - (bound[whatBuilding] + 1);
                        etClass.setText("");
                        break;
                    }
                }
            }
        });
        dialog_list.show();
    }

    public void buttonFunction() {

        testString = "";
        //brange = 0;
        bcnt = 0;

        String t = tvDate.getText().toString();
        String t3 = tvTime.getText().toString();
        if (!t.equals("") && !t3.equals("")) {

            dialog.setMessage("Please wait....");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            String[] t2 = t.split(" ");
            String[] t4 = t3.split(":");
            SearchClassRoom(etBuilding.getText().toString(), etClass.getText().toString(), t4[0] + "," + t2[1]);
        } else {
            Log.d("error", "請填寫日期與時間");
        }
    }

}


