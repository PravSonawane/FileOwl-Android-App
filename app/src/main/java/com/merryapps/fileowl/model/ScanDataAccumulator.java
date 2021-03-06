package com.merryapps.fileowl.model;

import android.support.annotation.NonNull;

import java.io.File;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Collects the result of a scan operation.
 * This class is thread safe. Updates and gets on this class are synchronized on the object instance.
 * It is expected that this object will be updated by multiple threads scanning the file system.
 *
 * @author Pravin Sonawane (june.pravin@gmail.com)
 * @since v1.0.0
 */
public class ScanDataAccumulator {

    private static final String TAG = "ScanDataAccumulator";

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
     * Creates a new {@link ScanDataAccumulator} to collect the scan data.
     * Calling this constructor is same as calling {@code new ScanDataAccumulator(10,5)}
     */
    ScanDataAccumulator() {
        this(10, 5);
    }

    /**
     * Creates a new {@link ScanDataAccumulator} to collect the scan data.
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
    ScanDataAccumulator(int largeFileCollectionSize, int highestFileFrequencyCollectionSize) {
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

    public synchronized int getLargeFileCollectionSize() {
        return largeFileCollectionSize;
    }

    public synchronized int getHighestFileFrequencyCollectionSize() {
        return highestFileFrequencyCollectionSize;
    }

    public synchronized long getTotalFilesScanned() {
        return totalFilesScanned.get();
    }

    public synchronized long getAverageFileSize() {
        if (totalFilesScanned.get() == 0) {
            return 0;
        }

        /*
         * Dynamically calculating here because the average need not be calculated
         * for every call to ScanDataAccumulator.add(file).
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
        return "ScanDataAccumulator{" +
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
}
