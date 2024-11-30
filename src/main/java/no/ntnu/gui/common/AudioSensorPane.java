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
import no.ntnu.greenhouse.sensor.AudioSensorReading;

/**
 * The AudioSensorPane class represents a pane that displays and plays audio from an AudioSensorReading.
 * It provides functionality to create a user interface component that includes a play button for the audio file.
 * If the audio file is not found, it displays a label indicating the file is not found.
 * 
 * <p>Note: This class requires the JavaFX library for the user interface components and the javax.sound.sampled library for audio playback.</p>
 */
public class AudioSensorPane {

    private AudioSensorReading sensorReading;
    
    /**
     * Constructs an AudioSensorPane with the specified AudioSensorReading.
     *
     * @param sensorReading the AudioSensorReading to be associated with this pane
     */
    public AudioSensorPane(AudioSensorReading sensorReading) {
        this.sensorReading = sensorReading;
    }

    /**
     * Creates the content for the AudioSensorPane.
     * This method checks if the audio file exists and creates a play button to play the audio.
     * If the audio file is not found, it returns a label indicating the file is not found.
     *
     * @return a Node containing the play button or a label if the audio file is not found
     */
    public Node createContent() {
    AudioSensorReading audioSensor = this.sensorReading;
    File audioFile = audioSensor.getAudioFile();
    
    if (audioFile == null || !audioFile.exists()) {
        return new Label("Audio file not found");
    }

    // Create a play button
    Button playButton = new Button("Play");
    playButton.cursorProperty().setValue(javafx.scene.Cursor.HAND);

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

    /**
     * Plays an audio file.
     *
     * @param audioFile the audio file to be played
     * @throws UnsupportedAudioFileException if the audio file format is not supported
     * @throws IOException if an I/O error occurs
     * @throws LineUnavailableException if a line cannot be opened because it is unavailable
     */
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
