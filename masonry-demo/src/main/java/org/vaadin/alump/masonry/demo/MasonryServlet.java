package org.vaadin.alump.masonry.demo;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.VaadinServlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(value = "/*", asyncSupported = true)
@VaadinServletConfiguration(productionMode = false, ui = MasonryDemoUI.class, widgetset = "org.vaadin.alump.masonry.demo.DemoWidgetSet")
public class MasonryServlet extends VaadinServlet {

    private static final ImagesRequestHandler imagesRequestHandler = new ImagesRequestHandler();

    @Override
    protected void servletInitialized() throws ServletException {
        super.servletInitialized();
        System.out.println("foo 1");
        getService().addSessionInitListener(event -> {
            System.out.println("foo 2");
            event.getSession().addRequestHandler(imagesRequestHandler);
        });
    }
}
