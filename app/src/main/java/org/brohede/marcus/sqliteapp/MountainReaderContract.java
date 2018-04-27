package org.brohede.marcus.sqliteapp;

import android.provider.BaseColumns;

/**
 * Created by marcus on 2018-04-25.
 */

public class MountainReaderContract {
    // This class should contain your database schema.
    // See: https://developer.android.com/training/data-storage/sqlite.html#DefineContract

    public static final String SQL_CREATE =
        "CREATE TABLE " + MountainReaderContract.MountainEntry.TABLE_NAME + " (" +
                        MountainReaderContract.MountainEntry._ID + " INTEGER PRIMARY KEY," +
                        MountainReaderContract.MountainEntry.COLUMN_NAME_NAME + " TEXT," +
                        MountainReaderContract.MountainEntry.COLUMN_NAME_LOCATION + " TEXT," +
                        MountainReaderContract.MountainEntry.COLUMN_NAME_HEIGHT + " INTEGER," +
                        MountainReaderContract.MountainEntry.COLUMN_NAME_IMG_URL+ " TEXT," +
                        MountainReaderContract.MountainEntry.COLUMN_NAME_INFO_URL + " TEXT)";

    public static final String SQL_DELETE_TABLE =
        "DROP TABLE IF EXISTS " + MountainReaderContract.MountainEntry.TABLE_NAME;

    private MountainReaderContract() {}

    // Inner class that defines the Mountain table contents
        public static class MountainEntry implements BaseColumns {


        public static final String TABLE_NAME = "mountains";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_LOCATION = "location";
        public static final String COLUMN_NAME_HEIGHT = "height";
        public static final String COLUMN_NAME_IMG_URL = "img_url";
        public static final String COLUMN_NAME_INFO_URL = "info_url";


    }

}
