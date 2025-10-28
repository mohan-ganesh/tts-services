package com.example.websocket;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MessagingStompWebsocketApplication {

	public static void main(String[] args) {
		SpringApplication.run(MessagingStompWebsocketApplication.class, args);
	}
}