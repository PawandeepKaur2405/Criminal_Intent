package com.example.criminalintent;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.criminalintent.CrimeDbSchema.CrimeTable;

public class CrimeBaseHelper extends SQLiteOpenHelper {

    private static final int version =1;
    private static final String DATABASE_NAME = "crimeBase.db";

    public CrimeBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("CREATE TABLE " + CrimeTable.NAME + "(" +
                "_id integer primary key autoincrement," +
                CrimeTable.cols.UUID + "," +
                CrimeTable.cols.TITLE + "," +
                CrimeTable.cols.DATE + "," +
                CrimeTable.cols.SOLVED + "," +
                CrimeTable.cols.SUSPECT + "," +
                CrimeTable.cols.PHONE +
                ")"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
