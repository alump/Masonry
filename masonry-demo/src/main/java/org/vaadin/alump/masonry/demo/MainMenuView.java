package org.vaadin.alump.masonry.demo;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Page;
import com.vaadin.ui.*;

/**
 * Menu for selecting correct test
 */
public class MainMenuView extends VerticalLayout implements View {

    public final static String VIEW_NAME = "";

    public MainMenuView() {
        setWidth("100%");
        setMargin(true);
        setSpacing(true);

        VerticalLayout infoLayout = new VerticalLayout();
        infoLayout.setWidth("100%");
        addComponent(infoLayout);

        Label label = new Label("Masonry addon adds cascading grid layout to Vaadin.");
        infoLayout.addComponent(label);

        Link link = new Link("This addon is based on David DeSandro's Masonry and imagesLoaded JavaScript libraries (MIT license).",
                new ExternalResource("http://masonry.desandro.com/"));
        infoLayout.addComponent(link);

        link = new Link("Source code and issue tracker, of this addon, are available in GitHub.",
                new ExternalResource("https://github.com/alump/Masonry"));
        infoLayout.addComponent(link);

        Button basicTests = new Button("Basic Demo", new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                UI.getCurrent().getNavigator().navigateTo(BasicTestsView.VIEW_NAME);
            }
        });
        addComponent(basicTests);

        Button dndTests = new Button("Vaadin Drag'n Drop Demo", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                UI.getCurrent().getNavigator().navigateTo(VaadinDnDTestsView.VIEW_NAME);
            }
        });
        addComponent(dndTests);

        Button dnd2Tests = new Button("HTML5 Drag'n Drop Demo", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                UI.getCurrent().getNavigator().navigateTo(DnDTestsView.VIEW_NAME);
            }
        });
        addComponent(dnd2Tests);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        Page.getCurrent().setTitle("MasonryLayout Demo Menu");
    }

}
