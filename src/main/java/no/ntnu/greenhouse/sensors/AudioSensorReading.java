package no.ntnu.greenhouse.sensors;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import no.ntnu.greenhouse.SensorType;
import no.ntnu.tools.Logger;
import no.ntnu.tools.stringification.Base64AudioEncoder;

/**
 * An audio sensor reading which represents the current state of an audio sensor.
 */
public class AudioSensorReading extends SensorReading {

    private File audioFile;
    private String fileExtension;
  
    /**
     * Create an audio sensor reading.
     *
     * @param type The type of the sensor.
     */
    public AudioSensorReading(SensorType type) {
      super(type);
      this.audioFile = null;
    }
  
    /**
     * Create an audio sensor reading with a specific starting file.
     *
     * @param type The type of the sensor.
     * @param audioFilePath The file path to the audio data.
     */
    public AudioSensorReading(SensorType type, File startingFile) {
      super(type);
      this.audioFile = startingFile;
    }

    /**
     * Get the audio file.
     *
     * @return The audio file
     */
    public File getAudioFile() {
      return audioFile;
    }

   /**
   * Generate a random audio file from the given file path.
   * File format must be .wav
   *
   * @param audioFilePath The file path to search for audio files
   */
  public void generateRandomAudio(String audioFilePath) {
      File chosenFile = null;
  
      try {
          File dir = new File(audioFilePath);
          // Logger.info("Looking for audio files in: " + dir.getAbsolutePath());
          // Logger.info("Files in directory: " + dir.listFiles().length);
          File[] files = dir.listFiles((d, name) -> name.endsWith(".wav"));
          if (files != null && files.length > 0) {
              int randomIndex = new Random().nextInt(files.length);
              chosenFile = files[randomIndex];
  
              // Extract file extension for the chosen file
              this.fileExtension = chosenFile.getName().substring(chosenFile.getName().lastIndexOf(".") + 1);
          } else {
              Logger.info("No audio files found in the specified directory.");
          }
      } catch (Exception e) {
          System.err.println("An error occurred while loading the audio file.");
          e.printStackTrace();
      }
  
      // Update current audio file if an audio file was successfully loaded
      this.audioFile = chosenFile;
    }


    public String getFormatted(){
        return this.getType() + " = " + this.getAudioFormatted() + " " + this.fileExtension;
    }


 /**
 * Get a Base64 encoded string representation of the audio file.
 *
 * @return A Base64 encoded string representing the audio file
 */
 public String getAudioFormatted() {
  try {
    return Base64AudioEncoder.audioToString(this.audioFile);
  } catch (IOException e) {
    return "Error encoding audio";
  }
  catch (IllegalArgumentException e){
    return "No audio file";
  }
}

/**
 * Indicates whether some other object is "equal to" this one.
 * 
 * @param o the reference object with which to compare.
 * @return {@code true} if this object is the same as the obj
 *         argument; {@code false} otherwise.
 */
@Override
public boolean equals(Object o) {
    if (this == o) {
        return true;
    }
    if (o == null || getClass() != o.getClass()) {
        return false;
    }
    AudioSensorReading that = (AudioSensorReading) o;
    return audioFile.equals(that.audioFile);
}

/**
   * Get a string representation of the sensor reading.
   *
   * @return A string representation of the sensor reading
   */
  @Override
  public String toString() {
    return "{ type=" + this.type + ", image=" + this.audioFile.toString() + ", fileExtension=" + this.fileExtension + " }";
  }
}