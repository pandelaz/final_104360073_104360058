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

    LoveDBHelper dbhelper;
    private ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

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
    }


}


