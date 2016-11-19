package manila.frogmod.mcs.API;

import com.google.gson.JsonElement;
import manila.frogmod.Config;
import manila.frogmod.mcs.JsonMessage;
import manila.frogmod.mcs.simpleHttp.SimpleHttpEndpoint;
import org.jdeferred.DonePipe;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;

/**
 * Created by swordfeng on 16-11-19.
 */
public class APICommon {
    static protected Config config = null;
    static protected SimpleHttpEndpoint endpoint = null;
    static protected String id;

    static public void init(SimpleHttpEndpoint endpoint) {
        config = Config.getInstance();
        APIMonitor.endpoint = endpoint;

        APIMonitor.init();
        APIChat.init();
    }

    static protected Promise<JsonMessage, Exception, Void> send(JsonMessage request) {
        return endpoint.send(request).then(new DonePipe<JsonMessage, JsonMessage, Exception, Void>() {
            @Override
            public Promise<JsonMessage, Exception, Void> pipeDone(JsonMessage jmsg) {
                if (jmsg.obj.get("result") == null ||
                        jmsg.obj.get("errormsg") == null) {
                    return new DeferredObject<JsonMessage, Exception, Void>()
                            .reject(new Exception("server response malformed")).promise();
                }
                return new DeferredObject<JsonMessage, Exception, Void>().resolve(jmsg).promise();
            }
        });
    }

    static protected Promise<JsonMessage, Exception, Void> sendWithId(JsonMessage request) {
        if (id == null) {
            return new DeferredObject<JsonMessage, Exception, Void>()
                    .reject(new RuntimeException("Server is not registered")).promise();
        }
        request.obj.addProperty("serverid", id);
        return endpoint.send(request);
    }

    static public boolean isRegistered() {
        return id != null;
    }

    /* convenient functions for interacting with gson */
    protected static String JsonGetString(JsonElement e) {
        if (e == null) return null;
        return e.getAsString();
    }

}
