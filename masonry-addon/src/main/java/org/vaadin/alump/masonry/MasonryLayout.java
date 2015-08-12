/**
 * MasonryLayout.java (Masonry)
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

import com.vaadin.annotations.JavaScript;
import com.vaadin.event.LayoutEvents;
import com.vaadin.shared.Connector;
import com.vaadin.shared.EventId;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Component;
import org.vaadin.alump.masonry.client.MasonryLayoutClientRpc;
import org.vaadin.alump.masonry.client.MasonryLayoutServerRpc;
import org.vaadin.alump.masonry.client.MasonryLayoutState;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Layout that uses Masonry JavaScript library to layout components
 *
 * Masonry by David DeSandro: http://masonry.desandro.com/
 */
@JavaScript({ "masonry.pkgd.min.js" })
public class MasonryLayout extends AbstractLayout implements LayoutEvents.LayoutClickNotifier {

    /**
     * Wrapper style name for components added. Will tell wrapper to take double width.
     */
    public static final String DOUBLE_WIDE_STYLENAME = "masonry-double-wide";

    /**
     * Wrapper style name for components added. Will tell wrapper to take triple width.
     */
    public static final String TRIPLE_WIDE_STYLENAME = "masonry-triple-wide";

    /**
     * Wrapper style name for components added. Will tell wrapper to take quadruple width.
     */
    public static final String QUADRUPLE_WIDE_STYLENAME = "masonry-quadruple-wide";

    /**
     * Add this style name to MasonryLayout component to get nicer paper shadow effect. This is more complex CSS that
     * affects to multiple layers of DOM tree component creates.
     */
    public static final String MASONRY_PAPER_SHADOW_STYLENAME = "masonry-paper-shadow";

    private boolean initialClientResponseSent = false;

    protected List<Component> components = new ArrayList<Component>();

    private final MasonryLayoutServerRpc serverRpc = new MasonryLayoutServerRpc() {

        @Override
        public void layoutClick(MouseEventDetails mouseDetails, Connector clickedConnector) {
            fireEvent(LayoutEvents.LayoutClickEvent.createEvent(MasonryLayout.this,
                    mouseDetails, clickedConnector));
        }
    };

    /**
     * Create masonry layout with default column width (300px)
     */
    public MasonryLayout() {
        super();
        this.registerRpc(serverRpc, MasonryLayoutServerRpc.class);
    }

    /**
     * Create new masonry layout with defined column width
     * @param columnWidth Column width used to calculate left positions of items. Can not be changed after start.
     *                    Remember to update horizontal values of CSS rules to match with this.
     */
	public MasonryLayout(int columnWidth) {
        this();
        setColumnWidth(columnWidth);
	}

	// We must override getState() to cast the state to MyComponentState
	@Override
	protected MasonryLayoutState getState() {
		return (MasonryLayoutState) super.getState();
	}

    @Override
    public void replaceComponent(Component oldComponent, Component newComponent) {

        if(oldComponent.getParent() != this) {
            throw new IllegalArgumentException("Old component has different parent");
        }

        if(oldComponent == newComponent) {
            return;
        }

        if(newComponent.getParent() == this) {
            removeComponent(newComponent);
        }

        int oldIndex = components.indexOf(oldComponent);
        removeComponent(oldComponent);
        addComponent(newComponent, oldIndex);
    }

    @Override
    public void addComponent(Component component) {
        addComponent(component, getComponentCount());
    }

    /**
     * Add component to given index
     * @param component Component added§
     * @param index Index where component is added
     */
    public void addComponent(Component component, int index) {

        if(component.getParent() == this) {
            int currentIndex = components.indexOf(component);
            if (currentIndex == index) {
                return;
            }
            removeComponent(component);
            if(index < components.size()) {
                components.add(index, component);
            } else {
                components.add(component);
            }
        } else {
            if(index < components.size()) {
                components.add(index, component);
            } else {
                components.add(component);
            }
        }

        try {
            super.addComponent(component);
            markAsDirty();
        } catch (IllegalArgumentException e) {
            components.remove(component);
            throw e;
        }
    }

