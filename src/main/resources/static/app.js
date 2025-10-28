let brokerURL;
if (
  window.location.hostname === "localhost" ||
  window.location.hostname === "127.0.0.1"
) {
  // Use ws for local development
  brokerURL = "ws://localhost:8080/gs-guide-websocket";
} else {
  // Use wss for the deployed environment
  brokerURL =
    "wss://tts-services-880624566657.us-central1.run.app/gs-guide-websocket";
}

const stompClient = new StompJs.Client({
  brokerURL: brokerURL,
});

let mediaRecorder;
let audioChunks = [];
let audioStream;

stompClient.onConnect = (frame) => {
  setConnected(true);
  console.log("Connected: " + frame);
  stompClient.subscribe("/topic/greetings", (greeting) => {
    const greetingBody = JSON.parse(greeting.body);
    console.log("Received greeting from /topic/greetings:", greetingBody);
    showGreeting(greetingBody.content);

    // Check for and play audio
    if (greetingBody.audioBase64String) {
      playAudio(greetingBody.audioBase64String);
    }
  });
};

stompClient.onWebSocketError = (error) => {
  console.error("Error with websocket", error);
};

stompClient.onStompError = (frame) => {
  console.error("Broker reported error: " + frame.headers["message"]);
  console.error("Additional details: " + frame.body);
};

function setConnected(connected) {
  $("#connect").prop("disabled", connected);
  $("#disconnect").prop("disabled", !connected);
  $("#audioError").text(""); // Clear any previous audio errors
  if (connected) {
    $("#conversation").show();
    $("#audioControls").show(); // Show audio controls when connected
    $("#startRecord").prop("disabled", false); // Enable start record button
  } else {
    $("#conversation").hide();
    $("#audioControls").hide(); // Hide audio controls when disconnected
  }
  $("#stopRecord").prop("disabled", true); // Always disable stop/send when not recording
  $("#sendAudioMessage").prop("disabled", true);
  $("#greetings").html("");
}

function connect() {
  stompClient.activate();
}

function disconnect() {
  stompClient.deactivate();
  setConnected(false);
  console.log("Disconnected");
}

function sendName() {
  const payload = { name: $("#name").val() };
  console.log("Publishing to /app/hello:", payload);
  stompClient.publish({
    destination: "/app/hello",
    body: JSON.stringify(payload),
  });
}

function startRecording() {
  $("#recordingStatus").text("Recording...");
  $("#audioError").text("");
  $("#startRecord").prop("disabled", true);
  $("#stopRecord").prop("disabled", false);
  $("#sendAudioMessage").prop("disabled", true);
  audioChunks = [];

  navigator.mediaDevices
    .getUserMedia({ audio: true })
    .then((stream) => {
      audioStream = stream;
      // Let's try to record in WAV format for better compatibility with Google STT
      const options = { mimeType: "audio/wav" };
      if (MediaRecorder.isTypeSupported("audio/wav")) {
        mediaRecorder = new MediaRecorder(stream, options);
      } else {
        console.warn(
          "audio/wav not supported, falling back to default (likely webm)"
        );
        mediaRecorder = new MediaRecorder(stream); // Fallback to browser default
      }
      mediaRecorder.ondataavailable = (event) => {
        audioChunks.push(event.data);
      };
      mediaRecorder.onstop = () => {
        $("#recordingStatus").text("Recording stopped.");
        $("#sendAudioMessage").prop("disabled", false);
        $("#startRecord").prop("disabled", false);
      };
      mediaRecorder.onerror = (error) => {
        console.error("MediaRecorder error:", error);
        $("#recordingStatus").text("Recording error.");
        $("#audioError").text("Recording error: " + error.name);
        $("#startRecord").prop("disabled", false);
        $("#stopRecord").prop("disabled", true);
        $("#sendAudioMessage").prop("disabled", true);
        if (audioStream) {
          audioStream.getTracks().forEach((track) => track.stop());
        }
      };
      mediaRecorder.start();
    })
    .catch((err) => {
      console.error("Error accessing microphone:", err);
      $("#recordingStatus").text("Microphone access denied.");
      $("#audioError").text(
        "Microphone access denied: " +
          err.name +
          ". Please allow microphone access."
      );
      $("#startRecord").prop("disabled", false);
      $("#stopRecord").prop("disabled", true);
      $("#sendAudioMessage").prop("disabled", true);
    });
}

function stopRecording() {
  if (mediaRecorder && mediaRecorder.state !== "inactive") {
    mediaRecorder.stop();
  }
  if (audioStream) {
    audioStream.getTracks().forEach((track) => track.stop()); // Stop microphone access
  }
  $("#stopRecord").prop("disabled", true);
  // sendAudioMessage button is enabled in mediaRecorder.onstop
}

function sendAudioMessage() {
  $("#recordingStatus").text("Sending audio...");
  $("#sendAudioMessage").prop("disabled", true);
  $("#startRecord").prop("disabled", false);

  // Use the actual mimeType from the recorder if available, otherwise from the blob.
  const mimeType =
    mediaRecorder && mediaRecorder.mimeType
      ? mediaRecorder.mimeType
      : "audio/webm";
  const audioBlob = new Blob(audioChunks, { type: mimeType });

  const reader = new FileReader();
  reader.onload = () => {
    const base64Audio = reader.result.split(",")[1]; // Get base64 string without data:mime/type;base64, prefix
    const payload = { audioData: base64Audio, mimeType: audioBlob.type };
    console.log(
      `Publishing to /app/audio: { mimeType: '${payload.mimeType}', audioData: '...' } (data length: ${payload.audioData.length})`
    );
    stompClient.publish({
      destination: "/app/audio", // A new endpoint for audio messages
      body: JSON.stringify(payload),
    });
    $("#recordingStatus").text("Audio sent!");
    audioChunks = []; // Clear chunks after sending
  };
  reader.onerror = (error) => {
    console.error("Error reading audio blob:", error);
    $("#audioError").text("Error preparing audio for sending.");
  };
  reader.readAsDataURL(audioBlob);
}

function showGreeting(message) {
  $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

function playAudio(base64String) {
  console.log("Attempting to play received audio...");
  const audioSource = `data:audio/mp3;base64,${base64String}`;
  const audio = new Audio(audioSource);
  audio.play().catch((e) => console.error("Error playing audio:", e));
}

$(function () {
  $("form").on("submit", (e) => e.preventDefault());
  $("#connect").click(() => connect());
  $("#disconnect").click(() => disconnect());
  $("#send").click(() => sendName());
  $("#startRecord").click(() => startRecording());
  $("#stopRecord").click(() => stopRecording());
  $("#sendAudioMessage").click(() => sendAudioMessage());
});
