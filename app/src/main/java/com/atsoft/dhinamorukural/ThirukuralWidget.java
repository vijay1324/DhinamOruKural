package com.atsoft.dhinamorukural;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Html;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.StringTokenizer;

/**
 * Implementation of App Widget functionality.
 */
public class ThirukuralWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        Defs defs = new Defs();
        SharedPreferences sharedPrefs = context.getSharedPreferences("kural", Context.MODE_PRIVATE);
        int currentNo = Integer.parseInt(sharedPrefs.getString("todaykuralno", "0"));
        Log.d("Syso My Wigetid : ", ""+appWidgetId);
        DBController controller = new DBController(context);
        SQLiteDatabase db = controller.getReadableDatabase();
        String wid_kural = "";
        String kuralnostr = String.valueOf(currentNo + 1);
        String qry = "SELECT thirukural FROM kural where kuralno = '"+kuralnostr+"'";
        System.out.println("Syso select qry : " +qry);
        Cursor cursor = db.rawQuery(qry, null);
        if (cursor.moveToNext()) {
            wid_kural = cursor.getString(0);
        } else
            System.out.println("Syso empty db");
        cursor.close();
        db.close();

        String[] athigaramarr = context.getResources().getStringArray(R.array.athigaram);
        int currentAgarathi = 0;
        if (currentNo < 10)
            currentAgarathi = 0;
        else {
            currentAgarathi  = currentNo / 10;
        }

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.thirukural_widget);
        views.setTextViewText(R.id.appwidget_text, wid_kural);
        views.setTextViewText(R.id.kural_no_widget, kuralnostr + " : " + athigaramarr[currentAgarathi]);

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("todayKural", ""+currentNo);
        intent.putExtra("preread", sharedPrefs.getString("prereadno", "0"));
        intent.putExtra("fromactivity", "wid");
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        views.setOnClickPendingIntent(R.id.wid_top_ll, pendingIntent);

        //Share from Widget
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH");
        int hour = Integer.parseInt(simpleDateFormat.format(calendar.getTime()));
        String greetingmsg = "";
        if (hour < 12)
            greetingmsg = "காலை வணக்கம்";
        else if (hour < 16)
            greetingmsg = "மதிய வணக்கம்";
        else if (hour < 20)
            greetingmsg = "மாலை வணக்கம்";
        else
            greetingmsg = "இனிய இரவு";

        StringTokenizer stringTokenizer = new StringTokenizer(defs.getValue(context), "$");
        int k = 0;
        String shareno = "", shareiyal = "", sharepal = "", shareathigaram = "", sharekural = "", shareurai = "";
        while (stringTokenizer.hasMoreTokens()) {
            switch (k) {
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
            k++;
        }

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.setType("text/html");

        shareIntent.putExtra(Intent.EXTRA_SUBJECT, context.getResources().getString(R.string.todays_kural));
//        intent.putExtra(android.content.Intent.EXTRA_TEXT, greetingmsg + "\n" +defs.getValue(context) + "\n\nhttps://play.google.com/store/apps/details?id=com.atsoft.dhinamorukural ");
        shareIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml("<b>   இன்றைய திருக்குறள்   </b>") + "\n\n" +
                sharepal + " - " + shareiyal + "\n" + shareno + " - " + shareathigaram + "\n\n" + Html.fromHtml("<b>திருக்குறள்: </b>") + "\n" +
                sharekural + "\n\n" + Html.fromHtml("<b> உரை:  </b>") + "\n" + shareurai + "\n\n" + greetingmsg + "\n\nhttps://play.google.com/store/apps/details?id=com.atsoft.dhinamorukural \n\n");
        PendingIntent sharePendingIntent = PendingIntent.getActivity(context, 0, shareIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        views.setOnClickPendingIntent(R.id.wid_share_btn, sharePendingIntent);
        views.setOnClickPendingIntent(R.id.wid_share_img_btn, sharePendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

