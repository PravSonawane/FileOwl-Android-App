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

import com.merryapps.fileowl.R;

import static com.merryapps.fileowl.model.ScanStatus.SCAN_CANCELLED;
import static com.merryapps.fileowl.model.ScanStatus.SCAN_COMPLETE;
import static com.merryapps.fileowl.model.ScanStatus.SCAN_ERROR;

/**
 * A service that scans the external storage.
 *
 * @author Pravin Sonawane (june.pravin@gmail.com)
 * @since v1.0.0
 */
public class FileScanService extends IntentService {

    private static final String TAG = "FileScanService";

    /** The action name which a broadcast receiver should use to identify a scan status sent by this service */
    public static final String SCAN_STATUS_ACTION = "com.merryapps.fileowl.SCAN_STATUS";

    /** The extra key name which a broadcast receiver should use to
     * extract scan status information about the scan */
    public static final String EXTRA_SCAN_STATUS = "EXTRA_SCAN_STATUS";

    /** The extra key name which a broadcast receiver should use to
     * extract scan result information about the scan */
    public static final String EXTRA_SCAN_RESULT = "EXTRA_SCAN_RESULT";

    /** The extra key name which a broadcast receiver should use to
     * extract total files scanned information about the scan */
    public static final String EXTRA_TOTAL_FILES_SCANNED = "EXTRA_TOTAL_FILES_SCANNED";

    private static final int SCAN_STATUS_BROADCAST_INTERVAL = 500; //millis

    private FileSystemUtil fileSystemUtil;
    private int scannerThreadCount = 1;
    private HandlerThread scannerThread;

    public FileScanService() {
        super("FileScanService");
        fileSystemUtil = new FileSystemUtil();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent() called with: intent = [" + intent + "]");

        scannerThread = new HandlerThread("ScannerThread-" + scannerThreadCount++);
        scannerThread.start();
        Handler scannerHandler = new Handler(scannerThread.getLooper());
        scannerHandler.post(() -> fileSystemUtil.scanFileSystem());

        NotificationManager managerCompat = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        managerCompat.notify(1, createNotification());

        while (fileSystemUtil.getScanStatus() != SCAN_COMPLETE
                && fileSystemUtil.getScanStatus() != SCAN_CANCELLED
                && fileSystemUtil.getScanStatus() != SCAN_ERROR) {
            Log.d(TAG, "onHandleIntent: fileSystemUtil.getScanStatus():" + fileSystemUtil.getScanStatus());
            send(fileSystemUtil.getScanResult());

            try {
                Thread.sleep(SCAN_STATUS_BROADCAST_INTERVAL);
            } catch (InterruptedException e) {
                Log.e(TAG, "onHandleIntent: error while waiting during a scan", e);
                fileSystemUtil.stopScan();
                break;
            }
        }

        //send file scan status
        send(fileSystemUtil.getScanResult());
        managerCompat.cancel(1);


    }

    private void send(ScanResult scanResult) {
        Intent scanStatusIntent = new Intent(SCAN_STATUS_ACTION);
        scanStatusIntent.putExtra(EXTRA_SCAN_STATUS, fileSystemUtil.getScanStatus());
        scanStatusIntent.putExtra(EXTRA_TOTAL_FILES_SCANNED, scanResult.getTotalFilesScanned());
        scanStatusIntent.putExtra(EXTRA_SCAN_RESULT, scanResult);
        sendBroadcast(scanStatusIntent);
    }

    private Notification createNotification() {

        return new NotificationCompat.Builder(this)
                .setContentTitle("Scanning external storage")
                .setContentText("In progress")
                .setSmallIcon(R.drawable.ic_stat_owl)
                .setProgress(100, 0, false)
                .setOngoing(true)
                .setAutoCancel(true)
                .build();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy() called");
        fileSystemUtil.stopScan();
        if (scannerThread != null) {
            scannerThread.quit();
            scannerThread.interrupt();
            stopSelf();
        }
        super.onDestroy();
    }
}
