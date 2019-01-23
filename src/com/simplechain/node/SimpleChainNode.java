package com.simplechain.node;

import com.google.gson.Gson;
import com.simplechain.data.BaseMessage;
import com.simplechain.network.client.NetworkClient;
import com.simplechain.network.server.NetworkServer;
import com.simplechain.network.server.NetworkServerInConnection;
import com.simplechain.network.server.NetworkServerMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.util.*;

public class SimpleChainNode implements NetworkServerMessageHandler {

    private static Logger logger = LoggerFactory.getLogger(SimpleChainNode.class);

    private final String nodeName;
    private final String version = "0.1";
    private final InetAddress address;
    private final int port;

    private final NetworkServer server;

    private final Gson gson = new Gson();

    private HashMap<String, NodeData> networkNodes = new HashMap<>();
    private HashMap<String, NodeData> potentialNetworkNodes = new HashMap<>();

    public SimpleChainNode(InetAddress address, int serverPort, String nodeName) throws IOException  {
        logger.info("Launching node ({}) to address: {} port: {}", nodeName, address.toString(), serverPort);
        server = NetworkServer.startServer(serverPort, address, this);
        this.nodeName = nodeName;
        this.address = address;
        this.port = serverPort;
    }

    private void LOG(String message) {
        logger.info("Node ({}): {}", nodeName, message);
    }

    public void newConnection(NetworkServerInConnection connection) {
        LOG("New connection from: " + connection.toString());
    }

    public void connectionClosed(NetworkServerInConnection connection) {
        LOG("Connection closed with: " + connection.toString());
    }

    public void handlePingMessage(String message) {
        NodeProtocol.PingMessage pingMessage = gson.fromJson(message, NodeProtocol.PingMessage.class);
        NodeProtocol.PongMessage pongMessage = new NodeProtocol.PongMessage();
        pongMessage.connectionIp = address.getHostAddress();
        pongMessage.connectionPort = port;
        pongMessage.version = version;
        try {
            NetworkClient msgReply = new NetworkClient(
                    InetAddress.getByName(pingMessage.connectionIp),
                    pingMessage.connectionPort);
            msgReply.sendData(pongMessage.toString());
            msgReply.closeConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        LOG("Pong message sent to node address " + pingMessage.connectionIp + ":" + pingMessage.connectionPort);
    }

    public void handlePongMessage(String message) {
        LOG("Pong message received");

    }

    public void messageReceived(NetworkServerInConnection connection, String message) {
        LOG("Received message " + message.trim() + ". " + connection.toString());
        BaseMessage msg = gson.fromJson(message, BaseMessage.class);

        if (msg.type.equalsIgnoreCase(NodeProtocol.PingMessage.TYPE)) {
            handlePingMessage(message);
        } else if (msg.type.equalsIgnoreCase(NodeProtocol.PongMessage.TYPE)) {
            handlePongMessage(message);
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
        } else if (msg.type.equalsIgnoreCase(NodeDiscoveryProtocol.HelloMsg.TYPE)) {
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
        } else if (msg.type.equalsIgnoreCase(NodeDiscoveryProtocol.HelloMsgAck.TYPE)) {
            NodeDiscoveryProtocol.HelloMsgAck helloMsgAck = gson.fromJson(message, NodeDiscoveryProtocol.HelloMsgAck.class);
            NodeData nodeData = potentialNetworkNodes.get(helloMsgAck.connectionIp + ":" + helloMsgAck.connectionPort);
        }
         /*   NodeDiscoveryProtocol.NodeDiscoveryData nodeData = node;
            nodeData.lastDataPacketReceived = System.currentTimeMillis();

            if (msg.type.equalsIgnoreCase(NodeDiscoveryProtocol.HelloMsgAck.TYPE)) {
                NodeDiscoveryProtocol.HelloMsgAck msgAck = gson.fromJson(message, NodeDiscoveryProtocol.HelloMsgAck.class);
                if (msgAck.confirmation) {
                    NodeDiscoveryProtocol.HelloMsgAckAck msgAckAck = new NodeDiscoveryProtocol.HelloMsgAckAck();
                    msgAckAck.confirmation = true;
                    msgAckAck.type = NodeDiscoveryProtocol.HelloMsgAckAck.TYPE;
                    node.outConnection.sendData(msgAckAck.toString());
                }
            } else  else if (msg.type.equalsIgnoreCase(NodeDiscoveryProtocol.HelloMsgAckAck.TYPE)) {
                if (!nodeData.connectionConfirmed) {
                    Log.debug("Hello message ack ack received. " + connection.toString());
                    nodeData.connectionConfirmed = true;
                } else {
                    NodeDiscoveryProtocol.NodeDiscoveryErrorMsg errorMsg = new NodeDiscoveryProtocol.NodeDiscoveryErrorMsg();
                    errorMsg.errorCode = -101;
                    errorMsg.errorMsg = "Invalid message, connection ack ack already received";
                    nodeData.outConnection.sendData(errorMsg.toString());
                }
            } else if (msg.type.equalsIgnoreCase(NodeDiscoveryProtocol.RequestNodeListMsg.TYPE)) {
                Log.debug("Request for returning all available nodes. " + connection.toString());
                NodeDiscoveryProtocol.RequestNodeListMsg nodeListMsg = gson.fromJson(message, NodeDiscoveryProtocol.RequestNodeListMsg.class);

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
            } else {
                NodeProtocol.NodeErrorMsg errorMsg = new NodeProtocol.NodeErrorMsg();
                errorMsg.errorCode = -200;
                errorMsg.errorMsg = "Invalid error message, not recognized by the Node";
                nodeData.outConnection.sendData(errorMsg.toString());
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

            logger.info("Sending hello message from node (" + nodeName +") to node (" + potentialNode.name + ")");
            helloMsgSender.sendData(helloMsg.toString());
        }

        potentialNetworkNodes.clear();*/
    }

    public void sendPing(InetAddress senderAddress, int senderPortNum) {
        LOG("Sending Ping message to node address " + senderAddress.toString() + ":" + senderPortNum);

        NodeProtocol.PingMessage pingMessage = new NodeProtocol.PingMessage();
        pingMessage.connectionIp = this.address.getHostAddress();
        pingMessage.connectionPort = port;

        try {
            NetworkClient msg= new NetworkClient(senderAddress, senderPortNum);
            msg.sendData(pingMessage.toString());
            msg.closeConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
