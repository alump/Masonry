package org.vaadin.alump.masonry.client.dnd;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.WidgetCollection;
import com.vaadin.client.ui.dd.VHtml5DragEvent;
import org.vaadin.alump.masonry.client.MasonryPanel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Adds HTML5 Drag and Drop features to MasonryPanel
 */
public class MasonryDnDPanel extends MasonryPanel {

    private boolean allowDragging = false;

    protected static int panelIdCounter = 0;

    protected final int panelId = ++panelIdCounter;

    private int wrapperIdCounter = 0;

    public final static String DND_DRAGGED_STYLENAME = "masonry-dnd-dragged";

    private String currentDraggedWrapper = null;

    private final static Logger LOGGER = Logger.getLogger(MasonryDnDPanel.class.getName());

    private ClientMasonryReorderHandler reOrderHandler = null;

    private boolean hasBeenReOrdered = false;

    private int positionToMovedLast = -1;

    @Override
    protected Element createComponentWrapper(String styleName, String id) {
        Element wrapper = super.createComponentWrapper(styleName, id);
        if(allowDragging) {
            wrapper.setAttribute("draggable", "true");
        }
        if(wrapper.getId() == null || wrapper.getId().isEmpty()) {
            wrapper.setId(generateWrapperId());
        }
        hookHtml5DragStart(wrapper, wrapper.getId());
        hookHtml5DropEvents(wrapper, wrapper.getId());
        return wrapper;
    }

    public void setDraggingAllowed(boolean allowed) {
        if(allowDragging == allowed) {
            return;
        }

        allowDragging = allowed;
        for(int i = 0; i < this.getWidgetCount(); ++i) {
            Widget child = getWidget(i);
            Element wrapper = child.getElement().getParentElement();
            if(allowDragging) {
                wrapper.setAttribute("draggable", "true");
            } else {
                wrapper.removeAttribute("draggable");
            }
        }

        if(!isRendering() && isRendered()) {
            layout();
        }
    }

    protected String generateWrapperId() {
        return "masonrywrapper-" + panelId + "-" + (++wrapperIdCounter);
    }

