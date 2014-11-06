package org.vaadin.alump.masonry;

import com.vaadin.server.Resource;
import com.vaadin.ui.Image;

/**
 * Just a image that is marked as non draggable (adds attribute draggable="false" to image)
 */
public class UnDraggableImage extends Image {

    public UnDraggableImage() {
        super();
    }

    public UnDraggableImage(String caption) {
        super(caption);
    }

    public UnDraggableImage(String caption, Resource source) {
        super(caption, source);
    }
}
