package com.simplechain.network.server;

public interface NetworkServerMessageHandler {

    void newConnection(NetworkServerInConnection connection);
    void connectionClosed(NetworkServerInConnection connection);
    void messageReceived(NetworkServerInConnection connection, String message);
}
