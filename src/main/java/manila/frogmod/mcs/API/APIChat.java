package manila.frogmod.mcs.API;

import manila.frogmod.FrogMod;
import manila.frogmod.mcs.APIUriHandler;
import manila.frogmod.mcs.JsonMessage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import org.jdeferred.Promise;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Created by swordfeng on 16-11-18.
 */
public class APIChat extends APICommon {
    static protected void init() {
        APIUriHandler.register("/api/chatmsg", (JsonMessage request) -> {
            String displayName = JsonGetString(request.obj.get("displayname"));
            String text = JsonGetString(request.obj.get("text"));
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

    private static void handleResult(Promise<JsonMessage, Exception, Void> result, JsonMessage request, String messageType) {
        result.fail((e) -> {
            FrogMod.logger.warn("failed to send %s message: %s (will retry)", messageType, e.getMessage());
            scheduleResend(request);
        }).done((jmsg) -> {
            if (!"success".equals(JsonGetString(jmsg.obj.get("result")))) {
                FrogMod.logger.warn("failed to send %s message: (server report) ", messageType,
                        JsonGetString(jmsg.obj.get("errormsg")));
            }
        });
    }

    public static void chatMessage(String playerName, String text) {
        JsonMessage request = new JsonMessage();
        request.uri = "/api/mcs/chatmsg";
        request.obj.addProperty("playername", playerName);
        request.obj.addProperty("text", text);
        handleResult(sendWithId(request), request, "chat");
    }

    public static void loginMessage(String playerName, boolean online) {
        JsonMessage request = new JsonMessage();
        request.uri = "/api/mcs/loginmsg";
        request.obj.addProperty("playername", playerName);
        request.obj.addProperty("action", online ? "login" : "logout");
        handleResult(sendWithId(request), request, "login");
    }

    public static void achieveMessage(String playerName, String achievement) {
        JsonMessage request = new JsonMessage();
        /* ARCHIEVE is typo */
        request.uri = "/api/mcs/archievemsg";
        request.obj.addProperty("playername", playerName);
        request.obj.addProperty("archieve", achievement);
        handleResult(sendWithId(request), request, "achieve");
    }

    public static void deathMessage(String playerName, String action) {
        JsonMessage request = new JsonMessage();
        request.uri = "/api/mcs/deathmsg";
        request.obj.addProperty("playername", playerName);
        request.obj.addProperty("action", action);
        handleResult(sendWithId(request), request, "death");
    }
}
