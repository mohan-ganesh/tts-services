package com.example.websocket.controller;

import com.example.websocket.service.GoogleSttService;
import com.example.websocket.service.GoogleTtsService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;
import com.example.websocket.pojo.Greeting;
import com.example.websocket.pojo.AudioMessage;
import com.example.websocket.pojo.Message;

import java.util.Base64;

@Controller
public class MessageController {
	public static final Logger logger = LoggerFactory.getLogger(MessageController.class);

	private final GoogleTtsService ttsService; // Inject our new TTS service

	private final GoogleSttService sttService;

	public MessageController(GoogleTtsService ttsService, GoogleSttService sttService) {
		this.ttsService = ttsService;
		this.sttService = sttService;
	}

	@MessageMapping("/hello")
	@SendTo("/topic/greetings")
	public Greeting greeting(Message message) throws Exception {
		logger.info("Received text message: {}", message.getName());
		return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
	}

	@MessageMapping("/audio")
	@SendTo("/topic/greetings") // Sending confirmation to the same topic for now
	public Greeting handleAudioMessage(AudioMessage audioMessage) throws Exception {
		int dataLength = audioMessage.getAudioData() != null ? audioMessage.getAudioData().length() : 0;
		logger.info("Received audio message of size: {}", dataLength);
		logger.info(audioMessage.getAudioData());
		String transcribedText = "Could not transcribe audio.";
		String audioBase64 = null;

		try {
			// 1. Decode the Base64 audio data from the client
			byte[] audioBytes = Base64.getDecoder().decode(audioMessage.getAudioData());
			logger.info("Decoded audio data. Size: {} bytes.", audioBytes.length);

			// 2. Transcribe the audio bytes to text using the Speech-to-Text service
			transcribedText = sttService.transcribe(audioBytes);
			logger.info("Transcribed text: '{}'", transcribedText);

			// 3. Generate a spoken response from the transcribed text using the
			// Text-to-Speech service
			String responseText = "You said: " + transcribedText;
			audioBase64 = ttsService.generateSpeechWithGemini(responseText);
			return new Greeting(responseText, audioBase64);
		} catch (Exception e) {
			logger.error("Error processing audio message: ", e);
			return new Greeting("Error processing audio: " + e.getMessage());
		}

	}
}
