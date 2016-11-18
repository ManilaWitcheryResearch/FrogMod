package com.frogmcs.frogmod.mcs.simpleHttp;

import com.frogmcs.frogmod.FrogMod;
import com.frogmcs.frogmod.mcs.Endpoint;
import com.frogmcs.frogmod.mcs.Message;
import com.frogmcs.frogmod.mcs.MessageHandler;

import java.util.logging.Logger;

/**
 * Created by swordfeng on 16-11-18.
 */
public class SimpleHttpEndpoint extends Endpoint {

    private int mPort;
    private String mRemoteAddr;
    private int mRemotePort;
    private MessageHandler mHandler;

    private SimpleHttpServer server = null;


    public SimpleHttpEndpoint(int port, String remoteAddr, int remotePort, MessageHandler handler) {
        super(handler);
        mPort = port;
        mRemoteAddr = remoteAddr;
        mRemotePort = remotePort;
        mHandler = handler;
    }

    @Override
    public void start() throws InterruptedException {
        assert(server == null);
        server = new SimpleHttpServer(mPort, mHandler);
        FrogMod.logger.info("HTTP server started");
    }

    @Override
    public void stop() {
        server.closeAllConnections();
        FrogMod.logger.info("HTTP server stopped");
        server = null;
    }

    @Override
    public void send(Message message) {

    }
}
