package com.atsoft.dhinamorukural;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.FirebaseApp;
import com.google.firebase.crash.FirebaseCrash;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class IndrayaKural extends Activity {

    Button expbtn, sharebtn;
    EditText greet;
    TextView thirukural, pal, iyal, athigaram, kuralno, exp, date;
    static int currentNo = 0;
    static int currentAgarathi = 0, currentPal = 0, currentIyal = 0;
    String[] iyalarr, athigaramarr, palarr;
    FloatingActionButton fab_close;
    View rootView;
    Bitmap ss;
    static String filename = "", dirPath = "", greetingmsg = "", datestr = "";
    LinearLayout btnll, bottom_ll;
    SharedPreferences sharedPrefs;
    ArrayList<String> kuralnoarr;

    private InterstitialAd mInterstitialAd;
    private AdView mAdView;

    static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_dialog);
        /*View bg = findViewById(R.id.shared_ll);
        Drawable backround = bg.getBackground();
        backround.setAlpha(60);*/
        FirebaseApp.initializeApp(this);
        context = this;
        MobileAds.initialize(getApplicationContext(), String.valueOf(R.string.YOUR_ADMOB_APP_ID));
        mAdView = (AdView) findViewById(R.id.dk_adView);
        mAdView.loadAd(Defs.adRequest);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_ad_unit_id));
//        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.loadAd(Defs.adRequest);
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
        bottom_ll = findViewById(R.id.bottom_ll);
        greet = (EditText) findViewById(R.id.greatinget);
        sharedPrefs = getSharedPreferences("kural", Context.MODE_PRIVATE);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH");
        int hour = Integer.parseInt(simpleDateFormat.format(calendar.getTime()));
