package com.simplechain.network.server;

import com.simplechain.main.Main;
import com.simplechain.protocol.BaseMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Class for handling incoming connection
public class NetworkServerInConnection extends Thread {

  private static Logger logger = LoggerFactory.getLogger(Main.class);

  protected final Socket socket;
  private final NetworkServerMessageHandler messageHandler;
  private final NetworkServerConnectionHandler connectionHandler;

  // Constructor
  public NetworkServerInConnection(
      Socket socket,
      NetworkServerConnectionHandler connectionHandler,
      NetworkServerMessageHandler messageHandler) {
    this.socket = socket;
    this.messageHandler = messageHandler;
    this.connectionHandler = connectionHandler;
  }

  // Thread loop. Reads content until message ends (and then connection gets closed) or if there is
  // IO exception during the transferring of the data. Loop to read the message until end-of-message
  // marker is found and then the message is parsed and connection is closed by the sender
  @Override
  public void run() {
    try {
      StringBuffer sb = new StringBuffer();
      while (true) {
        try (BufferedReader bufferedReader =
            new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
          String line = bufferedReader.readLine();
          if (line == null) {
            socket.close();
            break;
          }

          line = line.trim();
          if (line.endsWith(BaseMessage.END_OF_MESSAGE_MARKER)) {
            String lastLine =
                line.substring(0, line.length() - BaseMessage.END_OF_MESSAGE_MARKER.length());
            if (lastLine.length() > 0) {
              sb.append(lastLine).append(BaseMessage.NEWLINE);
            }
            messageHandler.messageReceived(this, sb.toString());
            sb = new StringBuffer();
          } else {
            sb.append(line).append(BaseMessage.NEWLINE);
          }
        }
      }
    } catch (IOException ex) {
      logger.info(this.toString() + " terminated, message: " + ex.getMessage());
    }
    connectionHandler.connectionClosed(this);
  }

  // Information to string
  @Override
  public String toString() {
    return "Network connection from: "
        + socket.getInetAddress().toString()
        + " port: "
        + socket.getPort();
  }
}
