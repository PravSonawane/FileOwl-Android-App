package com.merryapps.diskhero.model;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * //TODO add description here.
 * @author Pravin Sonawane (june.pravin@gmail.com)
 * @since v1.0.0
 */

public class FileManager {

    private static final String TAG = "FileManager";

    private static final int LARGE_FILE_COLLECTION_SIZE = 10;
    private static final int HIGHEST_FILE_FREQUENCY_COLLECTION_SIZE = 5;

    public List<File> getFiles(Context context, String sourcePath) {

        ScanResult scanResult = new ScanResult();
        createFileEmitter(Environment.getExternalStorageDirectory())
                .toList()
                .flatMap(accumulateIn(scanResult))
                .subscribeOn(Schedulers.from(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())))
                .subscribe(onNext());

        return Collections.emptyList();
    }

    @NonNull
    private Function<List<File>, SingleSource<? extends ScanResult>> accumulateIn(ScanResult scanResult) {
        return files -> {
            for (File file : files) {
                scanResult.add(file);
            }

            return Single.just(scanResult);
        };
    }

    @NonNull
    private Consumer<ScanResult> onNext() {
        return scanResult -> {
            Log.d(TAG, "onNext: files scanned:" + scanResult.getTotalFilesScanned());
            Log.d(TAG, "onNext: average file size:" + scanResult.getAverageFileSize());
            Log.d(TAG, "onNext: largest files:" + scanResult.getLargestFiles());
            Log.d(TAG, "onNext: most frequent files:" + scanResult.getMostFrequentFileTypes());
        };
    }

    /**
     * Checks if the external storage is mounted.
     * @return {@code true} if external storage is mounted. {@code false} otherwise
     */
    public boolean isExternalStorageMounted() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * Creates a {@link Flowable} that emits files as it traverses the directory
     * @param file the file system to be traversed.
     * @return a {@link Flowable} (does not operater on any scheduler)
     */
    private Flowable<File> createFileEmitter(@NonNull File file) {
        if (file.isDirectory()) {
            return Flowable.fromIterable(Arrays.asList(file.listFiles()))
                    .subscribeOn(Schedulers.from(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())))
                    .flatMap(this::createFileEmitter);
        }
        return Flowable.just(file);
    }
}
