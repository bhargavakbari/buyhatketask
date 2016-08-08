package com.example.bhargav.buyhatkesmsapp.databasemanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.bhargav.buyhatkesmsapp.datamodels.SMSData;

import java.util.ArrayList;

/**
 * Created by Bhargav on 8/7/2016.
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "buyhatake";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "contacts";
    public static final String MESSAGE_ID = "id";
    public static final String MESSAGE_COLUMN = "message";
    public static final String NUMBER_COLUMN = "phone";

    public String CREATE_TABLE_QUERY = "create table contacts(id text, message text, phone text)";
    public String DROP_TABLE_QUERY = "DROP TABLE IF EXISTS contacts";
    public String SELECT_QUERY = "select * from contacts";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_QUERY);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if(newVersion > oldVersion) {
            sqLiteDatabase.execSQL(DROP_TABLE_QUERY);
            onCreate(sqLiteDatabase);
        }
    }

    public void addAllContacts(ArrayList<SMSData> smsDataList) {

        SQLiteDatabase database = getWritableDatabase();
        for (SMSData smsData: smsDataList) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MESSAGE_ID,smsData.getId());
            contentValues.put(MESSAGE_COLUMN,smsData.getBody());
            contentValues.put(NUMBER_COLUMN,smsData.getNumber());
            database.insert(TABLE_NAME, null, contentValues);
        }
        database.close();
    }

    public ArrayList<SMSData> getAllContacts() {
        ArrayList<SMSData> smsDataArrayList = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(SELECT_QUERY,null);
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            SMSData smsData = new SMSData();
            smsData.setId(cursor.getString(cursor.getColumnIndex(MESSAGE_ID)));
            smsData.setBody(cursor.getString(cursor.getColumnIndex(MESSAGE_COLUMN)));
            smsData.setNumber(cursor.getString(cursor.getColumnIndex(NUMBER_COLUMN)));
            smsDataArrayList.add(smsData);
        }
        cursor.close();
        database.close();
        return smsDataArrayList;
    }

    public void deleteAllRecords() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_NAME,null,null);
        database.close();
    }
}
