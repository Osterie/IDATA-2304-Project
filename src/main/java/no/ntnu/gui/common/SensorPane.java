package no.ntnu.gui.common;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import no.ntnu.greenhouse.sensors.AudioSensorReading;
import no.ntnu.greenhouse.sensors.ImageSensorReading;
import no.ntnu.greenhouse.sensors.NumericSensorReading;
import no.ntnu.greenhouse.sensors.Sensor;
import no.ntnu.greenhouse.sensors.SensorReading;
import no.ntnu.tools.Logger;

// TODO refactor, create classes for alot of the logic in this class. This class does too much.
/**
 * A section of GUI displaying sensor data.
 */
public class SensorPane extends TitledPane {
  private final List<SimpleStringProperty> sensorProps = new ArrayList<>();
  private final VBox contentBox = new VBox();
  private final List<Node> thumbnailList = new LinkedList<>(); // TODO why?

  /**
   * Create a sensor pane.
   *
   * @param sensors The sensor data to be displayed on the pane.
   */
  public SensorPane(Iterable<SensorReading> sensors) {
    super();
    initialize(sensors);
  }

  private void initialize(Iterable<SensorReading> sensors) {
    setText("Sensors");
    sensors.forEach(sensor ->
        this.contentBox.getChildren().add(createAndRememberSensorLabel(sensor))
    );
    setContent(this.contentBox);
  }

  /**
   * Create an empty sensor pane, without any data.
   */
  public SensorPane() {
    initialize(new LinkedList<>());
  }

  /**
   * Create a sensor pane.
   * Wrapper for the other constructor with SensorReading-iterable parameter
   *
   * @param sensors The sensor data to be displayed on the pane.
   */
  public SensorPane(List<Sensor> sensors) {
    try {
      initialize(sensors.stream().map(Sensor::getReading).toList());
    } catch (IllegalStateException e) {
      if (e.getMessage().equals("The sensor is off.")) {
        System.out.println("Cannot add sensor to sensor pane becuase sensor is off");
      } else {
        throw e;
      }
    }
  }

  /**
   * Update the GUI according to the changes in sensor data.
   *
   * @param sensors The sensor data that has been updated
   */
  public void update(Iterable<SensorReading> sensors) {
    int index = 0;
    for (SensorReading sensor : sensors) {
      updateSensorLabel(sensor, index++);
    }
  }

  /**
   * Update the GUI according to the changes in sensor data.
   * Wrapper for the other method with SensorReading-iterable parameter
   *
   * @param sensors The sensor data that has been updated
   */
  public void update(List<Sensor> sensors) {
    try {
      update(sensors.stream().map(Sensor::getReading).toList());
    } catch (IllegalStateException e) {
      if (e.getMessage().equals("The sensor is off.")) {
        System.out.println("Cannot update sensor becuase sensor is off");
      } else {
        throw e;
      }
    }
  }

  // TODO refactor this logic. Create a method above this method and such. This method should create a label and then another for images.
  // Alternatively, would not be necessary when we have implemented the component builder class.
  private Node createAndRememberSensorLabel(SensorReading sensor) {
    Node nodeToReturn;
    
    if (sensor instanceof NumericSensorReading) {
      nodeToReturn = createNumericSensorLabel(sensor);
    } else if (sensor instanceof ImageSensorReading) {
      nodeToReturn = createImageSensorNode(sensor);
    } else if (sensor instanceof AudioSensorReading) {
      nodeToReturn = createAudioSensorNode(sensor);
    } else {
      return new Label("Unknown sensor type: " + sensor.getClass());
      // throw new IllegalArgumentException("Unknown sensor type: " + sensor.getClass());
    }
    return nodeToReturn;
  }


  private Node createAudioSensorNode(SensorReading sensor) {
    AudioSensorReading audioSensor = (AudioSensorReading) sensor;
    File audioFile = audioSensor.getAudioFile();
    
    if (audioFile == null || !audioFile.exists()) {
        return new Label("Audio file not found");
    }

    // Create a play button
    Button playButton = new Button("Play");
    Label lengthLabel = new Label();

    playButton.setOnAction(e -> {
        try {
            playAudio(audioFile);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    });

    // Get the length of the audio file
    try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile)) {
        AudioFormat format = audioInputStream.getFormat();
        long frames = audioInputStream.getFrameLength();
        double durationInSeconds = (frames + 0.0) / format.getFrameRate();
        lengthLabel.setText(String.format("Length: %.2f seconds", durationInSeconds));
    } catch (UnsupportedAudioFileException | IOException ex) {
        lengthLabel.setText("Error reading audio length");
    }

