package org.vaadin.alump.masonry.demo;

import com.vaadin.data.Property;
import com.vaadin.event.LayoutEvents;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.ui.*;
import org.vaadin.alump.masonry.MasonryLayout;

import java.util.*;

/**
 * Base test that covers most of the features
 */
public class BasicTestsView extends VerticalLayout implements View {

    public final static String VIEW_NAME = BasicTestsView.class.getSimpleName();

    private MasonryLayout layout;
    private int index = 0;

    private List<Component> itemsAdded = new ArrayList<Component>();

    private Random rand = new Random(0xDEADBEEF);

    public BasicTestsView() {
        setSizeFull();

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(true);
        addComponent(buttonLayout);

        Button back = new Button("←", new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                UI.getCurrent().getNavigator().navigateTo(MainMenuView.VIEW_NAME);
            }
        });
        back.setDescription("Returns to main menu");
        buttonLayout.addComponent(back);

        Button addItem = new Button("Add item", new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                createAndAddItem(index++, false);
            }
        });
        addItem.setDescription("Adds new item");
        buttonLayout.addComponent(addItem);

        Button addDwItem = new Button("Add DW", new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                createAndAddItem(2, true);
            }
        });
        addDwItem.setDescription("Adds double wide component");
        buttonLayout.addComponent(addDwItem);

        Button removeItem = new Button("Remove", new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if(itemsAdded.size() > 0) {
                    int remove = rand.nextInt(itemsAdded.size());
                    Component removed = itemsAdded.get(remove);
                    itemsAdded.remove(removed);
                    layout.removeComponent(removed);
                }
            }
        });
        removeItem.setDescription("Removes random component.");
        buttonLayout.addComponent(removeItem);

        Button removeItems = new Button("Remove all", new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                layout.removeAllComponents();
                itemsAdded.clear();
            }
        });
        removeItems.setDescription("Removes all components.");
        buttonLayout.addComponent(removeItems);

        Button requestLayout = new Button("Re-layout", new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                layout.requestLayout();
            }
        });
        requestLayout.setDescription("Will ask client side to relayout. Usually used as workaround for issues.");
        buttonLayout.addComponent(requestLayout);

        CheckBox clickListener = new CheckBox("Close when clicked");
        clickListener.setDescription("When true will close items when clicked.");
        clickListener.setImmediate(true);
        clickListener.addValueChangeListener(clickListenerCBListener);
        buttonLayout.addComponent(clickListener);

        Button reOrder = new Button("Shuffle", new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                shuffleLayout();
            }
        });
        reOrder.setDescription("Shuffles all items to new random order");
        buttonLayout.addComponent(reOrder);

        Panel panel = new Panel();
        panel.setSizeFull();
        addComponent(panel);
        setExpandRatio(panel, 1.0f);

        layout = new MasonryLayout();
        layout.addStyleName("demo-masonry");
        layout.setWidth("100%");
        panel.setContent(layout);

        for(index = 0; index < 5; ++index) {
            createAndAddItem(index, false);
        }

    }

    private void shuffleLayout() {
        // Generate new order
        List<Integer> indexList = new ArrayList<Integer>();
        for(int i = 0; i < layout.getComponentCount(); ++i) {
            indexList.add(i);
        }
        Collections.shuffle(indexList, rand);

        // Read new order
        Map<Component,String> wrapperStyleNames = new HashMap<Component,String>();
        List<Component> newOrder = new ArrayList<Component>();
        for(int index : indexList) {
            Component component = layout.getComponent(index);
            newOrder.add(component);
            wrapperStyleNames.put(component, layout.getComponentWrapperStyleName(component));
        }

        // Apply new order
        for(int newIndex = 0; newIndex < newOrder.size(); ++newIndex) {
            Component component = newOrder.get(newIndex);
            layout.addComponent(component, wrapperStyleNames.get(component), newIndex);
        }
    }

    private void createAndAddItem(int index, boolean doubleWidth) {
        Component itemLayout = ItemGenerator.createItem(index);

        // Just using data to remember the width, this to help when reordering
        layout.addComponent(itemLayout, doubleWidth ? MasonryLayout.DOUBLE_WIDE_STYLENAME : null);

        itemsAdded.add(itemLayout);
    }

    private LayoutEvents.LayoutClickListener layoutClickListener = new LayoutEvents.LayoutClickListener() {

        @Override
        public void layoutClick(LayoutEvents.LayoutClickEvent event) {
            Component child = event.getChildComponent();
            if(child != null) {
                layout.removeComponent(child);
            } else {
                Notification.show("Layout clicked!");
            }
        }
    };

    private Property.ValueChangeListener clickListenerCBListener = new Property.ValueChangeListener() {
        @Override
        public void valueChange(Property.ValueChangeEvent event) {
            boolean value = (Boolean)event.getProperty().getValue();
            if(value) {
                layout.addLayoutClickListener(layoutClickListener);
            } else {
                layout.removeLayoutClickListener(layoutClickListener);
            }
        }
    };

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        Page.getCurrent().setTitle("MasonryLayout Basic Tests");
    }
}
