package com.example.hongyi.afinal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Created by hongyi on 2017/12/8.
 */
public class LoveDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "HighCPShop.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "Shop";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_CLASSNAME = "classname";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_URL = "url";

    public LoveDBHelper(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
        Log.d(TAG, "DBHepler: TABLE_NAME = " + TABLE_NAME);
        onCreate(this.getWritableDatabase());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Log.d(TAG, "DBHepler: TABLE_NAME = " + TABLE_NAME);
        try {
            db.execSQL(
                    "CREATE TABLE " + TABLE_NAME +
                            "(" + COLUMN_ID + " INTEGER PRIMARY KEY, " +
                            COLUMN_CLASSNAME + " TEXT, " +
                            COLUMN_DATE + " TEXT, " +
                            COLUMN_URL + " TEXT)"
            );
        } catch (Exception e) {

        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Log.d(TAG, "ShopDBHepler: Shop_TABLE_NAME = " + Shop_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertLove(String name, String date, String url) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_CLASSNAME, name);
        contentValues.put(COLUMN_DATE, date);
        contentValues.put(COLUMN_URL, url);

        db.insert(TABLE_NAME, null, contentValues);
        return true;
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        return numRows;
    }

    public boolean updateLove(Integer id, String name, String date, String url) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_CLASSNAME, name);
        contentValues.put(COLUMN_DATE, date);
        contentValues.put(COLUMN_URL, url);

        db.update(TABLE_NAME, contentValues, COLUMN_ID + " = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deleteLove(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d(TAG, "DBHepler: deletelove = " + String.valueOf(id));

        return db.delete(TABLE_NAME,
                COLUMN_ID + " = ? ",
                new String[] { Integer.toString(id) });
    }

    public Integer deleteLovebyName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.delete(TABLE_NAME,
                COLUMN_CLASSNAME + " = ? ",
                new String[] { name });
    }

    public Cursor getLove(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " +
                COLUMN_ID + "=?", new String[]{Integer.toString(id)});
        return res;
    }

    public Cursor getAllLoves() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM " + TABLE_NAME, null );
        return res;
    }
}
