package com.merryapps.diskhero.model;

import android.support.annotation.NonNull;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Collects the result of a scan operation.
 * This class is not thread safe.
 *
 * @author Pravin Sonawane (june.pravin@gmail.com)
 * @since v1.0.0
 */
public class ScanResult {

    private long totalFilesScanned;
    private long totalFileSize;
    private List<FileStat> largestFiles;
    private List<FileTypeFrequency> mostFrequentFtfs;

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
        //since the size of these lists are typically small, overhead of traversal is not at play here.
        largestFiles = new LinkedList<>();
        mostFrequentFtfs = new LinkedList<>();
    }

    int getLargeFileCollectionSize() {
        return largeFileCollectionSize;
    }

    int getHighestFileFrequencyCollectionSize() {
        return highestFileFrequencyCollectionSize;
    }

    long getTotalFilesScanned() {
        return totalFilesScanned;
    }

    long getAverageFileSize() {
        if (totalFilesScanned == 0) {
            return 0;
        }

        /*
         * Dynamically calculating here because the average need not be calculated
         * for every call to ScanResult.add(file).
         */
        return totalFileSize / totalFilesScanned;
    }

    List<FileTypeFrequency> getMostFrequentFileTypes() {
        return mostFrequentFtfs;
    }

    List<FileStat> getLargestFiles() {
        return largestFiles;
    }

    private FileStat createFileStat(File file) {
        return new FileStat(file.getAbsolutePath(), file.length());
    }

    void add(@NonNull File file) {
        if (!file.isFile()) {
            throw new AssertionError("Wrong File type." +
                    "This class should not process anything but files");
        }
        this.add(createFileStat(file));
    }

    void add(@NonNull FileStat fileStat) {

        recomputeLargestFiles(fileStat);
        recomputeAverageFileSize(fileStat);
        recomputeMostFrequentFiles(fileStat);
    }

    private void recomputeLargestFiles(@NonNull FileStat fileStat) {
        if(largestFiles.size() < largeFileCollectionSize) {
            largestFiles.add(fileStat);
            if (smallestLargeFile == null) {
                smallestLargeFile = fileStat;
            }
            computeSmallestLargeFile();
            return;
        }

        if (fileStat.getSize() >= smallestLargeFile.getSize()) {
            largestFiles.remove(smallestLargeFile);
            largestFiles.add(fileStat);
            computeSmallestLargeFile();
        }
    }

    private void computeSmallestLargeFile() {
        smallestLargeFile = largestFiles.get(0); //reset
        for (FileStat f : largestFiles) {
            if (f.getSize() <= smallestLargeFile.getSize()) {
                smallestLargeFile = f;
            }
        }
    }

    private void recomputeAverageFileSize(FileStat fileStat) {
        totalFilesScanned++;
        totalFileSize += fileStat.getSize();
    }

    private void recomputeMostFrequentFiles(FileStat fileStat) {
        String fileType = fileStat.getType();

        FileTypeFrequency fileTypeFrequency = new FileTypeFrequency(fileType, 1);
        if(mostFrequentFtfs.size() < highestFileFrequencyCollectionSize) {
            if (leastFrequentFtf == null) {
                leastFrequentFtf = fileTypeFrequency;
            }
            if(!incrementIfExists(fileTypeFrequency)) {
                mostFrequentFtfs.add(fileTypeFrequency);
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
    private boolean incrementIfExists(@NonNull FileTypeFrequency fileTypeFrequency) {
        FileTypeFrequency existing = null;
        for (FileTypeFrequency f : mostFrequentFtfs) {
            if (f.getFileType().equals(fileTypeFrequency.getFileType())) {
                existing = f;
                break;
            }
        }

        //if found existing
        if(existing != null) {
            mostFrequentFtfs.get(mostFrequentFtfs.indexOf(existing))
                    .incrementFrequency();
            return true;
        }

        return false;
    }

    private void computeLeastFrequentFtf() {
        for (FileTypeFrequency f : mostFrequentFtfs) {
            if (leastFrequentFtf.getFrequency() < f.getFrequency()) {
                leastFrequentFtf = f;
            }
        }
    }


}
