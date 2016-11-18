package manila.frogmod.mcs.API;

import manila.frogmod.FrogMod;
import manila.frogmod.mcs.APIUriHandler;
import manila.frogmod.mcs.JsonMessage;
import net.minecraft.util.text.TextComponentString;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by swordfeng on 16-11-18.
 */
public class APIChat {
    static {
        APIUriHandler.register("/api/chatmsg", (JsonMessage request) -> {
            String displayName = request.obj.get("displayname").getAsString();
            String text = request.obj.get("text").getAsString();
            JsonMessage response = new JsonMessage();
            if (displayName != null && text != null) {
                FrogMod.mcServer.addChatMessage(new TextComponentString(String.format("[%s] %s", displayName, text)));
                response.setSuccess();
            } else {
                response.setFailure("malformed request");
            }
            return response;
        });
    }
    public static void init() {}

    private static ArrayList<JsonMessage> pending = new ArrayList<>();
    private static synchronized void scheduleResend(JsonMessage request) {
        // we won't resend currently
    }

    public static void chatMessage(String playerName, String text) {
        JsonMessage request = new JsonMessage();
        request.uri = "/api/mcs/chatmsg";
        request.obj.addProperty("playername", playerName);
        request.obj.addProperty("text", text);
        APIMonitor.endpoint.send(request).fail((e) -> {
            FrogMod.logger.warn("failed to send chat message: " + e.getMessage());
            FrogMod.logger.warn("will retry");
            scheduleResend(request);
        }).done((jmsg) -> {
            if (!"success".equals(jmsg.obj.get("result").getAsString())) {
                FrogMod.logger.warn("failed to send chat message: (server report) " + jmsg.obj.get("errormsg").getAsString());
            }
        });
    }

    public static void loginMessage(String playerName, boolean online) {
        JsonMessage request = new JsonMessage();
        request.uri = "/api/mcs/loginmsg";
        request.obj.addProperty("playername", playerName);
        request.obj.addProperty("action", online ? "login" : "logout");
        APIMonitor.endpoint.send(request).fail((e) -> {
            FrogMod.logger.warn("failed to send login message: " + e.getMessage());
            FrogMod.logger.warn("will retry");
            scheduleResend(request);
        }).done((jmsg) -> {
            if (!"success".equals(jmsg.obj.get("result").getAsString())) {
                FrogMod.logger.warn("failed to send login message: (server report) " + jmsg.obj.get("errormsg").getAsString());
            }
        });
    }
}
