package com.cominatyou.batterytile.standalone.debug;

import android.content.Context;
import androidx.appcompat.app.AlertDialog;

import com.cominatyou.batterytile.standalone.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DebugDialog {
    public static void show(Context context) {
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        final AlertDialog dialog = new MaterialAlertDialogBuilder(context)
                .setTitle(context.getString(R.string.debug_dialog_title))
                .setMessage(context.getString(R.string.debug_dialog_initial_description))
                .setView(R.layout.debug_dialog_layout)
                .setNegativeButton(android.R.string.cancel, (dialogInterface, which) -> dialogInterface.dismiss())
                .setCancelable(false)
                .create();

        // FIX: Java 17 doesn't have .close() for ExecutorService.
        // We use .shutdown() instead to kill the thread pool safely.
        dialog.setOnDismissListener(dialogInterface -> executorService.shutdown());

        dialog.show();

        executorService.execute(new DebugInfoCollector(context, dialog));
    }
}
