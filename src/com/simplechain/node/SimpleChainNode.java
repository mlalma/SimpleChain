package com.simplechain.node;

import com.google.gson.Gson;
import com.simplechain.data.NodeData;
import com.simplechain.protocol.BaseMessage;
import com.simplechain.network.client.NetworkClient;
import com.simplechain.network.server.NetworkServer;
import com.simplechain.network.server.NetworkServerInConnection;
import com.simplechain.network.server.NetworkServerMessageHandler;
import com.simplechain.protocol.NodeDiscoveryProtocol;
import com.simplechain.protocol.NodeDiscoveryProtocol.RegisterMessage;
import com.simplechain.protocol.NodeProtocol;
import java.net.UnknownHostException;
import org.omg.CORBA.NO_IMPLEMENT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.util.*;

// SimpleChain Node code
public class SimpleChainNode implements NetworkServerMessageHandler {

  private static Logger logger = LoggerFactory.getLogger(SimpleChainNode.class);

  private final String nodeName;
  private final String version = "0.1";
  private final InetAddress address;
  private final int port;

  private final NetworkServer server;

  private final Gson gson = new Gson();

  private final HashMap<String, NodeData> networkNodes = new HashMap<>();
  private final HashMap<String, NodeData> potentialNetworkNodes = new HashMap<>();

  private final HashSet<NodeJournalListener> listeners = new HashSet<>();

  // Constructor
  public SimpleChainNode(InetAddress address, int serverPort, String nodeName) throws IOException {
    logger.info(
        "Launching node ({}) to address: {} port: {}", nodeName, address.toString(), serverPort);
    server = NetworkServer.startServer(serverPort, address, this);
    this.nodeName = nodeName;
    this.address = address;
    this.port = serverPort;
  }

  // Convenience logger helper method
  private void LOG(String message) {
    logger.info("Node ({}): {}", nodeName, message);
  }

  // Sends message to journal listeners
  private void sendJournalMessage(String message) {
    for (NodeJournalListener listener : listeners) {
      listener.message(nodeName, message);
    }
  }

  // Adds journal listener
  public void addJournalListener(NodeJournalListener listener) {
    listeners.add(listener);
  }

  // Removes journal listener
  public void removeJournalListener(NodeJournalListener listener) {
    listeners.remove(listener);
  }

  // New connection announcer
  public void newConnection(NetworkServerInConnection connection) {
    LOG("New connection from: " + connection.toString());
  }

  // Connection closed with the remote node
  public void connectionClosed(NetworkServerInConnection connection) {
    LOG("Connection closed with: " + connection.toString());
  }

  // Handles ping protocol by sending pong protocol back
  private void handlePingMessage(String message) {
    NodeProtocol.PingMessage pingMessage = gson.fromJson(message, NodeProtocol.PingMessage.class);
    sendJournalMessage(
        "Received ping message from "
            + pingMessage.connectionIp
            + ":"
            + pingMessage.connectionPort);

    sendPong(pingMessage.connectionIp, pingMessage.connectionPort, pingMessage.nonce);
  }

  // Handles pong protocol by doing nothing
  private void handlePongMessage(String message) {
    NodeProtocol.PongMessage pongMessage = gson.fromJson(message, NodeProtocol.PongMessage.class);
    sendJournalMessage(
        "Received pong message from "
            + pongMessage.connectionIp
            + ":"
            + pongMessage.connectionPort);
  }

  private void handleRegisterNodeMessage(String message) {
    NodeDiscoveryProtocol.RegisterMessage registerMessage = gson.fromJson(message, NodeDiscoveryProtocol.RegisterMessage.class);
    // TO_DO: If valid message, create NodeData and add it to active nodes list
    // TO_DO: Send ACK message back to node
  }

  // Handles Hello handshake protocol for other node to register to this node
  /*public void handleHelloMessage(String message) {
      NodeDiscoveryProtocol.HelloMsg helloMsg = gson.fromJson(message, NodeDiscoveryProtocol.HelloMsg.class);
      NodeDiscoveryProtocol.HelloMsgAck msgAck = new NodeDiscoveryProtocol.HelloMsgAck();
      msgAck.connectionIp = address.getHostAddress();
      msgAck.connectionPort = port;
      msgAck.confirmation = true;
      try {
          NetworkClient msgReply = new NetworkClient(
                  InetAddress.getByName(helloMsg.node.connectionIp),
                  helloMsg.node.connectionPort);
          msgReply.sendData(msgAck.toString());
          msgReply.closeConnection();
          LOG("HelloMsgAck sent to node (" + helloMsg.node.name + ")");
      } catch (Exception ex) {
          ex.printStackTrace();
      }
  }*/

