/**
 * DnDMasonryLayout.java (Masonry)
 *
 * Copyright 2014 Vaadin Ltd, Sami Viitanen <sami.viitanen@vaadin.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.vaadin.alump.masonry;

import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.event.dd.acceptcriteria.ServerSideCriterion;
import com.vaadin.ui.Component;
import com.vaadin.ui.DragAndDropWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Adds DnD reordering features to MasonryLayout. DnDMasonryLayout inherits DragAndDropWrapper,
 * how ever this default implementation does not do any drop handling. If you want to handle dropping
 * on layout itself, set the DropHandler.
 */
public class DnDMasonryLayout extends DragAndDropWrapper {

    protected boolean allowReorder = true;
    protected List<DnDMasonryReorderListener> reorderListeners = new ArrayList<DnDMasonryReorderListener>();

    /**
     * Interface called when user has reordered components by dragging them
     */
    public interface DnDMasonryReorderListener {
        /**
         * Called when user reordered components
         * @param event Event containing the reordering information
         */
        void onUserReorder(DnDMasonryReorderEvent event);
    }

    /**
     * Event containing information of reorder event caused by user
     */
    public static class DnDMasonryReorderEvent {
        protected DnDMasonryLayout layout;

        public DnDMasonryReorderEvent(DnDMasonryLayout layout) {
            this.layout = layout;
            //TODO: more data
        }

        /**
         * Get layout where reordering happened
         * @return Layout where reordering happened
         */
        public DnDMasonryLayout getLayout() {
            return layout;
        }
    }

    private DragAndDropWrapper.DragStartMode componentDragStartMode = DragAndDropWrapper.DragStartMode.WRAPPER;

    /**
     * Default drop handler that takes care of reordering calls when child is dragged above other children
     */
    public static class DndMasonryDropHandler implements DropHandler {

        protected DnDMasonryLayout layout;
        protected DragAndDropWrapper target;

        public DndMasonryDropHandler(DnDMasonryLayout layout) {
            this(layout, null);
        }

        public DndMasonryDropHandler(DnDMasonryLayout layout, DragAndDropWrapper childWrapper) {
            this.layout = layout;
            this.target = childWrapper;
        }

        @Override
        public void drop(DragAndDropEvent event) {
            Component dragged = event.getTransferable().getSourceComponent();

            // If no target just add to end
            if(target == null) {
                layout.getMasonryLayout().addComponent(dragged);
            } else {
                WrapperTargetDetails details = (WrapperTargetDetails)event.getTargetDetails();

                String wrapperStyleName = layout.getMasonryLayout().getComponentWrapperStyleName(dragged);
                layout.getMasonryLayout().addComponentBefore(dragged, wrapperStyleName, target);
            }

            layout.notifyReorderListeners();
        }

