package com.cominatyou.batterytile.standalone;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

// Removed failing imports
import com.cominatyou.batterytile.standalone.DnsTileService;
import com.cominatyou.batterytile.standalone.LockTileService;
import com.cominatyou.batterytile.standalone.QuickSettingsTileService;
import com.cominatyou.batterytile.standalone.VolumeTileService;

public class QuickSettingsTileLongPressHandler extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // FIX 1: Use Intent.EXTRA_COMPONENT_NAME instead of TileService.EXTRA_COMPONENT_NAME
        ComponentName componentName = getIntent().getParcelableExtra(Intent.EXTRA_COMPONENT_NAME);

        if (componentName == null) {
            launchAppSettings();
            finish();
            return;
        }

        String className = componentName.getClassName();
        Intent targetIntent = null;

        // --- TRAFFIC COP LOGIC ---

        // 1. Battery Tile -> System Battery Settings
        if (className.equals(QuickSettingsTileService.class.getName())) {
            targetIntent = new Intent(Intent.ACTION_POWER_USAGE_SUMMARY);
        }
        
        // 2. Wattage Tile (Removed to prevent build error since file doesn't exist yet)
        
        // 3. Volume Tile -> System Sound Settings
        else if (className.equals(VolumeTileService.class.getName())) {
            targetIntent = new Intent(Settings.ACTION_SOUND_SETTINGS);
        }

        // 4. DNS Tile -> System Network Settings
        else if (className.equals(DnsTileService.class.getName())) {
            // FIX 2: Use string literal to avoid symbol errors on older compile SDKs
            targetIntent = new Intent("android.settings.NETWORK_AND_INTERNET_SETTINGS");
        }

        // 5. Lock Screen Tile -> This App's Settings (Tile Toolkit)
        else if (className.equals(LockTileService.class.getName())) {
            launchAppSettings();
            finish(); // We launched it manually, so we can exit now
            return;
        }

        // --- EXECUTE ---

        if (targetIntent != null) {
            try {
                if (targetIntent.resolveActivity(getPackageManager()) != null) {
                    targetIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(targetIntent);
                } else {
                    Toast.makeText(this, "Settings page not found", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Settings.ACTION_SETTINGS));
                }
            } catch (Exception e) {
                // Fallback just in case
                startActivity(new Intent(Settings.ACTION_SETTINGS));
            }
        } else {
            launchAppSettings();
        }

        finish();
    }

    // FIX 3: Dynamic launch method to avoid "cannot find symbol PreferencesActivity" error
    private void launchAppSettings() {
        try {
            Intent intent = new Intent();
            // explicitly point to the activity by string name
            intent.setClassName(this, "com.cominatyou.batterytile.preferences.PreferencesActivity");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Could not open App Settings", Toast.LENGTH_SHORT).show();
        }
    }
}
