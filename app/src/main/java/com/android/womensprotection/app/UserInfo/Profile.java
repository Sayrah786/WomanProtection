package com.android.womensprotection.app.UserInfo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.android.womensprotection.app.Home;
import com.android.womensprotection.app.R;

import java.util.zip.Inflater;


/**
 * Created by Sumanth on 03-02-2018.
 */

public class Profile extends AppCompatActivity {
    EditText ET_NAME,ET_ADDRESS,ET_PHONE,ET_UNAME;
    String url="http://192.168.43.172/fetch/load_personInfo.php";
    Button ALTER;
    public TextView tv_name;
    int Counter=0;
    @Override
    public void onCreate(Bundle SI) {
        super.onCreate(SI);
        setContentView(R.layout.menu_profile);
        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
        String user = sp.getString("username", " ");
        setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ET_NAME=(EditText) findViewById(R.id.name);
        ET_PHONE=(EditText) findViewById(R.id.ph_number);
        ET_ADDRESS=(EditText) findViewById(R.id.address);
        ET_UNAME=(EditText) findViewById(R.id.u_name);
        ALTER=(Button)findViewById(R.id.alter);
        tv_name=(TextView)findViewById(R.id.Tv_name);
        ALTER.setText("Edit");

      Disable_ET();
        SharedPreferences pro=getSharedPreferences("profile",MODE_PRIVATE);
        if(pro.contains("name")){
            tv_name.setText(pro.getString("name",""));
            ET_NAME.setText(pro.getString("name",""));
            ET_PHONE.setText(pro.getString("phone_no",""));
            ET_ADDRESS.setText(pro.getString("address",""));
            ET_UNAME.setText(pro.getString("u_name",""));
        }
        else {
            Profile_Data p = new Profile_Data(this, url, tv_name, ET_NAME,ET_PHONE,ET_ADDRESS);
            p.execute(user);
            ET_UNAME.setText(user);
        }
    }
    public void EditProfile(View v) {
        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
        String user = sp.getString("username", " ");
        Enable_ET();
        ALTER.setText("Update");
        Counter++;
        if(Counter==2) {
            String name, phone_no, address;
            name =  ET_NAME.getText().toString().trim();
            phone_no = ET_PHONE.getText().toString().trim();
            address = ET_ADDRESS.getText().toString().trim();
            tv_name.setText(ET_NAME.getText());
            if(name.length()==0) {
                ET_NAME.setError("Name is required");
                Counter=Counter-1;
            }else if(name.matches(".*[0-9].*")) {
                ET_NAME.setError("Name should contain alphabets");
                Counter=Counter-1;
            }else if(phone_no.length()<10) {
                ET_PHONE.setError("Invalid number");
                Counter=Counter-1;
            }else if(phone_no.matches(".*[A-Z].*") || phone_no.matches(".*[a-z].*")){
                ET_PHONE.setError("Invalid number");
                Counter=Counter-1;
            }else if(phone_no.length()==0){
                ET_ADDRESS.setError("Address is required");
                Counter=Counter-1;
            }else {
                Edit_Profile ep = new Edit_Profile(this);
                ep.execute("UPDATE", user, name,phone_no,address);
                Disable_ET();
                ALTER.setText("Edit");
                Counter=Counter-2;
            }
        }
    }
    private void Disable_ET(){
        ET_UNAME.setEnabled(false);
        ET_NAME.setEnabled(false);
        ET_PHONE.setEnabled(false);
        ET_ADDRESS.setEnabled(false);

        ET_NAME.setBackgroundResource(R.drawable.tf_bg333);
        ET_ADDRESS.setBackgroundResource(R.drawable.tf_bg333);
        ET_PHONE.setBackgroundResource(R.drawable.tf_bg333);
        ET_UNAME.setBackgroundResource(R.drawable.tf_bg333);

        ET_UNAME.setTextColor(Color.parseColor("#B5FF742F"));
        ET_PHONE.setTextColor(Color.parseColor("#B5FF742F"));
        ET_NAME.setTextColor(Color.parseColor("#B5FF742F"));
        ET_ADDRESS.setTextColor(Color.parseColor("#B5FF742F"));

        ALTER.requestFocus();

    }
    private void Enable_ET(){
        ET_NAME.setBackgroundResource(R.drawable.tf_bg4444);
        ET_ADDRESS.setBackgroundResource(R.drawable.tf_bg4444);
        ET_PHONE.setBackgroundResource(R.drawable.tf_bg4444);

        ET_NAME.setEnabled(true);
        ET_PHONE.setEnabled(true);
        ET_ADDRESS.setEnabled(true);

        ET_PHONE.setTextColor(Color.parseColor("#FF161616"));
        ET_NAME.setTextColor(Color.parseColor("#FF161616"));
        ET_ADDRESS.setTextColor(Color.parseColor("#FF161616"));

    }
    @Override
    public boolean onSupportNavigateUp() {
        Intent i=new Intent(Profile.this,Home.class);
        startActivity(i);
        finish();
        return true;
    }
    public void onBackPressed(){
        super.onBackPressed();
        Intent i=new Intent(Profile.this,Home.class);
        startActivity(i);
    }
}