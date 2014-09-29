/**
 * MasonryLayoutConnector.java (Masonry)
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

package org.vaadin.alump.masonry.client;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.Util;
import com.vaadin.client.ui.AbstractLayoutConnector;

import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.LayoutClickEventHandler;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.LayoutClickRpc;
import org.vaadin.alump.masonry.MasonryLayout;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Connector binds client-side widget class to server-side component class
// Connector lives in the client and the @Connect annotation specifies the
// corresponding server-side component
@Connect(MasonryLayout.class)
public class MasonryLayoutConnector extends AbstractLayoutConnector implements ImagesLoadedClientListener {

    private final MasonryLayoutClientRpc clientRpc = new MasonryLayoutClientRpc() {
        @Override
        public void layout() {
            scheduleLayout();
        }
    };

	public MasonryLayoutConnector() {
        registerRpc(MasonryLayoutClientRpc.class, clientRpc);
	}
	
	// We must implement getWidget() to cast to correct type
	@Override
	public MasonryPanel getWidget() {
		return (MasonryPanel) super.getWidget();
	}

	// We must implement getState() to cast to correct type
	@Override
	public MasonryLayoutState getState() {
		return (MasonryLayoutState) super.getState();
	}


    private final LayoutClickEventHandler clickEventHandler = new LayoutClickEventHandler(
            this) {

        @Override
        protected ComponentConnector getChildComponent(Element element) {
            return Util.getConnectorForElement(getConnection(), getWidget(),
                    element);
        }

        @Override
        protected LayoutClickRpc getLayoutClickRPC() {
            return getRpcProxy(MasonryLayoutServerRpc.class);
        };
    };

	// Whenever the state changes in the server-side, this method is called
	@Override
	public void onStateChanged(StateChangeEvent stateChangeEvent) {
		super.onStateChanged(stateChangeEvent);
        clickEventHandler.handleEventHandlerRegistration();

        // call always, will be ignored after first time
        getWidget().initialize(getState().columnWidth);

	}

    @Override
    public void onConnectorHierarchyChange(ConnectorHierarchyChangeEvent event) {

        getWidget().initialize(getState().columnWidth);

        for (ComponentConnector child : event.getOldChildren()) {
            if (child.getParent() != this) {
                Widget widget = child.getWidget();
                if (widget.isAttached()) {
                    getWidget().removeItem(widget);
                }
            }
        }

        int reconstructFrom = findFirstMismatchInChildren(getChildComponents());

        // Clean reordered widgets
        if(reconstructFrom < getWidget().getWidgetCount()) {
            Set<Widget> removeWidgets = new HashSet<Widget>();
            for(int i = reconstructFrom; i < getWidget().getWidgetCount(); ++i) {
                removeWidgets.add(getWidget().getWidget(i));
            }
            for(Widget remove : removeWidgets) {
                getWidget().removeItem(remove);
            }
        }

        // Add new or reordered children
        for(int i = reconstructFrom; i < getChildComponents().size(); ++i) {
            ComponentConnector cc = getChildComponents().get(i);
            getWidget().addItem(cc.getWidget(), getState().itemStyleNames.get(cc));
        }

        scheduleLayout();
    }

    /**
     * Find from where children has to be reconstructed
     * @param children Current list of children
     * @return
     */
    protected int findFirstMismatchInChildren(List<ComponentConnector> children) {
        int lastSame = 0;
        int oldWidgetCount = getWidget().getWidgetCount();

        for(lastSame = 0; lastSame < oldWidgetCount && lastSame < children.size(); ++lastSame) {
            if(getWidget().getWidget(lastSame) != children.get(lastSame).getWidget()) {
               break;
            }
        }
        return lastSame;
    }

    @Override
    public void updateCaption(ComponentConnector componentConnector) {
        // ignore for now
    }

    @Override
    public void onUnregister() {
        relayoutTimer.cancel();
        super.onUnregister();
    }

    @Override
    public void onImagesLoaded() {
        getWidget().layout();
    }

    /**
     * Schedule layout call(s) to widget
     */
    protected void scheduleLayout() {
        relayoutTimer.cancel();
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                getWidget().layout();
                relayoutTimer.schedule();
            }
        });
    }

    // As Vaadin is performing layouts with delay this timer is here to make sure layout is recalculated few times
    // after each request
    private RelayoutTimer relayoutTimer = new RelayoutTimer();


    private class RelayoutTimer extends Timer {

        public final static int LOOP_TIME_MS = 100;

        private int loopsLeft = 0;

        public void schedule() {
            if(loopsLeft > 0) {
                loopsLeft += 3;
            } else {
                loopsLeft = 3;
                this.schedule(LOOP_TIME_MS);
            }
        }

        public void cancel() {
            loopsLeft = 0;
            super.cancel();
        }

        @Override
        public void run() {
            getWidget().layout();
            if(--loopsLeft < 1) {
                loopsLeft = 0;
            } else {
                this.schedule(LOOP_TIME_MS);
            }
        }
    };
}
