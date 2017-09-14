package com.atsoft.dhinamorukural;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.firebase.FirebaseApp;

import com.google.android.gms.ads.AdView;
import com.google.firebase.crash.FirebaseCrash;

public class AboutUs extends AppCompatActivity {

    //TextView weblink;
    //Button rate, share;
    TextView versionName;

    private static final String TAG = "AboutUS";

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        versionName = (TextView) findViewById(R.id.version_name);
        versionName.setText("பதிப்பு எண் "+Defs.currentVersionName);
        /*View bg = findViewById(R.id.aboutus_top_ll);
        Drawable backround = bg.getBackground();
        backround.setAlpha(80);*/
        FirebaseApp.initializeApp(this);
        //weblink = (TextView) findViewById(R.id.weblinktv);
//        rate = (Button) findViewById(R.id.ratebtn);
//        share = (Button) findViewById(R.id.sharebtn);
        FirebaseApp.initializeApp(this);
        //weblink.setClickable(true);
        //weblink.setMovementMethod(LinkMovementMethod.getInstance());
        //String text = "<a href='http://www.thirukkural.com'> www.thirukkural.com </a>";
        //weblink.setText(Html.fromHtml(text));

        mAdView = (AdView) findViewById(R.id.about_adView);
        mAdView.loadAd(Defs.adRequest);

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
    public void onBackPressed() {
        SharedPreferences sharedPrefs = getSharedPreferences("kural", Context.MODE_PRIVATE);
        Intent intent = new Intent(AboutUs.this, MainActivity.class);
        intent.putExtra("todayKural", sharedPrefs.getString("todaykuralno", "0"));
        intent.putExtra("preread", sharedPrefs.getString("prereadno", "0"));
        intent.putExtra("fromactivity", "ss");
        startActivity(intent);
        AboutUs.this.finish();
    }
}
