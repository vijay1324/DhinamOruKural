package com.atsoft.dhinamorukural;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.FirebaseApp;
import com.google.firebase.crash.FirebaseCrash;

import org.jsoup.Jsoup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SplashScreen extends AppCompatActivity {

    SharedPreferences sharedPrefs;

    private AdView mAdView;


    private static final int REQUEST= 112;

    Context mContext = this;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        mAdView = (AdView) findViewById(R.id.ss_adView);
        progress = new ProgressDialog(this);
        progress.setTitle("Please wait.!");
        progress.setMessage("Uploading...");
        progress.setIcon(R.drawable.dialog_process);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setCancelable(false);
        progress.setIndeterminate(false);
        FirebaseApp.initializeApp(this);
        MobileAds.initialize(getApplicationContext(), String.valueOf(R.string.YOUR_ADMOB_APP_ID));
        mAdView.loadAd(Defs.adRequest);
        sharedPrefs = getSharedPreferences("kural", Context.MODE_PRIVATE);
        getAllPermission();

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

    private void checkDB() {
        String DB_NAME = "Thirukural.db";
        try {
            File database = getApplicationContext().getDatabasePath(DB_NAME);
            if (!database.exists()) {
                byte[] buffer = new byte[1024];
                OutputStream myOutput = null;
                int length;
                // Open your local db as the input stream
                InputStream myInput = null;
                try
                {
                    myInput =getApplicationContext().getAssets().open(DB_NAME);
                    // transfer bytes from the inputfile to the
                    // outputfile
                    myOutput =new FileOutputStream(database);
                    while((length = myInput.read(buffer)) > 0)
                    {
                        myOutput.write(buffer, 0, length);
                    }
                    myOutput.close();
                    myOutput.flush();
                    myInput.close();
                    Log.i("Syso Database",
                            "New database has been copied to device!"+getApplicationContext().getDatabasePath("Thirukural.db"));
                    //saveToSD();
                    new GetValueFromDB().execute("");

                }
                catch(IOException e)
                {
                    e.printStackTrace();
                    FirebaseCrash.report(e);
                }
            } else {
                //saveToSD();
                new GetValueFromDB().execute("");
            }
        } catch (Exception e) {
            e.printStackTrace();
            FirebaseCrash.report(e);
        }
    }

    private void saveToSD() {
        byte[] buffer = new byte[1024];
        OutputStream myOutput = null;
        int length;
        // Open your local db as the input stream
        InputStream myInput = null;
        try
        {
            myInput =getApplicationContext().getAssets().open("Thirukural.db");
            // transfer bytes from the inputfile to the
            // outputfile
            File dir = Environment.getExternalStorageDirectory();
            File desc = new File(dir, "Thirukural/Thirukural.db");
            myOutput =new FileOutputStream(desc);
            while((length = myInput.read(buffer)) > 0)
            {
                myOutput.write(buffer, 0, length);
            }
            myOutput.close();
            myOutput.flush();
            myInput.close();
            Log.i("Syso Database",
                    "New database has been copied to device!");
            //new GetValueFromDB().execute("");

        }
        catch(IOException e)
        {
            e.printStackTrace();
            FirebaseCrash.report(e);
        }
    }

    private void getAllPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            Log.d("TAG","@@@ IN IF Build.VERSION.SDK_INT >= 23");
            String[] PERMISSIONS = {
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.INTERNET,
                    android.Manifest.permission.EXPAND_STATUS_BAR,
                    android. Manifest.permission.ACCESS_COARSE_LOCATION,
            };


            if (!hasPermissions(mContext, PERMISSIONS)) {
                Log.d("TAG","@@@ IN IF hasPermissions");
                ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, REQUEST );
            } else {
                Log.d("TAG","@@@ IN ELSE hasPermissions");
                checkDB();
            }
        } else {
            Log.d("TAG","@@@ IN ELSE  Build.VERSION.SDK_INT >= 23");
            checkDB();
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("TAG","@@@ PERMISSIONS grant");
                    checkDB();
                } else {
                    Log.d("TAG","@@@ PERMISSIONS Denied");
                    Toast.makeText(mContext, "PERMISSIONS Denied", Toast.LENGTH_LONG).show();
                    final AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreen.this);
                    builder.setCancelable(false);
                    builder.setTitle("அனுமதி மறுக்கப்பட்டது");
                    builder.setIcon(R.drawable.indrayakural_icon);
                    builder.setMessage("நீங்கள் அனுமதி கொடுக்கவில்லை என்றால், இந்த பயன்பாடு வேலை செய்யாது");
                    builder.setPositiveButton("அனுமதி", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                        getAllPermission();
                        }
                    })
                            .setNegativeButton("வெளியேறு", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    SplashScreen.this.finish();
                                }
                            });

                    // Create the AlertDialog object and return it
                    builder.create().show();
                }
            }
        }
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private class GetValueFromDB extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {
            DBController controller = new DBController(mContext);
            SQLiteDatabase db = controller.getReadableDatabase();
            String qry = "SELECT words FROM allwords";
            Cursor cursor = db.rawQuery(qry, null);
            int count = 0;
            Defs.allwords = new String[4968];
            try {
                while (cursor.moveToNext()) {
                    try {
                        Defs.allwords[count] = cursor.getString(cursor.getColumnIndex("words"));
                    } catch (Exception e) {
                        e.printStackTrace();
                        FirebaseCrash.report(e);
                    }
                    count++;
                }
            } catch (Exception e) {
                e.printStackTrace();
                FirebaseCrash.report(e);
            }
            db.close();
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            progress.dismiss();
            //saveToSD();
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run() {
                    VersionChecker versionChecker = new VersionChecker();
                    versionChecker.execute();
                }
            }, 2000);
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
        }

        @Override
        protected void onPreExecute() {
            progress.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progress != null) {
            progress.dismiss();
            progress = null;
        }
    }

    @Override
    public void onBackPressed() {

    }

    public class VersionChecker extends AsyncTask<String, String, String> {

        String newVersion;

        @Override
        protected String doInBackground(String... params) {

            try {
                newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName() + "&hl=en")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select("div[itemprop=softwareVersion]")
                        .first()
                        .ownText();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return newVersion;
        }

        @Override
        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
            String currentVersion = "";
            try {
                currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            Log.d("Syso : new version ", s);
            if (s.equalsIgnoreCase(currentVersion)) {
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                intent.putExtra("todayKural", sharedPrefs.getString("todaykuralno", "0"));
                intent.putExtra("preread", sharedPrefs.getString("prereadno", "0"));
                intent.putExtra("fromactivity", "ss");
                startActivity(intent);
                SplashScreen.this.finish();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreen.this);
                builder.setCancelable(false);
                builder.setTitle(R.string.app_name);
                builder.setIcon(R.drawable.indrayakural_icon);
                builder.setMessage("புதிய பதிப்பு உள்ளது, நீங்கள் புதுப்பித்துக்கொள்கிறீர்களா?");
                builder.setPositiveButton("புதுப்பி", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
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
                        SplashScreen.this.finish();
                    }
                })
                        .setNegativeButton("இப்போது வேண்டாம்", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                                intent.putExtra("todayKural", sharedPrefs.getString("todaykuralno", "0"));
                                intent.putExtra("preread", sharedPrefs.getString("prereadno", "0"));
                                intent.putExtra("fromactivity", "ss");
                                startActivity(intent);
                                SplashScreen.this.finish();
                            }
                        });

                // Create the AlertDialog object and return it
                builder.create().show();
            }
        }
    }
}
