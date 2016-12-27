package com.merryapps.fileowl.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Collects the result of a scan operation.
 * This class is not thread safe.
 *
 * @author Pravin Sonawane (june.pravin@gmail.com)
 * @since v1.0.0
 */
public class ScanResult implements Parcelable {

    private static final String TAG = "ScanResult";

    private static final RoundingMode ROUNDING_MODE_HALF_UP = RoundingMode.HALF_UP;
    private static final int ONE_KB = 1024;
    private static final int ONE_MB = 1024 * ONE_KB;
    private static final long ONE_GB = (long)1024 * ONE_MB;
    private static final long ONE_TB = (long)1024 * ONE_GB;
    private static final int SCALE = 2;

    private AtomicLong totalFilesScanned;
    private AtomicLong totalFileSize;
    private List<FileStat> fileStats;
    private List<FileTypeFrequency> frequentFiles;

    private FileStat smallestLargeFile;
    private FileTypeFrequency leastFrequentFtf;

    private int largeFileCollectionSize;
    private int highestFileFrequencyCollectionSize;


    /**
     * Creates a new {@link ScanResult} to collect the scan data.
     * Calling this constructor is same as calling {@code new ScanResult(10,5)}
     */
    ScanResult() {
        this(10, 5);
    }

    /**
     * Creates a new {@link ScanResult} to collect the scan data.
     * The size of the collections of largest files and higest file frequencies can be controlled
     * by the parameters to this constructor.
     *
     * <p>
     *     The allowed values of these parameters are as follows:
     *     <ol>
     *         <li>largeFileCollectionSize = 10; // 1 to 10</li>
     *         <li>highestFileFrequencyCollectionSize = 5; // 1 to 5</li>
     *     </ol>
     * </p>
     * @param largeFileCollectionSize the max number of files to be termed as large files.
     * @param highestFileFrequencyCollectionSize the max number of file types to be termed as high frequency
     * @throws IllegalArgumentException if arguments are out of allowed value range
     */
    ScanResult(int largeFileCollectionSize, int highestFileFrequencyCollectionSize) {
        if (largeFileCollectionSize < 1 || largeFileCollectionSize > 10) {
            throw new IllegalArgumentException("largeFileCollectionSize out of allowed range (1,10):" + largeFileCollectionSize);
        }

        if (highestFileFrequencyCollectionSize < 1 || highestFileFrequencyCollectionSize > 5) {
            throw new IllegalArgumentException("largeFileCollectionSize out of allowed range (1,5):" + highestFileFrequencyCollectionSize);
        }
        this.largeFileCollectionSize = largeFileCollectionSize;
        this.highestFileFrequencyCollectionSize = highestFileFrequencyCollectionSize;

        //using linked lists here because deletion can be frequent
        //since the size of these lists are typically small, overhead of search is not at play here.
        fileStats = new LinkedList<>();
        frequentFiles = new LinkedList<>();

        this.totalFilesScanned = new AtomicLong();
        this.totalFileSize = new AtomicLong();
    }

    /**
     * Creates a {@link ScanResult} with the given data. This constructor is only for some of
     * this package's classes. Other usages might be indeterminate.
     * @param totalFilesScanned the total number of files scanned
     * @param totalFileSize the total of sizes of all files scanned
     * @param fileStats list of largest files
     * @param frequentFiles list of most frequent files
     */
    ScanResult(long totalFilesScanned, long totalFileSize,
               @NonNull List<FileStat> fileStats, @NonNull List<FileTypeFrequency> frequentFiles) {
        this.totalFilesScanned = new AtomicLong(totalFilesScanned);
        this.totalFileSize = new AtomicLong(totalFileSize);
        this.fileStats = fileStats;
        this.frequentFiles = frequentFiles;

        this.smallestLargeFile = fileStats.isEmpty()
                ? null : getFileStats().get(getFileStats().size()-1); //sorts in descending
        this.leastFrequentFtf = frequentFiles.isEmpty()
                ? null : getMostFrequentFileTypes().get(getMostFrequentFileTypes().size()-1); //sorts in descending
    }

    public synchronized int getLargeFileCollectionSize() {
        return largeFileCollectionSize;
    }

    public synchronized int getHighestFileFrequencyCollectionSize() {
        return highestFileFrequencyCollectionSize;
    }

    public synchronized long getTotalFilesScanned() {
        return totalFilesScanned.get();
    }

    public synchronized String getAverageFileSizeHumanReadable() {
        long size = getAverageFileSize();
        if (size < ONE_KB) {
            return size + " B";
        } else if (size < ONE_MB) {
            return new BigDecimal((double)size/ONE_KB).setScale(SCALE,ROUNDING_MODE_HALF_UP) + " KB";
        } else if (size < ONE_GB) {
            return new BigDecimal((double)size/ONE_MB).setScale(SCALE,ROUNDING_MODE_HALF_UP) + " MB";
        } else if (size < ONE_TB){
            return new BigDecimal((double)size/ONE_GB).setScale(SCALE,ROUNDING_MODE_HALF_UP) + " GB";
        } else {
            return new BigDecimal((double)size/ONE_TB) + " TB";
        }
    }

    public synchronized long getAverageFileSize() {
        if (totalFilesScanned.get() == 0) {
            return 0;
        }

        /*
         * Dynamically calculating here because the average need not be calculated
         * for every call to ScanResult.add(file).
         */
        return totalFileSize.get() / totalFilesScanned.get();
    }

