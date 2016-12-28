package com.merryapps.fileowl.model.db;

import com.merryapps.fileowl.model.ScanStatus;

import de.greenrobot.dao.converter.PropertyConverter;

/**
 * //TODO add description here
 *
 * @author Pravin Sonawane (june.pravin@gmail.com)
 * @since v1.0.0
 */

public class ScanStatusConverter implements PropertyConverter<ScanStatus, String> {
    @Override
    public ScanStatus convertToEntityProperty(String databaseValue) {
        return ScanStatus.convert(databaseValue);
    }

    @Override
    public String convertToDatabaseValue(ScanStatus entityProperty) {
        return entityProperty.get();
    }
}
