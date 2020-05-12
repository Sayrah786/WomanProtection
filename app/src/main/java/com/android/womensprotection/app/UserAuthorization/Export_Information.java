package com.android.womensprotection.app.UserAuthorization;


/**
 * Created by Sumanth on 19-01-18.
 */

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import com.android.womensprotection.app.Home;
import com.android.womensprotection.app.Login;
import com.android.womensprotection.app.MessageService.ImportContacts;
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

import static android.content.Context.MODE_PRIVATE;


public class Export_Information extends AsyncTask<String,Void,String> {
    AlertDialog alertDialog;
    Context ctx;
    public String getResult;
    public boolean isValid=false;
    SharedPreferences sp;
    String login_name,login_pass,user_name,user_pass;
    ProgressDialog pd;
    public Export_Information(Context ctx)
    {

        this.ctx =ctx;
    }
    @Override
    protected void onPreExecute() {
        alertDialog = new AlertDialog.Builder(ctx).create();
        alertDialog.setTitle("Login Information....");
        pd=new ProgressDialog(ctx);
        pd.setTitle("Registration");
        pd.setMessage("Authorizing...Please wait");
        pd.show();
    }
    @Override
    protected String doInBackground(String... params) {
        String reg_url = "http://192.168.43.172/webapp/register.php";
        String login_url = "http://192.168.43.172/webapp/login.php";
        String method = params[0];
        if (method.equals("register")) {
            String name = params[1];
            String phone_no = params[2];
            String address = params [3];
            user_name = params[4];
            user_pass = params[5];
            try {
                URL url = new URL(reg_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                //httpURLConnection.setDoInput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));
                String data = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8") + "&" +
                        URLEncoder.encode("phone_no", "UTF-8") + "=" + URLEncoder.encode(phone_no, "UTF-8") + "&" +
                        URLEncoder.encode("address", "UTF-8") + "=" + URLEncoder.encode(address, "UTF-8") + "&" +
                        URLEncoder.encode("user_name", "UTF-8") + "=" + URLEncoder.encode(user_name, "UTF-8") + "&" +
                        URLEncoder.encode("user_pass", "UTF-8") + "=" + URLEncoder.encode(user_pass, "UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                String response = "";
                String line = "";
                while ((line = bufferedReader.readLine())!=null)
                {
                    response+= line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return response;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        else if(method.equals("login"))
        {
            login_name = params[1];
            login_pass = params[2];
            try {
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                String data = URLEncoder.encode("login_name","UTF-8")+"="+URLEncoder.encode(login_name,"UTF-8")+"&"+
                        URLEncoder.encode("login_pass","UTF-8")+"="+URLEncoder.encode(login_pass,"UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                String response = "";
                String line = "";
                while ((line = bufferedReader.readLine())!=null)
                {
                    response+= line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return response;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
    @Override
    protected void onPostExecute(String result) {
        pd.dismiss();
        if(result.equals("Registration Success..."))
        {
            Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
            //wellcomeDialog("Registration success");
            Registration reg=new Registration();
            reg.finish();
            ctx.startActivity(new Intent(ctx,ImportContacts.class));
            session_Reg(user_name,user_pass);
        }
        else
        {
            Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
            //wellcomeDialog("Login success");
            if(result.equals("Login Success...Welcome"))
            {
                pd.dismiss();
                SharedPreferences sp = ctx.getSharedPreferences("login", MODE_PRIVATE);
                Intent ss=new Intent(ctx,Home.class);
                ctx.startActivity(ss);
                SharedPreferences.Editor e = sp.edit();
                e.putString("username",login_name);
                e.putString("password",login_pass);
                e.apply();
                e.commit();
                Intent intent = new Intent("finish");
                ctx.sendBroadcast(intent);
            }
        }
    }
    public void session_Reg(String u_name,String u_pass) {
        SharedPreferences sp = ctx.getSharedPreferences("login", MODE_PRIVATE);
        SharedPreferences.Editor e = sp.edit();
        e.putString("username",u_name);
        e.putString("password",u_pass);
        e.putString("isRegistered","registration");
        e.apply();
        e.commit();
        Intent intent = new Intent("finish");
        ctx.sendBroadcast(intent);
    }
}