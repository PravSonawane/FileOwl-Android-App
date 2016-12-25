package com.merryapps.diskhero.model;

import android.support.annotation.NonNull;

import java.io.File;

/**
 * The statistics of a file.
 *
 * @author Pravin Sonawane (june.pravin@gmail.com)
 * @since v1.0.0
 */
public class FileStat {

    private static final String STRING_DOT = ".";

    /**
     * The type returned by {@link FileStat#getType()} when a file's type cannot be determined.
     * For example,
     *
     * <code><pre>
     *      FileStat f = new FileStat("/sdcard/emulated/0/hello", 100);
     *      String type = f.getType(); //returns FileStat.NO_TYPE
     * </pre></code>
     *
     */
    public static final String NO_TYPE = "";
    private final String absolutePath;
    private final String name;
    private final String type;
    private final long size;
    /**
     * Creates a new {@link FileStat} with the given file information.
     * @param absolutePath the FQDN of the file (its absolutePath) (must not be {@code null}.
     * @param size the size of the file in bytes (must not be < 0)
     * @throws IllegalArgumentException if arguments are invalid.
     */
    FileStat(@NonNull final String absolutePath, long size) {
        if (absolutePath == null) {
            throw new IllegalArgumentException("absolutePath cannot be null");
        }
        if (size < 0) {
            throw new IllegalArgumentException("size cannot be < 0:" + size);
        }
        this.absolutePath = absolutePath;
        this.name = extractName(absolutePath);
        this.type = extractType(absolutePath);
        this.size = size;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    /** Returns the size in bytes */
    long getSize() {
        return size;
    }

    /**
     * Extracts the file type (extension) of the given file. The file type is calculated
     * as the string after the last '.' character in the string returned by {@link File#getName()}.
     * If the file name does not have a '.' in it or ends with '.', an empty string is returned.
     * @param absolutePath the FQDN of the file for which the file type is to be extracted.
     * @return the file extension as a {@link String}.
     */
    private static String extractType(@NonNull final String absolutePath) {
        int beginIndex = absolutePath.lastIndexOf(STRING_DOT);

        //extension could not be found.
        if (beginIndex < 0) {
            return NO_TYPE;
        }
        return absolutePath.substring(beginIndex+1);
    }

    /**
     * Extracts the file name of the given file. The file name is calculated
     * as the string after the last {@link File#separator} in the given string.
     * If the given string does not have a {@link File#separator} in it
     * or ends with {@link File#separator}, a {@code null} is returned (Such strings are not expected
     * to be an absolute path of a file i.e. the value returned by {@link File#getAbsolutePath()}
     * when {@link File#isFile()} is {@code true}.
     * @param absolutePath the FQDN of the file for which the file name is to be extracted.
     * @return the file name as a {@link String} or {@code null} if cannot be determined.
     */
    private static String extractName(@NonNull final String absolutePath) {
        int beginIndex = absolutePath.lastIndexOf(File.separator);

        //name could not be could not be determined.
        if (beginIndex < 0 || absolutePath.endsWith(File.separator)) {
            return null;
        }
        return absolutePath.substring(beginIndex+1);
    }

    /**
     * Two {@link FileStat} are equal if their {@link FileStat#getAbsolutePath()} is equal
     * @param o other fileStat
     * @return {@code true} if equal, {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileStat fileStat = (FileStat) o;

        return absolutePath != null ? absolutePath.equals(fileStat.absolutePath) : fileStat.absolutePath == null;

    }

    @Override
    public int hashCode() {
        return absolutePath != null ? absolutePath.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "FileStat{" +
                "absolutePath='" + absolutePath + '\'' +
                ", size=" + size +
                '}';
    }
}
