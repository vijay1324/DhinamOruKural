package com.atsoft.dhinamorukural;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.FirebaseApp;
import com.google.firebase.crash.FirebaseCrash;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.google.android.gms.ads.AdView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextView thirukural, englishkural, pal, iyal, athigaram, kuralno, exp_soloman, exp_mk, exp_varathan, exp_parimel, exp_manakudavar, exp_english,
                header_saloman, header_mk, header_varathu, header_paramal, header_mana, header_explain;
    static int currentNo = 0;
    static int currentAgarathi = 0, currentPal = 0, currentIyal = 0;
    static int todayKural = 0;
    String[] iyalarr, athigaramarr,palarr;

    //FloatingActionButton pre, next;
    FloatingActionButton fullscreen;
    ImageButton pre, next;
    SharedPreferences sharedPrefs;
    static String fromactivity = "";
    static boolean hideIcon = true;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerLisener;
    //private ListView listView;
    String[] drawContent = {"இன்றைய குறள்", "குறள் எண் தேடல்", "வார்த்தை தேடல்", "பால் தேர்வு", "இயல் தேர்வு", "அதிகாரம் தேர்வு", "அமைப்புகள்", "எங்களை பற்றி"};
    ArrayAdapter adapter;

    static int alarmHour = 8;


    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    //NativeExpressAdView adView;

    DBController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        controller = new DBController(this);
        FirebaseApp.initializeApp(this);
        FirebaseCrash.log("Activity created");
        //MobileAds.initialize(getApplicationContext(), String.valueOf(R.string.YOUR_ADMOB_APP_ID));
        String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
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
        exp_english = (TextView) findViewById(R.id.exp_english_tv);
        pre = (ImageButton) findViewById(R.id.pre_img_btn);
        next = (ImageButton) findViewById(R.id.nxt_img_btn);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //listView = (ListView) findViewById(R.id.mylistview);
        mAdView = (AdView) findViewById(R.id.main_adView);
        header_saloman = (TextView) findViewById(R.id.header_saloman);
        header_mk = (TextView) findViewById(R.id.header_mk);
        header_varathu = (TextView) findViewById(R.id.header_varathu);
        header_paramal = (TextView) findViewById(R.id.header_paramal);
        header_mana = (TextView) findViewById(R.id.header_mana);
        header_explain = (TextView) findViewById(R.id.header_explain);
        fullscreen = (FloatingActionButton) findViewById(R.id.fab_fullscreen);

        mAdView.loadAd(Defs.adRequest);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_ad_unit_id));
