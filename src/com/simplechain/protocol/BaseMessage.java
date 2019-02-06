package com.simplechain.protocol;

import com.google.gson.Gson;

// Base message class for all the messages between nodes
public class BaseMessage {
  
  static final Gson gson = new Gson();

  public static final String END_OF_MESSAGE_MARKER = "<eom>";
  public static final String NEWLINE = "\r\n";

  // Name of the node sending the message
  public final String name;
  // Protocol version that sending node supports
  public final String version;
  // Sending node's IP address
  public final String connectionIp;
  // Sending node's port for receiving communication
  public final int connectionPort;
  // Type of the message
  public final String type;

  // Constructor
  public BaseMessage(
      String name, String version, String connectionIp, int connectionPort, String type) {
    this.name = name;
    this.version = version;
    this.connectionIp = connectionIp;
    this.connectionPort = connectionPort;
    this.type = type;
  }

  // Converts the message to serialized format
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(gson.toJson(this));
    sb.append(END_OF_MESSAGE_MARKER);
    sb.append(NEWLINE);
    return sb.toString();
  }
}
