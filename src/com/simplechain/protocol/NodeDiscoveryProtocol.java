package com.simplechain.protocol;

import com.simplechain.data.NodeData;

import java.util.List;

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

  /*
  static public class HelloMsgAck extends BaseMessage {
      static final public String TYPE = "HELLO_MSG_ACK";
      public String connectionIp;
      public int connectionPort;
      public boolean confirmation;

      public HelloMsgAck() { super(TYPE); }

      @Override
      public String toString() {
          return super.toString();
      }
  }

  static public class NodeDiscoveryErrorMsg extends BaseMessage {
      static final public String TYPE = "ERROR_MSG";
      public int errorCode;
      public String errorMsg;

      public NodeDiscoveryErrorMsg() { super(TYPE); }

      @Override
      public String toString() {
          return super.toString();
      }
  }

  static public class RequestNodeListMsg extends BaseMessage {
      static final public String TYPE = "REQUEST_NODE_LIST_MSG";
      public int maxCount;

      public RequestNodeListMsg() { super(TYPE); }

      @Override
      public String toString() {
          return super.toString();
      }
  }

  static public class RequestNodeListReplyMsg extends BaseMessage {
      static final public String TYPE = "REQUEST_NODE_LIST_REPLY_MSG";
      public List<NodeData> nodeList;

      public RequestNodeListReplyMsg() { super(TYPE); }

      @Override
      public String toString() {
          return super.toString();
      }
  }
  */

  // Private constructor
  private NodeDiscoveryProtocol() {}
}
