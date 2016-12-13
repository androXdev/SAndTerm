package com.example.bonny.sandterm;

/**
 * Created by bonny on 2/26/2015.
 */


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by bonny on 2/25/2015.
 */

public class WiFiConnect  {

    //Declaration
    Context c;
    String TAG;
    int loop=0;
    Boolean incorrect=false;
    int wifistate = 0, j;
    String tempSSID;
    String networkSSID;
    String temppass;
    TextView ssidtext;
    EditText passkey;
    ProgressBar wifiprog;
    ProgressBar devprog;
    Boolean prog_success_msg = false;
    public Handler handler = new Handler();
    ImageView wifiimg;
    ImageView devimg;
    TextView incorrect_text;
    IntentFilter intentFilter = new IntentFilter();
    public Handler handler1 = new Handler();
    int network_ID;
    TextView tempTextView;




    public static class Global {

        public static AlertDialog alertDialog1=null;

    }

    //Constructer to grab Context
    public WiFiConnect(Context context) {

        c = context;


    }

    //Create IP Validate Class Object
    IP_Validation ip_class = new IP_Validation(c);

    public void check_wifi_enabled(String networkSSID) {

        WifiManager wifiManager = (WifiManager) c.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        boolean status = wifiManager.isWifiEnabled();
        if (status == false)
            Toast.makeText(c, "Wifi is Disabled!", Toast.LENGTH_SHORT).show();
        else
            check_already_connected();

    }

    //________________________________START_VALIDATION_ALERTBOX___________________________________

    public void start_validation(String tempSSID) {

        networkSSID = tempSSID;
        validate_alert();


    }



    //___________________________________CHECK_ALREADY_CONNECT____________________________________

    public void check_already_connected() {
        tempSSID = "";
        WifiManager wifiManager = (WifiManager) c.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifistate = wifiManager.getWifiState();
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration i : list) {

            if (i.status == 0) //zero equals to connected state
            {
                if (i.SSID.equals("\"" + networkSSID + "\"")) {
                    tempSSID = i.SSID;
                }
            }
        }

