package com.merryapps.diskhero.model;

import android.app.IntentService;
import android.content.Intent;

/**
 * A service that scans the external storage.
 * //TODO add how this service should be called
 *
 * @author Pravin Sonawane (june.pravin@gmail.com)
 * @since v1.0.0
 */
public class FileScanService extends IntentService {

    public FileScanService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //TODO implement this
        new FileSystemUtil().scanFileSystem();
    }
}
