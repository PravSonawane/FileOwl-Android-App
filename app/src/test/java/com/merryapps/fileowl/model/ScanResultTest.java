package com.merryapps.fileowl.model;

import org.junit.Test;

import java.util.Collections;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Unit Tests for {@link ScanResult}
 *
 * @author Pravin Sonawane (june.pravin@gmail.com)
 * @since v1.0.0
 */
public class ScanResultTest {

    @Test
    public void testNoArgConstructor() {
        ScanResult scanResult = new ScanResult();
        assertEquals(10, scanResult.getLargeFileCollectionSize());
        assertEquals(5, scanResult.getHighestFileFrequencyCollectionSize());
        assertEquals(0, scanResult.getTotalFilesScanned());
        assertEquals(0, scanResult.getAverageFileSize());
        assertEquals(Collections.emptyList(), scanResult.getFileStats());
        assertEquals(Collections.emptyList(), scanResult.getMostFrequentFileTypes());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorInvalidArgs1() {
        new ScanResult(0,1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorInvalidArgs2() {
        new ScanResult(1,0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorInvalidArgs3() {
        new ScanResult(11,1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorInvalidArgs4() {
        new ScanResult(5,6);
    }

    @Test
    public void testConstructorValidArgs() {
        ScanResult scanResult = new ScanResult(8,3);
        assertEquals(8, scanResult.getLargeFileCollectionSize());
        assertEquals(3, scanResult.getHighestFileFrequencyCollectionSize());
        assertEquals(0, scanResult.getTotalFilesScanned());
        assertEquals(0, scanResult.getAverageFileSize());
        assertEquals(Collections.emptyList(), scanResult.getFileStats());
        assertEquals(Collections.emptyList(), scanResult.getMostFrequentFileTypes());
    }

    @Test
    public void testAddWhenFirstFileAdded() {
        ScanResult scanResult = new ScanResult(8,3);
        assertEquals(8, scanResult.getLargeFileCollectionSize());
        assertEquals(3, scanResult.getHighestFileFrequencyCollectionSize());
        assertEquals(0, scanResult.getTotalFilesScanned());
        assertEquals(0, scanResult.getAverageFileSize());
        assertEquals(Collections.emptyList(), scanResult.getFileStats());
        assertEquals(Collections.emptyList(), scanResult.getMostFrequentFileTypes());

        //add one fileStat
        scanResult.add(new FileStat("/sdcard/emulated/0/hello.txt", 100));

        assertEquals(1, scanResult.getFileStats().size());
        assertEquals(1, scanResult.getMostFrequentFileTypes().size());

        assertEquals(new FileStat("/sdcard/emulated/0/hello.txt",100),
                scanResult.getFileStats().get(0));
        assertEquals(new FileTypeFrequency("txt", 1),
                scanResult.getMostFrequentFileTypes().get(0));
        assertEquals(1, scanResult.getTotalFilesScanned());
        assertEquals(100, scanResult.getAverageFileSize());
    }

    @Test
    public void testAddWhenMultipleFilesAdded() {
        ScanResult scanResult = new ScanResult(2,2);
        assertEquals(2, scanResult.getLargeFileCollectionSize());
        assertEquals(2, scanResult.getHighestFileFrequencyCollectionSize());
        assertEquals(0, scanResult.getTotalFilesScanned());
        assertEquals(0, scanResult.getAverageFileSize());
        assertEquals(Collections.emptyList(), scanResult.getFileStats());
        assertEquals(Collections.emptyList(), scanResult.getMostFrequentFileTypes());

        //add multiple
        scanResult.add(new FileStat("/sdcard/emulated/0/hello.txt", 10));
        scanResult.add(new FileStat("/sdcard/emulated/0/abc.per", 20));
        scanResult.add(new FileStat("/sdcard/emulated/0/world.txt", 40));
        scanResult.add(new FileStat("/sdcard/emulated/0/abc.zip", 20));
        scanResult.add(new FileStat("/sdcard/emulated/0/world.svg", 40));
        scanResult.add(new FileStat("/sdcard/emulated/0/def.per", 30));
        scanResult.add(new FileStat("/sdcard/emulated/0/howdy.txt", 77));
        scanResult.add(new FileStat("/sdcard/emulated/0/pqr.per", 81));
        scanResult.add(new FileStat("/sdcard/emulated/0/abc.zip", 20));
        scanResult.add(new FileStat("/sdcard/emulated/0/world.jpg", 40));


        assertEquals(2, scanResult.getFileStats().size());
        assertEquals(2, scanResult.getMostFrequentFileTypes().size());

        assertTrue(scanResult.getFileStats().contains(new FileStat("/sdcard/emulated/0/pqr.per", 81)));
        assertTrue(scanResult.getFileStats().contains(new FileStat("/sdcard/emulated/0/howdy.txt", 77)));

        assertTrue(scanResult.getMostFrequentFileTypes().contains(new FileTypeFrequency("txt", 3)));
        assertTrue(scanResult.getMostFrequentFileTypes().contains(new FileTypeFrequency("per", 3)));
        assertEquals(378/10, scanResult.getAverageFileSize());
        assertEquals(10, scanResult.getTotalFilesScanned());
    }
}
