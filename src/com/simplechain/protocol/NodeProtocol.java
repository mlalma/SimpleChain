package com.simplechain.protocol;

// Node protocol messages
public final class NodeProtocol {

  // Ping message for sending it to a node
  public static class PingMessage extends BaseMessage {
    public static final String TYPE = "PING_MSG";

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
    public static final String TYPE = "PONG_MSG";

    // Taken from ping message
    public final String nonce;

    // Constructor
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
    public String errorMsg;

    public NodeErrorMsg(final String protocolVersion, final String senderIp, final int senderPort, final String errorMsg) {
      super(null, protocolVersion, senderIp, senderPort, TYPE);
      this.errorMsg = errorMsg;
    }
  }

  // Private constructor
  private NodeProtocol() {}
}
