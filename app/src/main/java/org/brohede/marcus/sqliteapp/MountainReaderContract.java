package org.brohede.marcus.sqliteapp;

import android.provider.BaseColumns;

/**
 * Created by marcus on 2018-04-25.
 */

public class MountainReaderContract {
    // This class should contain your database schema.
    // See: https://developer.android.com/training/data-storage/sqlite.html#DefineContract

    private MountainReaderContract() {}

    // Inner class that defines the Mountain table contents
        public static class MountainEntry implements BaseColumns {


        public static final String TABLE_NAME = "Mountain";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_LOCATION = "location";
        public static final String COLUMN_NAME_HEIGHT = "height";
        public static final String COLUMN_NAME_IMG_URL = "img_url";
        public static final String COLUMN_NAME_INFO_URL = "info_url";

    }

    public static final String SQL_CREATE  =

        "CREATE TABLE " + MountainEntry.TABLE_NAME + " (" +
            MountainEntry.COLUMN_NAME_NAME + " TEXT, " +
            MountainEntry.COLUMN_NAME_LOCATION + " TEXT, " +
            MountainEntry.COLUMN_NAME_HEIGHT  + " int, " +
            MountainEntry.COLUMN_NAME_IMG_URL + " TEXT, " +
            MountainEntry.COLUMN_NAME_INFO_URL + " TEXT, " +
            MountainEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL" +
            ")";



    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MountainEntry.TABLE_NAME;

}
