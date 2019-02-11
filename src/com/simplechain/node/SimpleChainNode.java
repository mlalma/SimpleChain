package com.simplechain.node;

import static com.simplechain.network.server.NetworkServer.MAX_PORT;
import static com.simplechain.network.server.NetworkServer.MIN_PORT;

import com.google.gson.Gson;
import com.simplechain.data.NodeData;
import com.simplechain.protocol.BaseMessage;
import com.simplechain.network.client.NetworkClient;
import com.simplechain.network.server.NetworkServer;
import com.simplechain.network.server.NetworkServerInConnection;
import com.simplechain.network.server.NetworkServerMessageHandler;
import com.simplechain.protocol.NodeDiscoveryProtocol;
import com.simplechain.protocol.NodeDiscoveryProtocol.NodeDiscoveryQueryMessage;
import com.simplechain.protocol.NodeDiscoveryProtocol.RegisterMessage;
import com.simplechain.protocol.NodeDiscoveryProtocol.RegisterMessageACK;
import com.simplechain.protocol.NodeProtocol;

import static com.simplechain.util.SimpleChainUtil.wrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.util.*;

// SimpleChain Node code
public class SimpleChainNode implements NetworkServerMessageHandler {

  private static final long REPLY_TIMEOUT_DELAY = 5000L;

  private static final Logger logger = LoggerFactory.getLogger(SimpleChainNode.class);

  private static final String CONNECTION_SYMBOL = "@";

  private final String nodeName;
  private final String version = "0.1";
  private final InetAddress address;
  private final int port;

  private final NetworkServer server;

  private final Gson gson = new Gson();
  private final Timer taskTimer = new Timer();

  private final HashMap<String, NodeData> networkNodes = new HashMap<>();
  private final Set<String> potentialNodes = new HashSet<>();
  private final HashSet<NodeJournalListener> listeners = new HashSet<>();

  // Constructor
  public SimpleChainNode(final InetAddress address, final int serverPort, final String nodeName)
      throws IOException {
    logger.info(
        "Launching node ({}) to address: {} port: {}", nodeName, address.toString(), serverPort);
    server = NetworkServer.startServer(serverPort, address, this);
    this.nodeName = nodeName;
    this.address = address;
    this.port = serverPort;
  }

  // Convenience logger helper method
  private void LOG(final String message) {
    logger.info("Node ({}): {}", nodeName, message);
  }

  // Sends message to journal listeners
  private void sendJournalMessage(final String message) {
    for (NodeJournalListener listener : listeners) {
      listener.message(nodeName, message);
    }
  }

  // Adds journal listener
  public void addJournalListener(final NodeJournalListener listener) {
    listeners.add(listener);
  }

  // Removes journal listener
  public void removeJournalListener(final NodeJournalListener listener) {
    listeners.remove(listener);
  }

  // New connection announcer
  public void newConnection(final NetworkServerInConnection connection) {
    LOG("New connection from: " + connection.toString());
  }

  // Connection closed with the remote node
  public void connectionClosed(final NetworkServerInConnection connection) {
    LOG("Connection closed with: " + connection.toString());
  }

  // Returns unique node id based on IP address and port
  private String nodeId(String address, int port) {
    return address + CONNECTION_SYMBOL + port;
  }

  // Returns unique node id based on IP address and port
  private String nodeId(NodeData node) {
    return nodeId(node.connectionIp, node.connectionPort);
  }

  // Handles ping protocol by sending pong protocol back
  private void handlePingMessage(final String message) {
    NodeProtocol.PingMessage pingMessage = gson.fromJson(message, NodeProtocol.PingMessage.class);
    sendJournalMessage(
        "Received ping message from "
            + pingMessage.connectionIp
            + ":"
            + pingMessage.connectionPort);

    sendPong(pingMessage.connectionIp, pingMessage.connectionPort, pingMessage.nonce);
  }

  // Handles pong protocol by doing nothing
  private void handlePongMessage(final String message) {
    NodeProtocol.PongMessage pongMessage = gson.fromJson(message, NodeProtocol.PongMessage.class);
    sendJournalMessage(
        "Received pong message from "
            + pongMessage.connectionIp
            + ":"
            + pongMessage.connectionPort);
  }

