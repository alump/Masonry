package org.vaadin.alump.masonry.client.dnd;

import com.google.gwt.user.client.ui.Widget;

/**
 * Created by alump on 05/11/14.
 */
public interface ClientMasonryReorderHandler {
    /**
     * Called when user had reordered widgets
     * @param movedWidget Widget moved
     * @param newIndex Index where widget is moved
     */
    void onReorder(Widget movedWidget, int newIndex);

    /**
     * Called when content should be re-constructed (cancelled drag ordering)
     */
    void onReConstruct();
}
