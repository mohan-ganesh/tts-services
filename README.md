# Spring Boot WebSocket TTS/STT Demo

This project demonstrates a full-duplex voice conversation with a backend service using Spring Boot for WebSockets, Google Cloud Speech-to-Text (STT), and Google Cloud Text-to-Speech (TTS).

## Features

- **WebSocket Communication**: Uses STOMP over WebSockets for real-time, bidirectional communication between the client and server.
- **Voice Recording**: The frontend can record audio from the user's microphone.
- **Speech-to-Text**: The recorded audio is sent to the backend, transcribed to text using Google Cloud STT.
- **Text-to-Speech**: The backend generates a spoken response using Google Cloud TTS and sends the audio back to the client for playback.
- **Configurable Message Size**: Includes configuration to handle large WebSocket messages, which is common when sending audio data.

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
