package no.ntnu.gui.common;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import no.ntnu.greenhouse.sensors.AudioSensorReading;

public class AudioSensorPane {

    private AudioSensorReading sensorReading;
    
    public AudioSensorPane(AudioSensorReading sensorReading) {
        this.sensorReading = sensorReading;
    }

    public Node createContent() {
    AudioSensorReading audioSensor = this.sensorReading;
    File audioFile = audioSensor.getAudioFile();
    
    if (audioFile == null || !audioFile.exists()) {
        return new Label("Audio file not found");
    }

    // Create a play button
    Button playButton = new Button("Play");

    playButton.setOnAction(e -> {
        try {
            playAudio(audioFile);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    });

    // Create an HBox to hold the play button
    HBox hbox = new HBox(10, playButton);
    return hbox;
    }

    private void playAudio(File audioFile) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
    try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile)) {
        AudioFormat format = audioInputStream.getFormat();
        DataLine.Info info = new DataLine.Info(Clip.class, format);
        Clip audioClip = (Clip) AudioSystem.getLine(info);
        audioClip.open(audioInputStream);
        audioClip.start();
        }
    }
}
