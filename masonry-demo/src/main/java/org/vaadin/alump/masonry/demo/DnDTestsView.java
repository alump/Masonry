package org.vaadin.alump.masonry.demo;

import com.vaadin.data.Property;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import org.vaadin.alump.masonry.MasonryDnDLayout;
import org.vaadin.alump.masonry.MasonryDndReorderedEvent;
import org.vaadin.alump.masonry.MasonryLayout;

/**
 * Created by alump on 05/11/14.
 */
public class DnDTestsView extends AbstractTestView implements MasonryDnDLayout.MasonryDndReorderListener {

    public final static String VIEW_NAME = DnDTestsView.class.getSimpleName();

    protected MasonryDnDLayout layout;
    protected Label messageLabel;

    public DnDTestsView() {
        super("HTML5 based client side reordering DnD demo");

        CheckBox draggingAllowed = new CheckBox("Disable dragging");
        draggingAllowed.setDescription("If dragging should be disabled");
        draggingAllowed.setImmediate(true);
        draggingAllowed.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                boolean disabled = (Boolean)event.getProperty().getValue();
                layout.setDraggingAllowed(!disabled);
            }
        });
        buttonLayout.addComponent(draggingAllowed);

        addButton("relayout", "force relayouting", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                layout.requestLayout();
            }
        });

        messageLabel = new Label("-");
        messageLabel.setWidth("100%");
        buttonLayout.addComponent(messageLabel);
        buttonLayout.setExpandRatio(messageLabel, 1.0f);

        layout = new MasonryDnDLayout();
        layout.addMasonryDndReorderListener(this);
        layout.setTransitionDuration("0.2s");
        layout.setWidth("100%");
        layout.addStyleName(MasonryLayout.MASONRY_PAPER_SHADOW_STYLENAME);
        masonryPanel.setContent(layout);

        for(int i = 0; i < 10; ++i) {
            layout.addComponent(ItemGenerator.createItem(i, true));
        }

        layout.setDraggingAllowed(true);
    }


    @Override
    public void onReordered(MasonryDndReorderedEvent event) {
        int oldIndex = event.getOldOrder().indexOf(event.getMovedComponent());
        int newIndex = event.getLayout().getComponentIndex(event.getMovedComponent());
        messageLabel.setValue("Moved from " + oldIndex + " to " + newIndex);
    }
}
