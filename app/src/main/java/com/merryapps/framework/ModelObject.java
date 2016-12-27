package com.merryapps.framework;

/**
 * An business object.
 */
public interface ModelObject {

    Long getId();

    void toNewState(EntityState newState);

    EntityState getState();

}
