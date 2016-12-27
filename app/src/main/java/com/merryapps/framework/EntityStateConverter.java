package com.merryapps.framework;

import de.greenrobot.dao.converter.PropertyConverter;

/**
 * Converter for entity state.
 * @author Pravin Sonawane (june.pravin@gmail.com)
 * @since v1.0.0
 */
public class EntityStateConverter implements PropertyConverter<EntityState, String> {

    @Override
    public EntityState convertToEntityProperty(String databaseValue) {
        return EntityState.convert(databaseValue);
    }

    @Override
    public String convertToDatabaseValue(EntityState entityProperty) {
        return entityProperty.get();
    }
}
