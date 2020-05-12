package com.android.womensprotection.app;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.womensprotection.app.MessageService.ImportContacts;
import com.android.womensprotection.app.UserAuthorization.Export_Information;
import com.android.womensprotection.app.UserAuthorization.Registration;

import java.util.Objects;


public class Login extends AppCompatActivity {
    TextView TVSignup;
    Button submit;
    public EditText l_id, pw;
    public ProgressDialog pds;
    public String username;
    public SharedPreferences sp;

    String login_name, login_pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // da.flags(););
        pds = new ProgressDialog(Login.this);
        pds.dismiss();
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.SEND_SMS};
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        FLogin();
        registerReceiver(broadcast_reciever, new IntentFilter("finish"));

        sp = getSharedPreferences("login", MODE_PRIVATE);
        //if SharedPreferences contains username and password then redirect to Home activity
        String regCheck = sp.getString("isRegistered", "  ");
        if (Objects.equals(regCheck, "registration")) {
            startActivity(new Intent(Login.this, ImportContacts.class));
            finish();
        } else if (sp.contains("username") && sp.contains("password")) {
            startActivity(new Intent(Login.this, Home.class));
            finish();
        }
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {
            Toast.makeText(Login.this, "Wi-fi is connected", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(Login.this, "Network error", Toast.LENGTH_SHORT).show();
        }

    }

    public void FLogin() {

        setContentView(R.layout.login);

        final Animation shAnim = AnimationUtils.loadAnimation(this, R.anim.small_butt_click);

        final AnimationSet newA_Set = new AnimationSet(false);

        TVSignup = (TextView) findViewById(R.id.Signup);
        submit = (Button) findViewById(R.id.Submit);
        l_id = (EditText) findViewById(R.id.editText);
        pw = (EditText) findViewById(R.id.editText2);

        login_name = l_id.getText().toString().trim();
        login_pass = pw.getText().toString().trim();
        newA_Set.addAnimation(shAnim);

        TVSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(shAnim);
                Intent intent = new Intent(Login.this, Registration.class);
                startActivity(intent);
            }
        });
        l_id.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (l_id.getText().length() == 0) {
                    l_id.setError("Login name cannot be empty");
                }
            }
        });
        pw.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (pw.getText().length() == 0) {
                    pw.setError("Enter password");
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                } else {
                    return true;
                }
            }
        }
        return true;
    }

    public void submit(View v) {
        final Animation shAnim = AnimationUtils.loadAnimation(this, R.anim.small_butt_click);
        v.startAnimation(shAnim);
        login_name = l_id.getText().toString();
        login_pass = pw.getText().toString();
        String method = "login";
        Export_Information EI = new Export_Information(this);
        EI.execute(method, login_name, login_pass);
    }

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
