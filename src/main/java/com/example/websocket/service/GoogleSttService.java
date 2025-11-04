package com.example.websocket.service;

import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Service
public class GoogleSttService {

    private static final Logger logger = LoggerFactory.getLogger(GoogleSttService.class);

    private SpeechClient speechClient;

    @PostConstruct
    public void init() {
        try {
            speechClient = SpeechClient.create();
            // Initialize the client using Application Default Credentials
            logger.info("-------------");
            logger.info("init() -- Google SpeechClient initialized successfully.");
            logger.info("-------------");
        } catch (IOException e) {
            // deliberate
            logger.error("Failed to initialize Google SpeechClient", e);
        }
    }

    public String transcribe(byte[] audioData) throws IOException {
        ByteString audioBytes = ByteString.copyFrom(audioData);

        RecognitionConfig config = RecognitionConfig.newBuilder()
                .setEncoding(RecognitionConfig.AudioEncoding.WEBM_OPUS)
                .setModel("telephony") // Use a valid and robust model for voice audio.
                .setLanguageCode("en-US")
                // For WEBM_OPUS, sampleRateHertz is inferred from the file header and should
                // not be set.
                .build();

        RecognitionAudio audio = RecognitionAudio.newBuilder()
                .setContent(audioBytes)
                .build();

        RecognizeResponse response = speechClient.recognize(config, audio);

        // Check if the response has any results before trying to access them
        if (response.getResultsCount() > 0) {
            SpeechRecognitionResult result = response.getResultsList().get(0);
            if (result.getAlternativesCount() > 0) {
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                return alternative.getTranscript();
            }
        } else {
            logger.error("No results found in the response.");
        }

        return "[No speech detected]";
    }
}