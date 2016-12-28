package com.merryapps.fileowl.model;

import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static com.merryapps.fileowl.model.ScanStatus.EXTERNAL_STORAGE_NOT_MOUNTED;
import static com.merryapps.fileowl.model.ScanStatus.NOT_SCANNING;
import static com.merryapps.fileowl.model.ScanStatus.SCANNING_FILE_SYSTEM;
import static com.merryapps.fileowl.model.ScanStatus.SCAN_CANCELLED;
import static com.merryapps.fileowl.model.ScanStatus.SCAN_COMPLETE;
import static com.merryapps.fileowl.model.ScanStatus.SCAN_ERROR;

/**
 * //TODO add description here
 *
 * @author Pravin Sonawane (june.pravin@gmail.com)
 * @since v1.0.0
 */

public class FileSystemScanner {

    private static final String TAG = "FileSystemScanner";

    private ScanStatus scanStatus;
    private Date scanStartTime;
    private final ScanDataAccumulator scanDataAccumulator;

    FileSystemScanner() {
        this.scanDataAccumulator = new ScanDataAccumulator(10,5);
        this.scanStatus = NOT_SCANNING;
    }

    /** Get the current status of the scan synchronously */
    synchronized ScanStatus getScanStatus() {
        return scanStatus;
    }

    /** Set the current status of the scan synchronously */
    synchronized void setScanStatus(ScanStatus scanStatus) {
        this.scanStatus = scanStatus;
    }

    synchronized Date getScanStartTime() {
        return scanStartTime;
    }

    synchronized void setScanStartTime(Date scanStartTime) {
        this.scanStartTime = scanStartTime;
    }

    /**
     * Checks if the external storage is mounted.
     * @return {@code true} if external storage is mounted. {@code false} otherwise
     */
    public boolean isExternalStorageMounted() {
        String state = Environment.getExternalStorageState();
        Log.d(TAG, "isExternalStorageMounted: name:" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
        scanStatus = EXTERNAL_STORAGE_NOT_MOUNTED;
        return state.equals(Environment.MEDIA_MOUNTED);
    }
    /**
     * Scans the external storage. This method is <b>blocking</b> and should be executed on a worker thread.
     * The result of the scan progress can be obtained by calling {@link FileSystemScanner#fetchResult()}
     */
    void scanFileSystem() {

        Log.d(TAG, "scanFileSystem: isExternalStorageMounted:" + isExternalStorageMounted());
        if (!isExternalStorageMounted()) {
            setScanStatus(SCAN_ERROR);
        }

        CountDownLatch waitLatch = new CountDownLatch(1);
        try {
            setScanStatus(SCANNING_FILE_SYSTEM);
            setScanStartTime(new Date());
            traverse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM));
            traverse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                traverse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS));
            }
            traverse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES));
            traverse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC));
            traverse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_NOTIFICATIONS));
            traverse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
            traverse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS));
            traverse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES));

            setScanStatus(SCAN_COMPLETE);

        } catch (Exception exception) {
            Log.e(TAG, "scanFileSystem: error:", exception);
            if (getScanStatus() != SCAN_CANCELLED) {
                setScanStatus(ScanStatus.SCAN_ERROR);
            }

        } finally {
            waitLatch.countDown();
        }

        try {
            waitLatch.await();
        } catch (InterruptedException e) {
            setScanStatus(ScanStatus.SCAN_ERROR);
            Log.e(TAG, "scanFileSystem: Error while waiting", e);
        }

        //create and store the result of the scan.
        Log.d(TAG, "scanFileSystem: data collected:" + scanDataAccumulator);
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

    void signalWaitingError() {
        if(getScanStatus() == SCANNING_FILE_SYSTEM) {
            Log.d(TAG, "stopScan: stoping file system scan");
            setScanStatus(SCAN_ERROR);
        } else {
            Log.d(TAG, "stopScan: no scan running");
        }
    }

    /**
     * Returns a new {@link Result} every time containing the latest scan information so far.
     */
    Result fetchResult() {
        Result result = new Result();
        result.setTotalFilesScanned(getScanDataAccumulator().getTotalFilesScanned());
        result.setAverageFileSize(getScanDataAccumulator().getAverageFileSize());
        List<FileStat> fileStats = new ArrayList<>(getScanDataAccumulator().getFileStats().size());
        for (FileStat f : getScanDataAccumulator().getFileStats()) {
            fileStats.add(new FileStat(f.getAbsolutePath(), f.getSize()));
        }

        List<FileTypeFrequency> ftfList = new ArrayList<>(getScanDataAccumulator().getMostFrequentFileTypes().size());
        for (FileTypeFrequency f : getScanDataAccumulator().getMostFrequentFileTypes()) {
            ftfList.add(new FileTypeFrequency(f.getFileType(), f.getFrequency()));
        }
        result.setScanTime(getScanStartTime().getTime());
        result.setStatus(scanStatus);
        result.setLargestFiles(fileStats);
        result.setFrequentFiles(ftfList);

        return result;
    }


    private void traverse(@NonNull File file) {
        //stop the scan if user cancels it.
        if (getScanStatus() == SCAN_CANCELLED
                || getScanStatus() == SCAN_ERROR) {
            throw new RuntimeException("Scan cancelled");
        }
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                traverse(f);
            }
        } else if(file.isFile()){
            synchronized (scanDataAccumulator) {
                scanDataAccumulator.add(file);
            }

        }
    }

    synchronized ScanDataAccumulator getScanDataAccumulator() {
        return scanDataAccumulator;
    }
}
