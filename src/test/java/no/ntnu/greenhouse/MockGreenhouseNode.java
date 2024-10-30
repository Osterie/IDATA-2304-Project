package no.ntnu.greenhouse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MockGreenhouseNode {
  private Socket socket;
  private PrintWriter out;
  private BufferedReader in;

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