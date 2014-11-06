package org.vaadin.alump.masonry;

import com.vaadin.ui.Component;

import java.util.List;

/**
 * Drag and drop re-ordering event
 */
public class MasonryDndReorderedEvent {

    private MasonryDnDLayout layout;
    private Component movedComponent;
    private List<Component> oldOrder;

    public MasonryDndReorderedEvent(MasonryDnDLayout layout, Component movedComponent, List<Component> oldOrder) {
        this.layout = layout;
        this.movedComponent = movedComponent;
        this.oldOrder = oldOrder;
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
