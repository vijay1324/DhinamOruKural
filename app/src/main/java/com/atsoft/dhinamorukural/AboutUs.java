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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.crash.FirebaseCrash;

public class AboutUs extends AppCompatActivity {

    //TextView weblink;
    Button rate, share;

    private static final String TAG = "AboutUS";

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        View bg = findViewById(R.id.aboutus_top_ll);
        Drawable backround = bg.getBackground();
        backround.setAlpha(80);
        FirebaseApp.initializeApp(this);
        //weblink = (TextView) findViewById(R.id.weblinktv);
        rate = (Button) findViewById(R.id.ratebtn);
        share = (Button) findViewById(R.id.sharebtn);
        FirebaseApp.initializeApp(this);
        //weblink.setClickable(true);
        //weblink.setMovementMethod(LinkMovementMethod.getInstance());
        //String text = "<a href='http://www.thirukkural.com'> www.thirukkural.com </a>";
        //weblink.setText(Html.fromHtml(text));

        mAdView = (AdView) findViewById(R.id.about_adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

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

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name);
                    String sAux = "\nஇந்த செயலியை நான் பரிந்துரைக்கிறேன்\n\n";
                    sAux = sAux + "https://play.google.com/store/apps/details?id=com.atsoft.dhinamorukural \n\n";
                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                    startActivity(Intent.createChooser(i, "choose one"));
                } catch(Exception e) {
                    //e.toString();
                    FirebaseCrash.report(e);
                }
            }
        });

        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
