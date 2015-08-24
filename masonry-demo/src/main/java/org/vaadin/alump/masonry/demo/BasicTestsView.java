package org.vaadin.alump.masonry.demo;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.event.LayoutEvents;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
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

    private Optional<Component> reusedComponent = Optional.empty();

    private Random rand = new Random(0xDEADBEEF);

    public BasicTestsView() {
        super("MasonryLayout Basic Tests");
        layout = createLayout();

        addButton(FontAwesome.PLUS, "1", "Adds new item", clickEvent -> {
            createAndAddItem(layout, index++, false);
        });

        addButton(FontAwesome.PLUS, "DW", "Adds double wide component", clickEvent -> {
            createAndAddItem(layout, 2, true);
        });

        addButton(FontAwesome.PLUS, "5", "Adds 5 new items", clickEvent -> {
            for(int i = 0; i < 5; ++i) {
                createAndAddItem(layout, index++, false);
            }
        });

        addButton(FontAwesome.TRASH_O, "random", "Removes random component.", clickEvent -> {
            if (layout.getComponentCount() > 0) {
                int remove = rand.nextInt(layout.getComponentCount());
                Component removed = layout.getComponent(remove);
                layout.removeComponent(removed);
            }
        });

        addButton(FontAwesome.ERASER, "Removes all components.", clickEvent -> {
            layout.removeAllComponents();
        });

        addButton(FontAwesome.REFRESH, "Will ask client side to relayout. Usually used as workaround for issues.", clickEvent -> {
            layout.requestLayout();
        });

        addButton(FontAwesome.RANDOM, "Randomly reorder all children", e -> shuffleLayout());

        addButton(FontAwesome.PAPERCLIP, "Add post it note styled component", clickEvent -> {
            Component note = ItemGenerator.createPostItNote();
            layout.addComponent(note);
        });

        addButton(FontAwesome.CSS3, "Toggle theme", clickEvent -> {
            if (getUI().getTheme().equals("demo")) {
                System.out.println("Theme toggled to demo2");
                getUI().setTheme("demo2");
            } else {
                System.out.println("Theme toggled to demo");
                getUI().setTheme("demo");
            }
        });

        addButton(FontAwesome.ARROWS_H, "Toggle width of child component (first click adds)", clickEvent -> {
            if (!reusedComponent.isPresent()) {
                reusedComponent = Optional.of(ItemGenerator.createItem(1));
                reusedComponent.get().addDetachListener(event -> reusedComponent = Optional.empty());
                layout.addComponent(reusedComponent.get());
            } else if (MasonryLayout.DOUBLE_WIDE_STYLENAME.equals(layout.getComponentWrapperStyleName(reusedComponent.get()))) {
                layout.updateComponentWrapperStyleName(reusedComponent.get(), null);
            } else {
                layout.updateComponentWrapperStyleName(reusedComponent.get(), MasonryLayout.DOUBLE_WIDE_STYLENAME);
            }
        });

        buttonLayout.addComponent(createTransitionTimeComboBox());

        addButton(FontAwesome.CLOCK_O, "Adds slow images to layout to test re-layouting", event -> createSlowImage(index++));

        CheckBox clickListener = new CheckBox("Close when clicked");
        clickListener.setDescription("When true will close items when clicked.");
        clickListener.setImmediate(true);
        clickListener.addValueChangeListener(clickListenerCBListener);
        buttonLayout.addComponent(clickListener);
        buttonLayout.setComponentAlignment(clickListener, Alignment.BOTTOM_CENTER);

        CheckBox paperStyle = new CheckBox("Paper");
        paperStyle.setDescription("Use fancier paper styling");
        paperStyle.setValue(true);
        paperStyle.setImmediate(true);
        paperStyle.addValueChangeListener(event -> {
            boolean value = (Boolean) event.getProperty().getValue();
            if (value) {
                layout.addStyleName(MasonryLayout.MASONRY_PAPER_SHADOW_STYLENAME);
            } else {
                layout.removeStyleName(MasonryLayout.MASONRY_PAPER_SHADOW_STYLENAME);
            }
        });
        buttonLayout.addComponent(paperStyle);
        buttonLayout.setComponentAlignment(paperStyle, Alignment.BOTTOM_CENTER);

        setPanelContent(layout);
    }

    private MasonryLayout createLayout() {
        MasonryLayout layout = new MasonryLayout();
        layout.addStyleName(MasonryLayout.MASONRY_PAPER_SHADOW_STYLENAME);
        layout.addStyleName("demo-masonry");
        layout.setWidth("100%");

        layout.setAutomaticLayoutWhenImagesLoaded(true);

        // This line is just to see that ImagesLoaded events work correctly
        ImagesLoadedExtension.getExtension(layout).addImagesLoadedListener(this);

        for(index = 0; index < 5; ++index) {
            createAndAddItem(layout, index, false);
        }

        return layout;
    }

    private ComboBox createTransitionTimeComboBox() {
        ComboBox transitionTime = new ComboBox();
        transitionTime.setWidth("120px");
        transitionTime.setImmediate(true);
        transitionTime.addItem("0.2s");
        transitionTime.setItemCaption("0.2s", "Fast (0.2s)");
        transitionTime.addItem("0.4s");
        transitionTime.setItemCaption("0.4s", "Default (0.4s)");
        transitionTime.addItem("0.8s");
        transitionTime.setItemCaption("0.8s", "Slow (0.8s)");
        transitionTime.setValue(layout.getTransitionDuration());
        transitionTime.setNullSelectionAllowed(false);
        transitionTime.setNewItemsAllowed(false);

        transitionTime.addValueChangeListener(event -> {
            String newVal = event.getProperty().getValue().toString();
            if(newVal.equals(layout.getTransitionDuration())) {
                return;
            }

            MasonryLayout newLayout = createLayout();
            newLayout.setTransitionDuration(newVal);
            setPanelContent(newLayout);
            layout = newLayout;
        });

        return transitionTime;
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

    private void createAndAddItem(MasonryLayout layout, int index, boolean doubleWidth) {
        Component itemLayout = ItemGenerator.createItem(index);

        // Just using data to remember the width, this to help when reordering
        layout.addComponent(itemLayout, doubleWidth ? MasonryLayout.DOUBLE_WIDE_STYLENAME : null);
    }

    private LayoutEvents.LayoutClickListener layoutClickListener = event ->  {
        Component child = event.getChildComponent();
        if(child != null) {
            layout.removeComponent(child);
        } else {
            Notification.show("Layout clicked X:" + event.getClientX() + " Y:" + event.getClientY() + "!");
        }
    };

    private Property.ValueChangeListener clickListenerCBListener = event -> {
        boolean value = (Boolean)event.getProperty().getValue();
        if(value) {
            layout.addLayoutClickListener(layoutClickListener);
        } else {
            layout.removeLayoutClickListener(layoutClickListener);
        }
    };

    private void createSlowImage(int index) {
        Component itemLayout = ItemGenerator.createSlowImage(index);
        layout.addComponent(itemLayout);
    }

    @Override
    public void onImagesLoaded(ImagesLoadedExtension.ImagesLoadedEvent event) {
        //System.out.println("Images loaded in Masonry!");
    }
}
