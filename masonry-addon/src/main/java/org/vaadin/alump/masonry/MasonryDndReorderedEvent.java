package org.vaadin.alump.masonry;

import com.vaadin.ui.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Drag and drop re-ordering event
 */
public class MasonryDndReorderedEvent implements Serializable {

    private final MasonryDnDLayout layout;
    private final Component movedComponent;
    private final List<Component> oldOrder;

    public MasonryDndReorderedEvent(MasonryDnDLayout layout, Component movedComponent, List<Component> oldOrder) {
        this.layout = layout;
        this.movedComponent = movedComponent;
        this.oldOrder = Collections.unmodifiableList(new ArrayList<Component>(oldOrder));
    }

    /**
     * Get layout reordered by user
     * @return Layout reordered
     */
    public MasonryDnDLayout getLayout() {
        return layout;
    }

    /**
     * Get moved component
     * @return Component moved
     */
    public Component getMovedComponent() {
        return movedComponent;
    }

    /**
     * Get older of child component before ordering
     * @return Old order of components
     */
    public List<Component> getOldOrder() {
        return oldOrder;
    }
}
