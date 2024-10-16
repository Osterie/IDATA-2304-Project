package no.ntnu.greenhouse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import no.ntnu.tools.Logger;


public class NodeHandler implements Runnable {
    private final SensorActuatorNode node;
    private final PrintWriter socketWriter;
    private final BufferedReader socketReader;

    public NodeHandler(SensorActuatorNode node, PrintWriter socketWriter, BufferedReader socketReader) {
        this.node = node;
        this.socketWriter = socketWriter;
        this.socketReader = socketReader;
    }

    @Override
    public void run() {
        try {
            while (true) {
                // Simulate the node's periodic tasks, such as sensor data updates
                // TODO get data.
                // String sensorData = "Data";  // Get sensor data from the node
                // socketWriter.println(sensorData);          // Send the sensor data to the intermediary server
                // Logger.info("Sensor data sent: " + sensorData);

                // Check if there are control commands to execute
                if (socketReader.ready()) {
                    String controlCommand = socketReader.readLine();  // Read command from the server
                    if (controlCommand != null) {
                        Logger.info("Received command for node: " + controlCommand);
                        handleControlCommand(controlCommand);  // Handle the control command (fan, heater, etc.)
                    }
                }

                // Simulate periodic updates by sleeping for a while (e.g., 5 seconds)
                Thread.sleep(5000); 
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handleControlCommand(String command) {
        // TODO handle commands. implement Message and Command classes.
        // Process the control command, e.g., turn on/off fans, heaters, etc.
        if (command.contains("TURN_ON_FAN")) {
            // node.turnOnFan();
            Logger.info("Fan turned on for node " + node.getId());
        } else if (command.contains("TURN_OFF_FAN")) {
            // node.turnOffFan();
            Logger.info("Fan turned off for node " + node.getId());
        }
        // Add more logic for other commands (heaters, windows, etc.)
    }
}
