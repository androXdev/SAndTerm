package com.example.bonny.sandterm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class Control_Panel extends ActionBarActivity {

    android.support.v7.app.ActionBar actionbar;
    Button T_open,T_close,F_on,F_off,List_v,Logs_v;
    Remote_Connect r_connect = new Remote_Connect(this);

    Activity newActivity;
    String IP = "";
    AlertDialog pop_alert=null;


    public static class Global {

        public static boolean dev_on =false;
        public static boolean dev_off =false;
        public static boolean dev_initialize =false;
        public static boolean terminate = false;
        public static boolean fps_on = false;
        public static boolean fps_off = false;
        public static boolean fps_adduser = false;
        public static boolean fps_deleteuser = false;



    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control__panel);
        actionbar = getSupportActionBar();
        actionbar.setDisplayUseLogoEnabled(true);
        actionbar.setDisplayShowHomeEnabled(true);
        //actionbar.setLogo(R.drawable.title_icon);
        actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#673AB7")));

        T_open = (Button) findViewById(R.id.t_open);
        T_close = (Button) findViewById(R.id.t_close);

        F_on = (Button) findViewById(R.id.f_on);
        F_off = (Button) findViewById(R.id.f_off);

        List_v = (Button) findViewById(R.id.list_v);
        Logs_v = (Button) findViewById(R.id.log_v);



        // On start up
        startup_populate_dash();


        //On click listeners

        T_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Global.dev_on = true;
                Global.dev_off =  false;



            }
        });

        T_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Global.dev_on = false;
                Global.dev_off =  true;



            }
        });

        F_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Global.fps_on=true;


            }
        });

        F_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Global.fps_off=true;

            }
        });

        List_v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });

        Logs_v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });



    }

    //____________________________________ON_STARTUP_______________________________________//

    public void startup_populate_dash()
    {
        TextView textDevice = (TextView)findViewById(R.id.textDevice);
        TextView ipDevice = (TextView)findViewById(R.id.cp_dash_ip);
        TextView apDevice = (TextView)findViewById(R.id.cp_dash_ap);


        Intent intent = getIntent();
        String devname = (String) intent.getSerializableExtra("DeviceName");
        textDevice.setText(devname);

        Cursor cr = fetch_details(devname);

        if(cr!=null)
        {
            cr.moveToFirst();
            String tempIP = cr.getString(2);
            String tempAP = cr.getString(3);

            IP=tempIP;
            ipDevice.setText(tempIP);
            apDevice.setText(tempAP);

            //start the new class for connection

            r_connect.start(IP,6666);
        }

        cr.close();

    }

    public Cursor fetch_details(String name)
    {
        DatabaseOperations dbo = new DatabaseOperations(this);
        SQLiteDatabase sdb = dbo.getReadableDatabase();

        String query = "SELECT * FROM "+ TableData.TableInfo.DEVICE_TABLE_NAME+" WHERE "+TableData.TableInfo.DEVICE_NAME+" = '"+name+"'";

        Cursor CR = sdb.rawQuery(query, null);
        return CR;
    }



    public void popup_adddelete(String param)
    {

        final String params = param;
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView1 = li.inflate(R.layout.popup_user, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        TextView display = (TextView)promptsView1.findViewById(R.id.textView_adddelete);
        display.setText(param);
        if(param=="ADD USER")
        {
          display.setTextColor(Color.GREEN);
        } else
        {
            display.setTextColor(Color.RED);
        }


        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView1);
        // set dialog message
        alertDialogBuilder

                .setCancelable(false)

                .setNeutralButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // get user input and set it to result
                                // edit text

                                if(params=="ADD USER")
                                {
                                    Global.fps_adduser=true;
                                    Toast.makeText(getApplicationContext(), "Add User Protocol Initiated!!!", Toast.LENGTH_LONG).show();
                                } else
                                {
                                    Global.fps_deleteuser=true;
                                    Toast.makeText(getApplicationContext(), "Delete Protocol Initiated!!!", Toast.LENGTH_LONG).show();
                                }


                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.dismiss();
                            }
                        });



        // create alert dialog
        pop_alert = alertDialogBuilder.create();
        pop_alert.setCancelable(false);

        // show it
        pop_alert.show();

    }

























    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_control__panel, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }


        if (id == R.id.action_addUser )
        {

            popup_adddelete("ADD USER");
            //Global.fps_adduser=true;
            //Toast.makeText(getApplicationContext(), "Add User Protocol Initiated!!!", Toast.LENGTH_LONG).show();

        }

        if (id == R.id.action_deleteUser )
        {

            popup_adddelete("DELETE USER");
           //Global.fps_deleteuser=true;
            //Toast.makeText(getApplicationContext(), "Delete Protocol Initiated!!!", Toast.LENGTH_LONG).show();

        }






        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {


        //r_connect.stop_connection();
        Global.terminate=true;
        finish();
        overridePendingTransition(R.anim.animation_fadeout,R.anim.animation_fadein);


    }
}
