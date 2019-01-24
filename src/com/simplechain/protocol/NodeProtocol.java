package com.simplechain.protocol;

import com.simplechain.data.NodeData;

public class NodeProtocol {

    public static class PingMessage extends BaseMessage {
        public static String TYPE = "PING_MSG";
        public String connectionIp;
        public int connectionPort;

        public PingMessage(NodeData nodeData) { super(nodeData.name, nodeData.version, nodeData.connectionIp, nodeData.connectionPort, TYPE); }

        @Override
        public String toString() {
            return super.toString();
        }
    }

    public static class PongMessage extends BaseMessage {
        public static String TYPE = "PONG_MSG";
        public String connectionIp;
        public int connectionPort;
        public String version;

        public PongMessage(NodeData nodeData) { super(nodeData.name, nodeData.version, nodeData.connectionIp, nodeData.connectionPort, TYPE); }

        @Override
        public String toString() {
            return super.toString();
        }
    }

    static public class NodeErrorMsg extends BaseMessage {
        static final public String TYPE = "NODE_ERROR_MSG";
        public int errorCode;
        public String errorMsg;

        public NodeErrorMsg(NodeData nodeData) { super(nodeData.name, nodeData.version, nodeData.connectionIp, nodeData.connectionPort, TYPE); }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}
