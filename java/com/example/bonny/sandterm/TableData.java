package com.example.bonny.sandterm;

import android.provider.BaseColumns;

/**
 * Created by bonny on 3/13/2015.
 */
public class TableData {

    //Constructer
    public TableData()
    {

    }

    //Class to initialize table column names
    public static abstract class TableInfo implements BaseColumns
    {
        public static final String DEVICE_ID = "dev_id";
        public static final String DEVICE_NAME = "dev_name";
        public static final String DEVICE_PASSWORD = "dev_pass";
        public static final String DEVICE_IP = "dev_ip";
        public static final String DEVICE_AP = "dev_ap";

        public static final String DEVICE_DB_NAME = "dev_db";
        public static final String DEVICE_TABLE_NAME = "dev_table";

    }




}