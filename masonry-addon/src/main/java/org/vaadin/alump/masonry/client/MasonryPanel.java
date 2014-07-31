/**
 * MasonryPanel.java (Masonry)
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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.HashMap;
import java.util.Map;

/**
 * GWT implementation of MasonryPanel. Expects that masonry JavaScript code has been loaded
 * and run.
 */
public class MasonryPanel extends ComplexPanel {

    private final Map<Element, Widget> elementMap = new HashMap<Element,Widget>();
    private boolean masonryInitialized = false;
    private JavaScriptObject msnry = null;

    public static final String ITEM_CLASSNAME = "masonry-item";

    /**
     * Class name added while js library is performing layout
     */
    public static final String RENDERING_CLASSNAME = "masonry-rendering";

	public MasonryPanel() {

        setElement(Document.get().createDivElement());
	}

    public void initialize(int columnWidth) {
        if(msnry != null) {
            return;
        }
        msnry = initializeMasonry(getElement(), createMasonryProperties(columnWidth).getJavaScriptObject());
    }

    public void setVisible(boolean visible) {
        boolean wasVisible = this.isVisible();

        super.setVisible(visible);


        if(wasVisible == false && visible == true) {
            layout();
        }
    }

    public void onDetach() {
        if(msnry != null) {
            nativeDestroy(msnry);
            msnry = null;
        }

        super.onDetach();
    }

    public void addItem(Widget widget, String styleName) {
        Element item = Document.get().createDivElement();
        item.addClassName(ITEM_CLASSNAME);
        if(styleName != null) {
            item.addClassName(styleName);
        }
        elementMap.put(item, widget);
        getElement().appendChild(item);
        nativeAddItem(msnry, item);
        super.add(widget, (com.google.gwt.user.client.Element) item);
    }

    public void removeItem(Widget widget) {
        Element item = widget.getElement().getParentElement();
        elementMap.remove(item);

        if(widget.getParent() == this) {
            super.remove(widget);
        }

        nativeRemoveItem(msnry, item);
        item.removeFromParent();
    }

    public void layout() {
        if(isVisible() && isAttached()) {
            addStyleName(RENDERING_CLASSNAME);
            nativeLayout(msnry);
        }
    }

    protected static native  void nativeLayout(JavaScriptObject msnry)
    /*-{
        msnry.layout();
    }-*/;

    protected static native void nativeAddItem(JavaScriptObject msnry, Element itemElement)
    /*-{
        msnry.addItems(itemElement);
    }-*/;

    protected static native void nativeRemoveItem(JavaScriptObject msnry, Element itemElement)
    /*-{
        msnry.remove(itemElement);
    }-*/;

    protected static native void nativeDestroy(JavaScriptObject msnry)
    /*-{
        msnry.destroy();
    }-*/;

    protected static JSONObject createMasonryProperties(int columnWidth) {
        JSONObject obj = new JSONObject();
        obj.put("columnWidth", new JSONNumber(columnWidth));
        obj.put("itemSelector", new JSONString("."  + ITEM_CLASSNAME));
        return obj;
    }

    protected native JavaScriptObject initializeMasonry(Element element, JavaScriptObject properties)
    /*-{
        var that = this;
        var msnry = new $wnd.Masonry(element, properties);
        msnry.on('layoutComplete', function( msnryInstance, laidOutItems ) {
            that.@org.vaadin.alump.masonry.client.MasonryPanel::onLayoutComplete()();
        });
        return msnry;
    }-*/;

    /**
     * Called when JavaScript library has finished the layout processing and transitions
     */
    protected void onLayoutComplete() {
        removeStyleName(RENDERING_CLASSNAME);
    }

}