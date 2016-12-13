package com.example.bonny.sandterm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.ScanResult;


import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;

import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;

import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class MainActivity extends ActionBarActivity {

    TextView scantext;
    final Context context = this;

    String[] SSID = new String[15];
    String[] BSSID = new String[15];
    String tempSSID,tempMAC;
    String TAG;
    String temp_item_spinner;
    Button Accept,Cancel;
    int counter=0;
    boolean scanComplete=false;
    int check,j=0,k=0;




    List<ScanResult> scanList;
    Dialog addDevDialogue ;
    Dialog permissionDlg;
    CheckBox checkbox;
    EditText IPText,DeviceName,DevicePassword;
    TextView incorrectIP;
    TextView APname,DEVip,passTxt;
    Button okbutton;
    ProgressBar perm_prog;
    Spinner devListSpinner;
    TableLayout tablelayout;
    ImageButton imgbutton;
    public Handler handler = new Handler();
    public Handler handler1 = new Handler();
    public Handler checkHandler = new Handler();
    //public Handler wifiStatHandler = new Handler();
    IntentFilter filter = new IntentFilter();
    AlertDialog alertDialog=null;
    AlertDialog wait_alert=null;
    AlertDialog Add_Device=null;
    android.support.v7.app.ActionBar actionbar;
    Activity activity;



    TextView dash_wpa,dash_ip,dash_ap;
    ImageView dash_img;




    //Global Variables
    public static class Global {

        public static String dev_IP = "";
        public static int PORT =6666;
        public static boolean devValid =true;
        public static boolean chklooper=false;
        public static boolean stopcheck=false;

    }


    //_______________________________________On_Create_____________________________________//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionbar = getSupportActionBar();
        actionbar.setDisplayUseLogoEnabled(true);
        actionbar.setDisplayShowHomeEnabled(true);
        //actionbar.setLogo(R.drawable.title_icon);

        actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#673AB7")));
        setContentView(R.layout.activity_main);

        addDevDialogue = new Dialog(this);
        addDevDialogue.setContentView(R.layout.add_device_dialogue);
        permissionDlg = new Dialog(this);
        permissionDlg.setContentView(R.layout.permission_wifi);
        //background
        //View background = findViewById(R.id.background);
        // background.setBackgroundResource(R.drawable.background);
        tablelayout = (TableLayout) findViewById(R.id.devtable);

        dash_wpa = (TextView)findViewById(R.id.dash_wpa);
        dash_ip = (TextView) findViewById(R.id.dash_ip);
        dash_ap = (TextView)findViewById(R.id.dash_ap);
        dash_img = (ImageView) findViewById(R.id.dash_img);


        refreshDevicelist();
        //Check wifi
        getWifiNetworksList();
        //wifi stat dash board handler
        //wifiStatHandler.post(statCheckTask);





    }


    public void refreshDevicelist()
    {
        activity = this;
        Refresh_Device_List refdev = new Refresh_Device_List(this,tablelayout,activity);
        //refresh Device Icons
        refdev.populate_table();
    }


    //_____________________________________Custom_Methods_________________________________//



    //_____________________________________WiFi_Scanner___________________________________//

    private void getWifiNetworksList() {

        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        final WifiManager wifiManager =
                (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);;

        registerReceiver(new BroadcastReceiver() {

            @SuppressLint("UseValueOf") @Override
            public void onReceive(Context context, Intent intent) {

                check=0;
                scanComplete=false;
                counter =0;

                scanList = wifiManager.getScanResults();

                for(int i = 0; i < scanList.size(); i++){
                    String tempData = (scanList.get(i)).toString();
                    //SSID Extraction
                    tempSSID = tempData.substring(tempData.indexOf(":")+1, tempData.indexOf(","));
                    tempSSID = tempSSID.trim();
                    //BSSID Extraction
                    String tempValue;
                    tempValue = tempData.substring(tempData.indexOf(","));
                    tempMAC = tempValue.substring(9,26);
                    tempMAC = tempMAC.trim();

                    SSID[i]= tempSSID;
                    BSSID[i]=tempMAC;
                    tempSSID= "";
                    counter++;

                }

                //Clear the Scan List
                scanList.clear();
                //Unregister the Broadcast Reciever
                unregisterReceiver(this);

                scanComplete=true;


            }


        },filter);
        wifiManager.startScan();

    }

    //_____________________________________Dialogue_Box_________________________________//

    public void addDeviceDialogue ()
    {
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView3 = li.inflate(R.layout.add_device_dialogue, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView3);
        // set dialog message

        // create alert dialog
        Add_Device = alertDialogBuilder.create();
        Add_Device.setCancelable(false);

        // show it
        Add_Device.show();



        devListSpinner = (Spinner)promptsView3.findViewById(R.id.spinner);
        // addDevDialogue.setTitle("Add SAndTerm Device:");
        //  addDevDialogue.setCancelable(false);
        // addDevDialogue.show();



        //initialize the spinner in dialogue box
        spinnerLoader();
        //int i=1;
        //temp_item_spinner = devListSpinner.getItemAtPosition(i).toString();



        IPText = (EditText)promptsView3.findViewById(R.id.IPText);
        IPText.setText("");
        imgbutton = (ImageButton)promptsView3.findViewById(R.id.imageButton);
        scantext = (TextView)promptsView3.findViewById(R.id.scantext);
        scantext.setVisibility(View.INVISIBLE);
        Accept = (Button)promptsView3.findViewById(R.id.acceptbutton);
        Cancel = (Button)promptsView3.findViewById(R.id.cancelbutton);
        incorrectIP = (TextView)promptsView3.findViewById(R.id.rongtxt);
        incorrectIP.setVisibility(View.INVISIBLE);
        final WiFiConnect Connect = new WiFiConnect(this);



        //Refresh WiFi Search
        imgbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //wifi scan enable
                getWifiNetworksList();
                // call the scanning text handler
                handler.removeCallbacks(timedTask);
                timedTask.run();


                //while the scan is not complete, dont call spinner loader


            }
        });

        //Accept Button
        Accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //hideKeyboard
                if(Add_Device.getCurrentFocus() != null)
                {
                    InputMethodManager inputManager = (InputMethodManager) context.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(Add_Device.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }

                if(counter==0)
                {
                    Toast toast = Toast.makeText(context, "No Router to Select", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, -70);
                    toast.show();
                }
                else
                {
                    //Assign SAndTerm Device IP to global Variables
                    Global.dev_IP = IPText.getText().toString();


                    //Initialize Validation Process from WiFiConnect Class
                    Connect.start_validation(temp_item_spinner);

                    //Initialize background Device Validation handler
                    checkHandler.post(checkTask);
                    //reset looper counter to false
                    Global.chklooper =false;
                    Global.stopcheck=false;

                }



            }
        });

        //Cancel Button
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Add_Device.cancel();
                Global.stopcheck=true;

            }
        });

        //Listener for Item click from Spinner
        devListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {


                //temp_item_spinner = devListSpinner.getSelectedItem().toString();
                temp_item_spinner = SSID[arg2];
                Log.d(TAG, "SSID in spinner: " + temp_item_spinner);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }


        });


    }


    //_____________________________________Spinner_Wifi_________________________________//

    public void spinnerLoader()
    {

        try{

            ArrayAdapter<String> devicelist;

            if(counter==0)
            {
                String[] tSSID = new String[1];
                tSSID[0] = "No Wifi Router Found";
                devicelist = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tSSID);
                devListSpinner.setEnabled(false);

            }
            else
            {
                String[] tSSID = new String[counter];

                for (int i = 0; i < counter; i++) {
                    tSSID[i] = SSID[i];
                }
                devicelist = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tSSID);
                devListSpinner.setEnabled(true);


            }

            //devicelist = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tSSID);

            //for skipping this section wen called on Oncreate.

            devicelist.notifyDataSetChanged();
            devListSpinner.setAdapter(devicelist);


        } catch (Exception e) {

            Log.d(TAG, e.toString());
            Toast.makeText(getApplicationContext(),"Wifi is Disabled",Toast.LENGTH_SHORT).show();

        }



    }

    //_____________________________________Handler_[Scanning]_________________________________//

    public Runnable timedTask = new Runnable()
    {

        @Override
        public void run()
        {
            //scanning loop for 15 secs
            if(j>8)
            {
                //process
                spinnerLoader();
                scantext.setVisibility(View.INVISIBLE);
                j=0;
                if(counter==0)devListSpinner.setEnabled(false);
                else devListSpinner.setEnabled(true);


                //stopHandler();
                handler.removeCallbacks(this);

                return;

            }
            else
            {
                scantext.setVisibility(View.VISIBLE);
                //disable spinner during scanning
                devListSpinner.setEnabled(false);

            }
            j++;
            handler.postDelayed(timedTask, 250);
        }
    };

    //_______________________________Wifi_Enable_permission_Alert_Box_____________________________//

    public void perm_dialogue()
    {
        final WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.permission_wifi, null);


        final int wait_temp =10000;

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        // set dialog message
        alertDialogBuilder

                .setCancelable(false)

                .setNeutralButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // get user input and set it to result
                                // edit text
                                wifiManager.setWifiEnabled(true);

                                //call wait_alertbox
                                wait_alert_box();
                                //call delay handler
                                handler1.post(DelayTask);

                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                //dialog.dismiss();
                            }
                        });

        // create alert dialog
        alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }

    //_____________________________________Handler_[delay_while_enabling_Wifi_Module]_________________________________//

    public Runnable DelayTask = new Runnable()
    {

        @Override
        public void run()
        {

            final WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            //scanning loop for 15 secs
            if(wifiManager.isWifiEnabled()==true)
            {
                //process
                perm_prog.setVisibility(View.INVISIBLE);
                wait_alert.dismiss();
                //stopHandler();
                handler1.removeCallbacks(this);
                return;

            }
            else if(k==40) //10 secs has elasped
            {
                wait_alert.dismiss();
                //stopHandler();
                handler1.removeCallbacks(this);
                return;

            }
            else
            {

                perm_prog.setVisibility(View.VISIBLE);
            }
            k++;
            handler1.postDelayed(DelayTask, 250);
        }
    };

    //_____________________________AlertBox_Wifi_Enable__________________________________________//


    public void wait_alert_box()
    {
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView1 = li.inflate(R.layout.wait_wifi_enable, null);

        perm_prog = (ProgressBar)promptsView1.findViewById(R.id.perm_prog);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView1);
        // set dialog message


        // create alert dialog
        wait_alert = alertDialogBuilder.create();
        wait_alert.setCancelable(false);

        // show it
        wait_alert.show();

    }
