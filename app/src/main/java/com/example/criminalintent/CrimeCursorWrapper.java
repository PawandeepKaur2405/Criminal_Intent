package com.example.criminalintent;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.criminalintent.Crime;
import com.example.criminalintent.CrimeDbSchema;
import com.example.criminalintent.CrimeDbSchema.CrimeTable;

import java.util.Date;
import java.util.UUID;

//to read database and return an object based on it
public class CrimeCursorWrapper extends CursorWrapper {

    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime(){

        String uuidString = getString(getColumnIndex(CrimeTable.cols.UUID));
        String title  = getString(getColumnIndex(CrimeTable.cols.TITLE));
        long date = getLong(getColumnIndex(CrimeTable.cols.DATE));
        int isSolved = getInt(getColumnIndex(CrimeTable.cols.SOLVED));
        String suspect = getString(getColumnIndex(CrimeTable.cols.SUSPECT));
        String phone = getString(getColumnIndex(CrimeTable.cols.PHONE));

        Crime crime  = new Crime(UUID.fromString(uuidString));
        crime.setDate(new Date(date));
        crime.setSolved(isSolved != 0 );
        crime.setTitle(title);
        crime.setSuspect(suspect);
        crime.setSuspectPhone(phone);

        return crime;
    }
}
