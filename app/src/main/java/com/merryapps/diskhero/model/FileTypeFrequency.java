package com.merryapps.diskhero.model;


import android.support.annotation.NonNull;

/**
 * Defines the frequency of a file type.
 * This class is not thread safe.
 * @author Pravin Sonawane (june.pravin@gmail.com)
 * @since v1.0.0
 */

class FileTypeFrequency {
    final String fileType;
    long frequency;

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
    String getFileType() {
        return fileType;
    }

    long getFrequency() {
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
        return "FileTypeFrequency{" +
                "fileType='" + fileType + '\'' +
                ", frequency=" + frequency +
                '}';
    }

    void incrementFrequency() {
        frequency++;
    }
}
