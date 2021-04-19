package com.example.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.criminalintent.CrimeDbSchema.CrimeTable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeLab {

    private static CrimeLab sCrimeLab;  //instance of class
   // private List<Crime> mCrimes;        //list to store objects of crime
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static CrimeLab get(Context context)   //static method to return instance of class
    {
        if(sCrimeLab == null)
        {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    public Crime getCrime(UUID id)      //to return crime according to id passed
    {

       CrimeCursorWrapper cursor = queryCrimes(
               CrimeTable.cols.UUID + " = ? "
               ,new String[]{id.toString()});       //because we are giving only one id so it will return a single object

       try{
           if(cursor.getCount()==0)
           {
               return null;
           }
           cursor.moveToFirst();
           return cursor.getCrime();
       }
       finally {
           cursor.close();
       }

    }


    //to provide file paths to images
    public File getPhotoFile(Crime crime)
    {
        File filesDir = mContext.getFilesDir();
        return new File(filesDir , crime.getPhotoFilename());
    }

    public void addCrime(Crime c)
    {
      //  mCrimes.add(c);

        ContentValues values = getContentValues(c);
        mDatabase.insert(CrimeTable.NAME,null,values);
    }

    public List<Crime> getCrimes()      //  to return list of crimes
    {
      //  return mCrimes;

       List<Crime> crimes = new ArrayList<>();

       CrimeCursorWrapper cursor = queryCrimes(null ,null);

       try
       {
           cursor.moveToFirst();
           while(!cursor.isAfterLast())
           {
               crimes.add(cursor.getCrime());
               cursor.moveToNext();
           }

       }
      finally {
           cursor.close();
       }

       return crimes;
    }

    private CrimeLab(Context context)       //private Constructor of singleton class
    {

        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext)
                .getWritableDatabase();

      //  mCrimes = new ArrayList<>();

//       for(int i=0;i<100;i++)   //adding objects to list of type crime
//       {
//           Crime crime = new Crime();
//           crime.setTitle("Crime #" + i);
//           crime.setSolved(i%2==0);
//           mCrimes.add(crime);
//       }                      removed because addCrime method is defined
    }


    //writing and updating to databases
    private static ContentValues  getContentValues(Crime crime)
    {
        ContentValues values = new ContentValues();
        values.put(CrimeTable.cols.UUID,crime.getMid().toString());
        values.put(CrimeTable.cols.TITLE , crime.getTitle());
        values.put(CrimeTable.cols.DATE , crime.getDate().getTime());
        values.put(CrimeTable.cols.SOLVED , crime.isSolved() ? 1 : 0);
        values.put(CrimeTable.cols.SUSPECT, crime.getSuspect());
        values.put(CrimeTable.cols.PHONE , crime.getSuspectPhone());

        return values;
    }

    //to update database
    public void updateCrime(Crime crime)
    {
        String uuidString = crime.getMid().toString();
        ContentValues values = getContentValues(crime);

        mDatabase.update(CrimeTable.NAME , values ,
                CrimeTable.cols.UUID + "=?" ,
                new String[]{uuidString});
    }


    private CrimeCursorWrapper queryCrimes(String whereClause , String[] whereArgs)
    {
        Cursor cursor = mDatabase.query(
                CrimeTable.NAME,
                null   , //columns - null select all columns
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new CrimeCursorWrapper(cursor);  //do same thing as return cursor;
    }
}