//        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.loadAd(Defs.adRequest);
        System.out.println("Syso devise id : "+android_id);

        android.support.v7.app.ActionBarDrawerToggle toggle = new android.support.v7.app.ActionBarDrawerToggle(
                this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //adView = (NativeExpressAdView)findViewById(R.id.main_adView);

        /*AdRequest request = new AdRequest.Builder().build();
        adView.loadAd(request);
        adView.setVideoOptions(new VideoOptions.Builder()
                .setStartMuted(true)
                .build());

        VideoController vc = adView.getVideoController();

        vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
            public void onVideoEnd() {
                // Here apps can take action knowing video playback is finished
                // It's always a good idea to wait for playback to complete before
                // replacing or refreshing a native ad, for example.
                super.onVideoEnd();
            }
        });*/

        sharedPrefs = getSharedPreferences("kural", Context.MODE_PRIVATE);
        try {
            Bundle bundle = getIntent().getExtras();
            todayKural = Integer.parseInt(bundle.getString("todayKural"));
            currentNo = Integer.parseInt(bundle.getString("preread"));
            fromactivity = bundle.getString("fromactivity");
        } catch (Exception e) {
            e.printStackTrace();
            FirebaseCrash.report(e);
        }
        currentNo = Integer.parseInt(sharedPrefs.getString("prereadno", "0"));

        //adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, drawContent);
        //listView.setAdapter(adapter);
        /*drawerLisener = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_menu_open, R.string.drawer_open, R.string.drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                //Toast.makeText(getApplicationContext(), "Drawer Open", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                //Toast.makeText(getApplicationContext(), "Drawer Close", Toast.LENGTH_LONG).show();
            }
        };

        drawerLayout.setDrawerListener(drawerLisener);*/
        //getSupportActionBar().setHomeButtonEnabled(true);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       /* navDrawer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });*/

        if (fromactivity.equalsIgnoreCase("ss")) {
            next.setVisibility(View.VISIBLE);
            pre.setVisibility(View.VISIBLE);
            hideIcon = true;
            setValue(currentNo);
        } else {
            next.setVisibility(View.INVISIBLE);
            pre.setVisibility(View.INVISIBLE);
            hideIcon = false;
            setValue(todayKural);
        }
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

        fullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    Intent intent = new Intent(MainActivity.this, FullScreenExplainActivity.class);
                    intent.putExtra("salaman", exp_soloman.getText().toString());
                    intent.putExtra("mk", exp_mk.getText().toString());
                    intent.putExtra("varathu", exp_varathan.getText().toString());
                    intent.putExtra("palam", exp_parimel.getText().toString());
                    intent.putExtra("mana", exp_manakudavar.getText().toString());
                    intent.putExtra("exp_eng", exp_english.getText().toString());
                    startActivity(intent);
                }
            }
        });

        mInterstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {

            }

            @Override
            public void onAdClosed() {
                MainActivity.this.finish();
//                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                mInterstitialAd.loadAd(Defs.adRequest);
            }
        });

        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                if(drawerLayout.isDrawerOpen(Gravity.LEFT))
                    drawerLayout.closeDrawer(Gravity.LEFT);
            }
        });*/
    }

    private void searhByPal () {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this);
        builderSingle.setIcon(R.drawable.indrayakural_icon);
        builderSingle.setTitle("பாலை தேர்ந்தெடு:-");

        String[] palarr = getResources().getStringArray(R.array.nav_pal);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, palarr);

        builderSingle.setNegativeButton("ரத்து செய்", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int posision) {
                final String[] iyalarr;
                switch (posision) {
                    case 0:
                        iyalarr = getResources().getStringArray(R.array.nav_pal_iyal1);
                        break;
                    case 1:
                        iyalarr = getResources().getStringArray(R.array.nav_pal_iyal2);
                        break;
                    case 2:
                        iyalarr = getResources().getStringArray(R.array.nav_pal_iyal3);
                        break;
                    default:
                        iyalarr = getResources().getStringArray(R.array.nav_iyal);
                        break;
                }

                AlertDialog.Builder iyal_builderSingle = new AlertDialog.Builder(MainActivity.this);
                iyal_builderSingle.setIcon(R.drawable.indrayakural_icon);
                iyal_builderSingle.setTitle("இயலை தேர்ந்தெடு:-");

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, iyalarr);

                iyal_builderSingle.setNegativeButton("ரத்து செய்", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                iyal_builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int posision) {
                       String[] fulliyal = getResources().getStringArray(R.array.nav_iyal);
                        for (int i = 0; i < fulliyal.length; i++) {
                            if (fulliyal[i].equals(iyalarr[posision])) {
                                searchByIyal(i);
                                break;
                            }
                        }

                    }
                });
                iyal_builderSingle.show();

            }
        });
        builderSingle.show();
    }

    private void searchByIyal(int getPos) {
        final String[] athigaramarr;
        switch (getPos) {
            case 0:
                athigaramarr = getResources().getStringArray(R.array.nav_iyal_adthgaram1);
                break;
            case 1:
                athigaramarr = getResources().getStringArray(R.array.nav_iyal_adthgaram2);
                break;
            case 2:
                athigaramarr = getResources().getStringArray(R.array.nav_iyal_adthgaram3);
                break;
            case 3:
                athigaramarr = getResources().getStringArray(R.array.nav_iyal_adthgaram4);
                break;
            case 4:
                athigaramarr = getResources().getStringArray(R.array.nav_iyal_adthgaram5);
                break;
            case 5:
                athigaramarr = getResources().getStringArray(R.array.nav_iyal_adthgaram6);
                break;
            case 6:
                athigaramarr = getResources().getStringArray(R.array.nav_iyal_adthgaram7);
                break;
            case 7:
                athigaramarr = getResources().getStringArray(R.array.nav_iyal_adthgaram8);
                break;
            case 8:
                athigaramarr = getResources().getStringArray(R.array.nav_iyal_adthgaram9);
                break;
            case 9:
                athigaramarr = getResources().getStringArray(R.array.nav_iyal_adthgaram10);
                break;
            case 10:
                athigaramarr = getResources().getStringArray(R.array.nav_iyal_adthgaram11);
                break;
            case 11:
                athigaramarr = getResources().getStringArray(R.array.nav_iyal_adthgaram12);
                break;
            case 12:
                athigaramarr = getResources().getStringArray(R.array.nav_iyal_adthgaram13);
                break;
            default:
                athigaramarr = getResources().getStringArray(R.array.athigaram);
                break;
        }

        AlertDialog.Builder Athigaram_builderSingle = new AlertDialog.Builder(MainActivity.this);
        Athigaram_builderSingle.setIcon(R.drawable.indrayakural_icon);
        Athigaram_builderSingle.setTitle("அதிகாரத்தை தேர்ந்தெடு:-");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, athigaramarr);

        Athigaram_builderSingle.setNegativeButton("ரத்து செய்", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        Athigaram_builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int posision) {
                String[] fullathigaram = getResources().getStringArray(R.array.athigaram);
                for (int i = 0; i < fullathigaram.length; i++) {
                    if (fullathigaram[i].equals(athigaramarr[posision])) {
                        String strName = i + "1";
                        setValue(Integer.parseInt(strName) - 1);
                        break;
                    }
                }

            }
        });
        Athigaram_builderSingle.show();
    }

    private void searchByWord() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.search_by_word, null);
        dialogBuilder.setView(dialogView);

        final ArrayList<HashMap<String, String>> myList = new ArrayList<HashMap<String,String>>();

        final AutoCompleteTextView edt = dialogView.findViewById(R.id.searchet);
        final ListView lv = dialogView.findViewById(R.id.search_listview);
        /*View bg = dialogView.findViewById(R.id.dialog_top_ll);
        Drawable backround = bg.getBackground();
        backround.setAlpha(80);*/
        final AlertDialog adialog = dialogBuilder.create();
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (MainActivity.this,android.R.layout.simple_list_item_1,Defs.allwords);
        edt.setAdapter(adapter);
        edt.setThreshold(1);

        edt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if(event.getAction()==MotionEvent.ACTION_UP){

                    if(event.getRawX() >= (edt.getRight() - edt.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        try {
                            SQLiteDatabase db = controller.getReadableDatabase();
                            String qry = "SELECT kuralno, thirukural FROM kural where thirukural like '%"+edt.getText().toString().trim()+"%'";
                            Cursor cursor = db.rawQuery(qry, null);
                            try {
                                myList.clear();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            HashMap<String, String> map = null;
                            if (cursor.moveToFirst()) {
                                do {
                                    map = new HashMap<String, String>();
                                    map.put("kuralno", cursor.getString(0));
                                    map.put("thirukural", cursor.getString(1));
                                    myList.add(map);
                                } while (cursor.moveToNext());
                            }
                            cursor.close();
                            db.close();
                            if (myList.size() == 0) {
                                Toast.makeText(getApplicationContext(), "இந்த வார்த்தை கிடைக்கவில்லை, சரியான வார்த்தையை உள்ளிடவும்.", Toast.LENGTH_LONG).show();
                            }
                            edt.setText("");
                            SimpleAdapter sadapter = new SimpleAdapter(MainActivity.this, myList,
                                    R.layout.word_search_custom_listview, new String[]{"kuralno", "thirukural"}, new int[]{
                                    R.id.search_kural_no, R.id.src_kural_tv});
                            lv.setAdapter(sadapter);
                            sadapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                            FirebaseCrash.report(e);
                        }

                        return true;
                    }
                }
                return false;
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int index = i;
                String kno = myList.get(index).get("kuralno");
                int xx = Integer.parseInt(kno) - 1;
                setValue(xx);
                adialog.dismiss();
            }
        });

        dialogBuilder.setIcon(R.drawable.indrayakural_icon);
        dialogBuilder.setTitle("திருக்குறள்");
        dialogBuilder.setMessage("வார்த்தை தேடல்");
        dialogBuilder.setNegativeButton("ரத்து செய்", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
                dialog.dismiss();
            }
        });
        adialog.show();
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(Gravity.LEFT))
            drawerLayout.closeDrawer(Gravity.LEFT);
        else if (!fromactivity.equalsIgnoreCase("ss")) {
            startActivity(new Intent(MainActivity.this, SplashScreen.class));
            MainActivity.this.finish();
        } else {
            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setCancelable(false);
            builder.setTitle(R.string.app_name);
            builder.setIcon(R.drawable.indrayakural_icon);
            builder.setMessage("நீங்கள் வெளியேற விரும்புகிறீர்களா?");
            builder.setPositiveButton("வெளியேறு", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    if (mInterstitialAd.isLoaded()) {
                        Log.d("Syso TAG ", "The interstitial was loaded yet.");
                        mInterstitialAd.show();
                    } else {
                        MainActivity.this.finish();
                        Log.d("Syso TAG", "The interstitial wasn't loaded yet.");
                    }
                }
            })
                .setNegativeButton("ரத்து செய் ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

            // Create the AlertDialog object and return it
            builder.create().show();
        }
    }

    private void showButton (int getInt) {
        if (fromactivity.equalsIgnoreCase("ss")) {
            if (getInt == 0)
                pre.setVisibility(View.INVISIBLE);
            else
                pre.setVisibility(View.VISIBLE);
            if (getInt == 1329)
                next.setVisibility(View.INVISIBLE);
            else
                next.setVisibility(View.VISIBLE);
        }
    }

    private void setAlerm() {
        alarmHour = sharedPrefs.getInt("alermtime", 8);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Date dat = new Date();
        Calendar cal_alarm = Calendar.getInstance();
        Calendar cal_now = Calendar.getInstance();
        cal_now.setTime(dat);
        cal_alarm.setTime(dat);
        cal_alarm.set(Calendar.HOUR_OF_DAY,alarmHour);
        cal_alarm.set(Calendar.MINUTE,0);
        cal_alarm.set(Calendar.SECOND,0);
        if(cal_alarm.before(cal_now)){
            cal_alarm.add(Calendar.DATE,1);
        }

        Intent intent = new Intent(MainActivity.this, PopUpReceiver.class);

        PendingIntent sender = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        boolean alarmOnOff = sharedPrefs.getBoolean("popup_onoff", false);
        if (!alarmOnOff) {
            am.setRepeating(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), AlarmManager.INTERVAL_DAY, sender);
        } else {
            am.cancel(sender);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        if (!hideIcon){
            menu.findItem(R.id.action_share).setVisible(false);
        }else{
            menu.findItem(R.id.action_share).setVisible(true);
        }*/
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*if (item.getItemId() == R.id.action_share)
            shareKural();
        else {*/
            if (drawerLayout.isDrawerOpen(Gravity.LEFT))
                drawerLayout.closeDrawer(Gravity.LEFT);
            else
                drawerLayout.openDrawer(Gravity.LEFT);
        //}
        return super.onOptionsItemSelected(item);
    }

    private void setValue(int getInt) {
        currentNo = getInt;
        showButton(getInt);
        palarr = getResources().getStringArray(R.array.nav_pal);
        iyalarr = getResources().getStringArray(R.array.nav_iyal);
        athigaramarr = getResources().getStringArray(R.array.athigaram);

        String kuralnostr = String.valueOf(getInt+1);
        if (getInt < 10)
            currentAgarathi = 0;
        else {
            currentAgarathi  = getInt / 10;
        }

        if (getInt < 380)
            currentPal = 0;
        else if (getInt < 1080)
            currentPal = 1;
        else
            currentPal = 2;

        if (getInt < 40)
            currentIyal = 0;
        else if (getInt < 240)
            currentIyal = 1;
        else if (getInt < 370)
            currentIyal = 2;
        else if (getInt < 380)
            currentIyal = 3;
        else if (getInt < 630)
            currentIyal = 4;
        else if (getInt < 730)
            currentIyal = 5;
        else if (getInt < 750)
            currentIyal = 6;
        else if (getInt < 760)
            currentIyal = 7;
        else if (getInt < 780)
            currentIyal = 8;
        else if (getInt < 950)
            currentIyal = 9;
        else if (getInt < 1080)
            currentIyal = 10;
        else if (getInt < 1150)
            currentIyal = 11;
        else
            currentIyal = 12;


        SQLiteDatabase db = controller.getReadableDatabase();
        String qry = "SELECT kuralno, thirukural, mk_exp, varathu_exp, soloman_exp, parimelalagar_exp, manakadavure_exp, translate_kural, eng_exp FROM kural where kuralno = '"+kuralnostr+"'";
        Cursor cursor = db.rawQuery(qry, null);
        if (cursor.moveToNext()) {
            thirukural.setText(cursor.getString(1));
            englishkural.setText(cursor.getString(7));
            exp_varathan.setText("\t\t"+cursor.getString(3));
            exp_soloman.setText("\t\t"+cursor.getString(4));
            exp_mk.setText("\t\t"+cursor.getString(2));
            exp_parimel.setText("\t\t"+cursor.getString(5));
            exp_manakudavar.setText("\t\t"+cursor.getString(6));
            exp_english.setText("\t\t"+cursor.getString(8));
        }
        db.close();

        pal.setText(palarr[currentPal]);
        iyal.setText(iyalarr[currentIyal]);
        athigaram.setText(athigaramarr[currentAgarathi]);
        kuralno.setText(kuralnostr);
        setTheme();
        SharedPreferences.Editor editor = sharedPrefs.edit();
        try {
            editor.putString("prereadno", String.valueOf(currentNo));
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
            FirebaseCrash.report(e);
        }
    }

    private void setTheme() {
        float h1 = 0;
        float h2 = 0;
        float text = 0;
        boolean bigtext = sharedPrefs.getBoolean("bigtxt", false);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && bigtext) {
            h1 = getResources().getDimension(R.dimen.header_text_land_big) / getResources().getDisplayMetrics().density;
            h2 = getResources().getDimension(R.dimen.sub_header_land_big) / getResources().getDisplayMetrics().density;
            text = getResources().getDimension(R.dimen.normal_text_land_big) / getResources().getDisplayMetrics().density;
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            h1 = getResources().getDimension(R.dimen.header_text_land) / getResources().getDisplayMetrics().density;
            h2 = getResources().getDimension(R.dimen.sub_header_land) / getResources().getDisplayMetrics().density;
            text = getResources().getDimension(R.dimen.normal_text_land) / getResources().getDisplayMetrics().density;
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT && bigtext) {
            h1 = getResources().getDimension(R.dimen.header_text_big) / getResources().getDisplayMetrics().density;
            h2 = getResources().getDimension(R.dimen.sub_header_big) / getResources().getDisplayMetrics().density;
            text = getResources().getDimension(R.dimen.normal_text_big) / getResources().getDisplayMetrics().density;
        } else {
            h1 = getResources().getDimension(R.dimen.header_text) / getResources().getDisplayMetrics().density;
            h2 = getResources().getDimension(R.dimen.sub_header) / getResources().getDisplayMetrics().density;
            text = getResources().getDimension(R.dimen.normal_text) / getResources().getDisplayMetrics().density;
        }
        System.out.println("Syso : h1 : "+h1);
        System.out.println("Syso : h2 : "+h2);
        System.out.println("Syso : text : "+text);

            pal.setTextSize(h1);
            athigaram.setTextSize(h1);

            iyal.setTextSize(h2);
            kuralno.setTextSize(h2);
            header_saloman.setTextSize(h2);
            header_mk.setTextSize(h2);
            header_varathu.setTextSize(h2);
            header_paramal.setTextSize(h2);
            header_mana.setTextSize(h2);
            header_explain.setTextSize(h2);

            thirukural.setTextSize(text);
            englishkural.setTextSize(text);
            exp_soloman.setTextSize(text);
            exp_mk.setTextSize(text);
            exp_varathan.setTextSize(text);
            exp_parimel.setTextSize(text);
            exp_manakudavar.setTextSize(text);
            exp_english.setTextSize(text);
    }

    private void shareKural() {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Thirukural/Screenshots", "Thirukural.jpg");
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
        intent.putExtra(android.content.Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.atsoft.dhinamorukural \n\n");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        try {
            startActivity(Intent.createChooser(intent, "திருக்குறளை பகிர்"));
            //IndrayaKural.this.finish();
        } catch (ActivityNotFoundException e) {
            FirebaseCrash.report(e);
            Toast.makeText(getApplicationContext(), "பயன்பாடு இல்லை", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_today_kural_explain:
                fromactivity = "ad";
                next.setVisibility(View.INVISIBLE);
                pre.setVisibility(View.INVISIBLE);
                hideIcon = false;
                setValue(todayKural);
                break;
            case R.id.nav_today_kural_popup:
                startActivity(new Intent(MainActivity.this, IndrayaKural.class));
                MainActivity.this.finish();
                break;
            case R.id.nav_no_search:
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.edittext_dialog, null);
                dialogBuilder.setView(dialogView);

                final EditText edt = (EditText) dialogView.findViewById(R.id.kuralnoet);
                        /*View bg = dialogView.findViewById(R.id.dialog_top_ll);
                        Drawable backround = bg.getBackground();
                        backround.setAlpha(80);*/

                edt.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        try {
                            int kno = Integer.parseInt(edt.getText().toString().trim());
                            if (kno < 1 || kno > 1330) {
                                Toast.makeText(getApplicationContext(), "சரியான எண்ணை உள்ளிடவும்", Toast.LENGTH_LONG).show();
                                edt.setText("");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            FirebaseCrash.report(e);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                dialogBuilder.setTitle("குறள் எண் தேடல்");
                dialogBuilder.setPositiveButton("தேடு", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //do something with edt.getText().toString();
                        try {
                            int kuralno = Integer.parseInt(edt.getText().toString().trim());
                            if (kuralno < 1331) {
                                if (drawerLayout.isDrawerOpen(Gravity.LEFT))
                                    drawerLayout.closeDrawer(Gravity.LEFT);
                                setValue(kuralno - 1);
                            } else {
                                Toast.makeText(getApplicationContext(), "சரியான எண்ணை உள்ளிடவும்", Toast.LENGTH_LONG).show();
                                edt.setText("");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            FirebaseCrash.report(e);
                        }
                    }
                });
                dialogBuilder.setNegativeButton("ரத்து செய்", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //pass
                        dialog.dismiss();
                    }
                });
                AlertDialog b = dialogBuilder.create();
                b.show();
                break;
            case R.id.nav_word_search:
                searchByWord();
                break;
            case R.id.nav_pal_select:
                searhByPal();
                break;
            case R.id.nav_iyal_select:
                AlertDialog.Builder iyal_builderSingle = new AlertDialog.Builder(MainActivity.this);
                iyal_builderSingle.setIcon(R.drawable.indrayakural_icon);
                iyal_builderSingle.setTitle("இயலை தேர்ந்தெடு:-");

                String[] iyalarr = getResources().getStringArray(R.array.nav_iyal);

                final ArrayAdapter<String> iyalarrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, iyalarr);

                iyal_builderSingle.setNegativeButton("ரத்து செய்", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                iyal_builderSingle.setAdapter(iyalarrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int posision) {
                        searchByIyal(posision);
                    }
                });
                iyal_builderSingle.show();
                break;
            case R.id.nav_adhigaram_select:
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this);
                builderSingle.setIcon(R.drawable.indrayakural_icon);
                builderSingle.setTitle("அதிகாரத்தை தேர்ந்தெடு:-");

                String[] adigaramarr = getResources().getStringArray(R.array.athigaram);

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, adigaramarr);

                builderSingle.setNegativeButton("ரத்து செய்", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int posision) {
                        String strName = posision + "1";
                        setValue(Integer.parseInt(strName) - 1);
                    }
                });
                builderSingle.show();
                break;
            case R.id.nav_settings:
                AlertDialog.Builder set_dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater set_inflater = MainActivity.this.getLayoutInflater();
                final View set_dialogView = set_inflater.inflate(R.layout.settings, null);
                set_dialogBuilder.setView(set_dialogView);

                final EditText set_edt = (EditText) set_dialogView.findViewById(R.id.notifyet);
                final Switch sw_bigtext = set_dialogView.findViewById(R.id.switch_bigtxt);
                final Switch sw_onoff_popup = set_dialogView.findViewById(R.id.switch_off_popup);
                sw_bigtext.setChecked(sharedPrefs.getBoolean("bigtxt", false));
                sw_onoff_popup.setChecked(sharedPrefs.getBoolean("popup_onoff", false));
                set_edt.setHint("0 ~ 23");
                set_edt.setText(alarmHour+"");
                        /*View set_bg = set_dialogView.findViewById(R.id.dialog_top_ll);
                        Drawable set_backround = set_bg.getBackground();
                        set_backround.setAlpha(60);*/

                set_edt.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        try {
                            String edittexttest = set_edt.getText().toString().trim();
                            if (!edittexttest.equalsIgnoreCase("")) {
                                int kno = Integer.parseInt(edittexttest);
                                if (kno < 0 || kno > 23) {
                                    Toast.makeText(getApplicationContext(), "சரியான நேரத்தை உள்ளிடவும்", Toast.LENGTH_LONG).show();
                                    set_edt.setText("");
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            FirebaseCrash.report(e);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });


                sw_bigtext.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        SharedPreferences.Editor editor = sharedPrefs.edit();
                        editor.putBoolean("bigtxt", b);
                        editor.commit();
                    }
                });

                sw_onoff_popup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        SharedPreferences.Editor editor = sharedPrefs.edit();
                        editor.putBoolean("popup_onoff", b);
                        editor.commit();
                    }
                });

                set_dialogBuilder.setTitle("அமைப்புகள்");
                //set_dialogBuilder.setMessage("அறிவிப்பு நேரத்தை மாற்றவும்");
                set_dialogBuilder.setPositiveButton("அமை", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //do something with edt.getText().toString();
                        try {
                            int kuralno = Integer.parseInt(set_edt.getText().toString().trim());
                            if (kuralno < 24) {
                                if (drawerLayout.isDrawerOpen(Gravity.LEFT))
                                    drawerLayout.closeDrawer(Gravity.LEFT);
                                alarmHour = kuralno;
                                SharedPreferences.Editor editor = sharedPrefs.edit();
                                editor.putInt("alermtime", alarmHour);
                                editor.commit();
                                setAlerm();
                                setTheme();
                            } else {
                                Toast.makeText(getApplicationContext(), "சரியான எண்ணை உள்ளிடவும்", Toast.LENGTH_LONG).show();
                                set_edt.setText("");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            FirebaseCrash.report(e);
                        }

                    }
                });
                set_dialogBuilder.setNegativeButton("ரத்து செய்", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //pass
                        if(drawerLayout.isDrawerOpen(Gravity.LEFT))
                            drawerLayout.closeDrawer(Gravity.LEFT);
                        dialog.dismiss();
                    }
                });
                AlertDialog set_b = set_dialogBuilder.create();
                set_b.show();
                break;
            case R.id.nav_about_us:
                startActivity(new Intent(MainActivity.this, AboutUs.class));
                MainActivity.this.finish();
                break;
            case R.id.nav_rating:
                Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName())));
                    FirebaseCrash.report(e);
                } catch (Exception e) {
                    FirebaseCrash.report(e);
                }
                break;
            case R.id.nav_send:
                try {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name);
                    String sAux = "\nஇந்த செயலியை நான் பரிந்துரைக்கிறேன்\n\n";
                    sAux = sAux + "https://play.google.com/store/apps/details?id=com.atsoft.dhinamorukural \n\n";
                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                    startActivity(Intent.createChooser(i, "செயலியை பகிர்"));
                } catch(Exception e) {
                    //e.toString();
                    FirebaseCrash.report(e);
                }
                break;
            case R.id.nav_share:
                shareKural();
                break;
            default:
                Toast.makeText(getApplicationContext(), "Not Listed", Toast.LENGTH_LONG).show();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
