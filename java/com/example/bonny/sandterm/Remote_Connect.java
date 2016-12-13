package com.example.bonny.sandterm;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by bonny on 4/7/2015.
 */
public class Remote_Connect {

    Socket socket;
    Context context;
    MyClientTask myClientTask;





    //constructor
    public Remote_Connect(Context c)
    {
        context = c;
    }




    public void start(String IP, int port)
    {
        myClientTask = new MyClientTask(IP, port);
        myClientTask.execute();
    }

    //AsyncTask for SAndTerm Device Validation
    public class MyClientTask extends AsyncTask<Void, Void, Void> {

        String dstAddress;
        int dstPort;
        String response ="";
        String response_on = "1";
        String response_off = "2";
        String response_initialize = "0";
        String response_fps_on = "3";
        String response_fps_off = "4";
        String response_fps_adduser = "5";
        String response_fps_deleteuser = "6";

        MyClientTask(String addr, int port){
            dstAddress = addr;
            dstPort = port;
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            try {

                socket = null;
                socket = new Socket(dstAddress, dstPort);
                OutputStream OS = socket.getOutputStream();


                while(true)
                {

                    if(Control_Panel.Global.dev_on==true)
                    {
                        OS.write(response_on.getBytes());
                        Control_Panel.Global.dev_on=false;
                    }
                    else if (Control_Panel.Global.dev_off==true)
                    {
                        OS.write(response_off.getBytes());
                        Control_Panel.Global.dev_off=false;
                    }
                    else if(Control_Panel.Global.dev_initialize==true)
                    {

                        OS.write(response_initialize.getBytes());
                        Control_Panel.Global.dev_initialize=false;

                    }

                    else if(Control_Panel.Global.terminate==true)
                    {
                        Control_Panel.Global.terminate=false;
                        break;
                    }

                    else if(Control_Panel.Global.fps_on==true)
                    {
                        OS.write(response_fps_on.getBytes());
                        Control_Panel.Global.fps_on = false;

                    }

                    else if(Control_Panel.Global.fps_off==true)
                    {
                        OS.write(response_fps_off.getBytes());
                        Control_Panel.Global.fps_off = false;
                    }

                    else if(Control_Panel.Global.fps_adduser==true)
                    {
                        OS.write(response_fps_adduser.getBytes());
                        Control_Panel.Global.fps_adduser = false;

                    }

                    else if(Control_Panel.Global.fps_deleteuser==true)
                    {
                        OS.write(response_fps_deleteuser.getBytes());
                        Control_Panel.Global.fps_deleteuser = false;

                    }





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



            super.onPostExecute(result);
        }

    }


    public void stop_connection()
    {

        if(socket != null){
            try {
                socket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }




}
