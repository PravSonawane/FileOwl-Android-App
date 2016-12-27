package com.merryapps.framework;

/**
 * <pre>
 * Represents the get of an entity.
 *
 * An entity life cycle is as follows:
 *
 * NEW --> DRAFTED --> SYNCHRONIZING --> SYNCHRONIZED (entities that were created on this client and synchronized with the server)
 * SYNCHRONIZED (entities that were sent from the server and not created on this client)
 * SYNCHRONIZED --> UPDATED --> SYNCHRONIZING --> SYNCHRONIZED (updated and synchronized)
 * SYNCHRONIZED --> SCHEDULED_FOR_DELETION --> INACTIVE (deleted)
 * NEW --> DRAFTED --> LOCAL (entities created on this client and local to the client)
 * SYNCHRONIZING --> DRAFTED (new entities that failed to synchronize)
 * SYNCHRONIZING --> UPDATED (updated entities that failed to synchronize)
 *
 *
 * </pre>
 */
public enum EntityState {

    /** A new entity. This entity may or may not be stored in the local phone DB but is definitely not synchronized with the backend*/
    NEW("N"),
    /** State of the entity when its saved in the local phone DB but yet not synchronized with the backend */
    SYNCHRONIZING("P"),
    /** State of the entity when its being synchronized with the server. */
    SYNCHRONIZED("C"),
    /** A new entity that is updated in the local phone DB but not synchronized with the backend */
    UPDATED("U"),

    /**
     * State of an entity that was deleted on the local phone DB but yet has not been deleted from the server.
     * These entities will be permanently soft deleted from the local and backend and moved to INACTIVE.
     */
    SCHEDULED_FOR_DELETION("X"),

    /** State of the entity when soft deleted from the local and backend */
    INACTIVE("I"),

    /** Represents and entity that only needs to exist on the local phone DB. */
    LOCAL("L");

    private String state;

    EntityState(String state) {
        this.state = state;
    }

    public String get() {
        return this.state;
    }

    public static EntityState convert(String state) {
        switch (state) {
            case "N":
                return NEW;
            case "P":
                return SYNCHRONIZING;
            case "C":
                return SYNCHRONIZED;
            case "U":
                return UPDATED;
            case "X":
                return SCHEDULED_FOR_DELETION;
            case "L":
                return LOCAL;
            case "I":
                return INACTIVE;
        }

        throw new IllegalArgumentException("No defined get for input string :" + state);
    }
}
