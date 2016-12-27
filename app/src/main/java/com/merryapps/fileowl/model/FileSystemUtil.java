package com.merryapps.fileowl.model;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.merryapps.fileowl.model.ScanStatus.SCANNING_FILE_SYSTEM;
import static com.merryapps.fileowl.model.ScanStatus.SCAN_CANCELLED;
import static com.merryapps.fileowl.model.ScanStatus.SCAN_COMPLETE;

/**
 * //TODO add description here.
 * @author Pravin Sonawane (june.pravin@gmail.com)
 * @since v1.0.0
 */

public class FileSystemUtil {

    private static final String TAG = "FileSystemUtil";
    private ScanResult scanResult;
    private ScanStatus scanStatus;
    private Disposable subscribe;

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

//    /**
//     * Scans the external storage. This method is <b>blocking</b>.
//     * This method is same as calling {@code FileSystemUtil.scanFileSystem(10, 5)}
//     */
//    public void scanFileSystem() {
//        this.scanFileSystem(10, 5);
//    }

    /**
     * Scans the external storage. This method is <b>blocking</b>.
     */
    public void scanFileSystem() {

        Log.d(TAG, "scanFileSystem: isExternalStorageMounted:" + isExternalStorageMounted());

        //TODO handle Environment.getExternalStorageDirectory() not working on all devices
        CountDownLatch waitLatch = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(1);

//        try {
//            setScanStatus(SCANNING_FILE_SYSTEM);
//            scanResult.setScanStartTime(new Date());
//            subscribe = createFileEmitter(Environment.getExternalStorageDirectory(), executor)
//                    .toList()
//                    .delay(5, TimeUnit.SECONDS)
//                    .flatMap(accumulateIn(scanResult))
//                    .flatMap(saveToDb())
//                    .subscribeOn(Schedulers.from(executor))
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .doFinally(executor::shutdown).subscribe(onNext(waitLatch));
//        } catch (Exception exception) {
//            Log.e(TAG, "scanFileSystem: error:", exception);
//            setScanStatus(ScanStatus.SCAN_ERROR);
//        }

        try {
            setScanStatus(SCANNING_FILE_SYSTEM);
            traverse(scanResult, Environment.getExternalStorageDirectory());
            setScanStatus(SCAN_COMPLETE);
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
            if (!subscribe.isDisposed()) {
                subscribe.dispose();
                setScanStatus(SCAN_CANCELLED);
            }
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

    @NonNull
    private Function<ScanResult, SingleSource<ScanResult>> saveToDb() {
        return scanResult -> {
            //TODO implement this
            return Single.just(scanResult);
        };
    }

    @NonNull
    private Consumer<ScanResult> onNext(CountDownLatch waitLatch) {
        return scanResult -> {
            Log.d(TAG, "onNext: complete.. decrementing countdownLatch");
            waitLatch.countDown();
            setScanStatus(SCAN_COMPLETE);
        };
    }

    /**
     * Creates a {@link Flowable} that emits files as it traverses the directory
     * @param file the file system to be traversed.
     * @param executor the executor the get threads from for parallelism (do not shut down)
     * @return a {@link Flowable} (does not operator on any scheduler)
     */
    private Flowable<File> createFileEmitter(@NonNull File file, Executor executor) {
        if (file.isDirectory()) {
            return Flowable.fromArray(file.listFiles())
                    .subscribeOn(Schedulers.from(executor))
                    .flatMap(f -> createFileEmitter(f, executor));
        }
        return Flowable.just(file);
    }

    private void traverse(ScanResult scanResult, @NonNull File file) {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                traverse(scanResult, f);
            }
        } else {
            scanResult.add(file);
        }
    }

}
