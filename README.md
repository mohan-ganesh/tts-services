# Spring Boot WebSocket TTS/STT Demo

This project demonstrates a full-duplex, real-time voice conversation with a backend service using **Spring Boot WebSockets**, **Google Cloud Speech-to-Text (STT)**, and **Google Cloud Text-to-Speech (TTS)**.

It also serves as a **reference implementation** for handling large binary WebSocket messages (e.g., audio payloads) in Spring Boot applications running on Tomcat.


---

## 📘 Documentation & Root Cause Analysis

This repository is directly related to a real-world production issue involving **silent WebSocket disconnects** caused by Tomcat’s default **8KB message buffer limit**.

A detailed investigation, reproduction steps, root cause analysis, and production-ready fix are documented here:

👉 **Debugging Silent WebSocket Disconnects: Taming the 8KB Tomcat Limit in Spring Boot**  
https://www.garvik.dev/spring-boot/websocket-tomcat-buffer-limit

--

## ✨ Features

- **WebSocket Communication**  
  Uses STOMP over WebSockets for real-time, bidirectional communication.

- **Voice Recording**  
  The frontend records audio directly from the user’s microphone.

- **Speech-to-Text (STT)**  
  Audio is streamed to the backend and transcribed using Google Cloud STT.

- **Text-to-Speech (TTS)**  
  The backend generates synthesized speech using Google Cloud TTS and streams it back to the client.

- **Large Message Handling**  
  Demonstrates safe handling of large WebSocket payloads (audio frames), including Tomcat and Spring WebSocket buffer configuration.

---

## 🧪 Simulating and Fixing Large WebSocket Message Issues

A common production issue with WebSockets is handling messages that exceed the default buffer sizes enforced by **Tomcat** and the **Spring WebSocket stack**.

This project is **preconfigured with a workaround enabled by default**, but you can disable it to reproduce the failure scenario and understand the fix.

---

## Simulating and Fixing Large Message Size Issues

A common problem with WebSockets is handling messages that exceed the default buffer sizes of the underlying server (Tomcat) and the Spring WebSocket framework. This project is configured to handle larger messages by default, but you can simulate the error to understand the problem and the solution.

### How to Simulate the Error

1.  Open the `src/main/resources/application.properties` file.
2.  Find the property `server.tomcat.max-websocket-message-size.override`.
3.  Change its value from `true` to `false`:
    ```properties
    server.tomcat.max-websocket-message-size.override=false
    ```
4.  Restart the application.
5.  Open the web client at `http://localhost:8080/index.html`, connect, and record a few seconds of audio (e.g., 5-10 seconds) to generate a large message payload.
6.  When you send the audio, the WebSocket connection will be closed by the server. You will see an error in the server logs similar to this:

    ```
    o.s.w.s.h.LoggingWebSocketHandlerDecorator : StandardWebSocketSession[...] closed with CloseStatus[code=1009, reason=The decoded text message was too big for the output buffer and the endpoint does not support partial messages]
    ```

### The Solution

Setting `server.tomcat.max-websocket-message-size.override=true` in `application.properties` activates custom configuration in `TomcatConfig.java` and `WebSocketConfig.java`. This increases the default message size limits for both the embedded Tomcat server and the Spring WebSocket message broker, allowing larger audio payloads to be processed successfully.
