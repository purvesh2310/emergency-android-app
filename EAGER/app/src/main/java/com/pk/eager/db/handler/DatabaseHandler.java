package com.pk.eager.db.handler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pk.eager.db.model.Report;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Purvesh on 7/26/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "eager_db";

    private static final String TABLE_REPORTS = "reports";
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_INFORMATION = "information";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_TIMESTAMP = "timestamp";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_REPORTS_TABLE = "CREATE TABLE " + TABLE_REPORTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TITLE + " TEXT,"
                + KEY_INFORMATION + " TEXT," + KEY_LATITUDE + " TEXT,"
                + KEY_LONGITUDE + " TEXT," + KEY_TIMESTAMP + " TEXT)";

        db.execSQL(CREATE_REPORTS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPORTS);

        // Create tables again
        onCreate(db);

    }

    // code to add the new report
    public void addReport(Report report) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, report.getTitle());
        values.put(KEY_INFORMATION, report.getInformation());
        values.put(KEY_LATITUDE, report.getLatitude());
        values.put(KEY_LONGITUDE, report.getLongitude());
        values.put(KEY_TIMESTAMP,report.getTimestamp());

        // Inserting Row
        db.insert(TABLE_REPORTS, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    // code to get all reports in a list view
    public List<Report> getAllReports() {

        List<Report> reportList = new ArrayList<Report>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_REPORTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Report report = new Report();
                report.setId(Integer.parseInt(cursor.getString(0)));
                report.setTitle(cursor.getString(1));
                report.setInformation(cursor.getString(2));
                report.setLatitude(Double.parseDouble(cursor.getString(3)));
                report.setLongitude(Double.parseDouble(cursor.getString(4)));

                // Adding individual report to list
                reportList.add(report);
            } while (cursor.moveToNext());
        }

        // return contact list
        return reportList;
    }
}
