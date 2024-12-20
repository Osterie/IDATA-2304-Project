package no.ntnu.mockclients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * A mock greenhouse node to act as a client during testing.
 */
public class MockGreenhouseNode {
  private Socket socket;
  private PrintWriter out;
  private BufferedReader in;

  /**
   * Constructor for the mock greenhouse node.
   *
   * @param host The host to connect to.
   * @param port The port to connect to.
   * @throws IOException If an I/O error occurs when creating the socket.
   */
  public MockGreenhouseNode(String host, int port) throws IOException {
    socket = new Socket(host, port);
    out = new PrintWriter(socket.getOutputStream(), true);
    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
  }

  public void sendCommand(String command) {
    out.println(command);
  }

  public String receiveResponse() throws IOException {
    return in.readLine();
  }

  public void close() throws IOException {
    socket.close();
    out.close();
    in.close();
  }
}