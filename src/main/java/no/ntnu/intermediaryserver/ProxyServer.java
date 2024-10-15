

package no.ntnu.intermediaryserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import no.ntnu.tools.Logger;

public class ProxyServer implements Runnable {
    public static final int PORT_NUMBER = 50500;
    private boolean isTcpServerRunning;

    // Using ConcurrentHashMap for thread-safe access
    private final ConcurrentHashMap<String, Socket> controlPanels = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Socket> greenhouseNodes = new ConcurrentHashMap<>();
    private ServerSocket listeningSocket;

    public static void main(String[] args) throws IOException {
        new ProxyServer().startServer();
    }

    // Start the server to listen for client connections
    public void startServer() {
        listeningSocket = this.openListeningSocket(PORT_NUMBER);
        if (listeningSocket != null) {
            this.isTcpServerRunning = true;
            Logger.info("Server started on port " + PORT_NUMBER);
            
            while (isTcpServerRunning) {
                Socket clientSocket = acceptNextClientConnection();
                if (clientSocket != null) {
                    new Thread(new ClientHandler(clientSocket, this)).start();
                }
            }
        }
    }

    public synchronized void stopServer() {
        if (isTcpServerRunning) {
            isTcpServerRunning = false;
            try {
                if (listeningSocket != null && !listeningSocket.isClosed()) {
                    listeningSocket.close();
                }
                Logger.info("Server stopped.");
            } catch (IOException e) {
                Logger.error("Error closing server socket: " + e.getMessage());
            }
        }
    }

    public synchronized void addGreenhouseNode(String nodeId, Socket socket) {
        greenhouseNodes.put(nodeId, socket);
        Logger.info("Greenhouse node added: " + nodeId);
    }

    public synchronized void addControlPanel(String panelId, Socket socket) {
        controlPanels.put(panelId, socket);
        Logger.info("Control panel added: " + panelId);
    }

    public synchronized void removeClient(String clientId, boolean isGreenhouse) {
        if (isGreenhouse) {
            greenhouseNodes.remove(clientId);
            Logger.info("Greenhouse node removed: " + clientId);
        } else {
            controlPanels.remove(clientId);
            Logger.info("Control panel removed: " + clientId);
        }
    }

    public Socket getGreenhouseNode(String nodeId) {
        return greenhouseNodes.get(nodeId);
    }

    public ArrayList<Socket> getGreenhouseNodes() {
        return new ArrayList<>(greenhouseNodes.values());
    }

    public Socket getControlPanel(String panelId) {
        return controlPanels.get(panelId);
    }

    private Socket acceptNextClientConnection() {
        Socket clientSocket = null;
        try {
          clientSocket = listeningSocket.accept();
        } catch (IOException e) {
          System.err.println("Could not accept client connection: " + e.getMessage());
        }
        return clientSocket;
    }

    private ServerSocket openListeningSocket(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            Logger.info("Server listening on port " + port);
            return serverSocket;
        } catch (IOException e) {
            Logger.error("Could not open server socket on port " + port + ": " + e.getMessage());
        }
        return null;
    }

    @Override
    public void run() {
        startServer();
    }
}


// public class ProxyServer implements Runnable {
//     public static final int PORT_NUMBER = 50500;
//     private boolean isTcpServerRunning;

//     // Using ConcurrentHashMap for thread-safe access
//     private final ConcurrentHashMap<String, Socket> controlPanels = new ConcurrentHashMap<>();
//     private final ConcurrentHashMap<String, Socket> greenhouseNodes = new ConcurrentHashMap<>();

//     public static void main(String[] args) throws IOException {
//         new ProxyServer().startServer();
//     }

//     // Start the server to listen for client connections
//     public void startServer() throws IOException {
//         ServerSocket listeningSocket = openListeningSocket(PORT_NUMBER);
//         if (listeningSocket != null) {
//             this.isTcpServerRunning = true;
//             while (this.isTcpServerRunning) {
//                 Socket clientSocket = acceptNextClientConnection(listeningSocket);
//                 if (clientSocket != null) {
//                     new Thread(new ClientHandler(clientSocket, this)).start();
//                 }
//             }
//         }
//     }

//     public void stopServer() {
//         this.isTcpServerRunning = false;
//     }

//     public synchronized void addGreenhouseNode(String nodeId, Socket socket) {
//         greenhouseNodes.put(nodeId, socket);
//         Logger.info("Greenhouse node added: " + nodeId);
//     }

//     public synchronized void addControlPanel(String panelId, Socket socket) {
//         controlPanels.put(panelId, socket);
//         Logger.info("Control panel added: " + panelId);
//     }

//     public synchronized void removeClient(String clientId, boolean isGreenhouse) {
//         if (isGreenhouse) {
//             greenhouseNodes.remove(clientId);
//             Logger.info("Greenhouse node removed: " + clientId);
//         } else {
//             controlPanels.remove(clientId);
//             Logger.info("Control panel removed: " + clientId);
//         }
//     }

//     public Socket getGreenhouseNode(String nodeId) {
//         return greenhouseNodes.get(nodeId);
//     }

//     public ArrayList<Socket> getGreenhouseNodes() {
//         return greenhouseNodes.values().stream().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
//     }

//     public Socket getControlPanel(String panelId) {
//         return controlPanels.get(panelId);
//     }

//     private Socket acceptNextClientConnection(ServerSocket listeningSocket) {
//         try {
//             return listeningSocket.accept();
//         } catch (IOException e) {
//             System.err.println("Could not accept client connection: " + e.getMessage());
//         }
//         return null;
//     }

//     private ServerSocket openListeningSocket(int port) {
//         try {
//             ServerSocket listeningSocket = new ServerSocket(port);
//             System.out.println("Server listening on port " + port);
//             return listeningSocket;
//         } catch (IOException e) {
//             System.err.println("Could not open server socket for port " + port + ": " + e.getMessage());
//         }
//         return null;
//     }

//     @Override
//     public void run() {
//         try {
//             this.startServer();
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }
// }
