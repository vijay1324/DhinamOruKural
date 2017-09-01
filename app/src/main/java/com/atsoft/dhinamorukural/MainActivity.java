package com.atsoft.dhinamorukural;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActionBarDrawerToggle;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.FirebaseApp;
import com.google.firebase.crash.FirebaseCrash;

import java.util.Calendar;
import java.util.Date;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends AppCompatActivity {

    TextView thirukural, englishkural, pal, iyal, athigaram, kuralno, exp_soloman, exp_mk, exp_varathan, exp_parimel, exp_manakudavar, exp_english;
    static int currentNo = 0;
    static int currentAgarathi = 0, currentPal = 0, currentIyal = 0;
    static int todayKural = 0;
    String[] iyalarr, athigaramarr,palarr;

    FloatingActionButton pre, next;
    SharedPreferences sharedPrefs;
    static String fromactivity = "";

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerLisener;
    private ListView listView;
    String[] drawContent = {"இன்றைய குறள்", "குறள் எண் தேடல்", "பால் தேர்வு", "இயல் தேர்வு", "அதிகாரம் தேர்வு", "அமைப்புகள்", "எங்களை பற்றி"};
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
        View bg = findViewById(R.id.toplinearlayout);
        Drawable backround = bg.getBackground();
        backround.setAlpha(80);
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
        pre = (FloatingActionButton) findViewById(R.id.fab_previous);
        next = (FloatingActionButton) findViewById(R.id.fab_next);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        listView = (ListView) findViewById(R.id.mylistview);
        mAdView = (AdView) findViewById(R.id.main_adView);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_ad_unit_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        System.out.println("Syso devise id : "+android_id);
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

//        AdRequest adRequest = new AdRequest.Builder().addTestDevice("4c2da3293cd5f88b").addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        mAdView.loadAd(adRequest);
        sharedPrefs = getSharedPreferences("kural", Context.MODE_PRIVATE);
        System.out.println("Syso Current No : "+currentNo);
        //getPreviosValue();
        try {
            Bundle bundle = getIntent().getExtras();
            todayKural = Integer.parseInt(bundle.getString("todayKural"));
            currentNo = Integer.parseInt(bundle.getString("preread"));
            fromactivity = bundle.getString("fromactivity");
            System.out.println("Syso Get value");
        } catch (Exception e) {
            e.printStackTrace();
            FirebaseCrash.report(e);
        }
        currentNo = Integer.parseInt(sharedPrefs.getString("prereadno", "0"));
        System.out.println("Syso Current No : "+currentNo);

        adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, drawContent);
        listView.setAdapter(adapter);
        drawerLisener = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                //Toast.makeText(getApplicationContext(), "Drawer Open", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                //Toast.makeText(getApplicationContext(), "Drawer Close", Toast.LENGTH_LONG).show();
            }
        };

        drawerLayout.setDrawerListener(drawerLisener);
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
            setValue(currentNo);
        } else {
            next.setVisibility(View.INVISIBLE);
            pre.setVisibility(View.INVISIBLE);
            setValue(todayKural);
        }
        //showNotification();
        alarmHour = sharedPrefs.getInt("alermtime", 8);
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



        mInterstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {

            }

            @Override
            public void onAdClosed() {
                MainActivity.this.finish();
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        startActivity(new Intent(MainActivity.this, IndrayaKural.class));
                        MainActivity.this.finish();
                        break;
                    case 1:
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                        final View dialogView = inflater.inflate(R.layout.edittext_dialog, null);
                        dialogBuilder.setView(dialogView);

                        final EditText edt = (EditText) dialogView.findViewById(R.id.kuralnoet);
                        View bg = dialogView.findViewById(R.id.dialog_top_ll);
                        Drawable backround = bg.getBackground();
                        backround.setAlpha(80);

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
                    case 2:
                        searhByPal();
                        break;
                    case 3:
                        AlertDialog.Builder iyal_builderSingle = new AlertDialog.Builder(MainActivity.this);
                        iyal_builderSingle.setIcon(R.drawable.mini2_icon_42);
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
                    case 4:
                        AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this);
                        builderSingle.setIcon(R.drawable.mini2_icon_42);
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
                    case 5:
                        AlertDialog.Builder set_dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                        LayoutInflater set_inflater = MainActivity.this.getLayoutInflater();
                        final View set_dialogView = set_inflater.inflate(R.layout.edittext_dialog, null);
                        set_dialogBuilder.setView(set_dialogView);

                        final EditText set_edt = (EditText) set_dialogView.findViewById(R.id.kuralnoet);
                        set_edt.setHint("0 ~ 23");
                        View set_bg = set_dialogView.findViewById(R.id.dialog_top_ll);
                        Drawable set_backround = set_bg.getBackground();
                        set_backround.setAlpha(80);

                        set_edt.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                try {
                                    int kno = Integer.parseInt(set_edt.getText().toString().trim());
                                    if (kno < 0 || kno > 23) {
                                        Toast.makeText(getApplicationContext(), "சரியான நேரத்தை உள்ளிடவும்", Toast.LENGTH_LONG).show();
                                        set_edt.setText("");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                            }
                        });

                        set_dialogBuilder.setTitle("அறிவிப்பு நேரம்");
                        set_dialogBuilder.setMessage("அறிவிப்பு நேரத்தை மாற்றவும்");
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
                                    } else {
                                        Toast.makeText(getApplicationContext(), "சரியான எண்ணை உள்ளிடவும்", Toast.LENGTH_LONG).show();
                                        set_edt.setText("");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
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
                    case 6:
                        startActivity(new Intent(MainActivity.this, AboutUs.class));
                        MainActivity.this.finish();
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "Not Listed", Toast.LENGTH_LONG).show();
                        break;
                }

                if(drawerLayout.isDrawerOpen(Gravity.LEFT))
                    drawerLayout.closeDrawer(Gravity.LEFT);
            }
        });
    }

    private void searhByPal () {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this);
        builderSingle.setIcon(R.drawable.mini2_icon_42);
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
                iyal_builderSingle.setIcon(R.drawable.mini2_icon_42);
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
        Athigaram_builderSingle.setIcon(R.drawable.mini2_icon_42);
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
            builder.setIcon(R.drawable.mini_icon_42);
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
        am.setRepeating(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), AlarmManager.INTERVAL_DAY, sender);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(drawerLayout.isDrawerOpen(Gravity.LEFT))
            drawerLayout.closeDrawer(Gravity.LEFT);
        else
            drawerLayout.openDrawer(Gravity.LEFT);
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

        if (getInt < 381)
            currentPal = 0;
        else if (getInt < 1081)
            currentPal = 1;
        else
            currentPal = 2;

        if (getInt < 41)
            currentIyal = 0;
        else if (getInt < 241)
            currentIyal = 1;
        else if (getInt < 371)
            currentIyal = 2;
        else if (getInt < 381)
            currentIyal = 3;
        else if (getInt < 631)
            currentIyal = 4;
        else if (getInt < 731)
            currentIyal = 5;
        else if (getInt < 751)
            currentIyal = 6;
        else if (getInt < 761)
            currentIyal = 7;
        else if (getInt < 781)
            currentIyal = 8;
        else if (getInt < 951)
            currentIyal = 9;
        else if (getInt < 1081)
            currentIyal = 10;
        else if (getInt < 1151)
            currentIyal = 11;
        else
            currentIyal = 12;


        SQLiteDatabase db = controller.getReadableDatabase();
        String qry = "SELECT kuralno, thirukural, mk_exp, varathu_exp, soloman_exp, parimelalagar_exp, manakadavure_exp, translate_kural, eng_exp FROM kural where kuralno = '"+kuralnostr+"'";
        System.out.println("Syso select qry : " +qry);
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
        } else
            System.out.println("Syso empty db");
        db.close();

        pal.setText(palarr[currentPal]);
        iyal.setText("குறள் இயல்: "+ iyalarr[currentIyal]);
        athigaram.setText(athigaramarr[currentAgarathi]);
        kuralno.setText("குறள் "+kuralnostr);

        SharedPreferences.Editor editor = sharedPrefs.edit();
        try {
            editor.putString("prereadno", String.valueOf(currentNo));
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
            FirebaseCrash.report(e);
        }
    }
}
