package org.vaadin.alump.masonry.demo;

import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

/**
 * Created by alump on 05/11/14.
 */
public class NotFoundView extends AbstractTestView {
    public NotFoundView() {
        super("View not found!");

        VerticalLayout layout = new VerticalLayout();
        layout.setWidth("100%");
        layout.setMargin(true);
        layout.setSpacing(true);

        Label label = new Label("Something wrong with your URL :(");
        label.addStyleName(Reindeer.LABEL_H1);
        layout.addComponent(label);

        Button menuButton = new Button("Return to main menu", new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                getNavigator().navigateTo(MainMenuView.VIEW_NAME);
            }
        });
        menuButton.addStyleName(Reindeer.BUTTON_LINK);
        layout.addComponent(menuButton);

        setPanelContent(layout);
    }
}
