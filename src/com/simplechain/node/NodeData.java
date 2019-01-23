package com.simplechain.node;

import com.simplechain.network.client.NetworkClient;
import com.simplechain.network.server.NetworkServerInConnection;

public class NodeData {

    enum eNodeStatus {
        Unconfirmed,
        Confirmed,
        Connected
    }

    public String name;
    public String version;
    public String connectionIp;
    public int connectionPort;

    public transient NetworkServerInConnection inConnection = null;
    public transient NetworkClient outConnection = null;
    public transient eNodeStatus status = eNodeStatus.Unconfirmed;
    public transient long lastDataPacketReceived;
}
