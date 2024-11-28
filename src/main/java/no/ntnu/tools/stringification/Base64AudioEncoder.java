package no.ntnu.tools.stringification;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Utility class for encoding and decoding audio files to and from Base64 strings.
 *
 * <p>This class provides methods to:
 * <ul>
 *   <li>Convert an audio file (.wav) into a Base64 encoded string.</li>
 *   <li>Convert a Base64 encoded string back into an audio file.</li>
 * </ul>
 *
 * <p>Use this class to easily store or transmit audio files in text-based formats.
 * Only WAV format is supported.
 */
public class Base64AudioEncoder {

    /**
     * Converts an audio file into a Base64 encoded string.
     *
     * @param audioFile The audio file to be converted.
     * @return A Base64 encoded string representing the audio file.
     * @throws IOException if the audio file cannot be read.
     */
    public static String audioToString(File audioFile) throws IOException {

        if (audioFile == null) {
            throw new IllegalArgumentException("Audio file cannot be null");
        }

        try (FileInputStream fileInputStream = new FileInputStream(audioFile)) {
            byte[] audioBytes = new byte[(int) audioFile.length()];
            fileInputStream.read(audioBytes);

            // Encode the byte array to Base64 and return as a string
            return Base64.getEncoder().encodeToString(audioBytes);
        }

    }

    /**
     * Converts a Base64 encoded string back into an audio file.
     *
     * @param base64String The Base64 string representing the audio file.
     * @param outputFile The file to write the decoded audio data to.
     * @return The file with the decoded audio data.
     * @throws IOException if the Base64 string cannot be decoded or the file cannot be written.
     */
    public static File stringToAudio(String base64String) throws IOException {
        File outputFile = File.createTempFile("audio", ".wav");
        byte[] decodedBytes = Base64.getDecoder().decode(base64String);
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(decodedBytes);
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            byteArrayOutputStream.write(decodedBytes);
            byteArrayOutputStream.writeTo(new FileOutputStream(outputFile));
        }
        return outputFile;
    }
}