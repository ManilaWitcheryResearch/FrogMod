package manila.frogmod.mcs.simpleHttp;

import manila.frogmod.FrogMod;
import manila.frogmod.mcs.Endpoint;
import manila.frogmod.mcs.Message;
import manila.frogmod.mcs.MessageHandler;

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
