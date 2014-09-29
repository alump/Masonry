package org.vaadin.alump.masonry.client;

import com.vaadin.shared.communication.ServerRpc;

/**
 * Server RPC for ImagesLoaded extension
 */
public interface ImagesLoadedServerRpc extends ServerRpc {

    /**
     * Called when all images in given container are loaded
     */
    void onImagesLoaded();
}
