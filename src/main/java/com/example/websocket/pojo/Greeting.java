package com.example.websocket.pojo;

/**
 * 
 */
public class Greeting {

	private String content;
	private String audioBase64String;

	public String getAudioBase64String() {
		return audioBase64String;
	}

	public void setAudioBase64String(String audioBase64String) {
		this.audioBase64String = audioBase64String;
	}

	public Greeting(String content, String audioBase64String) {
		this.content = content;
		this.audioBase64String = audioBase64String;
	}

	public Greeting() {
	}

	public Greeting(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

}
