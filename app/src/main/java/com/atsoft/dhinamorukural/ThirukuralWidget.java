package com.atsoft.dhinamorukural;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.ArrayList;

/**
 * Implementation of App Widget functionality.
 */
public class ThirukuralWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

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

