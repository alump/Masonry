package org.vaadin.alump.masonry.client;

/**
 * Interface for Connector classes that want to be notified on client side when their images are loaded (only applies
 * if and when they are extended with ImagesLoadedExtension).
 */
public interface ImagesLoadedClientListener {

    /**
     * Called when all images under component are loaded.
     */
    void onImagesLoaded();
}
