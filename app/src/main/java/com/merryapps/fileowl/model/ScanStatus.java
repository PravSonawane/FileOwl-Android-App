package com.merryapps.fileowl.model;

/**
 * The status of a scan
 *
 * @author Pravin Sonawane (june.pravin@gmail.com)
 * @since v1.0.0
 */
public enum ScanStatus {
    NO_SCAN_DATA("NSD"),
    NOT_SCANNING("NS"),
    SCANNING_FILE_SYSTEM("SFS"),
    SCAN_COMPLETE("SC"),
    SCAN_CANCELLED("ST"),
    EXTERNAL_STORAGE_NOT_MOUNTED("ESNM"),
    SCAN_ERROR("SE");

    private String value;

    ScanStatus(String value) {
        this.value = value;
    }

    public String get() {
        return this.value;
    }

    public static ScanStatus convert(String value) {
        if(value == null || value.isEmpty()) {
            throw new IllegalArgumentException("value cannot be null or empty");
        }

        switch (value) {
            case "NSD":
                return NO_SCAN_DATA;
            case "NS":
                return NOT_SCANNING;
            case "SFS":
                return SCANNING_FILE_SYSTEM;
            case "SC":
                return SCAN_COMPLETE;
            case "ESNM":
                return EXTERNAL_STORAGE_NOT_MOUNTED;
            case "ST":
                return SCAN_CANCELLED;
            case "SE":
                return SCAN_ERROR;
        }

        throw new IllegalArgumentException("no type defined for value:" + value);
    }
}
