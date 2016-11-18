package com.frogmcs.frogmod.mcs;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by swordfeng on 16-11-18.
 */
public class APIUriHandler extends MessageHandler {

    static private APIUriHandler _instance = null;

    static public APIUriHandler getInstance() {
        if (_instance == null) {
            _instance = new APIUriHandler();
        }
        return _instance;
    }

    static private Map<String, IAPIHandler> handlers = new HashMap<String, IAPIHandler>();

    static public void register(String uri, IAPIHandler handler) {
        handlers.put(uri, handler);
    }

    @Override
    public Message onMessage(Message message) {
        JsonMessage jmsg = (JsonMessage) message;
        IAPIHandler handler = handlers.get(jmsg.uri);
        if (handler == null) return null;
        return handler.onMessage(jmsg);
    }
}