//___________________________Handler_TO_Check_DeviceValidation_Status__________________________//


    public Runnable checkTask = new Runnable()
    {

        @Override
        public void run()
        {
            //scanning loop for 15 secs
            if(Global.devValid==true)
            {
                //process
                incorrectIP.setVisibility(View.INVISIBLE);
            }
            else
            {
                incorrectIP.setVisibility(View.VISIBLE);
            }
            if(Global.chklooper==true)
            {
                //close ADD SANDTERM DEVICE Dialoguebox
                Add_Device.cancel();
                final_success_alertbox();
                checkHandler.removeCallbacks(this);
                return;
            }
            if(Global.stopcheck==true)
            {
                checkHandler.removeCallbacks(this);
                return;
            }


            checkHandler.postDelayed(checkTask, 200);
        }
    };

    public void final_success_alertbox()
    {

        LayoutInflater li = LayoutInflater.from(context);
        final View promptsView = li.inflate(R.layout.final_success_layout, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        APname = (TextView)promptsView.findViewById(R.id.APname);
        DEVip = (TextView) promptsView.findViewById(R.id.DEVip);
        DeviceName = (EditText) promptsView.findViewById(R.id.devname);
        DevicePassword = (EditText) promptsView.findViewById(R.id.passtxt);
        okbutton = (Button) promptsView.findViewById(R.id.okbutton);
        checkbox = (CheckBox) promptsView.findViewById(R.id.checkBox);
        passTxt = (TextView)promptsView.findViewById(R.id.devpasswordTxt);
        Button cancel = (Button)promptsView.findViewById(R.id.button);

        //creating object for Refresh class & initializing tablelayout

        final Refresh_Device_List refdev = new Refresh_Device_List(this,tablelayout,activity);

        APname.setText(temp_item_spinner);
        DEVip.setText(Global.dev_IP);

        passTxt.setVisibility(View.INVISIBLE);
        DevicePassword.setVisibility(View.INVISIBLE);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        // set dialog message
        alertDialogBuilder
                .setCancelable(false);


        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();


        okbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                //todo after all validation successful
                String uname,upass,uip,uap ="";
                uip=Global.dev_IP;
                uap=temp_item_spinner;
                uname = DeviceName.getText().toString();
                uname = uname.toUpperCase();
                upass = DevicePassword.getText().toString();


                if(uname.length()<=8) {

                    if (checkbox.isChecked()) {
                        if (!DeviceName.getText().toString().equals("") && !DevicePassword.getText().toString().equals("")) {

                            //DB Object
                            DatabaseOperations dbo = new DatabaseOperations(context);
                            //call the create method to creating table
                            dbo.putInformation(dbo, uname, upass, uip, uap);

                            DeviceName.setText("");
                            DevicePassword.setText("");
                            alertDialog.cancel();


                            refdev.populate_table();

                        } else {
                            Toast toast = Toast.makeText(context, "Please Enter Name And Password", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    } else {

                        if (!DeviceName.getText().toString().equals("")) {
                            //DB Object
                            DatabaseOperations dbo = new DatabaseOperations(context);
                            //call the create method to creating table
                            dbo.putInformation(dbo, uname, upass, uip, uap);

                            DeviceName.setText("");
                            DevicePassword.setText("");
                            alertDialog.cancel();
                            refdev.populate_table();

                        } else {
                            Toast toast = Toast.makeText(context, "Please Enter Name", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }

                    }
                } else

                {
                    Toast toast = Toast.makeText(context, "Name must be less than 8 Characters", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                }


            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.cancel();

            }
        });

        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkbox.isChecked())
                {
                    //Toast.makeText(context,"Is Checked",Toast.LENGTH_SHORT).show();
                    passTxt.setVisibility(View.VISIBLE);
                    DevicePassword.setVisibility(View.VISIBLE);
                }

                else
                {
                    passTxt.setVisibility(View.INVISIBLE);
                    DevicePassword.setVisibility(View.INVISIBLE);
                }


            }
        });

    }

    //_________________________wifi_stat_display_dashboard_______________________________________//




    //_____________________________________Other_Options________________________________________//

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_addDevice ) {

            //Either Popup or error for wifi disabled
            WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            boolean status = wifiManager.isWifiEnabled();
            if(status==false)
            {
                perm_dialogue();

            }
            else
                addDeviceDialogue();

            return true;
        }
        else if (id == R.id.action_delete ) {

            //Toast.makeText(getApplicationContext(),"All Devices Deleted!",Toast.LENGTH_LONG).show();

            DatabaseOperations dbo = new DatabaseOperations(context);
            // dbo.drop_table(dbo);
            tablelayout = (TableLayout) findViewById(R.id.devtable);
            // Refresh_Device_List refdev = new Refresh_Device_List(context,tablelayout,activity);
            // refdev.populate_table();

            Refresh_Device_List refdev = new Refresh_Device_List(context,tablelayout,activity);
            refdev.delete_item_alert();


            return true;
        }

        else if (id == R.id.action_about){

            return true;
        }

        else if (id == R.id.action_exit){

            finish();
            return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
