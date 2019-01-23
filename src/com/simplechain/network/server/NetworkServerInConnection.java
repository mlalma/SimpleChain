package com.simplechain.network.server;

import com.simplechain.data.BaseMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class NetworkServerInConnection extends Thread {
    protected final Socket socket;
    private final NetworkServerMessageHandler messageHandler;

    public NetworkServerInConnection(Socket socket, NetworkServerMessageHandler messageHandler) {
        this.socket = socket;
        this.messageHandler = messageHandler;
    }

    @Override
    public void run() {
        InputStream is;
        BufferedReader bufferedReader;

        try {
            is = socket.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(is));
        } catch (IOException ex) {
            ex.printStackTrace();
            messageHandler.connectionClosed(this);
            return;
        }

        StringBuffer sb = new StringBuffer();
        while (true) {
            try {
                String line = bufferedReader.readLine();
                if (line == null) {
                    socket.close();
                    messageHandler.connectionClosed(this);
                    return;
                }

                line = line.trim();
                if (line.endsWith(BaseMessage.END_OF_MESSAGE_MARKER)) {
                    String lastLine = line.substring(0, line.length() - BaseMessage.END_OF_MESSAGE_MARKER.length());
                    if (lastLine.length() > 0) {
                        sb.append(lastLine).append(BaseMessage.NEWLINE);
                    }
                    messageHandler.messageReceived(this, sb.toString());
                    sb = new StringBuffer();
                } else {
                    sb.append(line).append(BaseMessage.NEWLINE);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                messageHandler.connectionClosed(this);
                try { socket.close(); } catch (IOException ex2) { ex2.printStackTrace(); }
                return;
            }
        }
    }

    @Override
    public String toString() {
        return "Network connection from: " + socket.getInetAddress().toString() + " port: " + socket.getPort();
    }
}