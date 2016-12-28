package com.merryapps.fileowl.model;

import org.junit.Test;

import java.util.Collections;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Unit Tests for {@link ScanDataAccumulator}
 *
 * @author Pravin Sonawane (june.pravin@gmail.com)
 * @since v1.0.0
 */
public class ScanDataAccumulatorTest {

    @Test
    public void testNoArgConstructor() {
        ScanDataAccumulator scanDataAccumulator = new ScanDataAccumulator();
        assertEquals(10, scanDataAccumulator.getLargeFileCollectionSize());
        assertEquals(5, scanDataAccumulator.getHighestFileFrequencyCollectionSize());
        assertEquals(0, scanDataAccumulator.getTotalFilesScanned());
        assertEquals(0, scanDataAccumulator.getAverageFileSize());
        assertEquals(Collections.emptyList(), scanDataAccumulator.getFileStats());
        assertEquals(Collections.emptyList(), scanDataAccumulator.getMostFrequentFileTypes());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorInvalidArgs1() {
        new ScanDataAccumulator(0,1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorInvalidArgs2() {
        new ScanDataAccumulator(1,0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorInvalidArgs3() {
        new ScanDataAccumulator(11,1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorInvalidArgs4() {
        new ScanDataAccumulator(5,6);
    }

    @Test
    public void testConstructorValidArgs() {
        ScanDataAccumulator scanDataAccumulator = new ScanDataAccumulator(8,3);
        assertEquals(8, scanDataAccumulator.getLargeFileCollectionSize());
        assertEquals(3, scanDataAccumulator.getHighestFileFrequencyCollectionSize());
        assertEquals(0, scanDataAccumulator.getTotalFilesScanned());
        assertEquals(0, scanDataAccumulator.getAverageFileSize());
        assertEquals(Collections.emptyList(), scanDataAccumulator.getFileStats());
        assertEquals(Collections.emptyList(), scanDataAccumulator.getMostFrequentFileTypes());
    }

    @Test
    public void testAddWhenFirstFileAdded() {
        ScanDataAccumulator scanDataAccumulator = new ScanDataAccumulator(8,3);
        assertEquals(8, scanDataAccumulator.getLargeFileCollectionSize());
        assertEquals(3, scanDataAccumulator.getHighestFileFrequencyCollectionSize());
        assertEquals(0, scanDataAccumulator.getTotalFilesScanned());
        assertEquals(0, scanDataAccumulator.getAverageFileSize());
        assertEquals(Collections.emptyList(), scanDataAccumulator.getFileStats());
        assertEquals(Collections.emptyList(), scanDataAccumulator.getMostFrequentFileTypes());

        //add one fileStat
        scanDataAccumulator.add(new FileStat("/sdcard/emulated/0/hello.txt", 100));

        assertEquals(1, scanDataAccumulator.getFileStats().size());
        assertEquals(1, scanDataAccumulator.getMostFrequentFileTypes().size());

        assertEquals(new FileStat("/sdcard/emulated/0/hello.txt",100),
                scanDataAccumulator.getFileStats().get(0));
        assertEquals(new FileTypeFrequency("txt", 1),
                scanDataAccumulator.getMostFrequentFileTypes().get(0));
        assertEquals(1, scanDataAccumulator.getTotalFilesScanned());
        assertEquals(100, scanDataAccumulator.getAverageFileSize());
    }

    @Test
    public void testAddWhenMultipleFilesAdded() {
        ScanDataAccumulator scanDataAccumulator = new ScanDataAccumulator(2,2);
        assertEquals(2, scanDataAccumulator.getLargeFileCollectionSize());
        assertEquals(2, scanDataAccumulator.getHighestFileFrequencyCollectionSize());
        assertEquals(0, scanDataAccumulator.getTotalFilesScanned());
        assertEquals(0, scanDataAccumulator.getAverageFileSize());
        assertEquals(Collections.emptyList(), scanDataAccumulator.getFileStats());
        assertEquals(Collections.emptyList(), scanDataAccumulator.getMostFrequentFileTypes());

        //add multiple
        scanDataAccumulator.add(new FileStat("/sdcard/emulated/0/hello.txt", 10));
        scanDataAccumulator.add(new FileStat("/sdcard/emulated/0/abc.per", 20));
        scanDataAccumulator.add(new FileStat("/sdcard/emulated/0/world.txt", 40));
        scanDataAccumulator.add(new FileStat("/sdcard/emulated/0/abc.zip", 20));
        scanDataAccumulator.add(new FileStat("/sdcard/emulated/0/world.svg", 40));
        scanDataAccumulator.add(new FileStat("/sdcard/emulated/0/def.per", 30));
        scanDataAccumulator.add(new FileStat("/sdcard/emulated/0/howdy.txt", 77));
        scanDataAccumulator.add(new FileStat("/sdcard/emulated/0/pqr.per", 81));
        scanDataAccumulator.add(new FileStat("/sdcard/emulated/0/abc.zip", 20));
        scanDataAccumulator.add(new FileStat("/sdcard/emulated/0/world.jpg", 40));


        assertEquals(2, scanDataAccumulator.getFileStats().size());
        assertEquals(2, scanDataAccumulator.getMostFrequentFileTypes().size());

        assertTrue(scanDataAccumulator.getFileStats().contains(new FileStat("/sdcard/emulated/0/pqr.per", 81)));
        assertTrue(scanDataAccumulator.getFileStats().contains(new FileStat("/sdcard/emulated/0/howdy.txt", 77)));

        assertTrue(scanDataAccumulator.getMostFrequentFileTypes().contains(new FileTypeFrequency("txt", 3)));
        assertTrue(scanDataAccumulator.getMostFrequentFileTypes().contains(new FileTypeFrequency("per", 3)));
        assertEquals(378/10, scanDataAccumulator.getAverageFileSize());
        assertEquals(10, scanDataAccumulator.getTotalFilesScanned());
    }
}
