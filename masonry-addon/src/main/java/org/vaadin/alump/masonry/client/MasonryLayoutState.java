package org.vaadin.alump.masonry.client;

import com.vaadin.shared.Connector;
import com.vaadin.shared.ui.AbstractLayoutState;

import java.util.HashMap;
import java.util.Map;

public class MasonryLayoutState extends AbstractLayoutState {

    {
        primaryStyleName = "masonry-layout";
    }


    public int columnWidth = 300;

    public String transitionDuration = "0.4s";

    /**
     * Addtional stylenames for items (usually related to sizing)
     */
    public Map<Connector,String> itemStyleNames = new HashMap<Connector,String>();
}