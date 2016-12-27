package com.merryapps.fileowl.model;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Unit Tests for {@link FileStat}
 *
 * @author Pravin Sonawane (june.pravin@gmail.com)
 * @since v1.0.0
 */

public class FileStatTest {

    @Test
    public void testConstructorValidArgs() {
        FileStat stat = new FileStat("/sdcard/emulated/0/hello.txt", 100);
        assertEquals("/sdcard/emulated/0/hello.txt", stat.getAbsolutePath());
        assertEquals("hello.txt", stat.getName());
        assertEquals("txt", stat.getType());
        assertEquals(100, stat.getSize());
    }

    @Test
    public void testConstructorValidArgsWhenPathhasMultipleDots() {
        FileStat stat = new FileStat("/sdcard/emulated/0/he.llo.txt", 100);
        assertEquals("/sdcard/emulated/0/he.llo.txt", stat.getAbsolutePath());
        assertEquals("he.llo.txt", stat.getName());
        assertEquals("txt", stat.getType());
        assertEquals(100, stat.getSize());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorInvalidArgs1() {
        new FileStat(null, 100);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorInvalidArgs2() {
        new FileStat("/sdcard/emulated/0/hello.txt", -1);
    }

    @Test
    public void testConstructorWhenTypeCannotBeDetermined() {
        FileStat stat = new FileStat("/sdcard/emulated/0/hello", 100);
        assertEquals("/sdcard/emulated/0/hello", stat.getAbsolutePath());
        assertEquals("hello", stat.getName());
        assertEquals(FileStat.NO_TYPE, stat.getType());
        assertEquals(100, stat.getSize());
    }

    @Test
    public void testConstructorWhenNameCannotBeDetermined1() {
        FileStat stat = new FileStat("hello", 100);
        assertEquals("hello", stat.getAbsolutePath());
        assertEquals(null, stat.getName());
        assertEquals(FileStat.NO_TYPE, stat.getType());
        assertEquals(100, stat.getSize());
    }

    @Test
    public void testConstructorWhenNameCannotBeDetermined2() {
        FileStat stat = new FileStat("/sdcard/emulated/0/hello/", 100);
        assertEquals("/sdcard/emulated/0/hello/", stat.getAbsolutePath());
        assertEquals(null, stat.getName());
        assertEquals(FileStat.NO_TYPE, stat.getType());
        assertEquals(100, stat.getSize());
    }

    @Test
    public void testEqualsWhenEqual() {
        FileStat stat1 = new FileStat("/sdcard/emulated/0/hello.txt", 100);
        FileStat stat2 = new FileStat("/sdcard/emulated/0/hello.txt", 200);

        assertTrue(stat1.equals(stat2));
    }

    @Test
    public void testEqualsWhenUnequal() {
        FileStat stat1 = new FileStat("/sdcard/emulated/0/Hello.txt", 100);
        FileStat stat2 = new FileStat("/sdcard/emulated/0/hello.txt", 200);

        assertFalse(stat1.equals(stat2));
    }
}
