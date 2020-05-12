package com.android.womensprotection.app.UserInfo;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

/**
 * Created by Sandeep on 13-02-2018.
 */

public class Profile_Data extends AsyncTask<String,Integer,String> {

    Context c;
    String url;

    EditText nam,address,phn;
    TextView tv;
    ProgressDialog pd;

    public Profile_Data(Context c, String url, TextView tv, EditText nam,EditText phn,EditText address) {
        this.c = c;
        this.url= url;
        this.tv=tv;
        this.nam=nam;
        this.phn=phn;
        this.address=address;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        pd=new ProgressDialog(c);
        pd.setTitle("Fetch Data");
        pd.setMessage("Fetching Data...Please wait");
        pd.show();
    }

    @Override
    protected String doInBackground(String... params) {
        String po=params[0];
        String data=downloadData(po);
        return data;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        pd.dismiss();
        Profile_Load p=new Profile_Load(c,s,tv,nam,phn,address);
        p.execute();
    }

    private String downloadData(String po)
    {
        //connect and get a stream
        InputStream is=null;
        String line =null;
        String pos=po;

        try {
            URL setUrl=new URL(url);
            HttpURLConnection con= (HttpURLConnection) setUrl.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            //httpURLConnection.setDoInput(true);
            OutputStream OS = con.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));
            String data = URLEncoder.encode("pos", "UTF-8") + "=" + URLEncoder.encode(pos, "UTF-8");
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


