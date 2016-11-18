package com.frogmcs.frogmod.mcs.simpleHttp;


import com.frogmcs.frogmod.FrogMod;
import com.frogmcs.frogmod.mcs.JsonMessage;
import com.frogmcs.frogmod.mcs.MessageHandler;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fi.iki.elonen.NanoHTTPD;

import java.io.IOException;

/**
 * Created by swordfeng on 16-11-18.
 */
public class SimpleHttpServer extends NanoHTTPD {

    private MessageHandler mHandler;

    public SimpleHttpServer(int port, MessageHandler handler) {
        super(port);
        mHandler = handler;
    }

    @Override
    public Response serve(IHTTPSession session) {
        Method method = session.getMethod();
        if (!Method.POST.equals(method)) {
            return newFixedLengthResponse(Response.Status.BAD_REQUEST, "text/plain", "Only POST requests are accepted.");
        }

        JsonMessage request;

        try {
            int length = Integer.parseInt(session.getHeaders().get("content-length"));
            byte[] buffer = new byte[length];
            session.getInputStream().read(buffer);
            String body = new String(buffer, "UTF-8");
            request = JsonMessage.decode(body);
        } catch (Exception e) {
            return newFixedLengthResponse(Response.Status.BAD_REQUEST, "text/plain", "Bad request.");
        }
        request.uri = session.getUri();

        FrogMod.logger.info("request: " + session.getUri());

        JsonMessage response = (JsonMessage) mHandler.onMessage(request);
        if (response == null) {
            return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "API not found");
        }
        return newFixedLengthResponse(Response.Status.OK, "application/json", response.encode());
    }
}
