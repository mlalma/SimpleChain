package com.simplechain.network.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class NetworkClient {

    private final Socket socket;
    private final PrintWriter writer;

    public NetworkClient(InetAddress address, int port) throws IOException {
        socket = new Socket(address, port);
        writer = new PrintWriter(socket.getOutputStream(), true);
    }

    public void sendData(String data) {
        writer.print(data);
        writer.flush();
    }

    public void closeConnection() throws IOException {
        writer.close();
        socket.close();
    }
}
