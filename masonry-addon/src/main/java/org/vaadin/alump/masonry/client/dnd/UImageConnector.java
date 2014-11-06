package org.vaadin.alump.masonry.client.dnd;

import com.vaadin.client.ui.image.ImageConnector;
import com.vaadin.shared.ui.Connect;

/**
 * Connector for un-draggable image
 */
@Connect(org.vaadin.alump.masonry.UnDraggableImage.class)
public class UImageConnector extends ImageConnector {

    @Override
    protected void init() {
        super.init();

        getWidget().getElement().setAttribute("draggable", "false");
    }
}
