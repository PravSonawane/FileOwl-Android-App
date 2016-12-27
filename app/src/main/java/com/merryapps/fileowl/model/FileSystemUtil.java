package com.merryapps.fileowl.model;

import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;

import static com.merryapps.fileowl.model.ScanStatus.SCANNING_FILE_SYSTEM;
import static com.merryapps.fileowl.model.ScanStatus.SCAN_CANCELLED;
import static com.merryapps.fileowl.model.ScanStatus.SCAN_COMPLETE;
import static com.merryapps.fileowl.model.ScanStatus.SCAN_ERROR;

/**
 * Utility to scan the external file system
 * @author Pravin Sonawane (june.pravin@gmail.com)
 * @since v1.0.0
 */

public class FileSystemUtil {

    private static final String TAG = "FileSystemUtil";
    private ScanResult scanResult;
    private ScanStatus scanStatus;

    /**
     * Creates a new {@link FileSystemUtil}.
     */
    FileSystemUtil() {
        scanResult = new ScanResult(10,5);
    }

    synchronized ScanStatus getScanStatus() {
        return scanStatus;
    }

    private synchronized void setScanStatus(ScanStatus scanStatus) {
        this.scanStatus = scanStatus;
    }

    /**
     * Checks if the external storage is mounted.
     * @return {@code true} if external storage is mounted. {@code false} otherwise
     */
    public boolean isExternalStorageMounted() {
        String state = Environment.getExternalStorageState();
        Log.d(TAG, "isExternalStorageMounted: name:" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
        setScanStatus(ScanStatus.EXTERNAL_STORAGE_NOT_MOUNTED);
        return state.equals(Environment.MEDIA_MOUNTED);
    }
    /**
     * Scans the external storage. This method is <b>blocking</b>.
     */
    public void scanFileSystem() {

        Log.d(TAG, "scanFileSystem: isExternalStorageMounted:" + isExternalStorageMounted());
        if (!isExternalStorageMounted()) {
            setScanStatus(SCAN_ERROR);
            return;
        }
        CountDownLatch waitLatch = new CountDownLatch(1);
        try {
            setScanStatus(SCANNING_FILE_SYSTEM);
            traverse(scanResult, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
            traverse(scanResult, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                traverse(scanResult, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS));
            }
            traverse(scanResult, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES));
            traverse(scanResult, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC));
            traverse(scanResult, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_NOTIFICATIONS));
            traverse(scanResult, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
            traverse(scanResult, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS));
            traverse(scanResult, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES));
            setScanStatus(SCAN_COMPLETE);

            waitLatch.countDown();
        } catch (Exception exception) {
            Log.e(TAG, "scanFileSystem: error:", exception);
            setScanStatus(ScanStatus.SCAN_ERROR);
        }

        try {
            waitLatch.await();
        } catch (InterruptedException e) {
            setScanStatus(ScanStatus.SCAN_ERROR);
            Log.e(TAG, "scanFileSystem: Error while waiting", e);
        }

        Log.d(TAG, "scanFileSystem: scan complete");
    }

    void stopScan() {
        if(getScanStatus() == SCANNING_FILE_SYSTEM) {
            Log.d(TAG, "stopScan: stoping file system scan");
            setScanStatus(SCAN_CANCELLED);
        } else {
            Log.d(TAG, "stopScan: no scan running");
        }
    }

    public ScanResult getScanResult() {
        return scanResult;
    }

    @NonNull
    private Function<List<File>, SingleSource<? extends ScanResult>> accumulateIn(ScanResult scanResult) {
        return files -> {
            Log.d(TAG, "accumulateIn: Thread.name:" + Thread.currentThread().getName());
            Log.d(TAG, "accumulateIn: total files:" + scanResult.getTotalFilesScanned());
            Log.d(TAG, "accumulateIn: average file size:" + scanResult.getAverageFileSize());
            for (File file : files) {
                scanResult.add(file);
            }

            return Single.just(scanResult);
        };
    }

    private void traverse(ScanResult scanResult, @NonNull File file) {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                traverse(scanResult, f);
            }
        } else if(file.isFile()){
            scanResult.add(file);
        }
    }

}