    /**
     * Resolve wrapper if of given element
     * @param element Element read
     * @return ID number of element
     * @throws java.lang.IllegalArgumentException If given element is not identified as child of this widget
     */
    protected int getWrapperId(Element element) {
        String id = element.getId();
        if(id == null || id.isEmpty()) {
            throw new IllegalArgumentException("No ID defined in wrapper");
        }
        String parts[] = id.split("-");
        if(parts.length != 3) {
            throw new IllegalArgumentException("Unknown id: '" + id + "'");
        }

        try {
            if (Integer.valueOf(parts[1]).intValue() != panelId) {
                throw new IllegalArgumentException("Wrapper '" + panelId + "' is not child of this panel");
            }

            return Integer.valueOf(parts[2]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid wrapper ID: '" + id + "'");
        }
    }

    protected native void hookHtml5DragStart(Element el, String wrapperId)
    /*-{
        var me = this;
        el.addEventListener("dragstart",  $entry(function(ev) {
            return me.@org.vaadin.alump.masonry.client.dnd.MasonryDnDPanel::html5DragStart(Lcom/vaadin/client/ui/dd/VHtml5DragEvent;Ljava/lang/String;)(ev, wrapperId);
        }), false);
        el.addEventListener("dragend",  $entry(function(ev) {
            return me.@org.vaadin.alump.masonry.client.dnd.MasonryDnDPanel::html5DragEnd(Lcom/vaadin/client/ui/dd/VHtml5DragEvent;Ljava/lang/String;)(ev, wrapperId);
        }), false);
    }-*/;

    protected native void hookHtml5DropEvents(Element el, String wrapperId)
    /*-{
        var me = this;

        el.addEventListener("dragenter",  $entry(function(ev) {
            return me.@org.vaadin.alump.masonry.client.dnd.MasonryDnDPanel::html5DragEnter(Lcom/vaadin/client/ui/dd/VHtml5DragEvent;Ljava/lang/String;)(ev, wrapperId);
        }), false);

        el.addEventListener("dragleave",  $entry(function(ev) {
            return me.@org.vaadin.alump.masonry.client.dnd.MasonryDnDPanel::html5DragLeave(Lcom/vaadin/client/ui/dd/VHtml5DragEvent;Ljava/lang/String;)(ev, wrapperId);
        }), false);

        el.addEventListener("dragover",  $entry(function(ev) {
            return me.@org.vaadin.alump.masonry.client.dnd.MasonryDnDPanel::html5DragOver(Lcom/vaadin/client/ui/dd/VHtml5DragEvent;Ljava/lang/String;)(ev, wrapperId);
        }), false);

        el.addEventListener("drop",  $entry(function(ev) {
            return me.@org.vaadin.alump.masonry.client.dnd.MasonryDnDPanel::html5DragDrop(Lcom/vaadin/client/ui/dd/VHtml5DragEvent;Ljava/lang/String;)(ev, wrapperId);
        }), false);
    }-*/;

    protected boolean html5DragStart(VHtml5DragEvent event, String sourceId) {
        if(isRendering()) {
            LOGGER.severe("prevent drag start when rendering");
            return true;
        }

        hasBeenReOrdered = false;

        event.setEffectAllowed("move");
        setDraggedWrapper(getWrapper(sourceId));
        return false;
    }

    protected boolean html5DragEnd(VHtml5DragEvent event, String sourceId) {
        setDraggedWrapper(null);

        if(hasBeenReOrdered) {
            hasBeenReOrdered = false;
            if(reOrderHandler != null) {
                reOrderHandler.onReConstruct();
            } else {
                LOGGER.warning("No re-order handler defined");
            }
        }
        return false;
    }

    protected void setDraggedWrapper(Element wrapper) {
        if(currentDraggedWrapper != null) {
            Element element = getWrapper(currentDraggedWrapper);
            if(element != null) {
                element.removeClassName(DND_DRAGGED_STYLENAME);
            }
            currentDraggedWrapper = null;
        }


        if(wrapper != null) {
            currentDraggedWrapper = wrapper.getId();
            wrapper.addClassName(DND_DRAGGED_STYLENAME);
        }
    }

    protected boolean html5DragEnter(VHtml5DragEvent event, String sourceId) {
        if(!acceptDrag(event, sourceId)) {
            return true;
        }

        event.preventDefault();
        return false;
    }

    protected boolean html5DragLeave(VHtml5DragEvent event, String sourceId) {
        if(!acceptDrag(event, sourceId)) {
            return true;
        }

        event.preventDefault();
        return false;
    }

    protected boolean html5DragOver(VHtml5DragEvent event, String sourceId) {
        if(acceptDrag(event, sourceId)) {
            if (!isRendering()) {
                upgradeDropIndicator(getWrapper(sourceId), currentDraggedWrapper);
            }
        }

        event.preventDefault();
        return false;
    }

    protected void upgradeDropIndicator(Element target, String dragged) {
        WidgetCollection children = getChildren();
        List<Widget> reOrdered = new ArrayList<Widget>();
        Map<Widget,String> idMap = new HashMap<Widget,String>();
        //TODO: stylenames?

        boolean draggedSkipped = false;
        for(int i = 0; i < children.size(); ++i) {
            Widget child = children.get(i);
            Element wrapper = child.getElement().getParentElement();
            String childId = wrapper.getId();
            if(childId.equals(dragged)) {
                // skip
                draggedSkipped = true;
                continue;
            } else if(wrapper == target) {
                if(draggedSkipped) {
                    positionToMovedLast = i + 1;
                    Widget draggedWidget = getChildInWrapper(dragged);
                    reOrdered.add(child);
                    idMap.put(child, childId);
                    reOrdered.add(draggedWidget);
                    idMap.put(draggedWidget, dragged);
                } else {
                    positionToMovedLast = i;
                    Widget draggedWidget = getChildInWrapper(dragged);
                    reOrdered.add(draggedWidget);
                    idMap.put(draggedWidget, dragged);
                    reOrdered.add(child);
                    idMap.put(child, childId);
                }
            } else {
                reOrdered.add(child);
                idMap.put(child, childId);
            }
        }

        removeAllItems();
        for(Widget newChild : reOrdered) {
            addItem(newChild, null, idMap.get(newChild));
        }

        hasBeenReOrdered = true;
        layout();
    }

    protected boolean html5DragDrop(VHtml5DragEvent event, String sourceId) {
        if(!hasBeenReOrdered) {
            return true;
        }

        Widget dragged = getWidget(currentDraggedWrapper);
        Widget target = getWidget(sourceId);
        hasBeenReOrdered = false;

        setDraggedWrapper(null);

        if(reOrderHandler != null) {
            //LOGGER.severe("drop #3a");
            reOrderHandler.onReorder(dragged, positionToMovedLast);
        } else {
            LOGGER.warning("No reorder handler defined");
        }

        event.preventDefault();
        return false;
    }

    protected boolean acceptDrag(VHtml5DragEvent event, String target) {
        String wrapperId = currentDraggedWrapper;
        return wrapperId != null && !wrapperId.isEmpty() && !target.equals(wrapperId);
    }

    /**
     * Get widget inside given wrapper id
     * @param id Wrapper id of widget
     * @return Widget inside wrapper with given id, or null if not found
     */
    protected Widget getWidget(String id) {
        for(int i = 0; i < getWidgetCount(); ++i) {
            Widget child = getWidget(i);
            if(id.equals(child.getElement().getParentElement().getId())) {
                return child;
            }
        }
        return null;
    }

    /**
     * Get wrapper element with given id
     * @param id Wrapper id
     * @return Wrapper element, or null if not found
     */
    protected Element getWrapper(String id) {
        Widget child = getWidget(id);
        if(child != null) {
            return child.getElement().getParentElement();
        }
        return null;
    }

    public boolean isDragged() {
        return currentDraggedWrapper != null;
    }

    public Widget getChildInWrapper(String wrapperId) {
        for(int i = 0; i < getWidgetCount(); ++i) {
            if(getWidget(i).getElement().getParentElement().getId().equals(wrapperId)) {
               return getWidget(i);
            }
        }
        return null;
    }

    public void setClientMasonryReorderHandler(ClientMasonryReorderHandler handler) {
        reOrderHandler = handler;
    }

    protected List<Widget> getChildWidgetsList() {
        List<Widget> children = new ArrayList<Widget>();
        WidgetCollection collection = getChildren();
        for(int i = 0; i < collection.size(); ++i) {
            children.add(collection.get(i));
        }
        return children;
    }
}
