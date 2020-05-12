package com.android.womensprotection.app.CallingService;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.womensprotection.app.MessageService.Fetch_Contacts;
import com.android.womensprotection.app.R;

/**
 * Created by Sumanth on 18-02-2018.
 */

public class Call_Contacts extends AppCompatActivity {

    String url="http://192.168.43.172/fetch/alertload.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_contacts);
        setTitle("Call contact");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Toast.makeText(Call_Contacts.this,"Press and hold the contact name to call",Toast.LENGTH_LONG).show();
        final ListView lv= (ListView) findViewById(R.id.Contact_details);
        String key="Call_Contacts";
        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
        String user=sp.getString("username","");
        final Fetch_Contacts FC=new Fetch_Contacts(key,this,url,lv,user);
        FC.execute(user);
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}
