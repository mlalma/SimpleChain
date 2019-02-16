package com.simplechain.data;

public class NodeData {
    public String name;
    public String version;
    public String connectionIp;
    public int connectionPort;
    public transient long lastMessageReceived;
    public transient long lastMessageSent;
}
