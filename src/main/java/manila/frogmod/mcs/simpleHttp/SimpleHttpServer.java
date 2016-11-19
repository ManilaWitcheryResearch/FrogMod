package manila.frogmod.mcs.simpleHttp;


import com.google.gson.JsonObject;
import manila.frogmod.FrogMod;
import manila.frogmod.mcs.JsonMessage;
import manila.frogmod.mcs.MessageHandler;
import fi.iki.elonen.NanoHTTPD;

import java.util.Optional;

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
            return newFixedLengthResponse(Response.Status.BAD_REQUEST, "application/json",
                    new JsonMessage().setFailure("Bad request").encode());
        }

        JsonMessage request;

        try {
            int length = Integer.parseInt(session.getHeaders().get("content-length"));
            byte[] buffer = new byte[length];
            session.getInputStream().read(buffer);
            String body = new String(buffer, "UTF-8");
            request = JsonMessage.decode(body);
        } catch (Exception e) {
            return newFixedLengthResponse(Response.Status.BAD_REQUEST, "application/json",
                    new JsonMessage().setFailure("Bad request").encode());
        }
        request.uri = session.getUri();

        FrogMod.logger.info(String.format("<inreq %s> %s", request.uri, request.encode()));

        Optional<JsonMessage> response;
        try {
            response = (Optional<JsonMessage>) mHandler.onMessage(request);
        } catch (Exception e) {
            FrogMod.logger.error("Error handling request: " + e.getMessage());
            e.printStackTrace();
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "application/json",
                    new JsonMessage().setFailure("internal error").encode());
        }

        if (!response.isPresent()) {
            return newFixedLengthResponse(Response.Status.NOT_FOUND, "application/json",
                    new JsonMessage().setFailure("APIMonitor not found").encode());
        }
        assert(response.get().obj.has("result"));

        FrogMod.logger.info(String.format("<outres> %s", response.get().encode()));

        return newFixedLengthResponse(Response.Status.OK, "application/json", response.get().encode());
    }
}
