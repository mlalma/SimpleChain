package com.simplechain.network.server;

// Interface for handling network server connections
public interface NetworkServerConnectionHandler {

    // Called when connection is closed
    void connectionClosed(NetworkServerInConnection connection);
}
