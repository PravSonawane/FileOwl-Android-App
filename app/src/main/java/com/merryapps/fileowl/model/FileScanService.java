package com.merryapps.fileowl.model;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.merryapps.FileOwlApp;
import com.merryapps.fileowl.R;

import static com.merryapps.fileowl.model.ScanStatus.SCAN_CANCELLED;
import static com.merryapps.fileowl.model.ScanStatus.SCAN_COMPLETE;
import static com.merryapps.fileowl.model.ScanStatus.SCAN_ERROR;

/**
 * //TODO add description here
 *
 * @author Pravin Sonawane (june.pravin@gmail.com)
 * @since v1.0.0
 */

public class FileScanService extends IntentService {

    private static final String TAG = "FileScanService";

    /** The action name which a broadcast receiver should use to identify a scan status sent by this service */
    public static final String SCAN_STATUS_ACTION = "com.merryapps.fileowl.SCAN_STATUS";


    /** The extra key name which a broadcast receiver should use to
     * extract scan result information about the scan */
    public static final String EXTRA_SCAN_RESULT = "EXTRA_SCAN_RESULT";

    private static final int SCAN_STATUS_BROADCAST_INTERVAL = 1000; //millis


    private BackupManager backupManager;
    private FileSystemScanner fileSystemScanner;
    private HandlerThread scannerThread;
    private int scannerThreadCount = 1;
    private NotificationManager managerCompat;

    public FileScanService() {
        super("FileScanService");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        backupManager = ((FileOwlManagerFactory)
                ((FileOwlApp)this.getApplication())
                        .getManagerFactory()).backupManager();
        fileSystemScanner = new FileSystemScanner();
        managerCompat = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        super.onStart(intent, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null && intent.getExtras() != null
                && intent.getExtras().getString("USER_REQUEST") != null) {
            String userRequest = intent.getExtras().getString("USER_REQUEST");
            assert userRequest != null;
            switch (userRequest) {
                case "START_SCAN":
                    if (fileSystemScanner.getScanStatus() != ScanStatus.SCANNING_FILE_SYSTEM) {
                        //starting scanner thread
                        startScannerThread();
                    }
                    break;
            }
        }
    }

    private void startScannerThread() {
        //notify
        managerCompat.notify(1, createNotification());

        scannerThread = new HandlerThread("ScannerThread-" + scannerThreadCount++);
        scannerThread.start();
        Handler scannerHandler = new Handler(scannerThread.getLooper());
        scannerHandler.post(() -> fileSystemScanner.scanFileSystem());

        //poll the scan and broadcast updates
        poll();

        send(fileSystemScanner.fetchResult());
    }

    private void poll() {
        while (fileSystemScanner.getScanStatus() != SCAN_COMPLETE
                && fileSystemScanner.getScanStatus() != SCAN_CANCELLED
                && fileSystemScanner.getScanStatus() != SCAN_ERROR) {
            Log.d(TAG, "poll: scanStatus:" + fileSystemScanner.getScanStatus());

            try {
                Thread.sleep(SCAN_STATUS_BROADCAST_INTERVAL);
            } catch (InterruptedException e) {
                Log.e(TAG, "onHandleIntent: error while waiting during a scan", e);
                fileSystemScanner.signalWaitingError();
                break;
            }

            Log.d(TAG, "onHandleIntent: scanStatus:" + fileSystemScanner.getScanStatus());
            //save to db
            backupManager.save(fileSystemScanner.fetchResult());
            send(fileSystemScanner.fetchResult());
        }
    }

    @Override
    public void onDestroy() {
        if (managerCompat != null) {
            managerCompat.cancel(1);
        }
        if (fileSystemScanner != null) {
            fileSystemScanner.stopScan();
        }
        if (scannerThread != null) {
            scannerThread.quit();
            scannerThread.interrupt();
        }
        super.onDestroy();
    }

    private void send(Result result) {
        Intent scanStatusIntent = new Intent(SCAN_STATUS_ACTION);
        scanStatusIntent.putExtra(EXTRA_SCAN_RESULT, result);
        sendBroadcast(scanStatusIntent);
    }

    private Notification createNotification() {

        return new NotificationCompat.Builder(this)
                .setContentTitle("Scanning external storage")
                .setContentText("In progress")
                .setSmallIcon(R.drawable.ic_stat_owl)
                .setProgress(0, 0, false)
                .setOngoing(true)
                .setAutoCancel(true)
                .build();
    }
}
