package com.nxp.nfc_demo.activities;


import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.security.auth.Subject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class DatabaseHelper extends SQLiteOpenHelper {

    // Table Name
    public static final String TABLE_NAME = "Readings";

    // Table columns
    public static final String _ID = "_id";
    public static final String SUBJECT = "Resistance";
    public static final String VOUT = "Vout";
    public static final String RAW = "Raw";
    public static final String DESC = "time_stamp";
    public static final String VOL = "volume";
    public static final String VOL_PRED = "volume_predicted";

    // Database Information
    static final String DB_NAME = "menstruPal.db    ";

    // database version
    static final int DB_VERSION = 1;

    // Creating table query
    private static final String CREATE_TABLE = "create table " + TABLE_NAME + "(" + _ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + SUBJECT + " REAL NOT NULL, " + DESC + " TEXT, " + VOL+ " REAL , " + VOUT+ " REAL , " + RAW+ " REAL, "+ VOL_PRED+ " REAL  );";

    OkHttpClient client;
    public DatabaseHelper(Context context) {

        super(context, DB_NAME, null, DB_VERSION);
        Log.d("DATABASE_CONSTRUTOR","RUNNING");
        client = new OkHttpClient();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean updatePerson( String resitance, String time_stamp, int id, String vol) {

        float [] values=new float[3];
        StringTokenizer st = new StringTokenizer(resitance, ",");
        int i=0;
        while (st.hasMoreTokens()&& i<3) {
            String token = st.nextToken();
            values[i]=Float.parseFloat(token);
            i++;
        }
        i=0;
        System.out.println("resistance: "+values[0]);
        System.out.println("Vout: "+values[1]);
        System.out.println("raw: "+values[2]);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SUBJECT, values[0]);
        contentValues.put(DESC, time_stamp);
        contentValues.put(VOL, vol);
        contentValues.put(VOUT, values[1]);
        contentValues.put(RAW, values[2]);
        db.update(TABLE_NAME, contentValues, _ID + " = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "SELECT * FROM " + TABLE_NAME, null );
        return res;
    }

    public boolean insertPerson(String name, String ts, String vol) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        float [] values=new float[3];
        StringTokenizer st = new StringTokenizer(name, ",");
        int i=0;
        while (st.hasMoreTokens()&& i<3) {
            String token = st.nextToken();
            values[i]=Float.parseFloat(token);
            i++;
        }
        i=0;
        Request request = new Request.Builder()
                .url("http://192.168.2.229:5000/predict?volume="+values[1])
                .get()
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Postman-Token", "7813f971-1159-4e0c-8b2a-b33cb5ecf1ae")
                .build();

        try {
            Response response = client.newCall(request).execute();
            String s = response.body().string();
            System.out.println("Response s: "+ s);
            float pred=Float.parseFloat(s);
            contentValues.put(VOL_PRED, pred);
            System.out.println("raw: "+pred);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("resistance: "+values[0]);
        System.out.println("Vout: "+values[1]);
        System.out.println("raw: "+values[2]);


        contentValues.put(SUBJECT, values[0]);
        contentValues.put(DESC, ts);
        contentValues.put(VOL, vol);
        contentValues.put(VOUT, values[1]);
        contentValues.put(RAW, values[2]);
        db.insert(TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean deleteAllRows() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from " + TABLE_NAME);
        return true;
    }


}

