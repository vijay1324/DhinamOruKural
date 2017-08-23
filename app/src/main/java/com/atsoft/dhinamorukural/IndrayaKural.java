package com.atsoft.dhinamorukural;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class IndrayaKural extends Activity {

    Button expbtn, sharebtn;
    EditText greet;
    TextView thirukural, pal, iyal, athigaram, kuralno, exp, date;
    static int currentNo = 0;
    static int currentAgarathi = 0;
    String[] kuralarr, iyalarr, athigaramarr, exparr;
    FloatingActionButton fab_close;
    View rootView;
    Bitmap ss;
    static String filename = "", dirPath = "", greetingmsg = "", datestr = "";
    LinearLayout btnll;
    SharedPreferences sharedPrefs;
    ArrayList<String> kuralnoarr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_dialog);
        View bg = findViewById(R.id.popup_ll);
        Drawable backround = bg.getBackground();
        backround.setAlpha(60);
        rootView = getWindow().getDecorView().findViewById(R.id.shared_ll);
        expbtn = (Button) findViewById(R.id.exp_btn);
        sharebtn = (Button) findViewById(R.id.share_btn);
        thirukural = (TextView) findViewById(R.id.dia_thirukuraltv);
        pal = (TextView) findViewById(R.id.dia_paltv);
        iyal = (TextView) findViewById(R.id.dia_iyaltv);
        athigaram = (TextView) findViewById(R.id.dia_athigaramtv);
        kuralno = (TextView) findViewById(R.id.dia_kuralnotv);
        exp = (TextView) findViewById(R.id.dia_urai);
        date = (TextView) findViewById(R.id.date);
        fab_close = (FloatingActionButton) findViewById(R.id.fab_close);
        btnll = (LinearLayout) findViewById(R.id.btn_ll);
        greet = (EditText) findViewById(R.id.greatinget);
        sharedPrefs = getSharedPreferences("kural", Context.MODE_PRIVATE);

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int noon = calendar.get(Calendar.AM_PM);
        int cdate = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        if (noon == Calendar.PM && hour != 12)
            hour += 12;
        System.out.println("Syso now hour : "+hour);
        if (hour < 12)
            greet.setText("காலை வணக்கம்");
        else if (hour < 16)
            greet.setText("மதிய வணக்கம்");
        else if (hour < 20)
            greet.setText("மாலை வணக்கம்");
        else
            greet.setText("இனிய இரவு");