        if (tempSSID == "") {
            //call the next method to check if the APs PSK is available in system.
            check_available_AP_PSK();
        } else {
            //SUCCESS___Exit Entire Class

            //Toast.makeText(c, "You are Already Connected to " + tempSSID, Toast.LENGTH_SHORT).show();
            prog_success_msg = true;
            //Progressbar Initialization
            wifiprog.setVisibility(View.INVISIBLE);
            devprog.setVisibility(View.VISIBLE);
            //show success image for wifi
            wifiimg.setVisibility(View.VISIBLE);
            //Call next Class Chain: IP Validation
            ip_class.startMyasync();

        }


    }

    //___________________________________CHECK_AVAILABLE_AP_FOR_PSK________________________________

    public void check_available_AP_PSK() {
        int success = 0;
        tempSSID = "";

        WifiManager wifiManager = (WifiManager) c.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration i : list) {

            if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {

                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();
                success = 1;
                tempSSID = i.SSID;

                break;
            }
        }

        if (success == 1) {
            //SUCCESS___Exit Entire Class
            //Toast.makeText(c, "You are Now Connected to " + tempSSID, Toast.LENGTH_SHORT).show();
            //Progressbar Initialization
            wifiprog.setVisibility(View.INVISIBLE);
            devprog.setVisibility(View.VISIBLE);
            //show success image for wifi
            wifiimg.setVisibility(View.VISIBLE);
            //Call next Class Chain: IP Validation
            ip_class.startMyasync();

        } else {
            //Call the Next method to ask the PSK for the desired AP
            incorrect=false;
            ask_PSK();

        }
    }


    //___________________________________ASK_FOR_PSK___________________________________________

    public void ask_PSK() {


        LayoutInflater li = LayoutInflater.from(c);
        View promptsView = li.inflate(R.layout.popup, null);


        ssidtext = (TextView) promptsView.findViewById(R.id.SSIDtext);
        passkey = (EditText) promptsView.findViewById(R.id.passText);
        incorrect_text = (TextView)promptsView.findViewById(R.id.incorrecttext);

        if(incorrect ==false)
        {incorrect_text.setVisibility(promptsView.INVISIBLE);}
        else
        {incorrect_text.setVisibility(promptsView.VISIBLE);}

        ssidtext.setText(networkSSID);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(c);


        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // get user input and set it to result
                                // edit text
                                temppass = passkey.getText().toString();
                                connect_WPA(temppass);
                                dialog.cancel();

                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {


                                Global.alertDialog1.dismiss();

                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();


    }

//_____________________________VALIDATION_FOR_WIFI_AND MAC_________________________________________

    public void validate_alert() {


        LayoutInflater li = LayoutInflater.from(c);
        View promptsView = li.inflate(R.layout.device_validate_check, null);

        //Declaration
        wifiprog = (ProgressBar) promptsView.findViewById(R.id.wifiprogressBar);
        devprog = (ProgressBar) promptsView.findViewById(R.id.deviceprogressBar2);
        wifiimg = (ImageView)promptsView.findViewById(R.id.wifiimg);
        devimg = (ImageView)promptsView.findViewById(R.id.devimg);

        wifiprog.setVisibility(promptsView.INVISIBLE);
        devprog.setVisibility(promptsView.INVISIBLE);

        wifiimg.setVisibility(promptsView.INVISIBLE);
        devimg.setVisibility(promptsView.INVISIBLE);


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(c);
        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        // set dialog message

        alertDialogBuilder.setCancelable(false);

        // create alert dialog
        Global.alertDialog1 = alertDialogBuilder.create();
        Global.alertDialog1.setCancelable(false);

        // show it
        Global.alertDialog1.show();



        //Start_Validation_Chain

        delayTask.run();
    }

    //___________________________DELAYED_PROGRESSBAR_ENABLE/DISABLE________________________________


    public Runnable delayTask = new Runnable() {

        Context cxt;

        @Override
        public void run() {
            //Progressbar Initialization
            wifiprog.setVisibility(View.VISIBLE);
            devprog.setVisibility(View.INVISIBLE);

            //scanning loop for 15 secs
            if (j > 4) {

                //Start_Validation_Chain
                check_already_connected();



                //stopHandler();
                handler.removeCallbacks(this);
                return;
            } else {


            }
            j++;
            handler.postDelayed(delayTask, 1000);
        }
    };


//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //_____________________________________WiFi_Open___________________________________//

    public void connect_open(String networkSSID) {

        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + networkSSID + "\"";

        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

        WifiManager wifiManager = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
        //final WifiManager wifiManager = (WifiManager) MainActivity.this.getSystemService(Context.WIFI_SERVICE);
        wifiManager.addNetwork(conf);

        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration i : list) {
            if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();

                break;
            }
        }


    }

    //_____________________________________WiFi_WEP___________________________________//

    public void connect_WEP(String networkSSID, String networkPass) {

        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + networkSSID + "\"";

        conf.wepKeys[0] = "\"" + networkPass + "\"";
        conf.wepTxKeyIndex = 0;
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);

        WifiManager wifiManager = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
        //final WifiManager wifiManager = (WifiManager) MainActivity.this.getSystemService(Context.WIFI_SERVICE);
        wifiManager.addNetwork(conf);

        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration i : list) {
            if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();

                break;
            }
        }
    }
//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


    //_____________________________________WiFi_WPA___________________________________//

    public void connect_WPA(String networkPass) {

        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + networkSSID + "\"";
        //refresh loop for check task runnable
        loop=0;

        conf.preSharedKey = "\"" + networkPass + "\"";

        WifiManager wifiManager = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
        //final WifiManager wifiManager = (WifiManager) MainActivity.this.getSystemService(Context.WIFI_SERVICE);
        wifiManager.addNetwork(conf);

        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration i : list) {
            if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();
                network_ID=i.networkId;

                handler1.post(CheckTask);
                break;
            }
        }


    }

    //_________________________________Handler_delay_between_SSID_Password_check______________________________


    public Runnable CheckTask = new Runnable() {

        Context cxt;

        @Override
        public void run() {

            //scanning loop for 15 secs
            if (loop > 10) {

                check_Wifi_state();


                //stopHandler();
                handler1.removeCallbacks(this);
                return;
            } else {


            }
            loop++;
            handler1.postDelayed(CheckTask, 1000);
        }
    };

//____________________________________Wifi_Check_Connectivity____________________________________________


    private void check_Wifi_state() {

        ConnectivityManager connManager = (ConnectivityManager)c.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        WifiManager wifiManager = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);

        if (mWifi.isConnected())
            {
               //Wifi Procedure is Complete

                wifiprog.setVisibility(View.INVISIBLE);
                devprog.setVisibility(View.VISIBLE);
                //show success image for wifi
                wifiimg.setVisibility(View.VISIBLE);


               //Call next Class Chain: IP Validation
                ip_class.startMyasync();



            }
            else
            {
                // wifi connection not established
                incorrect=true;
                wifiManager.removeNetwork(network_ID);
                ask_PSK();
            }


    }




}