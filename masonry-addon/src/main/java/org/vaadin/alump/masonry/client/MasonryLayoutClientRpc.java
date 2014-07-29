package org.vaadin.alump.masonry.client;

import com.vaadin.shared.communication.ClientRpc;

// ClientRpc is used to pass events from server to client
// For sending information about the changes to component state, use State instead
public interface MasonryLayoutClientRpc extends ClientRpc {

	// Ask masonry to relayout items (if automatic layouting fails)
	public void layout();

}