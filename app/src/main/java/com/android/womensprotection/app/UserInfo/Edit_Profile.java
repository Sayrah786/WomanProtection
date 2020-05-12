package com.android.womensprotection.app.UserInfo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.womensprotection.app.R;

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

import javax.xml.transform.Result;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Sandeep on 16-02-2018.
 */

public class Edit_Profile extends AsyncTask<String,Void,String> {
    AlertDialog alertDialog;
    Context ctx;
    String user,name,address,phone_no;
    Edit_Profile(Context ctx) {
        this.ctx =  ctx;
    }

    @Override
    protected void onPreExecute() {

        alertDialog = new AlertDialog.Builder(ctx).create();
        alertDialog.setTitle("Login Information....");
    }

    @Override
    protected String doInBackground(String... params) {
        String reg_url = "http://192.168.43.172/fetch/updateac.php";
        String method = params[0];

        if (method.equals("UPDATE")) {
            user = params[1];
            name = params[2];
            phone_no = params[3];
            address = params[4];
            try {

                URL url = new URL(reg_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                //httpURLConnection.setDoInput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));
                String data = URLEncoder.encode("u_name", "UTF-8") + "=" + URLEncoder.encode(user, "UTF-8") + "&" +
                                URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8") + "&" +
                                URLEncoder.encode("phn", "UTF-8") + "=" + URLEncoder.encode(phone_no, "UTF-8") + "&" +
                                URLEncoder.encode("addrs", "UTF-8") + "=" + URLEncoder.encode(address, "UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();
                InputStream IS = httpURLConnection.getInputStream();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IS,"iso-8859-1"));
                String response = "";
                String line = "";
                while ((line = bufferedReader.readLine())!=null)
                {
                    response+= line;
                }
                bufferedReader.close();
                IS.close();
                httpURLConnection.disconnect();
                return response ;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return "Error: Could not connect";

    }
    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute (String result) {
        Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
        sessionProfile(name,phone_no,address);

    }
    public void sessionProfile(String name,String phone,String address) {
        SharedPreferences sps=ctx.getSharedPreferences("login",MODE_PRIVATE);
        String u_name=sps.getString("username","");

        SharedPreferences profile=ctx.getSharedPreferences("profile",MODE_PRIVATE);
        SharedPreferences.Editor e=profile.edit();

        e.putString("name",name);
        e.putString("phone_no",phone);
        e.putString("address",address);
        e.putString("u_name",u_name);
        e.apply();
        e.commit();
    }
}

