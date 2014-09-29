package org.vaadin.alump.masonry.demo;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.ui.*;

/**
 * Created by alump on 31/07/14.
 */
public abstract class AbstractTestView extends VerticalLayout implements View {

    protected String title;
    protected HorizontalLayout buttonLayout;
    protected Panel masonryPanel;

    protected AbstractTestView(String title) {
        this.title = title;
        setSizeFull();

        buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(true);
        addComponent(buttonLayout);

        addButton("←", "Back to menu", new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                UI.getCurrent().getNavigator().navigateTo(MainMenuView.VIEW_NAME);
            }
        });

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

    protected void addToButtonLayout(Component component) {
        buttonLayout.addComponent(component);
    }

    protected void setPanelContent(Component component) {
        masonryPanel.setContent(component);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        Page.getCurrent().setTitle(title);
    }
}
