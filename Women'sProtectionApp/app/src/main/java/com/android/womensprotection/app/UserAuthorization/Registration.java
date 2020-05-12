package com.android.womensprotection.app.UserAuthorization;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import com.android.womensprotection.app.R;

import java.util.Objects;
/**
 * Created by Sumanth on 4/01/2018.
 */
public class Registration extends AppCompatActivity {

    EditText ET_NAME,
            ET_PHONE_NO,
            ET_ADDRESS,
            ET_USER_NAME,
            ET_USER_PASS,
            ET_C_PW;
  public  String name,
        phone_no,
        address,
        user_name,
            user_pass,
            confirm_pw;
    public SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);
       setTitle("Women's Protection");

        registerReceiver(broadcast_reciever, new IntentFilter("finish"));

        ET_NAME = (EditText) findViewById(R.id.name);
        ET_PHONE_NO = (EditText) findViewById(R.id.ph_number);
        ET_ADDRESS = (EditText) findViewById(R.id.address);
        ET_USER_NAME = (EditText) findViewById(R.id.user_name);
        ET_USER_PASS = (EditText) findViewById(R.id.new_pw);
        ET_C_PW = (EditText) findViewById(R.id.confirm_pw);
        sp = getSharedPreferences("login", MODE_PRIVATE);
        ET_NAME.requestFocus();

        //if SharedPreferences contains username and password then redirect to Home activity
    }
    public void signup(View v){
        final Animation shAnim = AnimationUtils.loadAnimation(this, R.anim.small_butt_click);
        v.startAnimation(shAnim);
        name=ET_NAME.getText().toString().trim();
        phone_no=ET_PHONE_NO.getText().toString().trim();
        address=ET_ADDRESS.getText().toString().trim();
        user_name = ET_USER_NAME.getText().toString().trim();
        user_pass =ET_USER_PASS.getText().toString().trim();
        confirm_pw=ET_C_PW.getText().toString().trim();

        ET_C_PW.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                user_pass =ET_USER_PASS.getText().toString().trim();
                confirm_pw=ET_C_PW.getText().toString().trim();
                if(user_pass.length()<6&& user_pass!=null){
                    ET_USER_PASS.setError("Password must contain minimum 6 characters");
                }
            }
        });

        if(name.equals("")|| phone_no.equals("")||address.equals("")||user_name.equals("")||user_pass.equals("")) {
           final AlertDialog.Builder Error = new AlertDialog.Builder(this);
            Error.setTitle("Registration failed");
            Error.setMessage("Fields cannot be empty");
            Error.setNegativeButton("Ok",null);
            Error.setIcon(R.drawable.red_alert);
            Error.create();
            Error.show();
            Error.setCancelable(true);
        }
        if(name.isEmpty()){
            ET_NAME.setError("Name can't be empty");
        }else if(name.matches(".*[0-9].*")) {
            ET_NAME.setError("Name must contain alphabets");
        }
        else if(phone_no.isEmpty()){
            ET_PHONE_NO.setError("Phone no is empty");
        }else if(phone_no.length()>10 || phone_no.startsWith("+91")&&phone_no.length()>10) {
            ET_PHONE_NO.setError("Invalid number");
        }else if(!phone_no.matches(".*[0-9].*")) {
            ET_PHONE_NO.setError("Invalid number");
        }else if(phone_no.length()<10) {
            ET_PHONE_NO.setError("Invalid number");
        }else if(address.isEmpty()){
            ET_ADDRESS.setError("Enter the address");
        }else if(user_name.isEmpty()) {
            ET_USER_NAME.setError("User name is required");
        }else if(user_pass.isEmpty()) {
            ET_USER_PASS.setError("Enter a password");
        }else if(confirm_pw.isEmpty()) {
            ET_C_PW.setError("Re-enter the password");
        }else if(!Objects.equals(user_pass, confirm_pw)) {
            ET_C_PW.setError("Password do not match");
        }else if(user_name.length()<6) {
            ET_USER_NAME.setError("username must contain minimum 6 characters");
        }else if(user_pass.length()<6){
            ET_USER_PASS.setError("Password is too short");
        }
        else {
            String method = "register";
            Export_Information EI = new Export_Information(this);
            EI.execute(method, name, phone_no, address, user_name, user_pass);
            ET_USER_NAME.setFocusable(true);
        }
    }
    /**Receives the message finish from another activity and finishes the current activity*/
    BroadcastReceiver broadcast_reciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent intent) {
            String action = intent.getAction();
            if (action.equals("finish")) {
                //finishing the activity
                finish();
            }
        }
    };
}

