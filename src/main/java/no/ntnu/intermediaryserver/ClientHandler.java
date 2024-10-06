package no.ntnu.intermediaryserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

public class ClientHandler extends Thread {
    private Socket clientSocket;
    private BufferedReader input;
    private PrintWriter output;
    
    // Reference to the maps for storing control panels and greenhouses
    private Map<String, Socket> controlPanels;
    private Map<String, Socket> greenhouseNodes;

    // Constructor to initialize client handler with socket and maps
    public ClientHandler(Socket socket, Map<String, Socket> controlPanels, Map<String, Socket> greenhouseNodes) {
        this.clientSocket = socket;
        this.controlPanels = controlPanels;
        this.greenhouseNodes = greenhouseNodes;
    }

    @Override
    public void run() {
        try {
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            output = new PrintWriter(clientSocket.getOutputStream(), true);

            // Read client type and ID
            String clientType = input.readLine();  // "GREENHOUSE" or "CONTROL_PANEL"
            String clientId = input.readLine();    // Unique ID for the client

            System.out.println("Client connected: " + clientType + " " + clientId);
            

            if (clientType.equals("GREENHOUSE")) {
                greenhouseNodes.put(clientId, clientSocket);
                handleGreenhouse(clientId);
            } else if (clientType.equals("CONTROL_PANEL")) {
                controlPanels.put(clientId, clientSocket);
                handleControlPanel(clientId);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Handle communication with a greenhouse client
    private void handleGreenhouse(String greenhouseId) throws IOException {
        String request;
        while ((request = input.readLine()) != null) {
            // Greenhouse listens for commands or requests
            System.out.println("Received request/command for Greenhouse " + greenhouseId + ": " + request);
        }
    }

    // Handle communication with a control panel client
    private void handleControlPanel(String controlPanelId) throws IOException {
        String request;
        while ((request = input.readLine()) != null) {
            System.out.println("Control Panel " + controlPanelId + " is requesting data.");

            // Forward the request to the relevant greenhouse node
            Socket greenhouseSocket = greenhouseNodes.get("greenhouse1"); // Assuming a single greenhouse for now
            if (greenhouseSocket != null) {
                PrintWriter greenhouseWriter = new PrintWriter(greenhouseSocket.getOutputStream(), true);
                BufferedReader greenhouseReader = new BufferedReader(new InputStreamReader(greenhouseSocket.getInputStream()));

                // Request sensor data from the greenhouse
                greenhouseWriter.println("REQUEST_SENSOR_DATA");
                String sensorData = greenhouseReader.readLine(); // Get the sensor data from the greenhouse

                // Send the sensor data back to the control panel
                output.println(sensorData);
            } else {
                output.println("No greenhouse connected.");
            }
        }
    }
}
