package com.merryapps.fileowl.ui;

import com.merryapps.fileowl.model.Result;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * //TODO add description here
 *
 * @author Pravin Sonawane (june.pravin@gmail.com)
 * @since v1.0.0
 */
public class UiUtil {

    private static final RoundingMode ROUNDING_MODE_HALF_UP = RoundingMode.HALF_UP;
    private static final int ONE_KB = 1024;
    private static final int ONE_MB = 1024 * ONE_KB;
    private static final long ONE_GB = (long)1024 * ONE_MB;
    private static final long ONE_TB = (long)1024 * ONE_GB;
    private static final int SCALE = 2;

    public static String getHumanReadableSize(long size) {
        if (size < 0) {
            throw new IllegalArgumentException("size cannot be < 0");
        }
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

    public static String shareMessageGenerator(Result result) {
        String newLine = "\n";
        String colon = ":";

        String separator = "------------------------------------------------";
        return  "Scan statistics" +
                newLine +
                separator +
                newLine +
                "File Scanned" +
                colon +
                result.getTotalFilesScanned() +
                newLine +
                "Average file size" +
                colon +
                UiUtil.getHumanReadableSize(result.getAverageFileSize()) +
                newLine +
                "Largest files:" +
                colon +
                result.getLargestFiles().toString() +
                newLine +
                "Frequent files:" +
                colon +
                result.getFrequentFiles().toString() +
                newLine +
                separator;

    }
}
