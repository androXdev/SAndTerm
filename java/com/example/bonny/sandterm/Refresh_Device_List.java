package com.example.bonny.sandterm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by bonny on 3/14/2015.
 */
public class Refresh_Device_List {

    Context c;
    private int table_row=100;
    private int table_column=3;
    TableLayout tablelayout;
    Activity newActivity;
    AlertDialog pass_alert,pass_alert1;
    ArrayAdapter<String> devicelist;
    ListView list;
    int pos=0;



    Button buttonarray[][] = new Button[table_row][table_column];
    String device_names[][] = new String[table_row][table_column];

    public Refresh_Device_List(Context context, TableLayout tableLayout,Activity activity)
    {
        c =context;
        tablelayout = tableLayout;
        newActivity = activity;

    }


    //_____________________For populating the TableLayout of Devices added________________________//

    public void populate_table() {

        int loopcounter=0;
        int rowcount = count_rows();  //database row counts for added devices

        Cursor cr = return_Device_names();
        String[] tempNames = new String[rowcount];

        tablelayout.removeAllViews(); //empty tablelayout

        //if row is empty the dont run loop
        if(rowcount==0){}
        else {

            //move cursor to first index
            cr.moveToFirst();
            //create dynamic buttons for devices
            for (int i = 0; i < table_row; i++) {

                TableRow tablerow = new TableRow(c);
                tablelayout.addView(tablerow);

                //tablerow.setLayoutParams(new TableLayout.LayoutParams(200,200,1.0f));


                for (int j = 0; j < table_column; j++) {
                    if (rowcount == 0) {
                        break;
                    }
                    final int x_cor =i;
                    final int y_cor =j;


                    loopcounter++;

                    Button button = new Button(c);
                    button.setLayoutParams(new TableRow.LayoutParams(100, 120, 1));
                    //button.setGravity(Gravity.LEFT);
                    //text.setText(String.valueOf(i) +", " + String.valueOf(j));

                    TextView text = new TextView(c);
                    text.setTextColor(Color.BLACK);
                    text.setText(cr.getString(0));
                    text.setGravity(1);

                    LinearLayout buttonlayout = new LinearLayout(c);
                    buttonlayout.setOrientation(LinearLayout.VERTICAL);
                    buttonlayout.setGravity(1);

                    buttonlayout.addView(button);
                    buttonlayout.addView(text);

                    tablerow.addView(buttonlayout);

                    buttonarray[i][j] = button; //assign the button coordinates
                    device_names[i][j] = cr.getString(0); // assign the related name
                    tempNames[loopcounter-1] = cr.getString(0);
                    buttonarray[i][j].setBackgroundResource(R.drawable.lock); // assign the image


                    //assigning OnclickListeners on individual buttons
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            button_clicked(x_cor,y_cor);
                        }
                    });

                    cr.moveToNext();

                    if (loopcounter == rowcount) {
                        break;
                    }

                }
                if (rowcount == 0) {
                    break;
                }
                if (loopcounter == rowcount) {
                    break;
                }
            }




        }
    }

    public void button_clicked(int x_cor, int y_cor)
    {
        //Toast.makeText(c,"Device Name: "+device_names[x_cor][y_cor],Toast.LENGTH_SHORT).show();


        final Cursor CR = fetch_password(device_names[x_cor][y_cor]);
        CR.moveToFirst();
        //Toast.makeText(c,"Device Name: "+device_names[x_cor][y_cor] +"\n"+"Password: "+CR.getString(0),Toast.LENGTH_SHORT).show();
        if(CR.getString(0).equals(""))
        {
            Intent intent = new Intent(c,Control_Panel.class);
            intent.putExtra("DeviceName",device_names[x_cor][y_cor]);
            newActivity.startActivity(intent);
            newActivity.overridePendingTransition(R.anim.animation_fadeout,R.anim.animation_fadein);
        }
        else
        {

            LayoutInflater li = LayoutInflater.from(c);
            View promptsView1 = li.inflate(R.layout.pass_alert, null);

            TextView textview  = (TextView)promptsView1.findViewById(R.id.textView14);
            final EditText inputtxt = (EditText) promptsView1.findViewById(R.id.editText);
            Button cancel = (Button)promptsView1.findViewById(R.id.button2);
            Button ok = (Button)promptsView1.findViewById(R.id.button3);
            final String pass = CR.getString(0);
            CR.close();
            final int nx_cor = x_cor;
            final int ny_cor = y_cor;

            textview.setText(device_names[x_cor][y_cor]);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(c);
            // set prompts.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView1);
            // set dialog message

                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String temp = inputtxt.getText().toString();
                            if(pass.equals(temp))
                            {
                                pass_alert.cancel();
                                Intent intent = new Intent(c,Control_Panel.class);
                                intent.putExtra("DeviceName",device_names[nx_cor][ny_cor]);
                                newActivity.startActivity(intent);
                                newActivity.overridePendingTransition(R.anim.animation_fadeout,R.anim.animation_fadein);
                            }
                            else
                            {
                                Toast toast = Toast.makeText(c,"Password is Incorrect!",Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER,0,0);
                                toast.show();
                                inputtxt.setText("");


                            }


                        }
                    });

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                           pass_alert.cancel();

                        }
                    });

            // create alert dialog
            pass_alert = alertDialogBuilder.create();
            pass_alert.setCancelable(false);

            // show it
            pass_alert.show();
        }


    }


    public void delete_item_alert()
    {


        int i=0;
        pos =0;

        LayoutInflater li = LayoutInflater.from(c);
        View promptsView1 = li.inflate(R.layout.delete_item_list, null);


        final TextView txt  = (TextView)promptsView1.findViewById(R.id.textView19);
        TextView txtstatus  = (TextView)promptsView1.findViewById(R.id.textView20);
        Button cancel = (Button)promptsView1.findViewById(R.id.button4);
        Button ok = (Button)promptsView1.findViewById(R.id.button5);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(c);
        alertDialogBuilder.setView(promptsView1);

        txtstatus.setText("Available Devices");



        //populate the local array with names
        Cursor CR = return_Device_names();


        if(CR.getCount()==0)
        {
            txtstatus.setText("No Devices Found");
        }
        else
        {
            final String[] namelist = new String[CR.getCount()];

               CR.moveToFirst();

                do
                {
                    namelist[i] = CR.getString(0);
                    i++;
                }while(CR.moveToNext());

                //Toast.makeText(c,"No of Rows: "+ count,Toast.LENGTH_SHORT).show();

                devicelist = new ArrayAdapter<String>(c,android.R.layout.simple_list_item_1,namelist);
                list = (ListView) promptsView1.findViewById(R.id.listView);
                list.setAdapter(devicelist);


                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            txt.setText(namelist[position]);
                            pos = position;

                        }
                    });


        }

            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    String name = txt.getText().toString();
                    if(name.equals(""))
                    {
                        Toast toast = Toast.makeText(c,"Select A Device To Delete",Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                    }
                    else
                    {
                        delete_data(name);
                        pass_alert1.cancel();

                    }

                 }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    pass_alert1.cancel();

                }
            });


        // create alert dialog
        pass_alert1 = alertDialogBuilder.create();
        pass_alert1.setCancelable(false);

        // show it
        pass_alert1.show();


    }

    public void delete_data(String name)
    {

        DatabaseOperations dbo = new DatabaseOperations(c);
        SQLiteDatabase SQ =dbo.getWritableDatabase() ;

        SQ.delete(TableData.TableInfo.DEVICE_TABLE_NAME,TableData.TableInfo.DEVICE_NAME+" = '"+ name+"'",null);

        populate_table();


    }




    //row counter for device list
    public int count_rows()
    {
        DatabaseOperations dbo = new DatabaseOperations(c);
        int i = dbo.getRowCount();
        //Toast.makeText(c,"No of Rows: "+ i,Toast.LENGTH_SHORT).show();
        return i;
    }


    //return cursor with device names
    public Cursor return_Device_names()
    {
        DatabaseOperations dbo = new DatabaseOperations(c);
        SQLiteDatabase sdb = dbo.getReadableDatabase();

        String query = "SELECT "+ TableData.TableInfo.DEVICE_NAME +" FROM "+ TableData.TableInfo.DEVICE_TABLE_NAME;
        Cursor CR = sdb.rawQuery(query, null);
        return CR;

    }

    //Retutns Cursor with password
    public Cursor fetch_password(String name)
    {
        DatabaseOperations dbo = new DatabaseOperations(c);
        SQLiteDatabase sdb = dbo.getReadableDatabase();

        String query = "SELECT "+ TableData.TableInfo.DEVICE_PASSWORD +" FROM "+ TableData.TableInfo.DEVICE_TABLE_NAME+" WHERE "+TableData.TableInfo.DEVICE_NAME+" = '"+name+"'";

        Cursor CR = sdb.rawQuery(query, null);
        return CR;
    }

}
