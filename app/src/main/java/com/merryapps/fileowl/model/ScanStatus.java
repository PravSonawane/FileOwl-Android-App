package com.merryapps.fileowl.model;

/**
 * //TODO add description here
 *
 * @author Pravin Sonawane (june.pravin@gmail.com)
 * @since v1.0.0
 */ //TODO add description here
public enum ScanStatus {
    NO_SCAN_DATA,
    NOT_SCANNING,
    SCANNING_FILE_SYSTEM,
    SCAN_COMPLETE,
    SCAN_CANCELLED,
    EXTERNAL_STORAGE_NOT_MOUNTED,
    SCAN_ERROR;
}
