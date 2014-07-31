package org.vaadin.alump.masonry.demo;

import com.vaadin.data.Property;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.ui.*;
import org.vaadin.alump.masonry.DnDMasonryLayout;
import org.vaadin.alump.masonry.MasonryLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Test case for Drag'n drop reordering features
 */
public class DnDTestsView extends VerticalLayout implements View {

    public final static String VIEW_NAME = DnDTestsView.class.getSimpleName();

    private DnDMasonryLayout layout;

    private int index = 0;

    private Random rand = new Random(0xDEADBEEF);

    private List<Component> itemsAdded = new ArrayList<Component>();

    public DnDTestsView() {

        setSizeFull();

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setMargin(true);
        buttonLayout.setSpacing(true);
        addComponent(buttonLayout);

        Button back = new Button("←", new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                UI.getCurrent().getNavigator().navigateTo(MainMenuView.VIEW_NAME);
            }
        });
        buttonLayout.addComponent(back);

        Button addItem = new Button("Add", new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                createAndAddItem(index++, false);
            }
        });
        buttonLayout.addComponent(addItem);

        Button addDwItem = new Button("Add DW", new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                createAndAddItem(index++, true);
            }
        });
        buttonLayout.addComponent(addDwItem);

        Button removeAll = new Button("Remove all", new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                layout.removeAllComponentsFromLayout();
            }
        });
        buttonLayout.addComponent(removeAll);

        Button reLayout = new Button("Layout", new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                layout.requestLayout();
            }
        });
        buttonLayout.addComponent(reLayout);

        CheckBox disallowReorder = new CheckBox("Disallow reorder");
        disallowReorder.setImmediate(true);
        disallowReorder.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                layout.setReorderable(!((Boolean) event.getProperty().getValue()));
            }
        });
        buttonLayout.addComponent(disallowReorder);

        Panel panel = new Panel();
        panel.addStyleName("masonry-panel");
        panel.setSizeFull();
        addComponent(panel);
        setExpandRatio(panel, 1.0f);

        layout = new DnDMasonryLayout();
        // Tells to use fancier shadows
        layout.addStyleNameToLayout(MasonryLayout.MASONRY_PAPER_SHADOW_STYLENAME);
        layout.addMasonryReorderListener(reorderListener);
        layout.addStyleName("demo-masonry");
        layout.setWidth("100%");
        panel.setContent(layout);

        for(int i = 0; i < 5; ++i) {
            createAndAddItem(index++, false);
        }
    }

    private final DnDMasonryLayout.DnDMasonryReorderListener reorderListener = new DnDMasonryLayout.DnDMasonryReorderListener() {

        @Override
        public void onUserReorder(DnDMasonryLayout.DnDMasonryReorderEvent event) {
            System.out.println("User reordered stuff!");
        }
    };

    private void createAndAddItem(int index, boolean doubleWidth) {
        Component itemLayout = ItemGenerator.createItem(index);

        // Just using data to remember the width, this to help when reordering
        layout.addComponentToLayout(itemLayout, doubleWidth ? MasonryLayout.DOUBLE_WIDE_STYLENAME : null);

        itemsAdded.add(itemLayout);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        Page.getCurrent().setTitle("MasonryLayout DnD Tests");
    }
}
