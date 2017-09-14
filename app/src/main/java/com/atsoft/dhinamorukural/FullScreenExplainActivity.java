package com.atsoft.dhinamorukural;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;

public class FullScreenExplainActivity extends AppCompatActivity {

    TextView salaman, mk, varathu, paramu, mana, eng_exp;
    String salaman_str = "", mk_str = "", varathu_str = "", paramu_str = "", mana_str = "", eng_exp_str = "";
    FloatingActionButton exit;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_explain);
        salaman = (TextView) findViewById(R.id.full_screen_exp_salamon_tv);
        mk = (TextView) findViewById(R.id.full_screen_exp_mk_tv);
        varathu = (TextView) findViewById(R.id.full_screen_exp_varathu_tv);
        paramu = (TextView) findViewById(R.id.full_screen_exp_pari_tv);
        mana = (TextView) findViewById(R.id.full_screen_exp_manakuavar_tv);
        eng_exp = (TextView) findViewById(R.id.full_screen_exp_english_tv);
        exit = (FloatingActionButton) findViewById(R.id.full_screen_fab_fullscreen);
        mAdView = (AdView) findViewById(R.id.full_screen_main_adView);
        mAdView.loadAd(Defs.adRequest);
        try {
            salaman_str = getIntent().getStringExtra("salaman");
            mk_str = getIntent().getStringExtra("mk");
            varathu_str = getIntent().getStringExtra("varathu");
            paramu_str = getIntent().getStringExtra("palam");
            mana_str = getIntent().getStringExtra("mana");
            eng_exp_str = getIntent().getStringExtra("exp_eng");
        } catch (Exception e) {
            e.printStackTrace();
        }
        salaman.setText(salaman_str);
        mk.setText(mk_str);
        varathu.setText(varathu_str);
        paramu.setText(paramu_str);
        mana.setText(mana_str);
        eng_exp.setText(eng_exp_str);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FullScreenExplainActivity.this.finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        FullScreenExplainActivity.this.finish();
    }
}
