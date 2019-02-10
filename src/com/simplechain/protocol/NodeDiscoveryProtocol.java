package com.simplechain.protocol;

// Node discovery protocol messages
public final class NodeDiscoveryProtocol {

  // Hello message to another node for registering the node to their active node list
  public static class RegisterMessage extends BaseMessage {
    public static final String TYPE = "REGISTER_MSG";

    public RegisterMessage(
        final String name,
        final String protocolVersion,
        final String senderIp,
        final int senderPort) {
      super(name, protocolVersion, senderIp, senderPort, TYPE);
    }
  }

  // Hello ACK message to signal that registration done and other end can register as well node to their active node list
  public static class RegisterMessageACK extends BaseMessage {
    public static final String TYPE = "REGISTER_MSG_ACK";

    public RegisterMessageACK(
        final String name,
        final String protocolVersion,
        final String senderIp,
        final int senderPort) {
      super(name, protocolVersion, senderIp, senderPort, TYPE);
    }
  }

  // Private constructor
  private NodeDiscoveryProtocol() {}
}
