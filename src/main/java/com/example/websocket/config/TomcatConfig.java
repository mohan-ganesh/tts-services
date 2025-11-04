package com.example.websocket.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 */
@Configuration
public class TomcatConfig {

    private static final Logger logger = LoggerFactory.getLogger(TomcatConfig.class);

    @Value("${server.tomcat.max-websocket-message-size:10485760}")
    private int maxWebSocketMessageSize;

    @Value("${server.tomcat.max-websocket-message-size.override:false}")
    private boolean overrideMaxWebSocketMessageSize;

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer() {

        return factory -> {
            factory.addContextCustomizers(context -> {

                logger.info("*** Configuring Tomcat WebserverFactoryCustomizer ***");
                int bufferSize = maxWebSocketMessageSize;

                context.addServletContainerInitializer((sci, servletContext) -> {
                    if (servletContext != null) {
                        org.apache.tomcat.websocket.server.WsServerContainer container = (org.apache.tomcat.websocket.server.WsServerContainer) servletContext
                                .getAttribute("jakarta.websocket.server.ServerContainer");
                        if (container != null && overrideMaxWebSocketMessageSize) {
                            container.setDefaultMaxBinaryMessageBufferSize(bufferSize);
                            container.setDefaultMaxTextMessageBufferSize(bufferSize);
                            logger.info("** max binary size is set to "
                                    + container.getDefaultMaxBinaryMessageBufferSize() + " **");
                            logger.info("** max text size is set to "
                                    + container.getDefaultMaxTextMessageBufferSize() + " **");

                        } else if (container != null) {
                            logger.info("** Using existing WebSocket buffer sizes: max binary size = "
                                    + container.getDefaultMaxBinaryMessageBufferSize() + ", max text size = "
                                    + container.getDefaultMaxTextMessageBufferSize() + " **");
                        } else {
                            logger.info("** container is null ** ");
                        }

                    } else {
                        logger.info("** servletContext is null ** ");
                    }

                }, null);

            });

        };

    }
}