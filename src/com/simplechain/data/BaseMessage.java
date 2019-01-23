package com.simplechain.data;

import com.google.gson.Gson;

public class BaseMessage {
    static final Gson gson = new Gson();

    public final static String END_OF_MESSAGE_MARKER = "<eom>";
    public final static String NEWLINE = "\r\n";

    public final String type;

    public BaseMessage(String type) {
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