  public void messageReceived(NetworkServerInConnection connection, String message) {
    LOG("Received message " + message.trim() + ". " + connection.toString());
    BaseMessage msg = gson.fromJson(message, BaseMessage.class);

    if (msg.type.equals(NodeProtocol.PingMessage.TYPE)) {
      handlePingMessage(message);
    } else if (msg.type.equals(NodeProtocol.PongMessage.TYPE)) {
      handlePongMessage(message);
    } else if (msg.type.equals(RegisterMessage.TYPE)){
      handleRegisterNodeMessage(message);
    } /*else if (msg.type.equalsIgnoreCase(NodeDiscoveryProtocol.HelloMsg.TYPE)) {
          handleHelloMessage(message);
      } else if (msg.type.equalsIgnoreCase(NodeDiscoveryProtocol.HelloMsgAck.TYPE)) {
          NodeDiscoveryProtocol.HelloMsgAck helloMsgAck = gson.fromJson(message, NodeDiscoveryProtocol.HelloMsgAck.class);
          NodeData nodeData = potentialNetworkNodes.get(helloMsgAck.connectionIp + ":" + helloMsgAck.connectionPort);
      } else if (msg.type.equalsIgnoreCase(NodeDiscoveryProtocol.RequestNodeListReplyMsg.TYPE)) {
          NodeDiscoveryProtocol.RequestNodeListReplyMsg requestNodeListReplyMsg =
                  gson.fromJson(message, NodeDiscoveryProtocol.RequestNodeListReplyMsg.class);

          for (NodeData potentialNode : requestNodeListReplyMsg.nodeList) {
              String nodeIdentifier = potentialNode.connectionIp + ":" + potentialNode.connectionPort;
              if (networkNodes.get(nodeIdentifier) == null) {
                  potentialNetworkNodes.put(nodeIdentifier, potentialNode);
              }
          }
          LOG("Received a new list of network nodes. New node count on network: " + potentialNetworkNodes.size());
      }
       /*   NodeDiscoveryProtocol.NodeDiscoveryData nodeData = node;
          nodeData.lastDataPacketReceived = System.currentTimeMillis();

          if (msg.type.equalsIgnoreCase(NodeDiscoveryProtocol.HelloMsgAck.TYPE)) {
              NodeDiscoveryProtocol.HelloMsgAck msgAck = gson.fromJson(protocol, NodeDiscoveryProtocol.HelloMsgAck.class);
              if (msgAck.confirmation) {
                  NodeDiscoveryProtocol.HelloMsgAckAck msgAckAck = new NodeDiscoveryProtocol.HelloMsgAckAck();
                  msgAckAck.confirmation = true;
                  msgAckAck.type = NodeDiscoveryProtocol.HelloMsgAckAck.TYPE;
                  node.outConnection.sendData(msgAckAck.toString());
              }
          } else  else if (msg.type.equalsIgnoreCase(NodeDiscoveryProtocol.HelloMsgAckAck.TYPE)) {
              if (!nodeData.connectionConfirmed) {
                  Log.debug("Hello protocol ack ack received. " + connection.toString());
                  nodeData.connectionConfirmed = true;
              } else {
                  NodeDiscoveryProtocol.NodeDiscoveryErrorMsg errorMsg = new NodeDiscoveryProtocol.NodeDiscoveryErrorMsg();
                  errorMsg.errorCode = -101;
                  errorMsg.errorMsg = "Invalid protocol, connection ack ack already received";
                  nodeData.outConnection.sendData(errorMsg.toString());
              }
          } else if (msg.type.equalsIgnoreCase(NodeDiscoveryProtocol.RequestNodeListMsg.TYPE)) {
              Log.debug("Request for returning all available nodes. " + connection.toString());
              NodeDiscoveryProtocol.RequestNodeListMsg nodeListMsg = gson.fromJson(protocol, NodeDiscoveryProtocol.RequestNodeListMsg.class);

              List<NodeDiscoveryProtocol.NodeDiscoveryData> includedNodes;
              if (nodeListMsg.maxCount > 0) {
                  List<NodeDiscoveryProtocol.NodeDiscoveryData> nodes = new ArrayList<>(networkNodes);
                  Collections.shuffle(nodes);
                  includedNodes = nodes.stream()
                          .filter(filteredConnection -> filteredConnection.inConnection != connection)
                          .filter(filteredConnection -> filteredConnection.connectionConfirmed)
                          .limit(nodeListMsg.maxCount)
                          .collect(Collectors.toList());
              } else {
                  includedNodes = new ArrayList<>();
              }

              NodeDiscoveryProtocol.RequestNodeListReplyMsg nodeListReplyMsg = new NodeDiscoveryProtocol.RequestNodeListReplyMsg();
              nodeListReplyMsg.nodeList = includedNodes;
              nodeData.outConnection.sendData(nodeListReplyMsg.toString());
          }
      }*/
  }

