package manila.frogmod.mcs;

import org.jdeferred.Promise;

/**
 * Created by swordfeng on 16-11-18.
 */
public abstract class Endpoint {

    protected MessageHandler messageHandler;

    public abstract void start() throws InterruptedException;
    public abstract void stop();

    public abstract Promise<? extends Message, Exception, Void> send(Message message);

    protected Endpoint(MessageHandler handler) {
        messageHandler = handler;
    }
}