    /**
     * Get child component at given index
     * @param index Index number of child
     * @return Child at index
     * @throws java.lang.IndexOutOfBoundsException When given index is invalid
     */
    public Component getComponent(int index) {
        if(index < 0 || index >= components.size()) {
            throw new IndexOutOfBoundsException("Given index (" + index + ") is out of bounds (0.."
                    + (components.size() - 1) + ")");
        }

        return components.get(index);
    }

    /**
     * Add component with style name added to wrapping element
     * @param component Component added
     * @param wrapperStyleName Style name added to wrapping element, or null if no extra style names. Can be eq. used
     *                         to have double wide items.
     */
    public void addComponent(Component component, String wrapperStyleName) {
        addComponent(component, wrapperStyleName, getComponentCount());
    }

    /**
     * Add component with style name added to wrapping element to given index
     * @param component Component added
     * @param wrapperStyleName Style name added to wrapping element, or null if no extra style names. Can be eq. used to have
     *                    double wide items. To update this you need to re add component.
     * @param index Index where component is added
     */
    public void addComponent(Component component, String wrapperStyleName, int index) {
        // Correct index if already child of this layout
        int indexCorrection = 0;
        if(component.getParent() == this) {
            if(components.indexOf(component) < index) {
                indexCorrection = -1;
            }
        }

        addComponent(component, index + indexCorrection);

        if(wrapperStyleName != null) {
            getState().itemStyleNames.put(component, wrapperStyleName);
        } else {
            getState().itemStyleNames.remove(component);
        }
    }

    /**
     * Add component to first position
     * @param added Component added
     * @param wrapperStyleName Wrapper style name of component
     */
    public void addComponentFirst(Component added, String wrapperStyleName) {
        addComponent(added, wrapperStyleName, 0);
    }

    /**
     * Add component before given component
     * @param added Component added
     * @param wrapperStyleName Wrapper style name of component
     * @param before Component will be added before this component
     */
    public void addComponentBefore(Component added, String wrapperStyleName, Component before) {
        if(before.getParent() != this) {
            throw new IllegalArgumentException("Given target component is not child of this MasonryLayout");
        }
        if(added == before) {
            return;
        }
        addComponent(added, wrapperStyleName, components.indexOf(before));
    }

    /**
     * Add component after given component
     * @param added Component added
     * @param wrapperStyleName Wrapper stylename of component
     * @param after Component will be added after this component
     */
    public void addComponentAfter(Component added, String wrapperStyleName, Component after) {
        if(after.getParent() != this) {
            throw new IllegalArgumentException("Given target component is not child of this MasonryLayout");
        }
        if(added == after) {
            return;
        }
        addComponent(added, wrapperStyleName, components.indexOf(after) + 1);
    }

    /**
     * Get optional wrapper style name of component
     * @param component Component added earlier
     * @return Wrapper style name given for component, null if not defined
     */
    public String getComponentWrapperStyleName(Component component) {
        if(component.getParent() != this) {
            throw new IllegalArgumentException("Given component is not child of this MasonryLayout");
        }
        return getState().itemStyleNames.get(component);
    }

    /**
     * Update component's wrappers style name.
     * @param childComponent Child component of this MasonryLayout
     * @param wrapperStyleName New style name of wrapper
     * @throws IllegalArgumentException If given component is not child of this MasonryLayout
     */
    public void updateComponentWrapperStyleName(Component childComponent, String wrapperStyleName) {
        if(childComponent.getParent() != this) {
            throw new IllegalArgumentException("Given component is not child of this MasonryLayout");
        }
        getState().itemStyleNames.put(childComponent, wrapperStyleName);
    }

    @Override
    public void removeComponent(Component component) {

        getState().itemStyleNames.remove(component);

        if(components.remove(component)) {
            super.removeComponent(component);
            markAsDirty();
        }
    }

