package no.ntnu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

import no.ntnu.tools.Logger;

public class TcpClient {
    private Socket socket;
    private BufferedReader socketReader;
    private PrintWriter socketWriter;
    private boolean isOn;
    private Queue<String> messageQueue;

    private static final int MAX_RETRIES = 5;
    private static final int RETRY_DELAY_MS = 1000; // Time between retries

    public TcpClient() {
        this.messageQueue = new LinkedList<>();
    }

    public boolean isConnected() {
        return isOn;
    }

    public void connect(String host, int port) {
        try {
            Logger.info("Trying to establish connection to " + host + ":" + port);
            this.socket = new Socket(host, port);
            this.socket.setKeepAlive(true);
            this.socketReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.socketWriter = new PrintWriter(this.socket.getOutputStream(), true);
            this.isOn = true;
            Logger.info("Socket connection established with " + host + ":" + port);
            listenForMessages();
        } catch (IOException e) {
            Logger.error("Could not establish connection to the server: " + e.getMessage());
            reconnect(host, port);
        }
    }

    private void reconnect(String host, int port) {
        int attempts = 0;
        while (!isOn && attempts < MAX_RETRIES) {
            try {
                Thread.sleep(RETRY_DELAY_MS * (int) Math.pow(2, attempts)); // Exponential backoff
                Logger.info("Reconnecting attempt " + (attempts + 1));
                socket = new Socket(host, port);
                socket.setKeepAlive(true);
                socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                socketWriter = new PrintWriter(socket.getOutputStream(), true);
                isOn = true;
                Logger.info("Reconnected successfully to " + host + ":" + port);
                flushBufferedMessages();
                break;
            } catch (IOException | InterruptedException e) {
                attempts++;
                Logger.error("Reconnection attempt " + attempts + " failed: " + e.getMessage());
            }
        }
    }

    protected void listenForMessages() {
        Thread messageListener = new Thread(() -> {
            try {
                while (this.isOn) {
                    String serverMessage = this.socketReader.readLine();
                    if (serverMessage != null) {
                        Logger.info("Received from server: " + serverMessage);
                        handleMessage(serverMessage);
                    }
                }
            } catch (IOException e) {
                Logger.error("Connection lost: " + e.getMessage());
                this.isOn = false;
                reconnect(socket.getInetAddress().getHostAddress(), socket.getPort());
            } finally {
                close();
            }
        });
        messageListener.start();
    }

    public void sendMessage(String message) {
        if (isOn && socketWriter != null) {
            socketWriter.println(message);
            Logger.info("Sent message to server: " + message);
        } else {
            Logger.error("Unable to send message, socket is not connected.");
            messageQueue.offer(message); // Buffer the message
            reconnect(socket.getInetAddress().getHostAddress(), socket.getPort());
        }
    }

    private void flushBufferedMessages() {
        while (!messageQueue.isEmpty()) {
            String message = messageQueue.poll();
            try {
                // Check if the socket is still open
                if (socket != null && !socket.isClosed() && socket.isConnected() && socketWriter != null) {
                    socketWriter.println(message);
                    socketWriter.flush(); // Ensure the message is sent immediately
                    Logger.info("Resent buffered message: " + message);
                } else {
                    throw new IOException("Socket is not open or not connected.");
                }
            } catch (IOException e) {
                Logger.error("Failed to resend buffered message: " + e.getMessage());
                messageQueue.offer(message); // Put it back in the queue for retry later
                break;
            }
        }
    }
    

    public void close() {
        try {
            if (socket != null) socket.close();
            if (socketReader != null) socketReader.close();
            if (socketWriter != null) socketWriter.close();
            isOn = false;
            Logger.info("Socket connection closed.");
        } catch (IOException e) {
            Logger.error("Failed to close socket connection: " + e.getMessage());
        }
    }

    protected void handleMessage(String message) {
        // This will be overridden by subclasses
    }
}
