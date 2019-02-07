package com.simplechain.network.server;

// Message handler
public interface NetworkServerMessageHandler {

  // Called when new connection is established from other node
  void newConnection(NetworkServerInConnection connection);

  // Called when connection is closed to the other node
  void connectionClosed(NetworkServerInConnection connection);

  // Called when message received from another node
  void messageReceived(NetworkServerInConnection connection, String message);
}
