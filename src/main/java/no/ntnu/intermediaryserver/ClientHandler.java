package no.ntnu.intermediaryserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import no.ntnu.tools.Logger;

public class ClientHandler extends Thread {
    private final Socket clientSocket;
    private final ProxyServer server;
    private BufferedReader socketReader;
    private PrintWriter socketWriter;
    private String clientType;  // "CONTROL_PANEL" or "GREENHOUSE"
    private String clientId;    // Unique ID for the greenhouse node or control panel

    public ClientHandler(Socket socket, ProxyServer server) {
        this.clientSocket = socket;
        this.server = server;
        initializeStreams();
    }

    private void initializeStreams() {
        try {
            clientSocket.setKeepAlive(true); // Enable keep-alive on the socket
            this.socketReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.socketWriter = new PrintWriter(clientSocket.getOutputStream(), true);
            System.out.println("New client connected from " + clientSocket.getRemoteSocketAddress());
        } catch (IOException e) {
            System.err.println("Could not open reader or writer: " + e.getMessage());
        }
    }
    

    @Override
    public void run() {
        identifyClientType();  
        handleClient();
    }

    private void identifyClientType() {
        try {
            String identification = socketReader.readLine();
            String[] parts = identification.split(";");
            clientType = parts[0];
            clientId = parts.length > 1 ? parts[1] : null;

            if ("CONTROL_PANEL".equalsIgnoreCase(clientType) && clientId != null) {
                server.addControlPanel(clientId, clientSocket);
                System.out.println("Connected Control Panel with ID: " + clientId);
            } else if ("GREENHOUSE".equalsIgnoreCase(clientType) && clientId != null) {
                server.addGreenhouseNode(clientId, clientSocket);
                System.out.println("Connected Greenhouse Node with ID: " + clientId);
            } else {
                System.out.println("Unknown client type. Closing connection. Received: " + identification + " Client type: " + clientType + " Client ID: " + clientId);
                clientSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error identifying client type: " + e.getMessage());
        }
    }

    // private void handleClient() {
    //     String clientRequest;
    //     Logger.info(this.clientSocket.getRemoteSocketAddress() + " connected as " + clientType + " with ID " + clientId);
    
    //     // Start a thread to send a heartbeat every 30 seconds
    //     new Thread(() -> {
    //         while (!clientSocket.isClosed()) {
    //             try {
    //                 Thread.sleep(30000); // 30-second interval
    //                 socketWriter.println("PING");
    //             } catch (InterruptedException | IOException e) {
    //                 System.err.println("Heartbeat thread interrupted: " + e.getMessage());
    //             }
    //         }
    //     }).start();
    
    //     try {
    //         while (!clientSocket.isClosed() && (clientRequest = socketReader.readLine()) != null) {
    //             if ("PONG".equals(clientRequest)) {
    //                 continue; // Ignore pong responses to heartbeats
    //             }
    //             String response = processRequest(clientRequest);
    //             if (response != null) {
    //                 socketWriter.println(response);
    //             }
    //         }
    //     } catch (IOException e) {
    //         System.err.println("Error handling client request: " + e.getMessage());
    //     } finally {
    //         server.removeClient(clientId, "GREENHOUSE".equalsIgnoreCase(clientType));
    //     }
    // }
    

    private void handleClient() {
        String clientRequest;
        Logger.info(this.clientSocket.getRemoteSocketAddress() + " connected as " + clientType + " with ID " + clientId);
        try {
            while (!clientSocket.isClosed() && (clientRequest = socketReader.readLine()) != null) {
                String response = processRequest(clientRequest);
                if (response != null) {
                    System.out.println("Response: " + response);
                    String[] responseParts = response.split(";");
                    String target = responseParts[0];
                    String targetId = responseParts[1];
                    String info = responseParts[2];
                    if ("CONTROL_PANEL".equalsIgnoreCase(target)) {
                        Socket controlPanelSocket = server.getControlPanel(targetId);
                        if (controlPanelSocket != null) {
                            try (PrintWriter controlPanelWriter = new PrintWriter(controlPanelSocket.getOutputStream(), true)) {
                                Logger.info("Sending response to control panel " + targetId + ": " + info);
                                controlPanelWriter.println(info);
                            } catch (IOException e) {
                                System.err.println("Failed to send response to control panel: " + e.getMessage());
                            }
                        } else {
                            System.err.println("Control panel not found: " + targetId);
                        }
                    } else if ("GREENHOUSE".equalsIgnoreCase(target)) {
                        Socket greenhouseNodeSocket = server.getGreenhouseNode(targetId);
                        if (greenhouseNodeSocket != null) {
                            try (PrintWriter greenhouseNodeWriter = new PrintWriter(greenhouseNodeSocket.getOutputStream(), true)) {
                                greenhouseNodeWriter.println(info);
                            } catch (IOException e) {
                                System.err.println("Failed to send response to greenhouse node: " + e.getMessage());
                            }
                        } else {
                            System.err.println("Greenhouse node not found: " + targetId);
                        }
                    }

                    // socketWriter.println(response);
                }
            }

            // while ((clientRequest = socketReader.readLine()) != null) {
            //     String response = processRequest(clientRequest);
            //     if (response != null) {
            //         socketWriter.println(response);
            //     }
            // }
        } catch (IOException e) {
            System.err.println("Error handling client request: " + e.getMessage() + " " + e.getStackTrace());
        } finally {
            // server.removeClient(clientId, "GREENHOUSE".equalsIgnoreCase(clientType));
        }
    }

    private String processRequest(String clientRequest) {
        if ("CONTROL_PANEL".equalsIgnoreCase(clientType)) {
            Logger.info("Received command from control panel " + clientId + ": " + clientRequest);
            String[] commandParts = clientRequest.split(";");
            String nodeId = commandParts[1];
            String command = "CONTROL_PANEL;"+ clientId + ";" + commandParts[2];
            if (nodeId.equalsIgnoreCase("ALL")){
                String response = "";
                for (Socket nodeSocket : server.getGreenhouseNodes()) {
                    Logger.info("Sending command to node " + nodeSocket.getRemoteSocketAddress() + ": " + clientRequest);
                    response += sendToGreenhouseNode(nodeSocket, command) + ";"; 
                }
                return response;
                // for (Socket nodeSocket : server.getGreenhouseNodes()) {
                //     try (PrintWriter nodeWriter = new PrintWriter(nodeSocket.getOutputStream(), true)) {
                //         nodeWriter.println(clientRequest);  
                //     } catch (IOException e) {
                //         System.err.println("Failed to send command to node: " + e.getMessage());
                //     }
                // }
                // return "Command sent to all nodes";
            }
            else{
                Socket nodeSocket = server.getGreenhouseNode(nodeId);
                if (nodeSocket != null) {
                    sendToGreenhouseNode(nodeSocket, clientRequest);
                    return "Command sent to node " + nodeId;
                } else {
                    return "Error: Node " + nodeId + " not found";
                }
                // Socket nodeSocket = server.getGreenhouseNode(nodeId);
                // if (nodeSocket != null) {
                //     try (PrintWriter nodeWriter = new PrintWriter(nodeSocket.getOutputStream(), true)) {
                //         nodeWriter.println(clientRequest);  
                //         return "Command sent to node " + nodeId;
                //     } catch (IOException e) {
                //         System.err.println("Failed to send command to node: " + e.getMessage());
                //     }
                // } else {
                //     return "Error: Node " + nodeId + " not found";
                // }
            }
            // Socket nodeSocket = server.getGreenhouseNode(nodeId);

            // if (nodeSocket != null) {
            //     try (PrintWriter nodeWriter = new PrintWriter(nodeSocket.getOutputStream(), true)) {
            //         nodeWriter.println(clientRequest);  
            //         return "Command sent to node " + nodeId;
            //     } catch (IOException e) {
            //         System.err.println("Failed to send command to node: " + e.getMessage());
            //     }
            // } else {
            //     return "Error: Node " + nodeId + " not found";
            // }
        } else if ("GREENHOUSE".equalsIgnoreCase(clientType)) {
            // Parse sensor data or status update to forward to control panel(s)
            Logger.info("Received data from greenhouse " + clientId + ": " + clientRequest);
            return clientRequest;
        }
        return null;
    }

    private String sendToGreenhouseNode(Socket nodeSocket, String clientRequest) {
        String response = "";
        try {
            PrintWriter nodeWriter = new PrintWriter(nodeSocket.getOutputStream(), true);
            nodeWriter.println(clientRequest);  // Sends command to the greenhouse node
            // socketWriter.println(clientRequest);  // Sends command to the greenhouse node
            
            BufferedReader nodeReader = new BufferedReader(new InputStreamReader(nodeSocket.getInputStream()));
            response = nodeReader.readLine();
            Logger.info("Received response from node " + nodeSocket.getRemoteSocketAddress() + ": " + response);
        } catch (IOException e) {
            System.err.println("Failed to send command to node: " + e.getMessage());
            response = "Failed to send command to node " + nodeSocket.getRemoteSocketAddress();
        }
        return response;
        // try {
        //     PrintWriter nodeWriter = new PrintWriter(nodeSocket.getOutputStream(), true);
        //     nodeWriter.println(clientRequest);  // Sends command to the greenhouse node
        // } catch (IOException e) {
        //     System.err.println("Failed to send command to node: " + e.getMessage());
        // }
    }


    // private String sendToGreenhouseNode(Socket nodeSocket, String clientRequest) {
    //     if (nodeSocket == null || nodeSocket.isClosed()) {
    //         return "Node socket is closed or unavailable.";
    //     }
    
    //     try {
    //         PrintWriter nodeWriter = new PrintWriter(nodeSocket.getOutputStream(), true);
    //         nodeWriter.println(clientRequest);
    //         return "Command sent to node";
    //     } catch (IOException e) {
    //         System.err.println("Failed to send command to node: " + e.getMessage());
    //         return "Failed to send command to node due to IOException";
    //     }
    // }
    
}