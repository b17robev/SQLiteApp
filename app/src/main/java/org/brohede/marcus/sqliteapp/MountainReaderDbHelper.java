package org.brohede.marcus.sqliteapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by marcus on 2018-04-25.
 */

public class MountainReaderDbHelper extends SQLiteOpenHelper {

    // TODO: You need to add member variables and methods to this helper class
    // See: https://developer.android.com/training/data-storage/sqlite.html#DbHelper
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "mountains.db";

    MountainReaderDbHelper(Context c){
        //Constructor is run
        super (c,DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Not run for some reason
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("hiho", "Creating table....");
        db.execSQL(MountainReaderContract.SQL_CREATE);
        Log.d("hiho", "Table created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(MountainReaderContract.SQL_DELETE_TABLE);
        onCreate(db);
    }
}
