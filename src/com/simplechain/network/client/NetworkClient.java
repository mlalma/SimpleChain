package com.simplechain.network.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import static com.google.common.base.Preconditions.checkNotNull;

// Network client for sending data to another node
public class NetworkClient {

  // Sends data via socket
  static public boolean sendData(final InetAddress address, final int port, final String data) {
    checkNotNull(address);
    checkNotNull(data);
    try (Socket socket = new Socket(address, port);
        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
      writer.print(data);
      writer.flush();
    } catch (IOException e) {
      return false;
    }
    return true;
  }

  // Private constructor
  private NetworkClient() {}
}
