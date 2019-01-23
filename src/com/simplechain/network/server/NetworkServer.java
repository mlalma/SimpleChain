package com.simplechain.network.server;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class NetworkServer extends Thread implements NetworkServerConnectionHandler {


    public final static int MIN_PORT = 1000;

    private static final int BACKLOG_COUNT = 1000;

    private final ServerSocket serverSocket;
    private final NetworkServerMessageHandler messageHandler;
    private final Set<NetworkServerInConnection> serverConnections;

    public NetworkServer(int port, InetAddress bindAddress, NetworkServerMessageHandler messageHandler) throws IOException {
        serverSocket = new ServerSocket(port, BACKLOG_COUNT, bindAddress);
        this.messageHandler = messageHandler;
        this.serverConnections = new HashSet<>();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket s = serverSocket.accept();
                NetworkServerInConnection inConnection = new NetworkServerInConnection(s, messageHandler);
                inConnection.start();

                serverConnections.add(inConnection);
                messageHandler.newConnection(inConnection);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void connectionClosed(NetworkServerInConnection connection) {
        serverConnections.remove(connection);
        messageHandler.connectionClosed(connection);
    }

    static public NetworkServer startServer(int port, InetAddress bindAddress, NetworkServerMessageHandler messageHandler) throws IOException {
        NetworkServer server = new NetworkServer(port, bindAddress, messageHandler);
        server.start();
        return server;
    }
}
