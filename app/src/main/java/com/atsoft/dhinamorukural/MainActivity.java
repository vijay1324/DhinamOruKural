package com.atsoft.dhinamorukural;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    TextView thirukural, englishkural, pal, iyal, athigaram, kuralno, exp_soloman, exp_mk, exp_varathan, exp_parimel, exp_manakudavar, exp_munusami, exp_english;
    static int currentNo = 0;
    static int currentAgarathi = 0;
    static int todayKural = 0;
    String[] kuralarr, engkuralarr, iyalarr, athigaramarr,solomanarr, mkarr, varathuarr, parimalarr, manakadavurarr, munusamiarr, englisharr;

    FloatingActionButton pre, next;
    SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View bg = findViewById(R.id.toplinearlayout);
        Drawable backround = bg.getBackground();
        backround.setAlpha(80);
        getAllPermission();
        thirukural = (TextView) findViewById(R.id.thirukuraltv);
        englishkural = (TextView) findViewById(R.id.eng_thirukuraltv);
        pal = (TextView) findViewById(R.id.paltv);
        iyal = (TextView) findViewById(R.id.iyaltv);
        athigaram = (TextView) findViewById(R.id.athigaramtv);
        kuralno = (TextView) findViewById(R.id.kuralnotv);
        exp_soloman = (TextView) findViewById(R.id.exp_salamon_tv);
        exp_mk = (TextView) findViewById(R.id.exp_mk_tv);
        exp_varathan = (TextView) findViewById(R.id.exp_varathu_tv);
        exp_parimel = (TextView) findViewById(R.id.exp_pari_tv);
        exp_manakudavar = (TextView) findViewById(R.id.exp_manakuavar_tv);
        exp_munusami = (TextView) findViewById(R.id.exp_munusami_tv);
        exp_english = (TextView) findViewById(R.id.exp_english_tv);
        pre = (FloatingActionButton) findViewById(R.id.fab_previous);
        next = (FloatingActionButton) findViewById(R.id.fab_next);
        sharedPrefs = getSharedPreferences("kural", Context.MODE_PRIVATE);
        getPreviosValue();
        try {
            Bundle bundle = getIntent().getExtras();
            String xxx = bundle.getString("todayKural");
            if (!xxx.equalsIgnoreCase("")) {
                todayKural = Integer.parseInt(xxx);
                //setValue(todayKural);
            } else {
                todayKural = currentNo;
            }
        } catch (Exception e) {
            todayKural = currentNo;
            e.printStackTrace();
        }

        setValue(todayKural);
        //showNotification();
        setAlerm();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentNo += 1;
                setValue(currentNo);
            }
        });

        pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentNo -= 1;
                setValue(currentNo);
            }
        });

        thirukural.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AlertDialog.class));
            }
        });
    }

    private void getPreviosValue() {
        String prereadno = sharedPrefs.getString("prereadno", "");
        if (prereadno.equalsIgnoreCase("")) {
            SharedPreferences.Editor editor = sharedPrefs.edit();
            try {
                editor.clear();
                editor.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                editor.putString("prereadno", String.valueOf(currentNo));
                editor.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                currentNo = Integer.parseInt(prereadno);
                todayKural = Integer.parseInt(sharedPrefs.getString("preno", "0"));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
    }

    private void setValue(int getInt) throws IndexOutOfBoundsException {
        showButton(getInt);
        kuralarr = getResources().getStringArray(R.array.kural);
        engkuralarr = getResources().getStringArray(R.array.english_trans);
        iyalarr = getResources().getStringArray(R.array.iyal);
        athigaramarr = getResources().getStringArray(R.array.athigaram);
        solomanarr = getResources().getStringArray(R.array.explain_salaman);
        mkarr = getResources().getStringArray(R.array.explain_mk);
        varathuarr = getResources().getStringArray(R.array.explain_varatharasanar);
        parimalarr = getResources().getStringArray(R.array.explain_parimelagar);
        manakadavurarr = getResources().getStringArray(R.array.explain_manakudavar);
        munusamiarr = getResources().getStringArray(R.array.explain_munisami);
        englisharr = getResources().getStringArray(R.array.explain_english);

        String kuralnostr = String.valueOf(getInt+1);
        if (getInt < 10)
            currentAgarathi = 0;
        else {
            currentAgarathi  = getInt / 10;
        }

        thirukural.setText(kuralarr[getInt]);
        englishkural.setText(engkuralarr[getInt]);
        pal.setText(R.string.arathupal);
        iyal.setText("குறள் இயல்: "+ iyalarr[currentAgarathi]);
        athigaram.setText(athigaramarr[currentAgarathi]);
        kuralno.setText("குறள் "+kuralnostr);
        exp_varathan.setText("\t\t"+varathuarr[getInt]);
        exp_soloman.setText("\t\t"+solomanarr[getInt]);
        exp_mk.setText("\t\t"+mkarr[getInt]);
        exp_parimel.setText("\t\t"+parimalarr[getInt]);
        exp_manakudavar.setText("\t\t"+manakadavurarr[getInt]);
        exp_munusami.setText("\t\t"+munusamiarr[getInt]);
        exp_english.setText("\t\t"+engkuralarr[getInt]);
    }

    private void showButton (int getInt) {
        if (getInt==0)
            pre.setVisibility(View.INVISIBLE);
        else
            pre.setVisibility(View.VISIBLE);
        if (getInt==3)
            next.setVisibility(View.INVISIBLE);
        else
            next.setVisibility(View.VISIBLE);
    }

    private void setAlerm() {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        /*Date futureDate = new Date(new Date().getTime() + 86400000);
        futureDate.setHours(19);
        futureDate.setMinutes(01);
        futureDate.setSeconds(01);*/
        System.out.println("Syso setAlerm Call");

        Date dat = new Date();
        Calendar cal_alarm = Calendar.getInstance();
        Calendar cal_now = Calendar.getInstance();
        cal_now.setTime(dat);
        cal_alarm.setTime(dat);
        cal_alarm.set(Calendar.HOUR_OF_DAY,8);
        cal_alarm.set(Calendar.MINUTE,0);
        cal_alarm.set(Calendar.SECOND,0);
        if(cal_alarm.before(cal_now)){
            cal_alarm.add(Calendar.DATE,1);
        }

        Intent intent = new Intent(MainActivity.this, PopUpReceiver.class);

        PendingIntent sender = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), sender);
    }

    private void getAllPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat
                    .requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat
                    .requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.EXPAND_STATUS_BAR) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat
                    .requestPermissions(MainActivity.this, new String[]{Manifest.permission.EXPAND_STATUS_BAR}, 1);
        }
    }
}
