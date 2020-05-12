package com.android.womensprotection.app.MessageService;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.womensprotection.app.Home;
import com.android.womensprotection.app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Sumanth on 17-02-2018.
 */

public class Data_Loader extends AsyncTask<Void,Integer,Integer> {

    Context c;
    ListView lv, contacts_details;
    String data;
    ArrayList<String> c_name = new ArrayList<>();
    ArrayList<String> mob_no = new ArrayList<>();
    ArrayAdapter<String> adapter;
    ProgressDialog pd;
    String key,user;
    public Data_Loader(String key, Context c, String data, ListView lv,String user) {
        this.key = key;
        this.c = c;
        this.data = data;
        this.lv = lv;
        this.contacts_details = lv;
        this.user=user;

    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd = new ProgressDialog(c);
        pd.setTitle("Parser");
        pd.setMessage("Parsing ....Please wait");
        pd.show();
    }

    @Override
    protected Integer doInBackground(Void... params) {

        return this.parse();
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);

        if (integer == 1) {
            //ADAPTER
            adapter = new ArrayAdapter<String>(c, android.R.layout.simple_list_item_1, c_name);

            //ADAPT TO LISTVIEW
            lv.setAdapter(adapter);

            //LISTENET
            if (key == "Display_Contacts") {

                lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        deleteDialog((int)id);
                        return false;
                    }

                });
            } else {
                contacts_details.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        call(id);
                        return false;
                    }
                });
            }
        } else {
            Toast.makeText(c, "Unable to Parse", Toast.LENGTH_SHORT).show();
        }
        pd.dismiss();
    }
    //PARSE RECEIVED DATA
    private int parse() {
        try {
            //ADD THAT DATA TO JSON ARRAY FIRST
            JSONArray ja = new JSONArray(data);
            //CREATE JO OBJ TO HOLD A SINGLE ITEM
            JSONObject jo = null;
            c_name.clear();
            //LOOP THRU ARRAY
            for (int i = 0; i < ja.length(); i++) {
                jo = ja.getJSONObject(i);
                //RETRIOEVE NAME
                String name = jo.getString("name");
                String mob = jo.getString("mobile_no");
                //ADD IT TO OUR ARRAYLIST
                c_name.add(name);
                mob_no.add(mob);
            }
            return 1;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public void deleteDialog(final int getId) {
         AlertDialog.Builder Delete_Dbox = new AlertDialog.Builder(c);

        Delete_Dbox.setTitle(c_name.get(getId));
        Delete_Dbox.setIcon(R.drawable.ic_contact);
        Delete_Dbox.setItems(R.array.Dialog_options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    String method = "delete";
                    Delete_Contact dc=new Delete_Contact(c);

                    dc.execute(method, c_name.get(getId),user);
                    adapter.remove(adapter.getItem(getId));
                    adapter.notifyDataSetChanged();
                } else if (which == 1) {
                    AlertDialog.Builder Details = new AlertDialog.Builder(c);
                    Details.setTitle(c_name.get(getId));
                    Details.setMessage(mob_no.get(getId));
                    Details.create();
                    Details.show();
                }
            }
        });
        Delete_Dbox.create();
        Delete_Dbox.show();
    }

    public void call(long id) {
        String number = mob_no.get((int) id);
        final Intent call_fun = new Intent(Intent.ACTION_CALL);
        call_fun.setData(Uri.parse("tel:" + number));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (c.checkSelfPermission(android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(c.getApplicationContext(), "Unissued call permission", Toast.LENGTH_SHORT).show();
                return;
            } else {
                c.startActivity(call_fun);
            }
        }
    }
}
