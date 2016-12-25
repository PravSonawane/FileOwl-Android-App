package com.merryapps.diskhero.model;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * //TODO add description here.
 * @author Pravin Sonawane (june.pravin@gmail.com)
 * @since v1.0.0
 */

public class FileSystemUtil {

    private static final String TAG = "FileSystemUtil";

    /**
     * Checks if the external storage is mounted.
     * @return {@code true} if external storage is mounted. {@code false} otherwise
     */
    public boolean isExternalStorageMounted() {
        String state = Environment.getExternalStorageState();
        Log.d(TAG, "isExternalStorageMounted: name:" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * Scans the external storage. This method is <b>blocking</b>.
     * This method is same as calling {@code FileSystemUtil.scanFileSystem(10, 5)}
     */
    public ScanResult scanFileSystem() {
        return this.scanFileSystem(10, 5);
    }

    /**
     * Scans the external storage. This method is <b>blocking</b>.
     * @param largeFilesCount the top x number of large files to be returned.
     * @param highFrequecyFileCount the top x number of high frequency file counts to be returned.
     * @return a {@link ScanResult} containing the data.
     */
    public ScanResult scanFileSystem(int largeFilesCount, int highFrequecyFileCount) {

        Log.d(TAG, "scanFileSystem: isExternalStorageMounted:" + isExternalStorageMounted());

        CountDownLatch waitLatch = new CountDownLatch(1);
        ScanResult scanResult = new ScanResult(largeFilesCount, highFrequecyFileCount);
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        Single<ScanResult> scanResultSingle = createFileEmitter(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getParentFile())
                .toList().flatMap(accumulateIn(scanResult))
                .subscribeOn(Schedulers.from(executor))
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(executor::shutdown);

        scanResultSingle.subscribe(onNext(waitLatch));

        try {
            waitLatch.await();
        } catch (InterruptedException e) {
            Log.e(TAG, "scanFileSystem: Error while waiting", e);
        }

        Log.d(TAG, "onNext: files scanned:" + scanResult.getTotalFilesScanned());
        Log.d(TAG, "onNext: average file size:" + scanResult.getAverageFileSize());
        Log.d(TAG, "onNext: largest files:" + scanResult.getLargestFiles());
        Log.d(TAG, "onNext: most frequent files:" + scanResult.getMostFrequentFileTypes());
        return new ScanResult();
    }

    @NonNull
    private Function<List<File>, SingleSource<? extends ScanResult>> accumulateIn(ScanResult scanResult) {
        return files -> {
            Log.d(TAG, "accumulateIn: Thread.name:" + Thread.currentThread().getName());
            for (File file : files) {
                scanResult.add(file);
            }

            return Single.just(scanResult);
        };
    }

    @NonNull
    private Consumer<ScanResult> onNext(CountDownLatch waitLatch) {
        return scanResult -> {
            Log.d(TAG, "onNext: complete.. decrementing countdownLatch");
            waitLatch.countDown();
        };
    }

    /**
     * Creates a {@link Flowable} that emits files as it traverses the directory
     * @param file the file system to be traversed.
     * @return a {@link Flowable} (does not operater on any scheduler)
     */
    private Flowable<File> createFileEmitter(@NonNull File file) {
        if (file.isDirectory()) {
            return Flowable.fromArray(file.listFiles())
                    .flatMap(this::createFileEmitter);
        }
        return Flowable.just(file);
    }
}
