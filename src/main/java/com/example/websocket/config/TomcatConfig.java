package com.example.websocket.config;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 
 * 
 */
@Configuration
public class TomcatConfig {

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer() {

        return factory -> {
            factory.addContextCustomizers(context -> {

                System.out.println("*** Configuring Tomcat WebserverFactoryCustomizer ***");
                int bufferSize = 10 * 1024 * 1024;

                context.addServletContainerInitializer((sci, servletContext) -> {
                    if (servletContext != null) {
                        org.apache.tomcat.websocket.server.WsServerContainer container = (org.apache.tomcat.websocket.server.WsServerContainer) servletContext
                                .getAttribute("jakarta.websocket.server.ServerContainer");
                        if (container != null) {
                            container.setDefaultMaxBinaryMessageBufferSize(bufferSize);
                            container.setDefaultMaxTextMessageBufferSize(bufferSize);
                            System.out.println("** max binary size is set to "
                                    + container.getDefaultMaxBinaryMessageBufferSize() + " **");
                            System.out.println("** max text size is set to "
                                    + container.getDefaultMaxTextMessageBufferSize() + " **");

                        } else {
                            System.err.println("** container is null ** ");
                        }

                    } else {
                        System.err.println("** servletContext is null ** ");
                    }

                }, null);

            });

        };

    }
}