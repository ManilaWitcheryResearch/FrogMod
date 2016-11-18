package com.frogmcs.frogmod.mcs;

/**
 * Created by swordfeng on 16-11-18.
 */
public abstract class Endpoint {

    protected MessageHandler messageHandler;

    public abstract void start() throws InterruptedException;
    public abstract void stop();

    public abstract void send(Message message);

    protected Endpoint(MessageHandler handler) {
        messageHandler = handler;
    }
}