    public synchronized List<FileTypeFrequency> getMostFrequentFileTypes() {
        Collections.sort(frequentFiles, (t1, t2) ->
                Long.valueOf(t2.getFrequency()).compareTo(t1.getFrequency()));
        return frequentFiles;
    }

    public synchronized List<FileStat> getFileStats() {
        Collections.sort(fileStats, (t1, t2) ->
                Long.valueOf(t2.getSize()).compareTo(t1.getSize()));
        return fileStats;
    }

    synchronized private FileStat createFileStat(File file) {
        return new FileStat(file.getAbsolutePath(), file.length());
    }

    synchronized void add(@NonNull File file) {
        if (!file.isFile()) {
            throw new AssertionError("Wrong File type." +
                    "This class should not process anything but files");
        }
        this.add(createFileStat(file));
    }

    synchronized void add(@NonNull FileStat fileStat) {

        recomputeLargestFiles(fileStat);
        recomputeAverageFileSize(fileStat);
        recomputeMostFrequentFiles(fileStat);
    }

    synchronized private void recomputeLargestFiles(@NonNull FileStat fileStat) {

        if(fileStats.size() < largeFileCollectionSize) {
            fileStats.add(fileStat);
            if (smallestLargeFile == null) {
                smallestLargeFile = fileStat;
            }
            computeSmallestLargeFile();
            return;
        }

        if (fileStat.getSize() >= smallestLargeFile.getSize()) {
            fileStats.remove(smallestLargeFile);
            fileStats.add(fileStat);
            computeSmallestLargeFile();
        }
    }

    synchronized private void computeSmallestLargeFile() {
        smallestLargeFile = fileStats.get(0); //reset
        for (FileStat f : fileStats) {
            if (f.getSize() <= smallestLargeFile.getSize()) {
                smallestLargeFile = f;
            }
        }
    }

    synchronized private void recomputeAverageFileSize(FileStat fileStat) {

        totalFilesScanned.incrementAndGet();
        totalFileSize.set(totalFileSize.get() + fileStat.getSize());
    }

    synchronized private void recomputeMostFrequentFiles(FileStat fileStat) {
        String fileType = fileStat.getType();

        FileTypeFrequency fileTypeFrequency = new FileTypeFrequency(fileType, 1);
        if(frequentFiles.size() < highestFileFrequencyCollectionSize) {
            if (leastFrequentFtf == null) {
                leastFrequentFtf = fileTypeFrequency;
            }
            if(!incrementIfExists(fileTypeFrequency)) {
                frequentFiles.add(fileTypeFrequency);
            }
        } else {
            incrementIfExists(fileTypeFrequency);
        }

        computeLeastFrequentFtf();
    }

    /**
     * Checks if the given frequency exists in the collection of highest frequencies and increments
     * that frequency if found.
     * @param fileTypeFrequency the type to be checked for
     * @return {@code true} if found, {@code false} otherwise.
     */
    synchronized private boolean incrementIfExists(@NonNull FileTypeFrequency fileTypeFrequency) {
        FileTypeFrequency existing = null;
        for (FileTypeFrequency f : frequentFiles) {
            if (f.getFileType().equals(fileTypeFrequency.getFileType())) {
                existing = f;
                break;
            }
        }

        //if found existing
        if(existing != null) {
            frequentFiles.get(frequentFiles.indexOf(existing))
                    .incrementFrequency();
            return true;
        }

        return false;
    }

    synchronized private void computeLeastFrequentFtf() {
        for (FileTypeFrequency f : frequentFiles) {
            if (leastFrequentFtf.getFrequency() < f.getFrequency()) {
                leastFrequentFtf = f;
            }
        }
    }

    @Override
    synchronized public String toString() {
        return "ScanResult{" +
                "totalFilesScanned=" + totalFilesScanned +
                ", totalFileSize=" + totalFileSize +
                ", fileStats=" + fileStats +
                ", frequentFiles=" + frequentFiles +
                ", smallestLargeFile=" + smallestLargeFile +
                ", leastFrequentFtf=" + leastFrequentFtf +
                ", largeFileCollectionSize=" + largeFileCollectionSize +
                ", highestFileFrequencyCollectionSize=" + highestFileFrequencyCollectionSize +
                '}';
    }

    protected ScanResult(Parcel in) {
        fileStats = in.createTypedArrayList(FileStat.CREATOR);
        frequentFiles = in.createTypedArrayList(FileTypeFrequency.CREATOR);
        smallestLargeFile = in.readParcelable(FileStat.class.getClassLoader());
        leastFrequentFtf = in.readParcelable(FileTypeFrequency.class.getClassLoader());
        largeFileCollectionSize = in.readInt();
        highestFileFrequencyCollectionSize = in.readInt();
        totalFilesScanned = new AtomicLong(in.readLong());
        totalFileSize = new AtomicLong(in.readLong());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(fileStats);
        dest.writeTypedList(frequentFiles);
        dest.writeParcelable(smallestLargeFile, flags);
        dest.writeParcelable(leastFrequentFtf, flags);
        dest.writeInt(largeFileCollectionSize);
        dest.writeInt(highestFileFrequencyCollectionSize);
        dest.writeLong(totalFilesScanned.get());
        dest.writeLong(totalFileSize.get());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ScanResult> CREATOR = new Creator<ScanResult>() {
        @Override
        public ScanResult createFromParcel(Parcel in) {
            return new ScanResult(in);
        }

        @Override
        public ScanResult[] newArray(int size) {
            return new ScanResult[size];
        }
    };
}
