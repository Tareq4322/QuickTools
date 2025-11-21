package com.cominatyou.batterytile.standalone;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

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

        if (componentName == null) {
            openAppSettings();
            finish();
            return;
        }

        String className = componentName.getClassName();
        Intent targetIntent = null;

        // --- ROUTING LOGIC ---

        // 1. Battery -> Battery Settings
        if (className.equals(QuickSettingsTileService.class.getName())) {
            targetIntent = new Intent(Intent.ACTION_POWER_USAGE_SUMMARY);
        }

        // 2. Volume -> Sound Settings
        else if (className.equals(VolumeTileService.class.getName())) {
            targetIntent = new Intent(Settings.ACTION_SOUND_SETTINGS);
        }

        // 3. DNS -> Network Settings
        else if (className.equals(DnsTileService.class.getName())) {
            // Try specific Network page
            targetIntent = new Intent("android.settings.NETWORK_AND_INTERNET_SETTINGS");
        }

        // 4. Lock Screen -> App Settings
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
                // Fallback logic
                if (className.equals(DnsTileService.class.getName())) {
                    try {
                        Intent fallback = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                        fallback.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(fallback);
                    } catch (Exception ex) {
                        openAppSettings();
                    }
                } else {
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

    private void openAppSettings() {
        Intent intent = new Intent("android.intent.action.APPLICATION_PREFERENCES");
        intent.setPackage(getPackageName());
        
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

        if (activities != null && !activities.isEmpty()) {
            ResolveInfo info = activities.get(0);
            Intent launchIntent = new Intent();
            launchIntent.setClassName(info.activityInfo.packageName, info.activityInfo.name);
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(launchIntent);
        } else {
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
}        // --- ROUTING LOGIC ---

        // 1. Battery -> Battery Settings
        if (className.equals(QuickSettingsTileService.class.getName())) {
            targetIntent = new Intent(Intent.ACTION_POWER_USAGE_SUMMARY);
        }

        // 2. Volume -> Sound Settings
        else if (className.equals(VolumeTileService.class.getName())) {
            targetIntent = new Intent(Settings.ACTION_SOUND_SETTINGS);
        }

        // 3. DNS -> Network Settings
        else if (className.equals(DnsTileService.class.getName())) {
            // Try specific Network page
            targetIntent = new Intent("android.settings.NETWORK_AND_INTERNET_SETTINGS");
        }

        // 4. Lock Screen -> App Settings
        else if (className.equals(LockTileService.class.getName())) {
            openAppSettings();
            finish();
            return;
        }
        
        // 5. Internet Speed -> Data Usage Settings
        else if (className.equals(InternetSpeedTileService.class.getName())) {
            targetIntent = new Intent(Settings.ACTION_DATA_USAGE_SETTINGS);
        }

        // --- EXECUTE ---

        if (targetIntent != null) {
            try {
                targetIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(targetIntent);
            } catch (Exception e) {
                // Fallback logic
                boolean isNetworkTile = className.equals(DnsTileService.class.getName()) || 
                                      className.equals(InternetSpeedTileService.class.getName());
                
                if (isNetworkTile) {
                    try {
                        Intent fallback = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                        fallback.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(fallback);
                    } catch (Exception ex) {
                        openAppSettings();
                    }
                } else {
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

    private void openAppSettings() {
        Intent intent = new Intent("android.intent.action.APPLICATION_PREFERENCES");
        intent.setPackage(getPackageName());
        
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

        if (activities != null && !activities.isEmpty()) {
            ResolveInfo info = activities.get(0);
            Intent launchIntent = new Intent();
            launchIntent.setClassName(info.activityInfo.packageName, info.activityInfo.name);
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(launchIntent);
        } else {
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
