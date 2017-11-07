package com.example.borja.mapas;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Borja on 20/10/17.
 */

class DataBaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "favplaces";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE_PLACES = "create table places (_id integer primary key autoincrement,"+
            "latitud float not null, longitud float not null, titulo string,  descripcion string, addres string)";

    private static final String DATABASE_DELETE_PLACES=
            "drop table if exists places";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE_PLACES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DATABASE_DELETE_PLACES);
        sqLiteDatabase.execSQL(DATABASE_CREATE_PLACES);
    }
}