//        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int noon = calendar.get(Calendar.AM_PM);
        int cdate = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        /*if (noon == Calendar.PM && hour != 12)
            hour += 12;*/
        System.out.println("Syso now hour : "+hour);
        if (hour < 12)
            greet.setText("காலை வணக்கம்");
        else if (hour < 16)
            greet.setText("மதிய வணக்கம்");
        else if (hour < 20)
            greet.setText("மாலை வணக்கம்");
        else
            greet.setText("இனிய இரவு");

        datestr = new DecimalFormat("00").format(cdate) + "-" + new DecimalFormat("00").format(month) + "-" + year;
        String todaydate = sharedPrefs.getString("todaydate", "");
        if (!todaydate.equalsIgnoreCase(datestr) || todaydate.equalsIgnoreCase(""))
            getPreviosValue();
        else
            currentNo = Integer.parseInt(sharedPrefs.getString("todaykuralno", "0"));
        setValue();

        System.out.println("Syso IndrayaKural call");

        mInterstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {

            }

            @Override
            public void onAdClosed() {
                IndrayaKural.this.finish();
//                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                mInterstitialAd.loadAd(Defs.adRequest);
            }
        });

        fab_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInterstitialAd.isLoaded()) {
                    Log.d("Syso TAG ", "The interstitial was loaded yet.");
                    mInterstitialAd.show();
                } else {
                    IndrayaKural.this.finish();
                    Log.d("Syso TAG", "The interstitial wasn't loaded yet.");
                }
            }
        });

        sharebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager conMgr =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
                if (netInfo == null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(IndrayaKural.this);
                    builder.setCancelable(false);
                    builder.setTitle("Connect Internet");
                    builder.setIcon(android.R.drawable.presence_offline);
                    builder.setMessage("Turn on wifi or Mobile data.");
                    builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    // Create the AlertDialog object and return it
                    builder.create().show();
                } else {
                    greetingmsg = greet.getText().toString().trim();
                    greet.setVisibility(View.INVISIBLE);
                    fab_close.setVisibility(View.INVISIBLE);
                    bottom_ll.setVisibility(View.GONE);
//                    btnll.setVisibility(View.INVISIBLE);
                    mAdView.setVisibility(View.INVISIBLE);
                    ss = getScreenShot(rootView);
                    store(ss, filename);
                    greet.setVisibility(View.VISIBLE);
                    fab_close.setVisibility(View.VISIBLE);
                    bottom_ll.setVisibility(View.VISIBLE);
//                    btnll.setVisibility(View.VISIBLE);
                    mAdView.setVisibility(View.VISIBLE);
                    shareImage(new File(dirPath, filename));
                }
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

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdOpened() {
                super.onAdOpened();
                FirebaseCrash.log("onAdOpened");
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                FirebaseCrash.log("onAdLoaded");
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
                FirebaseCrash.log("onAdClicked");
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                FirebaseCrash.log("onAdFailedToLoad : "+i);
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
                FirebaseCrash.log("onAdImpression");
            }
        });
    }

    @Override
    protected void onPause() {
        mAdView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mAdView.resume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mAdView.destroy();
        super.onDestroy();
    }

    private void getPreviosValue() {
        String todaykuralno = sharedPrefs.getString("todaykuralno", "");
        kuralnoarr = new ArrayList<>();
        if (todaykuralno.equalsIgnoreCase("")) {
            for (int i = 0; i < 1330; i++)
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
                FirebaseCrash.report(e);
            }
            try {
                Set<String> set = new HashSet<>();
                set.addAll(kuralnoarr);
                editor.putStringSet("kural_no_set", set);
                editor.putString("todaykuralno", String.valueOf(currentNo));
                editor.putString("todaydate", datestr);
                editor.commit();
            } catch (Exception e) {
                e.printStackTrace();
                FirebaseCrash.report(e);
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
                    for (int i = 0; i < 1330; i++)
                        kuralnoarr.add(String.valueOf(i));
                    Collections.shuffle(kuralnoarr);
                }
                SharedPreferences.Editor editor = sharedPrefs.edit();
                try {
                    Set<String> set = new HashSet<>();
                    set.addAll(kuralnoarr);
                    editor.putStringSet("kural_no_set", set);
                    editor.putString("todaykuralno", String.valueOf(currentNo));
                    editor.putString("todaydate", datestr);
                    editor.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                    FirebaseCrash.report(e);
                }
            } catch (Exception e) {
                e.printStackTrace();
                FirebaseCrash.report(e);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mInterstitialAd.isLoaded()) {
            Log.d("Syso TAG ", "The interstitial was loaded yet.");
            mInterstitialAd.show();
        } else {
            IndrayaKural.this.finish();
            Log.d("Syso TAG", "The interstitial wasn't loaded yet.");
        }
    }

    private void setValue() throws IndexOutOfBoundsException {
        DBController controller = new DBController(this);
        SQLiteDatabase db = controller.getReadableDatabase();
        String noti_kural = "";
        String kuralnostr = String.valueOf(currentNo+1);
        String qry = "SELECT thirukural, soloman_exp FROM kural where kuralno = '"+kuralnostr+"'";
        System.out.println("Syso select qry : " +qry);
        Cursor cursor = db.rawQuery(qry, null);
        if (cursor.moveToNext()) {
            noti_kural = cursor.getString(0);
            thirukural.setText(cursor.getString(0));
            exp.setText(cursor.getString(1));
        } else
            System.out.println("Syso empty db");
        cursor.close();
        db.close();

        palarr = getResources().getStringArray(R.array.nav_pal);
        iyalarr = getResources().getStringArray(R.array.nav_iyal);
        athigaramarr = getResources().getStringArray(R.array.athigaram);
//        filename = "Thirukural_"+String.valueOf(currentNo+1)+".jpg";
        filename = "Thirukural.jpg";
        if (currentNo < 10)
            currentAgarathi = 0;
        else {
            currentAgarathi  = currentNo / 10;
        }

        if (currentNo < 381)
            currentPal = 0;
        else if (currentNo < 1081)
            currentPal = 1;
        else
            currentPal = 2;

        if (currentNo < 41)
            currentIyal = 0;
        else if (currentNo < 241)
            currentIyal = 1;
        else if (currentNo < 371)
            currentIyal = 2;
        else if (currentNo < 381)
            currentIyal = 3;
        else if (currentNo < 631)
            currentIyal = 4;
        else if (currentNo < 731)
            currentIyal = 5;
        else if (currentNo < 751)
            currentIyal = 6;
        else if (currentNo < 761)
            currentIyal = 7;
        else if (currentNo < 781)
            currentIyal = 8;
        else if (currentNo < 951)
            currentIyal = 9;
        else if (currentNo < 1081)
            currentIyal = 10;
        else if (currentNo < 1151)
            currentIyal = 11;
        else
            currentIyal = 12;


        pal.setText(palarr[currentPal]);
        iyal.setText(iyalarr[currentIyal]);
        athigaram.setText(athigaramarr[currentAgarathi]);
        kuralno.setText(kuralnostr);
        date.setText(datestr);
        showNotification(noti_kural);
    }

    private void showNotification(String todayKural) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getBaseContext())
                        .setSmallIcon(R.drawable.indrayakural_icon)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.indrayakural_icon))
                        .setContentTitle(getResources().getString(R.string.todays_kural))
                        .setContentText(todayKural)
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setPriority(Notification.PRIORITY_DEFAULT);

        /* Add Big View Specific Configuration */
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        String[] events = todayKural.split("\n");

        // Sets a title for the Inbox style big view
        inboxStyle.setBigContentTitle(getResources().getString(R.string.todays_kural));

        // Moves events into the big view
        for (int i=0; i < events.length; i++) {
            inboxStyle.addLine(events[i]);
        }

        mBuilder.setStyle(inboxStyle);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.putExtra("todayKural", ""+currentNo);
        notificationIntent.putExtra("preread", sharedPrefs.getString("prereadno", "0"));
        notificationIntent.putExtra("fromactivity", "ad");
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
        manager.notify(0, mBuilder.build());
    }

    public static Bitmap getScreenShot(View view) {
        View screenView = view.getRootView();
        screenView.layout(0, 0, Resources.getSystem().getDisplayMetrics().widthPixels, (int) context.getResources().getDimension(R.dimen.popup_ss_height) + 75);
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public void store(Bitmap bm, String fileName){
        dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Thirukural/Screenshots";
//        dirPath = getFilesDir().getAbsolutePath() + "/Thirukural/Screenshots";
        File dir = new File(dirPath);
        if(!dir.exists())
            dir.mkdirs();
        File file = new File(dirPath, fileName);
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
            FirebaseCrash.report(e);
        }
    }

    private void shareImage(File file){
        Uri uri;
        if (Build.VERSION.SDK_INT <= 23)
            uri = Uri.fromFile(file);
        else
            uri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", file);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/*");

        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.todays_kural));
        intent.putExtra(android.content.Intent.EXTRA_TEXT, greetingmsg+"\n\nhttps://play.google.com/store/apps/details?id=com.atsoft.dhinamorukural \n\n");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        try {
            startActivity(Intent.createChooser(intent, "திருக்குறளை பகிர்"));
            //IndrayaKural.this.finish();
        } catch (ActivityNotFoundException e) {
            FirebaseCrash.report(e);
            Toast.makeText(getApplicationContext(), "No App Available", Toast.LENGTH_SHORT).show();
        }
    }
}
