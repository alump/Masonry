package org.vaadin.alump.masonry.demo;

import com.vaadin.data.Property;
import com.vaadin.ui.*;
import org.vaadin.alump.masonry.MasonryDnDWrapper;
import org.vaadin.alump.masonry.MasonryLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Test case for Drag'n drop reordering features
 */
public class VaadinDnDTestsView extends AbstractTestView {

    public final static String VIEW_NAME = VaadinDnDTestsView.class.getSimpleName();

    private MasonryDnDWrapper layout;

    private int index = 0;

    private Random rand = new Random(0xDEADBEEF);

    private List<Component> itemsAdded = new ArrayList<Component>();

    public VaadinDnDTestsView() {
        super("Vaadin Drag'n drop support demo");

        addButton("Add", "Add component", clickEvent -> {
                createAndAddItem(index++, false);
        });

        addButton("Add DW", "Add double width component", clickEvent -> {
            createAndAddItem(index++, true);
        });

        addButton("Remove all", "Remove all components", clickEvent -> {
            layout.removeAllComponentsFromLayout();
        });

        addButton("Layout", "Relayout client side", clickEvent -> {
            layout.requestLayout();
        });

        addButton("Theme", "Toggle theme", clickEvent -> {
            if(getUI().getTheme().equals("demo2")) {
                getUI().setTheme("demo3");
            } else {
                getUI().setTheme("demo2");
            }
        });

        CheckBox disallowReorder = new CheckBox("Disallow reorder");
        disallowReorder.setImmediate(true);
        disallowReorder.addValueChangeListener(event -> {
            layout.setReorderable(!((Boolean) event.getProperty().getValue()));
        });
        buttonLayout.addComponent(disallowReorder);

        layout = new MasonryDnDWrapper();
        // Tells to use fancier shadows
        layout.addStyleNameToLayout(MasonryLayout.MASONRY_PAPER_SHADOW_STYLENAME);
        layout.addMasonryReorderListener(reorderListener);
        layout.addStyleName("demo-masonry");
        layout.setWidth("100%");
        layout.setAutomaticLayoutWhenImagesLoaded(true);
        setPanelContent(layout);

        for(int i = 0; i < 5; ++i) {
            createAndAddItem(index++, false);
        }
    }

    private final MasonryDnDWrapper.MasonryReorderListener reorderListener = new MasonryDnDWrapper.MasonryReorderListener() {

        @Override
        public void onUserReorder(MasonryDnDWrapper.MasonryReorderEvent event) {
            System.out.println("User reordered stuff!");
        }
    };

    private void createAndAddItem(int index, boolean doubleWidth) {
        Component itemLayout = ItemGenerator.createItem(index);

        // Just using data to remember the width, this to help when reordering
        layout.addComponentToLayout(itemLayout, doubleWidth ? MasonryLayout.DOUBLE_WIDE_STYLENAME : null);

        itemsAdded.add(itemLayout);
    }
}
