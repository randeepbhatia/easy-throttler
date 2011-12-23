package org.oasis.toolset.easythrottler;

/**
 * @author hsun
 *
 * Indicates the entity has a life cycle. It needs to be started before usage and needs to be stopped after usage.
 */
public interface Startable {

    /**
     * Starts the life cycle.
     */
    void start();

    /**
     * Stops the life cycle.
     */
    void stop();

}