package org.vaadin.alump.masonry.demo;

import com.vaadin.data.Property;
import com.vaadin.event.LayoutEvents;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.ui.*;
import org.vaadin.alump.masonry.ImagesLoadedExtension;
import org.vaadin.alump.masonry.MasonryLayout;

import java.util.*;

/**
 * Base test that covers most of the features
 */
public class BasicTestsView extends AbstractTestView implements ImagesLoadedExtension.ImagesLoadedListener {

    public final static String VIEW_NAME = BasicTestsView.class.getSimpleName();

    private MasonryLayout layout;
    private int index = 0;
    private CheckBox slowImage;

    private List<Component> itemsAdded = new ArrayList<Component>();

    private Random rand = new Random(0xDEADBEEF);

    public BasicTestsView() {
        super("MasonryLayout Basic Tests");

        addButton("Add 1", "Adds new item", new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                createAndAddItem(index++, false);
            }
        });

        addButton("Add DW", "Adds double wide component", new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                createAndAddItem(2, true);
            }
        });

        addButton("Add 5", "Adds 5 new items", new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                for(int i = 0; i < 5; ++i) {
                    if(i % 2 == 0 && slowImage.getValue()) {
                        createSlowImage(index++);
                    } else {
                        createAndAddItem(index++, false);
                    }
                }
            }
        });

        addButton("Remove", "Removes random component.", new Button.ClickListener() {

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

        addButton("Clear", "Removes all components.", new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                layout.removeAllComponents();
                itemsAdded.clear();
            }
        });

        addButton("Layout", "Will ask client side to relayout. Usually used as workaround for issues.", new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                layout.requestLayout();
            }
        });

        CheckBox clickListener = new CheckBox("Close when clicked");
        clickListener.setDescription("When true will close items when clicked.");
        clickListener.setImmediate(true);
        clickListener.addValueChangeListener(clickListenerCBListener);
        buttonLayout.addComponent(clickListener);

        slowImage = new CheckBox("Slow");
        slowImage.setDescription("Adds slow images to layout to test re-layouting");
        slowImage.setImmediate(true);
        slowImage.addValueChangeListener(slowImageCBListener);
        buttonLayout.addComponent(slowImage);

        Button reOrder = new Button("Random", new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                shuffleLayout();
            }
        });
        reOrder.setDescription("Shuffles all items to new random order");
        buttonLayout.addComponent(reOrder);

        CheckBox paperStyle = new CheckBox("Paper");
        paperStyle.setDescription("Use fancier paper styling");
        paperStyle.setValue(true);
        paperStyle.setImmediate(true);
        paperStyle.addValueChangeListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                boolean value = (Boolean)event.getProperty().getValue();
                if(value) {
                    layout.addStyleName(MasonryLayout.MASONRY_PAPER_SHADOW_STYLENAME);
                } else {
                    layout.removeStyleName(MasonryLayout.MASONRY_PAPER_SHADOW_STYLENAME);
                }
            }
        });
        buttonLayout.addComponent(paperStyle);

        addButton("PostIt", "Add post it note styled component", new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                Component note = ItemGenerator.createPostItNote();
                layout.addComponent(note);
                itemsAdded.add(note);
            }
        });

        layout = new MasonryLayout();
        layout.addStyleName(MasonryLayout.MASONRY_PAPER_SHADOW_STYLENAME);
        layout.addStyleName("demo-masonry");
        layout.setWidth("100%");
        setPanelContent(layout);

        layout.setAutomaticLayoutWhenImagesLoaded(true);

        // This line is just to see that ImagesLoaded events work correctly
        ImagesLoadedExtension.getExtension(layout).addImagesLoadedListener(this);

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

    private Property.ValueChangeListener slowImageCBListener = new Property.ValueChangeListener() {
        @Override
        public void valueChange(Property.ValueChangeEvent event) {
            boolean value = (Boolean)event.getProperty().getValue();
            if(value) {
                createSlowImage(index++);
            }
        }
    };

    private void createSlowImage(int index) {
        Component itemLayout = ItemGenerator.createSlowImage(index);
        layout.addComponent(itemLayout);
        itemsAdded.add(itemLayout);
    }

    @Override
    public void onImagesLoaded(ImagesLoadedExtension.ImagesLoadedEvent event) {
        System.out.println("Images loaded in Masonry!");
    }
}