//        SimpleDateFormat df = new SimpleDateFormat("dd-mm-yyyy");
//        datestr = df.format(calendar.getTime());
        datestr = cdate + "-" + month + "-" + year;
        getPreviosValue();
        setValue();

        System.out.println("Syso IndrayaKural call");

        fab_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IndrayaKural.this.finish();
            }
        });

        sharebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                greetingmsg = greet.getText().toString().trim();
                greet.setVisibility(View.INVISIBLE);
                fab_close.setVisibility(View.INVISIBLE);
                btnll.setVisibility(View.INVISIBLE);
                ss = getScreenShot(rootView);
                store(ss, filename);
                greet.setVisibility(View.VISIBLE);
                fab_close.setVisibility(View.VISIBLE);
                btnll.setVisibility(View.VISIBLE);
                shareImage(new File(dirPath, filename));
            }
        });

        expbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IndrayaKural.this, MainActivity.class);
                intent.putExtra("todayKural", ""+currentNo);
                intent.putExtra("preread", sharedPrefs.getString("prereadno", "0"));
                intent.putExtra("fromactivity", "ad");
                startActivity(intent);
                IndrayaKural.this.finish();
            }
        });
    }

    private void getPreviosValue() {
        String todaykuralno = sharedPrefs.getString("todaykuralno", "");
        kuralnoarr = new ArrayList<>();
        if (todaykuralno.equalsIgnoreCase("")) {
            for (int i = 0; i < 10; i++)
                kuralnoarr.add(String.valueOf(i));
            Collections.shuffle(kuralnoarr);
            System.out.println("Syso : Arrray create : "+kuralnoarr);
            currentNo = Integer.parseInt(kuralnoarr.get(0));
            kuralnoarr.remove(String.valueOf(currentNo));
            SharedPreferences.Editor editor = sharedPrefs.edit();
            try {
                editor.clear();
                editor.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Set<String> set = new HashSet<>();
                set.addAll(kuralnoarr);
                editor.putStringSet("kural_no_set", set);
                editor.putString("todaykuralno", String.valueOf(currentNo));
                editor.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                Set<String> stringset = sharedPrefs.getStringSet("kural_no_set", null);
                for (String str : stringset)
                    kuralnoarr.add(str);
                currentNo = Integer.parseInt(kuralnoarr.get(0));
                System.out.println("Syso : Arrray : "+kuralnoarr);
                kuralnoarr.remove(0);
                if (kuralnoarr.size() == 0) {
                    for (int i = 0; i < 10; i++)
                        kuralnoarr.add(String.valueOf(i));
                    Collections.shuffle(kuralnoarr);
                }
                SharedPreferences.Editor editor = sharedPrefs.edit();
                /*try {
                    editor.clear();
                    editor.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
                try {
                    Set<String> set = new HashSet<>();
                    set.addAll(kuralnoarr);
                    editor.putStringSet("kural_no_set", set);
                    editor.putString("todaykuralno", String.valueOf(currentNo));
                    editor.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {

    }

    private void setValue() throws IndexOutOfBoundsException {
        kuralarr = getResources().getStringArray(R.array.kural);
        iyalarr = getResources().getStringArray(R.array.iyal);
        athigaramarr = getResources().getStringArray(R.array.athigaram);
        exparr = getResources().getStringArray(R.array.explain_salaman);
        String kuralnostr = String.valueOf(currentNo+1);
        filename = "Thirukural_"+String.valueOf(currentNo+1)+".jpg";
        if (currentNo < 10)
            currentAgarathi = 0;
        else {
            currentAgarathi  = currentNo / 10;
        }

        thirukural.setText(kuralarr[currentNo]);
        pal.setText(R.string.arathupal);
        iyal.setText(iyalarr[currentAgarathi]);
        athigaram.setText(athigaramarr[currentAgarathi]);
        kuralno.setText(kuralnostr);
        exp.setText(exparr[currentNo]);
        date.setText(datestr);
        showNotification(kuralarr[currentNo]);
    }

    private void showNotification(String todayKural) {
        System.out.println("Syso Today kural : "+todayKural);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getBaseContext())
                        .setSmallIcon(R.drawable.mini_icon_42)
                        .setContentTitle(getResources().getString(R.string.todays_kural))
                        .setContentText(todayKural)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setPriority(Notification.PRIORITY_DEFAULT);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
        manager.notify(0, mBuilder.build());
    }

    public static Bitmap getScreenShot(View view) {
        View screenView = view.getRootView();
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        System.out.println("Syso SS Finish");
        return bitmap;
    }

    public void store(Bitmap bm, String fileName){
        dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Thirukural/Screenshots";
//        dirPath = getFilesDir().getAbsolutePath() + "/Thirukural/Screenshots";
        System.out.println("Syso dir path : "+dirPath);
        File dir = new File(dirPath);
        if(!dir.exists())
            dir.mkdirs();
        File file = new File(dirPath, fileName);
        System.out.println("Syso fileName : "+fileName);
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();
            System.out.println("Syso store success");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void shareImage(File file){
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");

        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.todays_kural));
        intent.putExtra(android.content.Intent.EXTRA_TEXT, greetingmsg);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        try {
            startActivity(Intent.createChooser(intent, "Share Thirukural"));
            //IndrayaKural.this.finish();
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), "No App Available", Toast.LENGTH_SHORT).show();
        }
    }
}
