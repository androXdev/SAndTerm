package com.example.bonny.sandterm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.bonny.sandterm.TableData.TableInfo;

/**
 * Created by bonny on 3/13/2015.
 */
public class DatabaseOperations extends SQLiteOpenHelper {


    public static final int database_version = 1;

    //Database Create query
    public String CREATE_QUERY = "CREATE TABLE "+ TableInfo.DEVICE_TABLE_NAME + "(" +TableInfo.DEVICE_NAME
            + " TEXT, "+TableInfo.DEVICE_PASSWORD+" TEXT, "+TableInfo.DEVICE_IP+" TEXT, "+TableInfo.DEVICE_AP+" TEXT);";

    //Delete Query
    public String DELETE_QUERY = "DROP TABLE IF EXISTS "+TableInfo.DEVICE_TABLE_NAME+";";

    //Constructor
    public DatabaseOperations(Context context) {

        //initializing Database
        super(context, TableInfo.DEVICE_DB_NAME, null, database_version);
    }

    //CREATE DATABASE
    @Override
    public void onCreate(SQLiteDatabase sdb) {

        sdb.execSQL(CREATE_QUERY);
        Log.d("DB Operations: ", " Table Created");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sdb, int oldVersion, int newVersion) {

    }

    //INSERT NEW TABLE
    public void putInformation(DatabaseOperations dbo,String name,String password,String IP,String AP)
    {
        SQLiteDatabase sdb = dbo.getWritableDatabase();

        ContentValues cv = new ContentValues();


        cv.put(TableInfo.DEVICE_NAME, name);
        cv.put(TableInfo.DEVICE_PASSWORD, password);
        cv.put(TableInfo.DEVICE_IP, IP);
        cv.put(TableInfo.DEVICE_AP, AP);

        long k = sdb.insert(TableInfo.DEVICE_TABLE_NAME,null,cv);

        Log.d("DB Operations: "," One Row Inserted");


    }

    public Cursor getInformation(DatabaseOperations dbo)
    {

        SQLiteDatabase sdb = dbo.getReadableDatabase();
        String[] columns = {TableInfo.DEVICE_NAME,TableInfo.DEVICE_PASSWORD,TableInfo.DEVICE_IP,TableInfo.DEVICE_AP};

        Cursor CR = sdb.query(TableInfo.DEVICE_TABLE_NAME,columns,null,null,null,null,null);

        return CR;

    }

    public void drop_table(DatabaseOperations dbo)
    {
        SQLiteDatabase SQ = dbo.getWritableDatabase();

        SQ.delete(TableInfo.DEVICE_TABLE_NAME,null,null);//for deleting contents in the table
        //SQ.execSQL(DELETE_QUERY);// for deleting the table itself
    }

    //checking for table availability
    boolean isTableExists(SQLiteDatabase mDatabase, String tblNameIn)
    {
        Cursor c =null ;
        boolean tableExists = false;
        /* get cursor on it */
        try
        {
            c = mDatabase.query(tblNameIn, null, null, null, null, null, null);
            tableExists = true;
        }
        catch (Exception e) {
    /* fail */
            // Log.d("Database Operation: ", tblNameIn+" doesn't exist :(((");

        }


        return tableExists;
    }

    public int getRowCount() {
        String countQuery = "SELECT  * FROM " + TableInfo.DEVICE_TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

}
