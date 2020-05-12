package com.android.womensprotection.app.UserInfo;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Sumanth on 13-02-2018.
 */

public class Profile_Load extends AsyncTask<Void,Integer,Integer> {

    Context c;
    String data;


    ArrayList<String> p_name = new ArrayList<>();
    ArrayList<String> p_number = new ArrayList<>();
    ArrayList<String> p_address = new ArrayList<>();
    EditText nam,address,phn;
    TextView tv;

    ProgressDialog pd;

    public Profile_Load(Context c, String data ,TextView tv, EditText nam, EditText phn, EditText address) {
        this.c = c;
        this.data = data;
        this.tv=tv;
        this.nam=nam;
        this.phn=phn;
        this.address=address;

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
            if(nam==null||phn==null||address==null){
                tv.setText(p_name.get(0));
            }else {
                tv.setText(p_name.get(0));
                nam.setText(p_name.get(0));
                phn.setText(p_number.get(0));
                address.setText(p_address.get(0));
            }
        } else {
            Toast.makeText(c, "No such data", Toast.LENGTH_SHORT).show();
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
            p_name.clear();

            //LOOP THRU ARRAY
            for (int i = 0; i < ja.length(); i++) {
                jo = ja.getJSONObject(i);

                //RETRIOEVE NAME
                String name1 = jo.getString("name");
                String number1 = jo.getString("phone_no");
                String address = jo.getString("address");

                //ADD IT TO OUR ARRAYLIST
                p_name.add(name1);

                p_number.add(number1);

                p_address.add(address);
                sessionProfile(name1,number1,address);
            }
            return 1;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public void sessionProfile(String name,String phone,String address) {
        SharedPreferences sps=c.getSharedPreferences("login",MODE_PRIVATE);
        String u_name=sps.getString("username","");

        SharedPreferences profile=c.getSharedPreferences("profile",MODE_PRIVATE);
        SharedPreferences.Editor e=profile.edit();

        e.putString("name",name);
        e.putString("phone_no",phone);
        e.putString("address",address);
        e.putString("u_name",u_name);
        e.apply();
        e.commit();
    }
}