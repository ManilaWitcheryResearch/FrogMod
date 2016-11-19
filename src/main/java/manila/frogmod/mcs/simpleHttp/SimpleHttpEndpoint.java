package manila.frogmod.mcs.simpleHttp;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import manila.frogmod.Config;
import manila.frogmod.FrogMod;
import manila.frogmod.mcs.Endpoint;
import manila.frogmod.mcs.JsonMessage;
import manila.frogmod.mcs.Message;
import manila.frogmod.mcs.MessageHandler;
import org.jdeferred.Deferred;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by swordfeng on 16-11-18.
 */
public class SimpleHttpEndpoint extends Endpoint {

    private int mPort;
    private String mRemoteAddr;
    private int mRemotePort;
    private String uriPrefix;
    private MessageHandler mHandler;

    private SimpleHttpServer httpServer;

    public SimpleHttpEndpoint(int port, String remoteAddr, int remotePort, MessageHandler handler) {
        super(handler);
        mPort = port;
        mRemoteAddr = remoteAddr;
        mRemotePort = remotePort;
        mHandler = handler;

        uriPrefix = String.format("http://%s:%d", mRemoteAddr, mRemotePort);

        Unirest.setTimeouts(2000, 10000);
    }

    @Override
    public void start() throws InterruptedException {
        assert(httpServer == null);
        httpServer = new SimpleHttpServer(mPort, mHandler);
        try {
            httpServer.start();
        } catch (IOException e) {
            throw new InterruptedException(e.getMessage());
        }
        FrogMod.logger.info("HTTP server started");
    }

    @Override
    public void stop() {
        httpServer.closeAllConnections();
        FrogMod.logger.info("HTTP server stopped");
        httpServer = null;
    }

    @Override
    public Promise<JsonMessage, Exception, Object> send(Message message) {
        JsonMessage jmsg = (JsonMessage) message;
        assert(jmsg.uri != null);

        Deferred<JsonMessage, Exception, Object> deferred = new DeferredObject<>();

        try {
            Unirest.post(uriPrefix + jmsg.uri)
                    .header("accept", "application/json")
                    .body(jmsg.encode())
                    .asStringAsync(new Callback<String>() {
                        @Override
                        public void completed(HttpResponse<String> httpResponse) {
                            deferred.resolve(JsonMessage.decode(httpResponse.getBody()));
                        }

                        @Override
                        public void failed(UnirestException e) {
                            deferred.reject(e);
                        }

                        @Override
                        public void cancelled() {
                            deferred.reject(new InterruptedException("cancelled"));
                        }
                    });
        } catch (Exception e) {
            deferred.reject(e);
        }
        
        return deferred.promise();
    }
}
