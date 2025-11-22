package com.cominatyou.batterytile.standalone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScreenOffReceiver extends BroadcastReceiver {
    
    private final Runnable onScreenOff;

    // Constructor takes a "Runnable" (a command to run when screen turns off)
    public ScreenOffReceiver(Runnable onScreenOff) {
        this.onScreenOff = onScreenOff;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
            if (onScreenOff != null) {
                onScreenOff.run();
            }
        }
    }
}
