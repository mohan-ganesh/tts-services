package com.example.websocket.pojo;

public class AudioMessage {
    private String audioData;
    private String mimeType;

    public AudioMessage() {
    }

    public AudioMessage(String audioData, String mimeType) {
        this.audioData = audioData;
        this.mimeType = mimeType;
    }

    public String getAudioData() {
        return audioData;
    }

    public void setAudioData(String audioData) {
        this.audioData = audioData;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}