package com.simplechain.protocol;

import com.simplechain.data.NodeData;
import java.util.Set;

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

  // Hello ACK message to signal that registration done and other end can register as well node to
  // their active node list
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

  // Message to query all nodes that the target node has connection to
  public static class NodeDiscoveryQueryMessage extends BaseMessage {
    public static final String TYPE = "NODE_DISCOVERY_Q_MSG";
    public static final int DEFAULT_NODE_COUNT = 10;

    public final int maxNodeCount;

    public NodeDiscoveryQueryMessage(
        final String name,
        final String protocolVersion,
        final String senderIp,
        final int senderPort) {
      super(name, protocolVersion, senderIp, senderPort, TYPE);
      maxNodeCount = DEFAULT_NODE_COUNT;
    }

    public NodeDiscoveryQueryMessage(
        final String name,
        final String protocolVersion,
        final String senderIp,
        final int senderPort,
        final int maxNodeCount) {
      super(name, protocolVersion, senderIp, senderPort, TYPE);
      this.maxNodeCount = maxNodeCount;
    }
  }

  // Message to reply node discovery query
  public static class NodeDiscoveryReplyMessage extends BaseMessage {
    public static final String TYPE = "NODE_DISCOVERY_R_MSG";

    public final Set<NodeData> nodes;

    public NodeDiscoveryReplyMessage(
        final String name,
        final String protocolVersion,
        final String senderIp,
        final int senderPort,
        final Set<NodeData> nodes) {
      super(name, protocolVersion, senderIp, senderPort, TYPE);
      this.nodes = nodes;
    }
  }

  // Private constructor
  private NodeDiscoveryProtocol() {}
}
