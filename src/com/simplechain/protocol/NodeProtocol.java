package com.simplechain.protocol;

import com.simplechain.data.NodeData;

// Node protocol messages
public class NodeProtocol {

  // Ping message for sending it to a node
  public static class PingMessage extends BaseMessage {
    public static String TYPE = "PING_MSG";

    // Unique nonce to recognize the node in pong message reply
    public final String nonce;

    // Constructor
    public PingMessage(
        final String protocolVersion,
        final String senderIp,
        final int senderPort,
        final String nonce) {
      super(null, protocolVersion, senderIp, senderPort, TYPE);
      this.nonce = nonce;
    }
  }

  // Pong message, answering to ping message
  public static class PongMessage extends BaseMessage {
    public static String TYPE = "PONG_MSG";

    // Taken from ping message
    public final String nonce;

    public PongMessage(
        final String protocolVersion,
        final String senderIp,
        final int senderPort,
        final String nonce) {
      super(null, protocolVersion, senderIp, senderPort, TYPE);
      this.nonce = nonce;
    }
  }

  // Error message coming from node
  public static class NodeErrorMsg extends BaseMessage {
    public static final String TYPE = "NODE_ERROR_MSG";
    public int errorCode;
    public String errorMsg;

    public NodeErrorMsg(NodeData nodeData) {
      super(nodeData.name, nodeData.version, nodeData.connectionIp, nodeData.connectionPort, TYPE);
    }

    @Override
    public String toString() {
      return super.toString();
    }
  }
}
