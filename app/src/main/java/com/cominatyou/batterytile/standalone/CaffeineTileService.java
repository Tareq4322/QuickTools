package com.cominatyou.batterytile.standalone;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.widget.Toast;

public class CaffeineTileService extends TileService {

    @Override
    public void onStartListening() {
        updateTile();
    }

    private void updateTile() {
        Tile tile = getQsTile();
        if (tile == null) return;

        tile.setLabel("Caffeine");
        tile.setIcon(Icon.createWithResource(this, R.drawable.ic_coffee));

        if (KeepAwakeService.isRunning) {
            tile.setState(Tile.STATE_ACTIVE);
            tile.setSubtitle("On");
        } else {
            tile.setState(Tile.STATE_INACTIVE);
            tile.setSubtitle("Off");
        }
        
        tile.updateTile();
    }

    @Override
    public void onClick() {
        Intent intent = new Intent(this, KeepAwakeService.class);

        try {
            if (KeepAwakeService.isRunning) {
                stopService(intent);
            } else {
                // Wrapped in try-catch to handle background start restrictions
                startForegroundService(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Could not start Caffeine service", Toast.LENGTH_SHORT).show();
            // Reset visual state if it failed
            updateTile();
        }
        
        // Optimistic update
        updateTile();
    }

    public static void requestUpdate(Context context) {
        try {
            requestListeningState(context, new ComponentName(context, CaffeineTileService.class));
        } catch (Exception e) {
            // Tile might not be active
        }
    }
}    public void onClick() {
        Intent intent = new Intent(this, KeepAwakeService.class);

        try {
            if (KeepAwakeService.isRunning) {
                stopService(intent);
            } else {
                // FIX: Wrapped in try-catch to handle background start restrictions
                startForegroundService(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Could not start Caffeine service", Toast.LENGTH_SHORT).show();
            // Reset visual state if it failed
            updateTile();
        }
        
        // Optimistic update
        updateTile();
    }

    public static void requestUpdate(Context context) {
        try {
            requestListeningState(context, new ComponentName(context, CaffeineTileService.class));
        } catch (Exception e) {
            // Tile might not be active
        }
    }
}    public void onClick() {
        Intent intent = new Intent(this, KeepAwakeService.class);

        if (KeepAwakeService.isRunning) {
            // Turn OFF
            stopService(intent);
        } else {
            // Turn ON
            startForegroundService(intent);
        }
        
        // Optimistic update (will be confirmed by service)
        updateTile();
    }

    // Helper method so the Service can tell the Tile to refresh if the system kills it
    public static void requestUpdate(Context context) {
        try {
            requestListeningState(context, new ComponentName(context, CaffeineTileService.class));
        } catch (Exception e) {
            // Tile might not be active
        }
    }
}
