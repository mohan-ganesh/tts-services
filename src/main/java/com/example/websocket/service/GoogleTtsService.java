package com.example.websocket.service;

import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class GoogleTtsService {
    private static final Logger logger = LoggerFactory.getLogger(GoogleTtsService.class);

    private TextToSpeechClient textToSpeechClient;

    @PostConstruct
    public void init() {
        try {
            // Initialize the client using Application Default Credentials
            textToSpeechClient = TextToSpeechClient.create();
            logger.info(
                    "Google TextToSpeechClient initialized successfully using Application Default Credentials.");
        } catch (IOException e) {
            logger.error("Failed to initialize Google TextToSpeechClient: " + e.getMessage());

            throw new RuntimeException("Could not initialize Google TextToSpeechClient", e);
        }
    }

    @PreDestroy
    public void cleanup() {
        if (textToSpeechClient != null) {
            textToSpeechClient.close();
            logger.info("Google TextToSpeechClient closed successfully.");
        } else {
            logger.warn("Google TextToSpeechClient did not close.");
        }
    }

    /**
     * Synthesizes speech from the given text using the specified voice.
     *
     * @param text The text to synthesize.
     * @return Base64 encoded audio content (MP3 format).
     * @throws IOException If an API call fails.
     */
    public String synthesizeTextToMp3Base64(String text) throws IOException {
        // Set the text input to be synthesized
        logger.info("Synthesizing text to speech: " + text);
        // Build the synthesis input
        SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();

        // Build the voice request, select the language and SSML voice gender (optional)
        VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                .setLanguageCode("en-US") // For Gemini, use an appropriate language code
                // .setName("en-US-Standard-E") // You can specify a particular voice
                .setSsmlGender(SsmlVoiceGender.NEUTRAL)
                .build();

        // Select the type of audio file you want returned
        AudioConfig audioConfig = AudioConfig.newBuilder().setAudioEncoding(AudioEncoding.MP3).build();

        // Perform the text-to-speech request
        SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);

        // Get the audio contents as bytes
        ByteString audioContents = response.getAudioContent();

        logger.info("Text-to-speech synthesis completed successfully.");
        // Encode to Base64
        return java.util.Base64.getEncoder().encodeToString(audioContents.toByteArray());
    }

    /**
     * This method would be used to interact with the actual Gemini API
     * if there was a direct Text-to-Speech endpoint for the Gemini model itself.
     * Currently, 'gemini-2.5-pro-preview-tts' is more conceptual for models
     * offering TTS,
     * and Google Cloud Text-to-Speech API is the way to get high-quality TTS.
     * We are essentially using the Google Cloud TTS service *as if* it were the TTS
     * part of Gemini.
     *
     * In a future where 'gemini-2.5-pro-preview-tts' directly exposes a different
     * TTS API,
     * this is where you'd put that specific client integration.
     */
    public String generateSpeechWithGemini(String text) throws IOException {
        logger.info("Using Google Cloud TTS as 'Gemini' TTS for text: " + text);
        return synthesizeTextToMp3Base64(text);
    }
}