    @Override
    public int getComponentCount() {
        return components.size();
    }

    @Override
    public Iterator<Component> iterator() {
        return components.iterator();
    }

    /**
     * Request client side to re-layout. Useful if component sizes have changed.
     */
    public void requestLayout() {
        getRpcProxy(MasonryLayoutClientRpc.class).layout();
    }

    /**
     * Get column width used with this masonry layout
     * @return Column width in pixels
     */
    public int getColumnWidth() {
        return getState().columnWidth;
    }

    /**
     * Set column width used in this masonry layout. Can be only set before initial client response is sent.
     * @param columnWidth Width of column in pixels
     * @throws java.lang.IllegalStateException If initial client response has been already sent
     */
    public void setColumnWidth(int columnWidth) {
        if(initialClientResponseSent) {
            throw new IllegalStateException("Transition time can not be changed after it has been rendered to client");
        }
        getState().columnWidth = columnWidth;
    }

    @Override
    public void addLayoutClickListener(LayoutEvents.LayoutClickListener listener) {
        addListener(EventId.LAYOUT_CLICK_EVENT_IDENTIFIER,
                LayoutEvents.LayoutClickEvent.class, listener,
                LayoutEvents.LayoutClickListener.clickMethod);
    }

    @Override
    public void addListener(LayoutEvents.LayoutClickListener layoutClickListener) {
        addLayoutClickListener(layoutClickListener);
    }

    @Override
    public void removeLayoutClickListener(LayoutEvents.LayoutClickListener listener) {
        removeListener(EventId.LAYOUT_CLICK_EVENT_IDENTIFIER,
                LayoutEvents.LayoutClickEvent.class, listener);
    }

    @Override
    public void removeListener(LayoutEvents.LayoutClickListener layoutClickListener) {
        removeLayoutClickListener(layoutClickListener);
    }

    /**
     * Define if client side should automatically relayout when images are loaded. Use this when you images with
     * undefined heights in your layouts.
     * @param relayout if true client side will relayout automatically when images loaded
     */
    public void setAutomaticLayoutWhenImagesLoaded(boolean relayout) {
        if(relayout) {
            ImagesLoadedExtension.createExtension(this);
        } else {
            ImagesLoadedExtension.removeExtension(this);
        }
    }

    /**
     * Check if client side is hooked to relayout when images are loaded.
     * @return true if client side will automatically relayout when images loaded.
     */
    public boolean isAutomaticLayoutWhenImagesLoaded() {
        return (ImagesLoadedExtension.getExtension(this) != null);
    }

    /**
     * Get transition duration
     * @return Duration as CSS time value (eg. "0.4s")
     */
    public String getTransitionDuration() {
        return getState().transitionDuration;
    }

    /**
     * Define transition duration. Can be only changed before component is rendered to client side.
     * @param time Time is CSS time value format (eg. "0.4s")
     * @throws java.lang.IllegalStateException If initial client response is already sent
     */
    public void setTransitionDuration(String time) {
        if(time == null) {
            throw new IllegalArgumentException("Time can not be null");
        }
        if(initialClientResponseSent) {
            throw new IllegalStateException("Transition time can not be changed after it has been rendered to client");
        }
        getState().transitionDuration = time;
    }

    @Override
    public void beforeClientResponse(boolean initial) {
        super.beforeClientResponse(initial);

        if(initial) {
            initialClientResponseSent = true;
        }
    }

    @Override
    public void detach() {
        initialClientResponseSent = false;
        super.detach();
    }

    /**
     * Get index of given child component
     * @param component Child component of layout
     * @return Index of child component, or -1 if not found
     */
    public int getComponentIndex(Component component) {
        return components.indexOf(component);
    }

    /**
     * Get child components order list
     * @return Order list of child components
     */
    public List<Component> getComponents() {
        return new ArrayList<Component>(components);
    }
}
