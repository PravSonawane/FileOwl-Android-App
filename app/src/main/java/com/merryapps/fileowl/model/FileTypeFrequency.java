package com.merryapps.fileowl.model;


import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Defines the frequency of a file type.
 * This class is not thread safe.
 * @author Pravin Sonawane (june.pravin@gmail.com)
 * @since v1.0.0
 */

public class FileTypeFrequency implements Parcelable {
    private final String fileType;
    private long frequency;

    FileTypeFrequency(@NonNull String fileType, long frequency) {
        if (frequency < 0) {
            throw new IllegalArgumentException("frequency must be >= 0:" + frequency);
        }

        if (fileType == null) {
            throw new IllegalArgumentException("fileType cannot be null");
        }

        this.fileType = fileType;
        this.frequency = frequency;
    }

    @NonNull
    public String getFileType() {
        return fileType;
    }

    public long getFrequency() {
        return frequency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileTypeFrequency that = (FileTypeFrequency) o;

        return frequency == that.frequency && fileType.equals(that.fileType);

    }

    @Override
    public int hashCode() {
        int result = fileType.hashCode();
        result = 31 * result + (int) (frequency ^ (frequency >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "{" +
                "fileType='" + fileType + '\'' +
                ", frequency=" + frequency +
                '}';
    }

    void incrementFrequency() {
        frequency++;
    }

    protected FileTypeFrequency(Parcel in) {
        fileType = in.readString();
        frequency = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fileType);
        dest.writeLong(frequency);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FileTypeFrequency> CREATOR = new Creator<FileTypeFrequency>() {
        @Override
        public FileTypeFrequency createFromParcel(Parcel in) {
            return new FileTypeFrequency(in);
        }

        @Override
        public FileTypeFrequency[] newArray(int size) {
            return new FileTypeFrequency[size];
        }
    };
}
