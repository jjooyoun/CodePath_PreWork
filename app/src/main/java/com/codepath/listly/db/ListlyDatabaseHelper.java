package com.codepath.listly.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ListlyDatabaseHelper extends SQLiteOpenHelper {

    private static ListlyDatabaseHelper sInstance = null;
    private static final String DATABASE_NAME = "listly.db";
    private static final int DATABASE_VERSION = 1;

    private ListlyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    static synchronized ListlyDatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ListlyDatabaseHelper(context);
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createMyTaskTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int currentVersion) {
        drop(db);
        onCreate(db);
    }

    private void createMyTaskTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + ListlyConstant.ListlyField.MY_TASK_TABLE + " ( "
                + ListlyConstant.ListlyField._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ListlyConstant.ListlyField.TASK_NAME + " TEXT, "
                + ListlyConstant.ListlyField.DUE_DATE + " INTEGER, "
                + ListlyConstant.ListlyField.TASK_NOTES + " TEXT, "
                + ListlyConstant.ListlyField.PRIORITY + " INTEGER DEFAULT "
                + ListlyConstant.PRIORITY_HIGH + ", "
                + ListlyConstant.ListlyField.STATUS + " INTEGER DEFAULT "
                + ListlyConstant.STATUS_DONE + ", "
                + ListlyConstant.ListlyField.DATA + " TEXT " + " ) ");
    }

    private void drop(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS "
                + ListlyConstant.ListlyField.MY_TASK_TABLE);
    }
}