    // Create an HBox to hold the play button and length label
    HBox hbox = new HBox(10, playButton, lengthLabel);
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

private Node createNumericSensorLabel(SensorReading sensor){
  SimpleStringProperty props = new SimpleStringProperty(generateSensorText(sensor));
    sensorProps.add(props);
    Label label = new Label();
    label.textProperty().bind(props);
  return label;
}

  
  /**
   * Creates a JavaFX Node that displays a thumbnail image for the given sensor reading.
   * The thumbnail is clickable and opens a new window displaying the full image.
   *
   * @param sensor the sensor reading containing the image data
   * @return a VBox containing the image label and thumbnail
   */
  private Node createImageSensorNode(SensorReading sensor) {
    Logger.info("Creating image view for thumbnail");
    ImageSensorReading imageSensor = (ImageSensorReading) sensor;

    //TODO REFACTOR TO GET IMAGE FROM SENSOR
    // Generate image
    imageSensor.generateRandomImage("images/");
    BufferedImage bufferedImage = imageSensor.getImage();
    if (bufferedImage == null) {
        Logger.error("Buffered image is null");
        return new Label("No image found");
    }

    // Convert to JavaFX Image
    Image image = SwingFXUtils.toFXImage(bufferedImage, null);

    // Create a small thumbnail
    ImageView thumbnail = new ImageView(image);
    thumbnail.setFitWidth(100); // Set desired thumbnail width
    thumbnail.setPreserveRatio(true); // Maintain aspect ratio
    thumbnail.cursorProperty().setValue(javafx.scene.Cursor.HAND);

    // Add click listener to open a new window
    thumbnail.setOnMouseClicked(event -> showFullImage(image));

    Label imageLabel = new Label("Image: ");
    VBox imageNode = new VBox(5); // Add spacing between items
    imageNode.getChildren().addAll(imageLabel, thumbnail);

    // Add thumbnail to the UI
    addThumbnailToUI(imageNode);

    return imageNode;
  }

  /**
   * Add a thumbnail to the UI.
   * This method will add the thumbnail to the list of thumbnails and update the VBox to show only the last thumbnail.
   * 
   * @param thumbnail The thumbnail to add.
   */
  private void addThumbnailToUI(Node thumbnail) {
    // Add the new thumbnail to the list
    thumbnailList.add(thumbnail);

    //Makes it so that only the last thumbnail is shown
    if (thumbnailList.size() > 1) {
        thumbnailList.remove(0);
    }

    // Update the VBox to show only the thumbnails in the list
    Platform.runLater(() -> {
        contentBox.getChildren().clear();
        contentBox.getChildren().addAll(thumbnailList);
    });
  }

  
  /**
   * Displays the given image in a new window with a specified size.
   *
   * @param image the Image object to be displayed in full size
   */
  private void showFullImage(Image image) {
    // Create a new Stage (window)
    Stage fullImageStage = new Stage();
    fullImageStage.setTitle("Full Image");

    // Create an ImageView for the full image
    ImageView fullImageView = new ImageView(image);
    fullImageView.setPreserveRatio(true);
    fullImageView.setFitWidth(800); // Optional: Set window size
    fullImageView.setFitHeight(600);

    // Add the full image to the scene
    Scene scene = new Scene(new StackPane(fullImageView), 800, 600); // Optional: Set initial size
    fullImageStage.setScene(scene);

    // Show the stage
    fullImageStage.show();
  }

  private String generateSensorText(SensorReading sensor) {
    return sensor.getFormatted();
  }

  private void updateSensorLabel(SensorReading sensor, int index) {
    if (sensorProps.size() > index) {
      SimpleStringProperty props = sensorProps.get(index);
      Platform.runLater(() -> props.set(generateSensorText(sensor)));
    } else {
      Logger.info("Adding sensor[" + index + "]");
      Platform.runLater(() -> contentBox.getChildren().add(createAndRememberSensorLabel(sensor)));
    }
  }
}