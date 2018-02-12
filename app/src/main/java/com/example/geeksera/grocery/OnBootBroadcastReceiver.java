package com.example.geeksera.grocery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.geeksera.grocery.GCM.MyInstanceIDListenerService;

public class OnBootBroadcastReceiver extends BroadcastReceiver {

    private Intent i = new Intent("com.example.geeksera.grocery.GCM.MyInstanceIDListenerService");

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            i.setClass(context, MyInstanceIDListenerService.class);
            context.startService(i);
        } catch (IllegalStateException e) {
            Log.d("", "----");
        }
    }
}
