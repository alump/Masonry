/**
 * ImagesLoadedExtension.java (Masonry)
 *
 * Copyright 2014 Vaadin Ltd, Sami Viitanen <sami.viitanen@vaadin.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.vaadin.alump.masonry;

import com.vaadin.annotations.JavaScript;
import com.vaadin.server.AbstractClientConnector;
import com.vaadin.server.AbstractExtension;
import com.vaadin.server.ClientConnector;
import com.vaadin.server.Extension;
import com.vaadin.ui.AbstractComponent;
import org.vaadin.alump.masonry.client.ImagesLoadedServerRpc;
import org.vaadin.alump.masonry.client.ImagesLoadedState;

import java.util.ArrayList;
import java.util.List;

/**
 * Extension that will attach imagesLoaded library to component
 * given. Components that implement ImagesLoadedClientListener on
 * client side will be also notified on client side.
 *
 * This extension is in experimental state. And for now for use
 * with MasonryLayout. You can try this out with other components,
 * but use at your own risk :)
 *
 * imagesLoaded by David DeSandro: http://imagesloaded.desandro.com/
 */
@JavaScript({ "imagesloaded.pkgd.min.js" })
public class ImagesLoadedExtension extends AbstractExtension {

    protected final List<ImagesLoadedListener> listeners = new ArrayList<ImagesLoadedListener>();

    /**
     * Event for ImagesLoadedListeners
     */
    public static class ImagesLoadedEvent {
        private ImagesLoadedExtension instance;

        protected ImagesLoadedEvent(ImagesLoadedExtension instance) {
            this.instance = instance;
        }

        /**
         * Get instance of ImagesLoadedExtension sending this event
         * @return
         */
        public ImagesLoadedExtension getInstance() {
            return instance;
        }
    }

    /**
     * Interface for server side images loaded listeners
     */
    public interface ImagesLoadedListener {

        /**
         * Called when images under given component are loaded. Remember that if images do not need actual loading this
         * method might not called.
         * @param event Event information
         */
        void onImagesLoaded(ImagesLoadedEvent event);
    }

    private ImagesLoadedExtension(AbstractComponent component) {
        extend(component);

        registerRpc(new ImagesLoadedServerRpc() {
            @Override
            public void onImagesLoaded() {
                ImagesLoadedEvent event = new ImagesLoadedEvent(ImagesLoadedExtension.this);
                for(ImagesLoadedListener listener : listeners) {
                    listener.onImagesLoaded(event);
                }
            }
        });
    }

    /**
     * Get instance of ImagesLoadedExtension currently extending given component.
     * @param component Component with instance of extension
     * @return Instance of ImagesLoadedExtension if found, null if no instance found
     */
    public static ImagesLoadedExtension getExtension(AbstractComponent component) {
        if(component == null) {
            throw new IllegalArgumentException("Can not resolve extension from null component");
        }
        for(Extension extension : component.getExtensions()) {
            if(extension instanceof ImagesLoadedExtension) {
                return (ImagesLoadedExtension) extension;
            }
        }
        return null;
    }

    /**
     * Create new instance of ImagesLoadedExtension (or use old) for for given component.
     * @param component Component extended
     * @param listener Server side listener called when images are loaded
     * @return Instance of extension created, or one already extending given component
     */
    public static ImagesLoadedExtension createExtension(AbstractComponent component, ImagesLoadedListener listener) {
        ImagesLoadedExtension extension = createExtension(component);
        extension.addImagesLoadedListener(listener);
        return extension;
    }

    /**
    * Create new instance of ImagesLoadedExtension (or use old) for for given component.
            * @param component Component extended
    * @return Instance of extension created, or one already extending given component
    */
    public static ImagesLoadedExtension createExtension(AbstractComponent component) {
        ImagesLoadedExtension extension = getExtension(component);
        if(extension == null) {
            extension = new ImagesLoadedExtension(component);
        }
        return extension;
    }

    /**
     * Remove extension from given component
     * @param component Component which ImageLoadedExtension instance will be removed
     */
    public static void removeExtension(AbstractComponent component) {
        ImagesLoadedExtension extension = getExtension(component);
        if(extension != null) {
            extension.remove();
        }
    }

    /**
     * Add new server side listener to extension instance
     * @param listener Listener added
     */
    public void addImagesLoadedListener(ImagesLoadedListener listener) {
        if(listener == null) {
            throw new IllegalArgumentException("Listener can not be null");
        }
        listeners.add(listener);
        getState().callServer = !listeners.isEmpty();
    }

    /**
     * Remove new server side listener from extension instance
     * @param listener Listener added
     */
    public void removeImagesLoadedListener(ImagesLoadedListener listener) {
        listeners.remove(listener);
        getState().callServer = !listeners.isEmpty();
    }

    @Override
    protected ImagesLoadedState getState() {
        return (ImagesLoadedState)super.getState();
    }
}
