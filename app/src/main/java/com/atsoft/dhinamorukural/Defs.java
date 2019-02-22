package com.atsoft.dhinamorukural;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Html;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.firebase.crash.FirebaseCrash;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Created by ATSoft on 9/3/2017.
 */

public class Defs {
    public static String[] allwords;
    public static String currentVersionName = "2.4";
    public static String currentVersionCode = "9";
    //public static AdRequest adRequest = new AdRequest.Builder().addTestDevice("4c2da3293cd5f88b").addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
    public static AdRequest adRequest = new AdRequest.Builder().build();


    SharedPreferences sharedPrefs;
    ArrayList<String> kuralnoarr;
    static int currentNo = 0;
    static String datestr = "";
    String[] iyalarr, athigaramarr, palarr;
    static int currentAgarathi = 0, currentPal = 0, currentIyal = 0;

    public void getTodayKuralNo() {
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

    public String getValue(Context context) throws IndexOutOfBoundsException {
        Calendar calendar = Calendar.getInstance();
        int cdate = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        sharedPrefs = context.getSharedPreferences("kural", Context.MODE_PRIVATE);
        datestr = new DecimalFormat("00").format(cdate) + "-" + new DecimalFormat("00").format(month) + "-" + year;
        String todaydate = sharedPrefs.getString("todaydate", "");
        if (!todaydate.equalsIgnoreCase(datestr) || todaydate.equalsIgnoreCase(""))
            getTodayKuralNo();
        else
            currentNo = Integer.parseInt(sharedPrefs.getString("todaykuralno", "0"));
        DBController controller = new DBController(context);
        SQLiteDatabase db = controller.getReadableDatabase();
        String shareThirukural = "", shareExp = "";
        String kuralnostr = String.valueOf(currentNo+1);
        String qry = "SELECT thirukural, soloman_exp FROM kural where kuralno = '"+kuralnostr+"'";
        System.out.println("Syso select qry : " +qry);
        Cursor cursor = db.rawQuery(qry, null);
        if (cursor.moveToNext()) {
            shareThirukural = cursor.getString(0);
            shareExp = cursor.getString(1);
        } else
            System.out.println("Syso empty db");
        cursor.close();
        db.close();

        palarr = context.getResources().getStringArray(R.array.nav_pal);
        iyalarr = context.getResources().getStringArray(R.array.nav_iyal);
        athigaramarr = context.getResources().getStringArray(R.array.athigaram);
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


        /*pal.setText(palarr[currentPal]);
        iyal.setText(iyalarr[currentIyal]);
        athigaram.setText(athigaramarr[currentAgarathi]);
        kuralno.setText(kuralnostr);
        date.setText(datestr);
        showNotification(noti_kural);*/
        String returnval = R.string.indraya_kural + "\n\t" + R.string.share_pal + palarr[currentPal] + "\n\t" + R.string.share_iyal + iyalarr[currentIyal] + "\n\t" + athigaramarr[currentAgarathi] + " - " + kuralnostr
                + "\n\n" + R.string.share_kural + "\n" + shareThirukural + "\n\n" + R.string.share_urai + "\n\t" + shareExp;
        return kuralnostr + "$" + palarr[currentPal] + "$" + iyalarr[currentIyal] + "$" + athigaramarr[currentAgarathi] + "$" + shareThirukural + "$" + shareExp;
//        return returnval;
    }



    public void shareKural(Context context) {
        StringTokenizer stringTokenizer = new StringTokenizer(getValue(context), "$");
        int i = 0;
        String shareno = "", shareiyal = "", sharepal = "", shareathigaram = "", sharekural = "", shareurai = "";
        while (stringTokenizer.hasMoreTokens()) {
            switch (i) {
                case 0:
                    shareno = stringTokenizer.nextToken();
                    break;
                case 1:
                    sharepal = stringTokenizer.nextToken();
                    break;
                case 2:
                    shareiyal = stringTokenizer.nextToken();
                    break;
                case 3:
                    shareathigaram = stringTokenizer.nextToken();
                    break;
                case 4:
                    sharekural = stringTokenizer.nextToken();
                    break;
                case 5:
                    shareurai = stringTokenizer.nextToken();
                    break;
            }
            i++;
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("text/html");

        intent.putExtra(Intent.EXTRA_SUBJECT, context.getResources().getString(R.string.todays_kural));
        intent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml("<b>   இன்றைய திருக்குறள்   </b>") + "\n\n" +
                sharepal + " - " + shareiyal + "\n" + shareno + " - " + shareathigaram + "\n\n" + Html.fromHtml("<b>திருக்குறள்: </b>") + "\n" +
                sharekural + "\n\n" + Html.fromHtml("<b> உரை:  </b>") + "\n" + shareurai + "\n\nhttps://play.google.com/store/apps/details?id=com.atsoft.dhinamorukural \n\n");
        try {
            context.startActivity(Intent.createChooser(intent, "திருக்குறளை பகிர்"));
        } catch (ActivityNotFoundException e) {
            FirebaseCrash.report(e);
            Toast.makeText(context.getApplicationContext(), "பயன்பாடு இல்லை", Toast.LENGTH_SHORT).show();
        }
    }
}
