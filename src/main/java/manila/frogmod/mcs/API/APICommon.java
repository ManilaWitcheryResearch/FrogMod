package manila.frogmod.mcs.API;

import manila.frogmod.Config;
import manila.frogmod.mcs.JsonMessage;
import manila.frogmod.mcs.simpleHttp.SimpleHttpEndpoint;
import org.jdeferred.Deferred;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;
import org.omg.CORBA.OBJ_ADAPTER;

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

    static protected Promise<JsonMessage, Exception, Object> send(JsonMessage request) {
        return endpoint.send(request);
    }

    static protected Promise<JsonMessage, Exception, Object> sendWithId(JsonMessage request) {
        if (id == null) {
            return new DeferredObject().reject(new RuntimeException("Server is not registered")).promise();
        }
        request.obj.addProperty("serverid", id);
        return endpoint.send(request);
    }

    static public boolean isRegistered() {
        return id != null;
    }
}
