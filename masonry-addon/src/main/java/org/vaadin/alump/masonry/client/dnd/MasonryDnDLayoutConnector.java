package org.vaadin.alump.masonry.client.dnd;

import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.shared.ui.Connect;
import org.vaadin.alump.masonry.MasonryDnDLayout;
import org.vaadin.alump.masonry.client.MasonryLayoutConnector;

/**
 * Drag and drop enabled version of MasonryLayoutConnector
 */
@Connect(MasonryDnDLayout.class)
public class MasonryDnDLayoutConnector extends MasonryLayoutConnector {

    protected void init() {
        super.init();

        getWidget().setClientMasonryReorderHandler(reOrderListener);
    }

    public void onStateChanged(StateChangeEvent event) {
        super.onStateChanged(event);

        getWidget().setDraggingAllowed(getState().draggingAllowed);
    }

    @Override
    public MasonryDnDPanel getWidget() {
        return (MasonryDnDPanel)super.getWidget();
    }

    @Override
    public MasonryDnDLayoutState getState() {
        return (MasonryDnDLayoutState)super.getState();
    }

    protected ClientMasonryReorderHandler reOrderListener = new ClientMasonryReorderHandler() {

        @Override
        public void onReorder(Widget movedWidget, int newIndex) {
            getRpcProxy(MasonryDndServerRpc.class).onReorder(resolveChild(movedWidget), newIndex);
        }

        @Override
        public void onReConstruct() {
            updateWidget(false);
        }
    };

    protected ComponentConnector resolveChild(Widget widget) {
        for(ComponentConnector child : this.getChildComponents()) {
            if(child.getWidget() == widget) {
                return child;
            }
        }
        return null;
    }
}