        @Override
        public AcceptCriterion getAcceptCriterion() {
            ServerSideCriterion criterion = new ServerSideCriterion() {
                @Override
                public boolean accept(DragAndDropEvent dragEvent) {
                    if(!layout.isReorderable()) {
                        return false;
                    }

                    try {
                        Component source = dragEvent.getTransferable().getSourceComponent();
                        if(target != null) {
                            return target != source.getParent() && source.getParent().getParent() == layout;
                        } else {
                            return source.getParent().getParent() == layout;
                        }
                    } catch(Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            };
            return criterion;
        }
    }

    public DnDMasonryLayout() {
        super(new MasonryLayout());
        addStyleName("dnd-masonry-layout");

        // Simplifies the drag hints
        getMasonryLayout().addStyleName("no-vertical-drag-hints");
        getMasonryLayout().addStyleName("no-horizontal-drag-hints");

        // Make sure layout takes full width
        getMasonryLayout().setWidth("100%");

        // No drop handler used in this version
        setDragStartMode(DragStartMode.NONE);
        //setDropHandler(new DndMasonryDropHandler(this));
    }

    public DnDMasonryLayout(int columnWidth) {
        super(new MasonryLayout(columnWidth));
        addStyleName("dnd-masonry-layout");

        // Simplifies the drag hints
        getMasonryLayout().addStyleName("no-vertical-drag-hints");
        getMasonryLayout().addStyleName("no-horizontal-drag-hints");

        // Make sure layout takes full width
        getMasonryLayout().setWidth("100%");

        // No drop handler used in this version
        setDragStartMode(DragStartMode.NONE);
        //setDropHandler(new DndMasonryDropHandler(this));
    }

    public void setComponentDragStartMode(DragAndDropWrapper.DragStartMode dragStartMode) {
        this.componentDragStartMode = dragStartMode;
    }

    public DragAndDropWrapper.DragStartMode getComponentDragStartMode() {
        return componentDragStartMode;
    }

    /**
     * Add component to layout. Will wrap component with DragAndDropWrapper.
     * @param component Component added
     */
    public void addComponentToLayout(Component component) {
        addComponentToLayout(component, getMasonryLayout().getComponentCount());
    }

    /**
     * Add component to layout to given index. Will wrap component with DragAndDropWrapper.
     * @param component Component added
     * @param index Index where component is added
     */
    public void addComponentToLayout(Component component, int index) {
        addComponentToLayout(component, null, index);
    }

    /**
     * Add component to layout with given wrapper style name. Will wrap component with DragAndDropWrapper.
     * @param component Component added
     * @param wrapperStyleName Style name added to wrapper (eq. use to define it take double width)
     */
    public void addComponentToLayout(Component component, String wrapperStyleName) {
        addComponentToLayout(component, wrapperStyleName, getMasonryLayout().getComponentCount());
    }

    /**
     * Add component to layout with given wrapper style name to given index. Will wrap component with
     * DragAndDropWrapper.
     * @param component Component added
     * @param wrapperStyleName Style name added to wrapper (eq. use to define it take double width)
     * @param index Index where component is added
     */
    public void addComponentToLayout(Component component, String wrapperStyleName, int index) {
        getMasonryLayout().addComponent(createComponentDnDWrapper(component, wrapperStyleName), wrapperStyleName, index);
    }

    /**
     * Remove component from layout
     * @param component Component remvoed
     */
    public void removeComponentInLayout(Component component) {
        DragAndDropWrapper wrapper = getComponentDnDWrapper(component);
        if(wrapper != null) {
            getMasonryLayout().removeComponent(wrapper);
        }
    }

    /**
     * Replace component with another component
     * @param oldComponent Old component replaced
     * @param newComponent New component used as replacement
     */
    public void replaceComponentInLayout(Component oldComponent, Component newComponent) {
        DragAndDropWrapper oldWrapper = getComponentDnDWrapper(oldComponent);
        if(oldWrapper == null) {
            throw new IllegalArgumentException("Given component not found");
        }
        DragAndDropWrapper newWrapper = createComponentDnDWrapper(newComponent, null);
        getMasonryLayout().replaceComponent(oldWrapper, newWrapper);
    }

    /**
     * Request client side to re-layout. Useful if component sizes have changed.
     */
    public void requestLayout() {
        getMasonryLayout().requestLayout();
    }

    /**
     * Create DnD wrapper for component if not yet defined
     * @param component Component wrapped
     * @return Wrapper made or found
     */
    protected DragAndDropWrapper createComponentDnDWrapper(Component component, String wrapperStyleName) {
        DragAndDropWrapper wrapper = getComponentDnDWrapper(component);
        if(wrapper == null) {
            wrapper = new DragAndDropWrapper(component);
            wrapper.addStyleName("masonry-dnd-wrapper");
            if(wrapperStyleName != null) {
                wrapper.addStyleName(wrapperStyleName);
            }
            wrapper.setDragStartMode(allowReorder ? getComponentDragStartMode() : DragStartMode.NONE);
            wrapper.setDropHandler(createDropHandlerForComponents(wrapper));
        }
        return wrapper;
    }

    /**
     * Override to have custom drop handler for your items
     * @param childWrapper Wrapper of child component that need DropHandler
     * @return DropHandler made for childWrapper
     */
    protected DropHandler createDropHandlerForComponents(DragAndDropWrapper childWrapper) {
        return new DndMasonryDropHandler(this, childWrapper);
    }

    /**
     * Get DragAndDropWrapper for given component
     * @param component Component added to this DndMasonryLayout
     * @return Wrapper of component or null if not set yet, or if component is not under this layout
     */
    protected DragAndDropWrapper getComponentDnDWrapper(Component component) {
        if(!(component.getParent() instanceof DragAndDropWrapper)) {
            return null;
        }
        DragAndDropWrapper wrapper = (DragAndDropWrapper)component.getParent();
        if(wrapper.getParent() != this) {
            return null;
        }
        return wrapper;
    }

    /**
     * Get access to Masonry layout inside DnDMasonryLayout. This is protected to prevent messing up wrapper structure
     * this component is trying to maintain. If you really need to access this, inherit class and call it that way. Just
     * be warned issues might follow.
     * @return MasonryLayout wrapped inside this DragAndDropWrapper
     */
    protected MasonryLayout getMasonryLayout() {
        return (MasonryLayout)super.getCompositionRoot();
    }

    /**
     * Get component in layout with given index. When you index components with this, use
     * {@link #getComponentCountInLayout()} as indexing limit.
     * @param index Index of component searched
     * @return Component at given index
     */
    public Component getComponentInLayout(int index) {
        DragAndDropWrapper wrapper = (DragAndDropWrapper)getMasonryLayout().getComponent(index);
        if(wrapper != null) {
            return wrapper.iterator().next();
        } else {
            return null;
        }
    }

    /**
     * Use this number when you want to know how many components have been added to layout.
     * @return Number of components in layout
     */
    public int getComponentCountInLayout() {
        return getMasonryLayout().getComponentCount();
    }

    /**
     * Add component to layout with given wrapper style name to given index. Will wrap component with
     * DragAndDropWrapper.
     * @param component Component added
     * @param wrapperStyleName Style name added to wrapper (eq. use to define it take double width)
     */
    public void addComponentFirst(Component component, String wrapperStyleName) {
        addComponentToLayout(component, wrapperStyleName, 0);
    }

    /**
     * Set if user is allowed to reorder components by dragging them. This does not affect to server APIs.
     * @param reorderable true to allow, false to disallow
     */
    public void setReorderable(boolean reorderable) {
        allowReorder = reorderable;

        // Update drag start modes
        for(int i = 0; i < getMasonryLayout().getComponentCount(); ++i) {
            Component child = getMasonryLayout().getComponent(i);
            if(child instanceof DragAndDropWrapper) {
                DragAndDropWrapper wrapper = (DragAndDropWrapper) child;
                wrapper.setDragStartMode(allowReorder ? getComponentDragStartMode() : DragStartMode.NONE);
            }
        }
    }

    /**
     * If user is allowed to reorder components by dragging them
     * @return true if allowed, false if not
     */
    public boolean isReorderable() {
        return allowReorder;
    }

    /**
     * Remove all components from layout
     */
    public void removeAllComponentsFromLayout() {
        getMasonryLayout().removeAllComponents();
    }

    /**
     * Add reorder listener that will be called when user has reordered components
     * @param listener Listener added
     */
    public void addMasonryReorderListener(DnDMasonryReorderListener listener) {
        reorderListeners.add(listener);
    }

    /**
     * Remove reorder listener that will be called when user has reordered components
     * @param listener Listener removed
     */
    public void removeMasonryReorderListener(DnDMasonryReorderListener listener) {
        reorderListeners.remove(listener);
    }

    /**
     * Notify all listeners that user has changed the order of components
     */
    protected void notifyReorderListeners() {
        DnDMasonryReorderEvent event = new DnDMasonryReorderEvent(this);
        //TODO: add more data to event

        for(DnDMasonryReorderListener listener : reorderListeners) {
            listener.onUserReorder(event);
        }
    }

    /**
     * Get column width used with this masonry layout
     * @return Column width in pixels
     */
    public int getColumnWidth() {
        return getMasonryLayout().getColumnWidth();
    }

    /**
     * Adds style name to internal layout
     * @param styleName Style name added
     */
    public void addStyleNameToLayout(String styleName) {
        getMasonryLayout().addStyleName(styleName);
    }

    /**
     * Removes style name from internal layout
     * @param styleName Style name removed
     */
    public void removeStyleNameFromLayout(String styleName) {
        getMasonryLayout().removeStyleName(styleName);
    }

    /**
     * Define if client side should automatically relayout when images are loaded. Use this when you images with
     * undefined heights in your layouts.
     * @param relayout if true client side will relayout automatically when images loaded
     */
    public void setAutomaticLayoutWhenImagesLoaded(boolean relayout) {
        getMasonryLayout().setAutomaticLayoutWhenImagesLoaded(relayout);
    }

    /**
     * Check if client side is hooked to relayout when images are loaded.
     * @return true if client side will automatically relayout when images loaded.
     */
    public boolean isAutomaticLayoutWhenImagesLoaded() {
        return getMasonryLayout().isAutomaticLayoutWhenImagesLoaded();
    }
}
