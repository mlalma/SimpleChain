package com.simplechain.node;

import com.simplechain.data.BaseMessage;
import com.simplechain.network.client.NetworkClient;
import com.simplechain.network.server.NetworkServerInConnection;

import java.util.List;

public class NodeDiscoveryProtocol {

    static public class HelloMsg extends BaseMessage {
        static final public String TYPE = "HELLO_MSG";
        public NodeData node;

        public HelloMsg() { super(TYPE); }

        public String toString() {
            return super.toString();
        }
    }

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
}
