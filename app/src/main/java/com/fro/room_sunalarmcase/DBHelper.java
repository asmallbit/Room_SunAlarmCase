package com.fro.room_sunalarmcase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteCursor;

import java.util.ArrayList;
import java.util.List;

//Thanks fo the tutorial https://www.youtube.com/watch?v=312RhjfetP8
public class DBHelper extends SQLiteOpenHelper {

    public static final String COL_DATE = "DATE";
    public static final String COL_SUNKEY = "SUNKEY";
    public static final String COL_SUNMAX = "SUNMAX";
    public static final String COL_ISOVER = "ISOVER";
    public static final String TABLE_NAME = "SUN KEY";
    public static final String COL_ID = "ID";

    public DBHelper(Context context) {
        super(context, "sun_key.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + TABLE_NAME + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_DATE + " TEXT, " + COL_SUNKEY + " INT, " + COL_SUNMAX + " INT, " + COL_ISOVER + " BOOL";

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addOne(String date, int sunkey, int sunmax, boolean isover){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_DATE, date);
        cv.put(COL_SUNKEY, sunkey);
        cv.put(COL_SUNMAX, sunmax);
        cv.put(COL_ISOVER, isover);

        long insert = db.insert(TABLE_NAME, null, cv);
        if (insert==-1){
            return false;
        }else {
            return true;
        }
    }



    public List<ALineofDB> getEveryone(){
        List<ALineofDB> returnList = new ArrayList<>();

        String queryString = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);

        if(cursor.moveToFirst()){
            do{
                int id = cursor.getInt(0);
                String date = cursor.getString(1);
                int sunkey = cursor.getInt(2);
                int sunmax = cursor.getInt(3);
                boolean isover = cursor.getInt(4) == 1;

                ALineofDB newLine = new ALineofDB(id, date, sunkey, sunmax, isover);
                returnList.add(newLine);

            } while (cursor.moveToNext());



        } else {
            //failure, dont do anything here

        }

        //close the guys
        cursor.close();
        db.close();
        return returnList;
    }
}
