package com.cod3x.ViP3Raddon;

import static com.cod3x.ViP3Raddon.MainActivity.contains;
import static com.cod3x.ViP3Raddon.MainActivity.customCommand;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.ArrayList;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

            Log.e("BootReceiver", "onReceive: Boot completed");
            Prefs prefs = new Prefs(context);
            if (prefs.getBoolean("boot")) {
                int loaded = 0;
                for (String module : prefs.getModules()) {
                    customCommand("insmod /system/lib/modules/"+module);
                    ArrayList<String> driverList = customCommand("ls /sys/bus/usb/drivers");
                    if (contains(driverList, module.replace(".ko", ""))){
                        loaded++;
                    }
                }
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"NE")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Modules booted")
                        .setContentText("Loaded "+loaded+" modules, "+(prefs.getModules().size()-loaded)+" failed")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                createNotificationChannel(context);
                notificationManager.notify(12, builder.build());
            }







    }
    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ViP3R Addon";
            String description = "Driver status notification..";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("NE", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}

