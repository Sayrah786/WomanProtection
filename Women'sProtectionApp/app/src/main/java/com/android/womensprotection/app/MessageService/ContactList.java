package com.android.womensprotection.app.MessageService;

/**
 * Created by Sumanth on 19-02-2018.
 */
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
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
public class ContactList extends AsyncTask<String,Integer,String> {

    Context c;
    String address;
    Button lv;

    ProgressDialog pd;

    public ContactList(Context c, String address, Button lv) {
        this.c = c;
        this.address = address;
        this.lv = lv;
    }

    //B4 JOB STARTS
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
        SendMessageService sms=new SendMessageService(c,s,lv);
        sms.execute();

    }

    private String downloadData(String po)
    {
        //connect and get a stream
        InputStream is=null;
        String line =null;
        String pos=po;


        try {
            URL url=new URL(address);
            HttpURLConnection con= (HttpURLConnection) url.openConnection();
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