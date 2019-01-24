package com.simplechain.protocol;

import com.google.gson.Gson;

public class BaseMessage {
    static final Gson gson = new Gson();

    public final static String END_OF_MESSAGE_MARKER = "<eom>";
    public final static String NEWLINE = "\r\n";

    public final String name;
    public final String version;
    public final String connectionIp;
    public final int connectionPort;
    public final String type;

    public BaseMessage(String name, String version, String connectionIp, int connectionPort, String type) {
        this.name = name;
        this.version = version;
        this.connectionIp = connectionIp;
        this.connectionPort = connectionPort;
        this.type = type;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(gson.toJson(this));
        sb.append(END_OF_MESSAGE_MARKER);
        sb.append(NEWLINE);
        return sb.toString();
    }
}
