package me.josena.currencies_services_notifications.utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import me.josena.currencies_services_notifications.R;

public class CurrencyDownloadBroadcastReceiver extends BroadcastReceiver {

    private final int NOTIFICATION_ID = 1;

    private String message;

    @Override
    public void onReceive(Context context, Intent intent) {

        message = intent.getStringExtra("message");
        // Handle the broadcast here
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "currency_downloader")
                .setContentTitle("Divisas actualizadas")
                .setContentText(message)
//                .setContentText("Se han actualizado los valores de cada divisa.\n" + message)
                .setSmallIcon(R.drawable.icon_notification)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 0);
            // Return instead of showing the notification
            return;
        }
        // If the permission is already granted, show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

}