  public void connectToNodes() throws IOException {
    /*logger.info("Starting to connect to other nodes");
    for (NodeDiscoveryProtocol.NodeDiscoveryData potentialNode : potentialNetworkNodes.values()) {
        NodeDiscoveryProtocol.HelloMsg helloMsg = new NodeDiscoveryProtocol.HelloMsg();
        NodeDiscoveryProtocol.NodeDiscoveryData node = new NodeDiscoveryProtocol.NodeDiscoveryData();
        node.version = version;
        node.name = nodeName;
        node.connectionIp = this.address.getHostAddress();
        node.connectionPort = this.port;

        helloMsg.node = node;

        NetworkClient helloMsgSender = new NetworkClient(
                InetAddress.getByName(potentialNode.connectionIp),
                potentialNode.connectionPort);

        logger.info("Sending hello protocol from node (" + nodeName +") to node (" + potentialNode.name + ")");
        helloMsgSender.sendData(helloMsg.toString());
    }

    potentialNetworkNodes.clear();*/
  }

  // Sends ping message to given address
  public boolean sendPing(InetAddress senderAddress, int senderPortNum, String nonce) {
    LOG("Sending ping message to node address " + senderAddress.toString() + ":" + senderPortNum);
    NodeProtocol.PingMessage pingMessage =
        new NodeProtocol.PingMessage(version, address.getHostAddress(), port, nonce);
    try {
      NetworkClient msg = new NetworkClient(senderAddress, senderPortNum);
      msg.sendData(pingMessage.toString());
      msg.closeConnection();
      return true;
    } catch (Exception ex) {
      ex.printStackTrace();
      return false;
    }
  }

  // Sends pong message to given address
  public boolean sendPong(String senderAddressStr, int senderPortNum, String nonce) {
    NodeProtocol.PongMessage pongMessage =
        new NodeProtocol.PongMessage(version, address.getHostAddress(), port, nonce);
    try {
      InetAddress senderAddress = InetAddress.getByName(senderAddressStr);
      NetworkClient msg = new NetworkClient(senderAddress, senderPortNum);
      msg.sendData(pongMessage.toString());
      msg.closeConnection();
      return true;
    } catch (Exception ex) {
      ex.printStackTrace();
      return false;
    }
  }

  // Sends registration request to given address
  public boolean sendRegistration(InetAddress senderAddress, int senderPortNum) {
    LOG(
        "Sending registration message to node address "
            + senderAddress.toString()
            + ":"
            + senderPortNum);
    NodeDiscoveryProtocol.RegisterMessage registerMessage =
        new NodeDiscoveryProtocol.RegisterMessage(
            nodeName, version, address.getHostAddress(), port);
    try {
      NetworkClient msg = new NetworkClient(senderAddress, senderPortNum);
      msg.sendData(registerMessage.toString());
      msg.closeConnection();
      return true;
    } catch (Exception ex) {
      ex.printStackTrace();
      return false;
    }
  }

  // Sends error message
  public boolean sendErrorMessage(
      InetAddress senderAddress, int senderPortNum, String messageBack) {
    LOG("Sending error message to node address " + senderAddress.toString() + ":" + senderPortNum);
    NodeProtocol.NodeErrorMsg errorMsg =
        new NodeProtocol.NodeErrorMsg(
            version,
            address.getHostAddress(),
            port,
            "Invalid error protocol, not recognized by the Node");
    try {
      NetworkClient msg = new NetworkClient(senderAddress, senderPortNum);
      msg.sendData(errorMsg.toString());
      msg.closeConnection();
      return true;
    } catch (Exception ex) {
      ex.printStackTrace();
      return false;
    }
  }

  // Closes node
  public void closeNode() throws IOException {
    server.close();
    listeners.clear();
  }
}
