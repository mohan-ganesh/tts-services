package com.example.websocket.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    public static final Logger logger = LoggerFactory.getLogger(WebSocketSecurityConfig.class);


    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        logger.info("Configuring inbound message security...");
        // Permit all incoming STOMP messages (CONNECT, SEND, SUBSCRIBE, etc.)
        messages.anyMessage().permitAll();
    }

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }
}