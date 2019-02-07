package com.simplechain.network.server;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

// Network server for node to receive connections
public class NetworkServer extends Thread implements NetworkServerConnectionHandler {

  // The minimum port number, which can be used
  public static final int MIN_PORT = 1000;
  public static final int MAX_PORT = 65535;

  // Max amount of messages on the backlog
  private static final int BACKLOG_COUNT = 1000;

  // Receiver server backlog
  private final ServerSocket serverSocket;

  // Message handler for parsing messages and logic for handling them
  private final NetworkServerMessageHandler messageHandler;

  // Connections from other nodes
  private final Set<NetworkServerInConnection> serverConnections;

  // Constructor
  public NetworkServer(
      final int port,
      final InetAddress bindAddress,
      final NetworkServerMessageHandler messageHandler)
      throws IOException, NullPointerException, IllegalArgumentException {
    checkNotNull(bindAddress);
    checkNotNull(messageHandler);
    if (port < MIN_PORT || port > MAX_PORT) {
      throw new IllegalArgumentException("port must be between " + MIN_PORT + " and " + MAX_PORT);
    }

    serverSocket = new ServerSocket(port, BACKLOG_COUNT, bindAddress);
    this.messageHandler = messageHandler;
    this.serverConnections = new HashSet<>();
  }

  // Thread loop. Waits for incoming connections, adds them to server connection set and registers
  // them to message handler
  @Override
  public void run() {
    while (true) {
      try {
        Socket s = serverSocket.accept();
        NetworkServerInConnection inConnection =
            new NetworkServerInConnection(s, this, messageHandler);
        inConnection.start();

        serverConnections.add(inConnection);
        messageHandler.newConnection(inConnection);
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }

  // Closes connection when connection is done
  @Override
  public void connectionClosed(NetworkServerInConnection connection) {
    serverConnections.remove(connection);
    messageHandler.connectionClosed(connection);
  }

  // Static constructor to create server and start it
  public static NetworkServer startServer(
      int port, InetAddress bindAddress, NetworkServerMessageHandler messageHandler)
      throws IOException {
    NetworkServer server = new NetworkServer(port, bindAddress, messageHandler);
    server.start();
    return server;
  }
}
