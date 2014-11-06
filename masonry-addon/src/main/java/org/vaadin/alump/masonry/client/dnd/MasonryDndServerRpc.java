package org.vaadin.alump.masonry.client.dnd;

import com.vaadin.shared.Connector;
import com.vaadin.shared.communication.ServerRpc;

/**
 * Interface client uses to notify server when user has reordered components
 */
public interface MasonryDndServerRpc extends ServerRpc {

    /**
     * Called when user have moved component to new position
     * @param movedChild Child moved
     * @param newIndex New index of child
     */
    void onReorder(Connector movedChild, int newIndex);
}
