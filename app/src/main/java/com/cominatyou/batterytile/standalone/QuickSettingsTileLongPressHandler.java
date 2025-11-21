package com.cominatyou.batterytile.standalone;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

// Only importing the services to check names
import com.cominatyou.batterytile.standalone.DnsTileService;
import com.cominatyou.batterytile.standalone.LockTileService;
import com.cominatyou.batterytile.standalone.QuickSettingsTileService;
import com.cominatyou.batterytile.standalone.VolumeTileService;

import java.util.List;

public class QuickSettingsTileLongPressHandler extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ComponentName componentName = getIntent().getParcelableExtra(Intent.EXTRA_COMPONENT_NAME);

        // If we don't know who sent us, open App Settings
        if (componentName == null) {
            openAppSettings();
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

        // 2. Volume Tile -> System Sound Settings
        else if (className.equals(VolumeTileService.class.getName())) {
            targetIntent = new Intent(Settings.ACTION_SOUND_SETTINGS);
        }

        // 3. DNS Tile -> System Network Settings
        else if (className.equals(DnsTileService.class.getName())) {
            // Try specific Network page
            targetIntent = new Intent("android.settings.NETWORK_AND_INTERNET_SETTINGS");
        }

        // 4. Lock Screen Tile -> This App's Settings
        else if (className.equals(LockTileService.class.getName())) {
            openAppSettings();
            finish();
            return;
        }

        // --- EXECUTE ---

        if (targetIntent != null) {
            try {
                targetIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(targetIntent);
            } catch (Exception e) {
                // DNS Fallback: Wireless Settings
                if (className.equals(DnsTileService.class.getName())) {
                    try {
                        Intent fallback = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                        fallback.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(fallback);
                    } catch (Exception ex) {
                        openAppSettings(); // Give up, show app settings
                    }
                } else {
                    // General Fallback: Main Settings
                    try {
                        startActivity(new Intent(Settings.ACTION_SETTINGS));
                    } catch (Exception ex2) {
                        Toast.makeText(this, "Could not open settings", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } else {
            openAppSettings();
        }

        finish();
    }

    /**
     * THE FIX: Ask the system to find our own Settings Activity.
     * This works regardless of package names or folder structures.
     */
    private void openAppSettings() {
        // 1. Create an intent that asks for "The Preferences page for THIS app"
        Intent intent = new Intent("android.intent.action.APPLICATION_PREFERENCES");
        intent.setPackage(getPackageName()); // Restrict search to our own app
        
        // 2. Ask PackageManager if it can find a matching activity
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

        if (activities != null && !activities.isEmpty()) {
            // 3. Found it! Grab the class name dynamically
            ResolveInfo info = activities.get(0);
            Intent launchIntent = new Intent();
            launchIntent.setClassName(info.activityInfo.packageName, info.activityInfo.name);
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(launchIntent);
        } else {
            // 4. If that fails, try the "App Info" page (Uninstall/Permissions page)
            // This GUARANTEED to exist on every Android phone.
            try {
                Intent appInfoIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                appInfoIntent.setData(android.net.Uri.parse("package:" + getPackageName()));
                appInfoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(appInfoIntent);
            } catch (Exception e) {
                Toast.makeText(this, "Error: Could not open App Settings", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
