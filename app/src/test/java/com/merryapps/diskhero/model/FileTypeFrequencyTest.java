package com.merryapps.diskhero.model;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * //TODO add description here
 *
 * @author Pravin Sonawane (june.pravin@gmail.com)
 * @since v1.0.0
 */

public class FileTypeFrequencyTest {

    @Test
    public void testConstructorValidArgs() {
        FileTypeFrequency f = new FileTypeFrequency("txt", 12);
        assertEquals("txt", f.getFileType());
        assertEquals(12, f.getFrequency());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorInvalidArgs1() {
        new FileTypeFrequency(null, 12);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorInvalidArgs2() {
        new FileTypeFrequency("txt", -12);
    }

    @Test
    public void testIncrementFrequency() {
        FileTypeFrequency f = new FileTypeFrequency("txt", 12);
        f.incrementFrequency();
        assertEquals(13, f.getFrequency());
    }

    @Test
    public void testEqualsWhenEqual() {
        FileTypeFrequency f1 = new FileTypeFrequency("txt", 12);
        FileTypeFrequency f2 = new FileTypeFrequency("txt", 12);

        assertTrue(f1.equals(f2));
    }

    @Test
    public void testEqualsWhenUnequal() {
        FileTypeFrequency f1 = new FileTypeFrequency("txt", 12);
        FileTypeFrequency f2 = new FileTypeFrequency("txt", 23);

        assertFalse(f1.equals(f2));
    }
}
