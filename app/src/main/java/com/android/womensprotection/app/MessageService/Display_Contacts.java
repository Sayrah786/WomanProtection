package com.android.womensprotection.app.MessageService;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.womensprotection.app.Home;
import com.android.womensprotection.app.Login;
import com.android.womensprotection.app.R;
import com.android.womensprotection.app.UserAuthorization.Registration;

/**
 * Created by Sumanth on 17-02-2018.
 */

public class Display_Contacts extends AppCompatActivity {

    String url="http://192.168.43.172/fetch/alertload.php";

     ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_contacts);
         lv= (ListView) findViewById(R.id.lv);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        clearRegSession();
        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
        String user=sp.getString("username","");

        String key="Display_Contacts";
        final Fetch_Contacts FC=new Fetch_Contacts(key,this,url,lv,user);
        FC.execute(user);
        Intent intent = new Intent("finish_ImportContacts");
        sendBroadcast(intent);
    }
    public void HomeLoader(View v) {
        startActivity(new Intent(Display_Contacts.this,Home.class));
        finish();
    }

    public  void addcon(View v){
        startActivity(new Intent(Display_Contacts.this ,ImportContacts.class));
    }
    public void clearRegSession() {
        SharedPreferences ss=getSharedPreferences("login",MODE_PRIVATE);
        SharedPreferences.Editor e=ss.edit();
        e.remove("isRegistered");
        e.apply();
        e.commit();
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}