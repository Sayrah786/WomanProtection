package com.android.womensprotection.app.Tools;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import com.android.womensprotection.app.Login;
import com.android.womensprotection.app.MessageService.Display_Contacts;
import com.android.womensprotection.app.MessageService.Fetch_Contacts;
import com.android.womensprotection.app.R;

/**
 * Created by Sumanth on 23-02-2018.
 */

public class show_contacts extends AppCompatActivity {

    String url="http://192.168.43.172/fetch/alertload.php";

    ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tool_show_con);
        setTitle("Contacts");
        lv= (ListView) findViewById(R.id.lv);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
        String user=sp.getString("username","");

        String key="Display_Contacts";
        final Fetch_Contacts FC=new Fetch_Contacts(key,this,url,lv,user);
        FC.execute(user);
    }
    public void HomeLoader(View v) {
        startActivity(new Intent(show_contacts.this,Login.class));
    }

    public  void addcon(View v){
        startActivity(new Intent(show_contacts.this ,tool_add_contacts.class));
        finish();
    }
  @Override
  public boolean onSupportNavigateUp() {
      finish();
      return true;
  }

}