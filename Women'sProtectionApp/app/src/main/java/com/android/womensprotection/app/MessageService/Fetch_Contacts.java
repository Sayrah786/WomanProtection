package com.android.womensprotection.app.MessageService;

/**
 * Created by Sumanth on 16-02-18.
 */

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.Parser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class Fetch_Contacts extends AsyncTask<String,Integer,String> {

    Context c;
    String address;
    ListView lv; Button lvv;
    ProgressDialog pd;
    String key,user;
    public Fetch_Contacts(String key,Context c, String address, ListView lv,String user) {
        this.key=key;
        this.c = c;
        this.address = address;
        this.lv = lv;
        this.user=user;
    }

    @Override
    protected String doInBackground(String... params) {
        String user=params[0];
        String data=downloadData(user);
        return data;

    }

    //B4 JOB STARTS
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        pd=new ProgressDialog(c);
        pd.setTitle("Retrieve contacts");
        pd.setMessage("Fetching contacts...Please wait");
        pd.show();
    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        pd.dismiss();

        if(s != null)
        {
            Data_Loader DL=new Data_Loader(key,c,s,lv,user);
            DL.execute();

        }else
        {
            Toast.makeText(c,"Unable to fetch contacts",Toast.LENGTH_SHORT).show();
        }
    }

    private String downloadData(String user)
    {
        //connect and get a stream
        InputStream is=null;
        String line =null;
        String u=user;

        try {
            URL url=new URL(address);
            HttpURLConnection con= (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            //httpURLConnection.setDoInput(true);
            OutputStream OS = con.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));
            String data =
                    URLEncoder.encode("pos", "UTF-8") + "=" + URLEncoder.encode(u, "UTF-8");
            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();
            OS.close();
            is=new BufferedInputStream(con.getInputStream());

            BufferedReader br=new BufferedReader(new InputStreamReader(is));

            StringBuffer sb=new StringBuffer();

            if(br != null) {

                while ((line=br.readLine()) != null) {
                    sb.append(line+"\n");
                }

            }else {
                return null;
            }

            return sb.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(is != null)
            {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}