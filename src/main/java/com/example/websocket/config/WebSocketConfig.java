package com.example.websocket.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	public static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);
	
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
		logger.info("Configuring message broker...");

        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(1);
        taskScheduler.setThreadNamePrefix("wss-broker-");
        taskScheduler.initialize();

        config.enableSimpleBroker("/topic")
              .setTaskScheduler(taskScheduler);
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
		logger.info("Registering STOMP endpoint...");
        registry.addEndpoint("/gs-guide-websocket").setAllowedOrigins("*");
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
		logger.info("Configuring WebSocket transport...");
        registration.setMessageSizeLimit(512 * 1024); // Set to 512KB
        registration.setSendBufferSizeLimit(1024 * 1024); // Set to 1MB
        registration.setSendTimeLimit(20000); // 20 seconds
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.taskExecutor()
            .corePoolSize(4)
            .maxPoolSize(8);
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.taskExecutor()
            .corePoolSize(4)
            .maxPoolSize(8);
    }
}