package manila.frogmod.mcs.API;

import manila.frogmod.FrogMod;
import manila.frogmod.mcs.APIUriHandler;
import manila.frogmod.mcs.JsonMessage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

/**
 * Created by swordfeng on 16-11-18.
 */
public class APIChat extends APICommon {
    static protected void init() {
        APIUriHandler.register("/api/chatmsg", (JsonMessage request) -> {
            String displayName = request.obj.get("displayname").getAsString();
            String text = request.obj.get("text").getAsString();
            JsonMessage response = new JsonMessage();
            if (displayName != null && text != null) {
                // cannot send out chat message
                // FrogMod.mcServer.addChatMessage(new TextComponentString(String.format("[%s] %s", displayName, text)));
                ITextComponent chatMessage = new TextComponentString(String.format("[%s] %s", displayName, text));
                for (EntityPlayerMP player : FrogMod.mcServer.getPlayerList().getPlayerList()) {
                    player.addChatMessage(chatMessage);
                }
                response.setSuccess();
            } else {
                response.setFailure("malformed request");
            }
            return Optional.of(response);
        });
    }

    private static ArrayList<JsonMessage> pending = new ArrayList<>();
    private static synchronized void scheduleResend(JsonMessage request) {
        // we won't resend currently
    }

    public static void chatMessage(String playerName, String text) {
        JsonMessage request = new JsonMessage();
        request.uri = "/api/mcs/chatmsg";
        request.obj.addProperty("playername", playerName);
        request.obj.addProperty("text", text);
        sendWithId(request).fail((e) -> {
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
        sendWithId(request).fail((e) -> {
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
