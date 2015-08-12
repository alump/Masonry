package org.vaadin.alump.masonry.demo;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.ui.*;

/**
 * Created by alump on 31/07/14.
 */
public abstract class AbstractTestView extends VerticalLayout implements View {

    protected String title;
    protected HorizontalLayout buttonLayout;
    protected Panel masonryPanel;
    protected Navigator navigator;

    protected AbstractTestView(String title) {
        this.title = title;
        setSizeFull();

        buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(true);
        addComponent(buttonLayout);

        addButton(FontAwesome.BARS, "Back to menu", clickEvent ->
                UI.getCurrent().getNavigator().navigateTo(MainMenuView.VIEW_NAME));

        masonryPanel = new Panel();
        masonryPanel.addStyleName("masonry-panel");
        masonryPanel.setSizeFull();
        addComponent(masonryPanel);
        setExpandRatio(masonryPanel, 1.0f);
    }

    protected void addButton(String caption, String description, Button.ClickListener clickListener) {
        Button button = new Button(caption, clickListener);
        if(description != null) {
            button.setDescription(description);
        }
        addToButtonLayout(button);
    }

    protected void addButton(Resource icon, String description, Button.ClickListener clickListener) {
        Button button = new Button();
        button.addClickListener(clickListener);
        button.setIcon(icon);
        if(description != null) {
            button.setDescription(description);
        }
        addToButtonLayout(button);
    }

    protected void addToButtonLayout(Component component) {
        buttonLayout.addComponent(component);
    }

    protected void setPanelContent(Component component) {
        masonryPanel.setContent(component);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        this.navigator = event.getNavigator();
        Page.getCurrent().setTitle(title);
    }

    protected Navigator getNavigator() {
        return navigator;
    }
}