  // Handles register node message and sends ACK message back
  private void handleRegisterNodeMessage(final String message) {
    NodeDiscoveryProtocol.RegisterMessage registerMessage =
        gson.fromJson(message, NodeDiscoveryProtocol.RegisterMessage.class);

    sendJournalMessage(
        "Received registration message from "
            + registerMessage.connectionIp
            + ":"
            + registerMessage.connectionPort);

    if (registerMessage.connectionIp != null
        && registerMessage.connectionPort >= MIN_PORT
        && registerMessage.connectionPort <= MAX_PORT) {
      NodeData newNode = new NodeData();
      newNode.connectionIp = registerMessage.connectionIp;
      newNode.connectionPort = registerMessage.connectionPort;
      newNode.lastMessageReceived = System.currentTimeMillis();
      newNode.name = registerMessage.name;
      newNode.version = registerMessage.version;

      if (sendRegistrationAck(newNode)) {
        networkNodes.put(nodeId(newNode), newNode);
      }
    }
  }

  // Handles register node ACK message
  private void handleRegisterACKNodeMessage(final String message) {
    RegisterMessageACK registerMessageACK = gson.fromJson(message, RegisterMessageACK.class);

    sendJournalMessage(
        "Received registration ack message from "
            + registerMessageACK.connectionIp
            + ":"
            + registerMessageACK.connectionPort);

    if (registerMessageACK.connectionIp != null
        && registerMessageACK.connectionPort >= MIN_PORT
        && registerMessageACK.connectionPort <= MAX_PORT) {
      NodeData newNode = new NodeData();
      newNode.connectionIp = registerMessageACK.connectionIp;
      newNode.connectionPort = registerMessageACK.connectionPort;
      newNode.lastMessageReceived = System.currentTimeMillis();
      newNode.name = registerMessageACK.name;
      newNode.version = registerMessageACK.version;

      String nodeId = nodeId(newNode);

      if (potentialNodes.contains(nodeId)) {
        networkNodes.put(nodeId, newNode);
        potentialNodes.remove(nodeId);
      }
    }
  }

  private void handleNodeDiscoveryQueryMessage(String message) {
    NodeDiscoveryQueryMessage queryMessage = gson
        .fromJson(message, NodeDiscoveryQueryMessage.class);

    sendJournalMessage(
        "Received node discovery request from "
            + queryMessage.connectionIp
            + ":"
            + queryMessage.connectionPort);

    if (queryMessage.connectionIp != null && queryMessage.connectionPort >= MIN_PORT && queryMessage.connectionPort <= MAX_PORT) {
      // TO_DO: Send message back with list of nodes
    }
  }

  // TO_DO: Heartbeat to send connection live messages consistently
  // TO_DO: Tests for the node discovery & node discovery list back

  // Message handler
  public void messageReceived(NetworkServerInConnection connection, String message) {
    LOG("Received message " + message.trim() + ". " + connection.toString());
    BaseMessage msg = gson.fromJson(message, BaseMessage.class);

    // Check if need to update the last message received field for indicating active node
    if (msg.connectionIp != null) {
      String nodeId = msg.connectionIp + CONNECTION_SYMBOL + msg.connectionPort;
      NodeData nodeData = networkNodes.get(nodeId);
      if (nodeData != null) {
        nodeData.lastMessageReceived = System.currentTimeMillis();
      }
    }

    if (msg.type.equals(NodeProtocol.PingMessage.TYPE)) {
      handlePingMessage(message);
    } else if (msg.type.equals(NodeProtocol.PongMessage.TYPE)) {
      handlePongMessage(message);
    } else if (msg.type.equals(RegisterMessage.TYPE)) {
      handleRegisterNodeMessage(message);
    } else if (msg.type.equals(RegisterMessageACK.TYPE)) {
      handleRegisterACKNodeMessage(message);
    } else if (msg.type.equals(NodeDiscoveryQueryMessage.TYPE)) {
      handleNodeDiscoveryQueryMessage(message);
    }
  }

