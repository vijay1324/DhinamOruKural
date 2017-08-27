package com.atsoft.dhinamorukural;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.FirebaseApp;
import com.google.firebase.crash.FirebaseCrash;

public class SplashScreen extends AppCompatActivity {

    SharedPreferences sharedPrefs;

    private static final String TAG = "SplashScreen";

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        mAdView = (AdView) findViewById(R.id.ss_adView);
        FirebaseApp.initializeApp(this);
        MobileAds.initialize(this, String.valueOf(R.string.YOUR_ADMOB_APP_ID));
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        sharedPrefs = getSharedPreferences("kural", Context.MODE_PRIVATE);

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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                intent.putExtra("todayKural", sharedPrefs.getString("todaykuralno", "0"));
                intent.putExtra("preread", sharedPrefs.getString("prereadno", "0"));
                intent.putExtra("fromactivity", "ss");
                startActivity(intent);
                SplashScreen.this.finish();
            }
        }, 3000);
    }
}
