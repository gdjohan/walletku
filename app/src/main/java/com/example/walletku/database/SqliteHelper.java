package com.example.walletku.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqliteHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "walletkuDB";
    public static final int DATABASE_VERSION = 1;
    public static final SQLiteDatabase.CursorFactory DATABASE_FACTORY = null;
    public SqliteHelper(Context context) {
        super(context, DATABASE_NAME, DATABASE_FACTORY, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        RecordDAO recordDAO = new RecordDAO(this);
        recordDAO.createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTable = "DROP TABLE IF EXISTS " + "MsRecord";
        db.execSQL(dropTable);
        String dropAmount = "DROP TABLE IF EXISTS " + "MsAmount";
        db.execSQL(dropAmount);
        onCreate(db);
    }
}
