package org.vaadin.alump.masonry.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.ConnectorHierarchyChangeEvent.ConnectorHierarchyChangeHandler;
import com.vaadin.client.HasComponentsConnector;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;

import java.util.logging.Logger;

/**
 * Connector for ImagesLoaded extension
 */
@Connect(org.vaadin.alump.masonry.ImagesLoadedExtension.class)
public class ImagesLoadedConnector extends AbstractExtensionConnector {

    private final static Logger LOGGER = Logger.getLogger(ImagesLoadedConnector.class.getName());

    protected ImagesLoadedClientListener listener;
    protected HandlerRegistration hierarchyChangeHandler;

    public ImagesLoadedState getState() {
        return (ImagesLoadedState) super.getState();
    }

    @Override
    protected void extend(ServerConnector target) {
        if (!(target instanceof AbstractComponentConnector)) {
            LOGGER.severe("Can not extend non component connector");
            return;
        }

        // Remember connector only it implements our interface
        if (target instanceof ImagesLoadedClientListener) {
            listener = (ImagesLoadedClientListener) target;
        }

        final AbstractComponentConnector cc = (AbstractComponentConnector) target;

        // If connector contains containers, run images loaded script when hierarchy changes
        if (cc instanceof HasComponentsConnector) {
            HasComponentsConnector hcc = (HasComponentsConnector) cc;
            hierarchyChangeHandler = hcc.addConnectorHierarchyChangeHandler(
                new ConnectorHierarchyChangeHandler() {

                    @Override
                    public void onConnectorHierarchyChange(ConnectorHierarchyChangeEvent connectorHierarchyChangeEvent) {
                        // Schedule delay, to perform script after state updates
                        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                            @Override
                            public void execute() {
                                runImagesLoaded(cc.getWidget().getElement());
                            }
                        });
                    }
                });
        }

        // Attach imagesLoaded
        runImagesLoaded(cc.getWidget().getElement());
    }

    public void onUnregister() {
        if(hierarchyChangeHandler != null) {
            hierarchyChangeHandler.removeHandler();
            hierarchyChangeHandler = null;
        }
        super.onUnregister();
    }

    /**
     * Will ask JavaScript library to run images loaded check for element
     * @param element Parent element with img children
     */
    public final native void runImagesLoaded(JavaScriptObject element)
    /*-{
        var that = this;
        $wnd.imagesLoaded(element, function () {
            that.@org.vaadin.alump.masonry.client.ImagesLoadedConnector::onImagesLoaded()();
        });
    }-*/;

    /**
     * Callback called from native attachment to imagesLoaded library
     */
    private void onImagesLoaded() {
        listener.onImagesLoaded();
        if (getState().callServer) {
            getRpcProxy(ImagesLoadedServerRpc.class).onImagesLoaded();
        }
    }
}
