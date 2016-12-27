package com.merryapps.fileowl.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * The statistics of a file.
 *
 * @author Pravin Sonawane (june.pravin@gmail.com)
 * @since v1.0.0
 */
public class FileStat implements Parcelable {

    private static final String TAG = "FileStat";

    private static final String STRING_DOT = ".";
    private static final RoundingMode ROUNDING_MODE_HALF_UP = RoundingMode.HALF_UP;
    private static final int ONE_KB = 1024;
    private static final int ONE_MB = 1024 * ONE_KB;
    private static final long ONE_GB = (long)1024 * ONE_MB;
    private static final long ONE_TB = (long)1024 * ONE_GB;
    private static final int SCALE = 2;

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
    //TODO change this to package private
    public FileStat(@NonNull final String absolutePath, long size) {
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

    public long getSize() {
        return size;
    }

    /**
     * Returns the size in a human readable form. For example, if size = 1024 bytes
     * <code>
     *     <pre>
     *         String size = getSizeHumanReadable(); // returns 1 KB
     *     </pre>
     * </code>
     * @return a {@link String}
     */
    public String getSizeHumanReadable() {

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

        return absolutePath != null ?
                absolutePath.equals(fileStat.absolutePath) : fileStat.absolutePath == null;

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

    protected FileStat(Parcel in) {
        absolutePath = in.readString();
        name = in.readString();
        type = in.readString();
        size = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(absolutePath);
        dest.writeString(name);
        dest.writeString(type);
        dest.writeLong(size);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FileStat> CREATOR = new Creator<FileStat>() {
        @Override
        public FileStat createFromParcel(Parcel in) {
            return new FileStat(in);
        }

        @Override
        public FileStat[] newArray(int size) {
            return new FileStat[size];
        }
    };
}