  // Sends ping message to given address
  public boolean sendPing(InetAddress senderAddress, int senderPortNum, String nonce) {
    LOG("Sending ping message to " + senderAddress.toString() + ":" + senderPortNum);
    NodeProtocol.PingMessage pingMessage =
        new NodeProtocol.PingMessage(version, address.getHostAddress(), port, nonce);

    return NetworkClient.sendData(senderAddress, senderPortNum, pingMessage.toString());
  }

  // Sends ping to tracked node
  public boolean sendPing(NodeData data, String nonce) {
    try {
      InetAddress address = InetAddress.getByName(data.connectionIp);
      data.lastMessageSent = System.currentTimeMillis();
      return sendPing(address, data.connectionPort, nonce);
    } catch (Exception ex) {
      return false;
    }
  }

  // Sends pong message to given address
  public boolean sendPong(String senderAddressStr, int senderPortNum, String nonce) {
    LOG("Sending pong message to " + senderAddressStr + ":" + senderPortNum);

    NodeProtocol.PongMessage pongMessage =
        new NodeProtocol.PongMessage(version, address.getHostAddress(), port, nonce);

    try {
      InetAddress senderAddress = InetAddress.getByName(senderAddressStr);
      return NetworkClient.sendData(senderAddress, senderPortNum, pongMessage.toString());
    } catch (Exception ex) {
      return false;
    }
  }

  // Sends registration request to given address
  public boolean sendRegistration(InetAddress targetNodeAddress, int targetNodePort) {
    LOG("Sending registration message to " + targetNodeAddress.toString() + ":" + targetNodePort);

    RegisterMessage registerMessage = new RegisterMessage(
        nodeName, version, address.getHostAddress(), port);

    if (NetworkClient.sendData(targetNodeAddress, targetNodePort, registerMessage.toString())) {
      final String potentialNodeId = nodeId(targetNodeAddress.getHostAddress(), targetNodePort);
      potentialNodes.add(potentialNodeId);
      // If reply does not arrive soon enough, remove node from potential node pool
      taskTimer.schedule(wrap(() -> potentialNodes.remove(potentialNodeId)), REPLY_TIMEOUT_DELAY);

      return true;
    } else {
      return false;
    }
  }

  // Sends registration ACK message back
  public boolean sendRegistrationAck(NodeData data) {
    LOG("Sending registration ACK message to " + data.connectionIp + ":" + data.connectionPort);

    try {
      InetAddress targetAddress = InetAddress.getByName(data.connectionIp);
      RegisterMessageACK registerMessageACK = new RegisterMessageACK(
          nodeName, version, address.getHostAddress(), port);

      return NetworkClient.sendData(
          targetAddress, data.connectionPort, registerMessageACK.toString());
    } catch (Exception ex) {
      return false;
    }
  }

  // Sends node discovery query message
  public boolean sendNodeDiscoveryQuery(InetAddress targetAddress, int targetPortNum) {
    LOG("Sending node discovery message to " + targetAddress.getHostAddress() + ":"
        + targetPortNum);

    try {
      NodeDiscoveryQueryMessage queryMessage = new NodeDiscoveryQueryMessage(nodeName, version,
          address.getHostAddress(), port);

      return NetworkClient.sendData(targetAddress, targetPortNum, queryMessage.toString());
    } catch (Exception ex) {
      return false;
    }
  }

  // Sends error message
  public boolean sendErrorMessage(
      InetAddress targetAddress, int targetPortNum, String messageBack) {
    LOG("Sending error message to " + targetAddress.toString() + ":" + targetPortNum);
    NodeProtocol.NodeErrorMsg errorMsg =
        new NodeProtocol.NodeErrorMsg(
            version,
            address.getHostAddress(),
            port,
            "Invalid error protocol, not recognized by the Node");

    return NetworkClient.sendData(targetAddress, targetPortNum, errorMsg.toString());
  }

  // Closes node
  public void closeNode() throws IOException {
    server.close();
    listeners.clear();
  }
}
