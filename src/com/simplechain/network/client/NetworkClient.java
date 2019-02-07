package com.simplechain.network.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import static com.google.common.base.Preconditions.checkNotNull;

// Network client for sending data to another node
public class NetworkClient {

  // Socket to the other node
  private final Socket socket;
  // Writer for writing the data
  private final PrintWriter writer;

  // Constructor
  public NetworkClient(final InetAddress address, final int port)
      throws IOException, NullPointerException {
    checkNotNull(address);
    socket = new Socket(address, port);
    writer = new PrintWriter(socket.getOutputStream(), true);
  }

  // Sends data via socket
  public void sendData(final String data) {
    checkNotNull(data);
    writer.print(data);
    writer.flush();
  }

  // Closes connection
  public void closeConnection() throws IOException {
    writer.close();
    socket.close();
  }
}
