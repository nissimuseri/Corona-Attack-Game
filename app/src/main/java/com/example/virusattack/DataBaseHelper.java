package com.example.virusattack;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "virusattack.db";
    public static final String TABLE_NAME = "records";
    public static final String COL_1 = "score";
    public static final String COL_2 = "name";
    public static final Integer TABLE_SIZE_LIMIT = 10;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    //Create new table if doesn't exist
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table "+TABLE_NAME+" (score INTEGER, name TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }

    //Insert data to records DB
    public boolean insertDataRecords(int i_score, String i_name) {
        SQLiteDatabase db= this.getWritableDatabase();
        if(QueryNumEntries() >= TABLE_SIZE_LIMIT)
        {
            Cursor cursor = db.rawQuery("select MIN("+COL_1+ "),"+COL_2 + " from "+TABLE_NAME ,null);
            cursor.moveToNext();
            int minScore = cursor.getInt(0);
            String minName = cursor.getString(1);

            if(minScore<i_score)
            {
                db.delete(TABLE_NAME,COL_1 +"=?"+" and "+
                        COL_2+"=?",new String[]{String.valueOf(minScore),minName});
            }
            else
                return false;

        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, i_score);
        contentValues.put(COL_2,i_name);
        long result = db.insert(TABLE_NAME, null, contentValues);

        return result != -1;
    }

    public long QueryNumEntries()
    {
        SQLiteDatabase db = this.getReadableDatabase();

        return DatabaseUtils.queryNumEntries(db, TABLE_NAME);
    }

    //Get all data from records DB
    public ArrayList<Player> getAllData()
    {
        int counter = 0;
        ArrayList<Player> arrayList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_NAME + " ORDER BY " + COL_1 + " DESC",null);
        while(cursor.moveToNext())
        {
            int score = cursor.getInt(0);
            String name = cursor.getString(1);
            Player player = new Player(score,name);
            if(counter < 10)
                arrayList.add(player);
            else
                break;
            counter++;
        }

        return arrayList;
    }
}