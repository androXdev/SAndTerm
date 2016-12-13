package com.example.bonny.sandterm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;


public class IP_Validation {

   Context context;
   Socket socket = null;
   MyClientTask myClientTask ;


    //Constructer
    public IP_Validation(Context cont)
     {
        this.context=cont;
     }

    //Returns App IP as String
    public String own_ip()
    {
        WifiManager wifiMan = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        int ipAddress = wifiInf.getIpAddress();
        String ip = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
        return ip;
    }

    public void startMyasync()
    {
        myClientTask = new MyClientTask(MainActivity.Global.dev_IP, MainActivity.Global.PORT);
        myClientTask.execute();
    }

    //AsyncTask for SAndTerm Device Validation
    public class MyClientTask extends AsyncTask<Void, Void, Void> {

        String dstAddress;
        int dstPort;
        String response = "";
        String response_initialize = "0";

        MyClientTask(String addr, int port){
            dstAddress = addr;
            dstPort = port;
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            try {
                socket = new Socket(dstAddress, dstPort);

               // ping the arduino to initialize for device validation protocol
                OutputStream OS = socket.getOutputStream();
                OS.write(response_initialize.getBytes());


                // to receive output from arduino
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
                byte[] buffer = new byte[1024];
                int bytesRead;
                InputStream inputStream = socket.getInputStream();

                while ((bytesRead = inputStream.read(buffer)) != -1){
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                    response += byteArrayOutputStream.toString("UTF-8");
                }






            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "IOException: " + e.toString();
            }finally{
                if(socket != null){
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            response = response.trim();
            if(response.equals("SAndTerm"))
            {
                WiFiConnect.Global.alertDialog1.cancel();
                MainActivity.Global.devValid=true;
                MainActivity.Global.chklooper=true;

            } else
            {
                //retype_IP();
                MainActivity.Global.devValid=false;
                WiFiConnect.Global.alertDialog1.cancel();
            }


            super.onPostExecute(result);
          }

    }

 }
