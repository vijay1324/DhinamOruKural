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
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;
import android.text.Html;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.StringTokenizer;

public class PopUpReceiver extends BroadcastReceiver {

    SharedPreferences sharedPrefs;
    Defs defs;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");
        defs = new Defs();
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

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("text/html");

        intent.putExtra(Intent.EXTRA_SUBJECT, context.getResources().getString(R.string.todays_kural));
//        intent.putExtra(android.content.Intent.EXTRA_TEXT, greetingmsg + "\n" +defs.getValue(context) + "\n\nhttps://play.google.com/store/apps/details?id=com.atsoft.dhinamorukural ");
        intent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml("<b>   இன்றைய திருக்குறள்   </b>") + "\n\n" +
                sharepal + " - " + shareiyal + "\n" + shareno + " - " + shareathigaram + "\n\n" + Html.fromHtml("<b>திருக்குறள்: </b>") + "\n" +
                sharekural + "\n\n" + Html.fromHtml("<b> உரை:  </b>") + "\n" + shareurai + "\n\n" + greetingmsg + "\n\nhttps://play.google.com/store/apps/details?id=com.atsoft.dhinamorukural \n\n");
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.indrayakural_icon)
                        //.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.indrayakural_icon))
                        .setContentTitle(context.getResources().getString(R.string.todays_kural))
                        .setContentText(todayKural)
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setPriority(Notification.PRIORITY_DEFAULT);

        /* Add Big View Specific Configuration */
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        PendingIntent shareIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        RemoteInput remoteInput;
        NotificationCompat.Action action;
        if (Build.VERSION.SDK_INT >= 20) {
//                remoteInput = new RemoteInput.Builder("Reply Key").setLabel(greetingmsg).build();

            action = new NotificationCompat.Action.Builder(R.drawable.ic_share_white_18dp,
                    "பகிர்", shareIntent)
//                        .addRemoteInput(remoteInput)
                    .build();
            mBuilder.addAction(action);
        } else {
            Log.i("Syso else : ","Verison below");
        }


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
