package com.merryapps.diskhero.model;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * A service that scans the external storage.
 * //TODO add how this service should be called
 *
 * @author Pravin Sonawane (june.pravin@gmail.com)
 * @since v1.0.0
 */
public class FileScanService extends IntentService {

    private static final String TAG = "FileScanService";

    public FileScanService() {
        super("FileScanService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent() called with: intent = [" + intent + "]");
        //TODO implement this
        new FileSystemUtil().scanFileSystem();
    }
}
