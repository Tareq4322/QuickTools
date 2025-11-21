package com.cominatyou.batterytile.standalone;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.service.quicksettings.TileService;
import android.widget.Toast;

// We need to import the Settings Activity since it's in a different package
import com.cominatyou.batterytile.preferences.PreferencesActivity;

public class QuickSettingsTileLongPressHandler extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Figure out WHO sent us here (which tile was long-pressed?)
        ComponentName componentName = getIntent().getParcelableExtra(TileService.EXTRA_COMPONENT_NAME);

        if (componentName == null) {
            // If we don't know who sent us, default to the App Settings
            startActivity(new Intent(this, PreferencesActivity.class));
            finish();
            return;
        }

        String className = componentName.getClassName();
        Intent targetIntent = null;

        // --- ROUTING LOGIC ---

        // 1. Battery Tile -> System Battery Settings
        if (className.equals(QuickSettingsTileService.class.getName())) {
            targetIntent = new Intent(Intent.ACTION_POWER_USAGE_SUMMARY);
        }
        
        // 2. Wattage Tile -> System Battery Settings
        else if (className.equals(WattageTileService.class.getName())) {
            targetIntent = new Intent(Intent.ACTION_POWER_USAGE_SUMMARY);
        }

        // 3. Volume Tile -> System Sound Settings
        else if (className.equals(VolumeTileService.class.getName())) {
            targetIntent = new Intent(Settings.ACTION_SOUND_SETTINGS);
        }

        // 4. DNS Tile -> System Network Settings
        else if (className.equals(DnsTileService.class.getName())) {
            targetIntent = new Intent(Settings.ACTION_NETWORK_AND_INTERNET_SETTINGS);
        }

        // 5. Lock Screen Tile -> This App's Settings (Tile Toolkit)
        // (So you can easily toggle the Accessibility permission if needed)
        else if (className.equals(LockTileService.class.getName())) {
            targetIntent = new Intent(this, PreferencesActivity.class);
        }

        // --- LAUNCH ---

        if (targetIntent != null) {
            // Safety Check: Ensure the phone actually HAS this settings page
            if (targetIntent.resolveActivity(getPackageManager()) != null) {
                targetIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(targetIntent);
            } else {
                // Fallback if the specific page is missing on this ROM
                Toast.makeText(this, "Settings page not found", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Settings.ACTION_SETTINGS));
            }
        } else {
            // Default Catch-all: App Settings
            startActivity(new Intent(this, PreferencesActivity.class));
        }

        // Close this invisible activity instantly so it feels seamless
        finish();
    }
}
