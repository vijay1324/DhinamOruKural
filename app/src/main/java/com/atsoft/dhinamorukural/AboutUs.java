package com.atsoft.dhinamorukural;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class AboutUs extends AppCompatActivity {

    TextView weblink;
    Button rate, share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        View bg = findViewById(R.id.aboutus_top_ll);
        Drawable backround = bg.getBackground();
        backround.setAlpha(80);
        weblink = (TextView) findViewById(R.id.weblinktv);
        rate = (Button) findViewById(R.id.ratebtn);
        share = (Button) findViewById(R.id.sharebtn);
        weblink.setClickable(true);
        weblink.setMovementMethod(LinkMovementMethod.getInstance());
        String text = "<a href='http://www.thirukkural.com'> www.thirukkural.com </a>";
        weblink.setText(Html.fromHtml(text));


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