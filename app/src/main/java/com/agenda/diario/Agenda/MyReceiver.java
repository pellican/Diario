package com.agenda.diario.Agenda;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.agenda.diario.MainActivity;
import com.agenda.diario.R;

/**
 * Created by portatile on 17/03/2016.
 */
public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getStringExtra("notifica") != null ){
            String messagio= intent.getStringExtra("notifica");
            String data= intent.getStringExtra("data");
            int ora=intent.getIntExtra("ora",-1);
            NotificationManager mNotifyMgr =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_launcher)
                            //example for large icon
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                    .setContentTitle("Agenda")
                    .setContentText(messagio)
                    .setOngoing(false)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setVibrate(new long[]{500 ,500,500})
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);
            Intent i = new Intent(context, MainActivity.class);
            i.setAction("notifica");
            i.putExtra("data",data);
            i.putExtra("ora",ora);
            PendingIntent pendingIntent =
                    PendingIntent.getActivity(
                            context,
                            0,
                            i,
                            PendingIntent.FLAG_CANCEL_CURRENT);
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            // example for blinking LED
            mBuilder.setLights(0xFFb71c1c, 1000, 2000);
            mBuilder.setSound(soundUri);

            mBuilder.setContentIntent(pendingIntent);

            mNotifyMgr.notify(12345, mBuilder.build());
        }

    }
}