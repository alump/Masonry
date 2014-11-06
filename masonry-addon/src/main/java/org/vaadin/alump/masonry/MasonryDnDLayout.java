package org.vaadin.alump.masonry;

import com.vaadin.shared.Connector;
import com.vaadin.ui.Component;
import org.vaadin.alump.masonry.client.dnd.MasonryDnDLayoutState;
import org.vaadin.alump.masonry.client.dnd.MasonryDndServerRpc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Adds HTML5 live reorder dragging to MasonryLayout. This class is in experimental state. If you need full Vaadin
 * drag and drop support use MasonryDnDWrapper class.
 * @see org.vaadin.alump.masonry.MasonryDnDWrapper
 */
public class MasonryDnDLayout extends MasonryLayout {

    private final List<MasonryDndReorderListener> listeners = new ArrayList<MasonryDndReorderListener>();

    public interface MasonryDndReorderListener {
        void onReordered(MasonryDndReorderedEvent event);
    }

    public MasonryDnDLayout() {
        addStyleName("masonry-dnd-layout");
        registerRpc(serverRpc, MasonryDndServerRpc.class);
    }

    @Override
    protected MasonryDnDLayoutState getState() {
        return (MasonryDnDLayoutState) super.getState();
    }

    /**
     * Define if user is allowed to reorder components by dragging them
     * @param allowed true if allowed, false if not
     */
    public void setDraggingAllowed(boolean allowed) {
        getState().draggingAllowed = allowed;
    }

    /**
     * Check if user is allowed to reorder components by dragging
     * @return true if allowed, false if not
     */
    public boolean isDraggingAllowed() {
        return getState().draggingAllowed;
    }

    private final MasonryDndServerRpc serverRpc = new MasonryDndServerRpc() {
        @Override
        public void onReorder(Connector movedChild, int newIndex) {
            System.out.println("On re-order from " + components.indexOf(movedChild) + " to " + newIndex);

            Component movedComponent = (Component)movedChild;

            MasonryDndReorderedEvent event = new MasonryDndReorderedEvent(MasonryDnDLayout.this, movedComponent,
                    MasonryDnDLayout.this.getComponents());

            if(components.indexOf(movedComponent) < newIndex) {
                newIndex -= 1;
            }

            MasonryDnDLayout.this.addComponent(movedComponent, newIndex);

            for(MasonryDndReorderListener listener : listeners) {
                listener.onReordered(event);
            }
        }
    };

    /**
     * Add drag and drop re-ordering listener
     * @param listener Listener added
     */
    public void addMasonryDndReorderListener(MasonryDndReorderListener listener) {
        listeners.add(listener);
    }

    /**
     * Remove drag and drop re-ordering listener
     * @param listener Listener removed
     */
    public void removeMasonryDndReorderListener(MasonryDndReorderListener listener) {
        listeners.remove(listener);
    }
}
