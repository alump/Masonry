package org.vaadin.alump.masonry.demo;

import com.vaadin.server.RequestHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinSession;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by alump on 03/05/15.
 */
public class ImagesRequestHandler implements RequestHandler {

    public static final String PREFIX = "/images/";
    public static final String SUFFIX = ".jpg";

    @Override
    public boolean handleRequest(VaadinSession session, VaadinRequest request, VaadinResponse response) throws IOException {

        String path = request.getPathInfo();

        if(path.startsWith(PREFIX) && path.endsWith(SUFFIX)) {
            response.setContentType("image/jpeg");
            path = path.substring(PREFIX.length() - 1);
            InputStream stream = getClass().getResourceAsStream(path);
            if(stream != null) {
                IOUtils.copy(stream, response.getOutputStream());
                return true;
            }
        }

        return false;
    }

}
