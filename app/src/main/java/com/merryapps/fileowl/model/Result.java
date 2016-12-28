package com.merryapps.fileowl.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Holds the result of a scan.
 * This is broadcasted by {@link FileScanService} and should be used
 * by the UI to render itself using this data. This class is not thread safe and is intended to be
 * used by one thread only.
 *
 * @author Pravin Sonawane (june.pravin@gmail.com)
 * @since v1.0.0
 */

public class Result implements Parcelable {

    private long totalFilesScanned;
    private long averageFileSize;
    private List<FileStat> largestFiles;
    private List<FileTypeFrequency> frequentFiles;
    private long scanTime;
    private long scanDuration;
    private ScanStatus status;

    Result() {
        totalFilesScanned = 0;
        averageFileSize = 0;
        largestFiles = Collections.emptyList();
        frequentFiles = Collections.emptyList();
        scanTime = Calendar.getInstance(Locale.getDefault()).getTime().getTime();
        scanDuration = 0;
        status = ScanStatus.NO_SCAN_DATA;
    }

    public long getTotalFilesScanned() {
        return totalFilesScanned;
    }

    void setTotalFilesScanned(long totalFilesScanned) {
        this.totalFilesScanned = totalFilesScanned;
    }

    public long getAverageFileSize() {
        return averageFileSize;
    }

    void setAverageFileSize(long averageFileSize) {
        this.averageFileSize = averageFileSize;
    }

    @NonNull
    public List<FileStat> getLargestFiles() {
        return Collections.unmodifiableList(largestFiles);
    }

    void setLargestFiles(@NonNull List<FileStat> largestFiles) {
        this.largestFiles = largestFiles;
    }

    @NonNull
    public List<FileTypeFrequency> getFrequentFiles() {
        return Collections.unmodifiableList(frequentFiles);
    }

    void setFrequentFiles(List<FileTypeFrequency> frequentFiles) {
        this.frequentFiles = frequentFiles;
    }

    public long getScanTime() {
        return scanTime;
    }

    void setScanTime(long scanTime) {
        this.scanTime = scanTime;
    }

    public long getScanDuration() {
        return scanDuration;
    }

    void setScanDuration(long scanDuration) {
        this.scanDuration = scanDuration;
    }

    @NonNull
    public ScanStatus getStatus() {
        return status;
    }

    void setStatus(@NonNull ScanStatus status) {
        this.status = status;
    }

    protected Result(Parcel in) {
        totalFilesScanned = in.readLong();
        averageFileSize = in.readLong();
        largestFiles = in.createTypedArrayList(FileStat.CREATOR);
        frequentFiles = in.createTypedArrayList(FileTypeFrequency.CREATOR);
        scanTime = in.readLong();
        scanDuration = in.readLong();
        status = (ScanStatus)in.readSerializable();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(totalFilesScanned);
        dest.writeLong(averageFileSize);
        dest.writeTypedList(largestFiles);
        dest.writeTypedList(frequentFiles);
        dest.writeLong(scanTime);
        dest.writeLong(scanDuration);
        dest.writeSerializable(status);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Result> CREATOR = new Creator<Result>() {
        @Override
        public Result createFromParcel(Parcel in) {
            return new Result(in);
        }

        @Override
        public Result[] newArray(int size) {
            return new Result[size];
        }
    };

    @Override
    public String toString() {
        return "Result{" +
                "totalFilesScanned=" + totalFilesScanned +
                ", averageFileSize=" + averageFileSize +
                ", largestFiles=" + largestFiles +
                ", frequentFiles=" + frequentFiles +
                ", scanTime=" + scanTime +
                ", scanDuration=" + scanDuration +
                ", status=" + status +
                '}';
    }
}
