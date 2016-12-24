package com.merryapps.diskhero.model;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * //TODO add description here.
 * @author Pravin Sonawane (june.pravin@gmail.com)
 * @since v1.0.0
 */

public class FileManager {

    private static final String TAG = "FileManager";

    public List<File> getFiles(Context context, String sourcePath) {
        Log.d(TAG, "getFiles() called with: sourcePath = [" + sourcePath + "]");

        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        Log.d(TAG, "getFiles: directory:" + Arrays.toString(directory.listFiles()));

        File directory1 = Environment.getExternalStorageDirectory();
        //File directory1 = new File("/sdcard/emulated/0");
        Log.d(TAG, "getFiles: directory1:" + Arrays.toString(directory1.listFiles()));
        Log.d(TAG, "getFiles: emulated?:" + Environment.isExternalStorageEmulated());
        Log.d(TAG, "getFiles: removable:" + Environment.isExternalStorageRemovable());

        File file = new File(directory1.getAbsolutePath() + "/myFolder");
        boolean result = file.mkdirs();
        Log.d(TAG, "getFiles: result:" + result);
        Log.d(TAG, "getFiles: exists and isDir:" + file.exists() + " and " + file.isDirectory());

        return Collections.emptyList();
    }

    /**
     * Checks if the external storage is mounted.
     * @return {@code true} if external storage is mounted. {@code false} otherwise
     */
    private boolean isExternalStorageMounted() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }
}
