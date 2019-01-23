package com.simplechain.node;

import com.simplechain.data.BaseMessage;

public class NodeProtocol {

    public static class PingMessage extends BaseMessage {
        public static String TYPE = "PING_MSG";
        public String connectionIp;
        public int connectionPort;

        public PingMessage() { super(TYPE); }

        public String toString() {
            return super.toString();
        }
    }

    public static class PongMessage extends BaseMessage {
        public static String TYPE = "PONG_MSG";
        public String connectionIp;
        public int connectionPort;
        public String version;

        public PongMessage() { super(TYPE); }

        public String toString() {
            return super.toString();
        }
    }

    static public class NodeErrorMsg extends BaseMessage {
        static final public String TYPE = "NODE_ERROR_MSG";
        public int errorCode;
        public String errorMsg;

        public NodeErrorMsg() { super(TYPE); }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}
