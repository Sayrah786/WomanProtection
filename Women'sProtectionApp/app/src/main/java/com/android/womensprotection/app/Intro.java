package com.android.womensprotection.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import java.util.Timer;
import java.util.TimerTask;

public class Intro extends AppCompatActivity {
    ProgressBar p;
    MyCountDownTimer myCountDownTimer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro);
        p = (ProgressBar) findViewById(R.id.progressBar2);
        myCountDownTimer = new MyCountDownTimer(2000, 10);
        myCountDownTimer.start();

        SharedPreferences s=getSharedPreferences("intro",MODE_PRIVATE);
        if(s.contains("isFirstTime")){
            startActivity(new Intent(Intro.this,Login.class));
            finish();
        }
        SharedPreferences sp = getSharedPreferences("intro", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("isFirstTime", "yes");
        editor.apply();
        editor.commit();
    }
    public class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            int progress = (int) (millisUntilFinished/10);
            p.setProgress(p.getMax()-progress);
        }

        @Override
        public void onFinish() {
            finish();
        }
    }
}


