package org.vaadin.alump.masonry;

import com.vaadin.ui.DragAndDropWrapper;

/**
 * This class has been renamed as MasonryDndWrapper, and will be removed from future versions.
 * @see org.vaadin.alump.masonry.MasonryDnDWrapper
 */
@Deprecated
public class DnDMasonryLayout extends MasonryDnDWrapper {

    @Deprecated
    public interface DnDMasonryReorderListener extends MasonryReorderListener {

    }

    @Deprecated
    public static class DnDMasonryReorderEvent extends MasonryReorderEvent {
        public DnDMasonryReorderEvent(MasonryDnDWrapper layout) {
            super(layout);
        }
    }

    @Deprecated
    public static class DndMasonryDropHandler extends MasonryDropHandler {
        public DndMasonryDropHandler(MasonryDnDWrapper layout, DragAndDropWrapper childWrapper) {
            super(layout, childWrapper);
        }
    }

    public DnDMasonryLayout() {
        super();
    }

    public DnDMasonryLayout(int columnWidth) {
        super(columnWidth);
    }
}
