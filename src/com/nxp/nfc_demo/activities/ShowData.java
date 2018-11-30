package com.nxp.nfc_demo.activities;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import com.opencsv.CSVWriter;

import com.nxp.ntagi2cdemo.R;

import java.io.File;
import java.io.FileWriter;

public class ShowData extends Activity {

    DatabaseHelper dbHelper;
    ListView listView;
    Button export;
    Button clear;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.data_show);


        dbHelper = new DatabaseHelper(this);

        final ShowData temp=new ShowData();
        final Cursor cursor = dbHelper.getAllData();
        String [] columns = new String[] {
                dbHelper.SUBJECT,
                dbHelper.DESC,
                dbHelper.VOL,
                dbHelper.VOUT,
                dbHelper.RAW
        };
        int [] widgets = new int[] {
                R.id.resistance,
                R.id.time_stamp,
                R.id.volume_show,
                R.id.vout_show,
                R.id.raw_show
        };



        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this, R.layout.listview,
                cursor, columns, widgets, 0);
        listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(cursorAdapter);

        clear=(Button) findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbHelper.deleteAllRows();
//                final Cursor cursor_new = dbHelper.getAllData();
//                String [] columns = new String[] {
//                        dbHelper.SUBJECT,
//                        dbHelper.DESC
//                };
//                int [] widgets = new int[] {
//                        R.id.resistance,
//                        R.id.time_stamp
//                };
//
//
//                SimpleCursorAdapter cursorAdapter_new = new SimpleCursorAdapter(temp, R.layout.listview,
//                        cursor, columns, widgets, 0);
//                listView.setAdapter(cursorAdapter_new);
            }
        });

        export=(Button) findViewById(R.id.export);
        export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Long tsLong = System.currentTimeMillis()/1000;
                String ts = tsLong.toString();
                String name=ts+"_mp.csv";
                File exportDir = new File(Environment.getExternalStorageDirectory(), name);
                if (!exportDir.exists())
                {
                    exportDir.mkdirs();
                }

                File file = new File(exportDir, name);
                try
                {
                    file.createNewFile();
                    CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
                    SQLiteDatabase db = dbHelper.getReadableDatabase();
                    Cursor curCSV = db.rawQuery("SELECT * FROM Readings",null);
                    csvWrite.writeNext(curCSV.getColumnNames());
                    while(curCSV.moveToNext())
                    {
                        //Which column you want to exprort
                        String arrStr[] ={curCSV.getString(0),curCSV.getString(1),curCSV.getString(2),curCSV.getString(3),curCSV.getString(4),curCSV.getString(5),curCSV.getString(6)};
                        csvWrite.writeNext(arrStr,false);
                    }
                    System.out.println("Done Export");
                    //dbHelper.deleteAllRows();
                    csvWrite.close();
                    curCSV.close();
                }
                catch(Exception sqlEx)
                {
                    System.out.println("Show data sql error");
                }
            }
        });
    }

}
