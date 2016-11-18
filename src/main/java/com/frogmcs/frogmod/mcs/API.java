package com.frogmcs.frogmod.mcs;

import com.google.gson.JsonObject;

/**
 * Created by swordfeng on 16-11-18.
 */
public class API {
    static {
        APIUriHandler.register("/api/status", (JsonMessage msg) -> {
            JsonObject obj = new JsonObject();
            obj.addProperty("test", "test");
            return new JsonMessage(obj);
        });
    }
    static public void init() {
        /* make static code to run */
    }
}
