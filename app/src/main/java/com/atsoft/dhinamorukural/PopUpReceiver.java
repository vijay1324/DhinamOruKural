package com.atsoft.dhinamorukural;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

public class PopUpReceiver extends BroadcastReceiver {

    SharedPreferences sharedPrefs;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");
        sharedPrefs = context.getSharedPreferences("kural", Context.MODE_PRIVATE);
        boolean alarmOnOff = sharedPrefs.getBoolean("popup_onoff", false);
        if (!alarmOnOff) {
            Intent intent1 = new Intent(context, IndrayaKural.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);
        } else {
            showNotification(context);
        }
    }

    private void showNotification(Context context) {
        DBController controller = new DBController(context);
        SQLiteDatabase db = controller.getReadableDatabase();
        String qry = "SELECT thirukural FROM kural where kuralno = '"+String.valueOf(Integer.parseInt(sharedPrefs.getString("todaykuralno", "0")) + 1)+"'";
        System.out.println("Syso select qry : " +qry);
        String todayKural = "";
        Cursor cursor = db.rawQuery(qry, null);
        if (cursor.moveToNext()) {
            todayKural = cursor.getString(0);
        }
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.indrayakural_icon)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.indrayakural_icon))
                        .setContentTitle(context.getResources().getString(R.string.todays_kural))
                        .setContentText(todayKural)
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setPriority(Notification.PRIORITY_DEFAULT);

        /* Add Big View Specific Configuration */
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        String[] events = todayKural.split("\n");

        // Sets a title for the Inbox style big view
        inboxStyle.setBigContentTitle(context.getResources().getString(R.string.todays_kural));

        // Moves events into the big view
        for (int i=0; i < events.length; i++) {
            inboxStyle.addLine(events[i]);
        }

        mBuilder.setStyle(inboxStyle);

        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.putExtra("todayKural", ""+Integer.parseInt(sharedPrefs.getString("todaykuralno", "0")));
        notificationIntent.putExtra("preread", sharedPrefs.getString("prereadno", "0"));
        notificationIntent.putExtra("fromactivity", "ad");
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) context.getSystemService(context.getApplicationContext().NOTIFICATION_SERVICE);
        manager.notify(0, mBuilder.build());
    }
}
