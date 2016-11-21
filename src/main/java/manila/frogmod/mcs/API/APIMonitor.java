package manila.frogmod.mcs.API;

import com.google.gson.JsonObject;
import manila.frogmod.FrogMod;
import manila.frogmod.mcs.APIUriHandler;
import manila.frogmod.mcs.JsonMessage;

import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by swordfeng on 16-11-18.
 */
public class APIMonitor extends APICommon {
    static protected void init() {
        APIUriHandler.register("/api/status", (JsonMessage request) -> {
            JsonMessage response = new JsonMessage();
            response.obj.addProperty("name", config.getName());
            response.obj.addProperty("address", config.getAddress());
            response.obj.addProperty("mc_version", FrogMod.mcServer.getMinecraftVersion());
            response.obj.addProperty("mod_port", config.getPort());
            response.obj.addProperty("mod_version", FrogMod.VERSION);
            response.obj.addProperty("onlines", FrogMod.mcServer.getCurrentPlayerCount());
            response.obj.addProperty("running_time", (System.currentTimeMillis() - FrogMod.startTime) / 1000);
            response.setSuccess();
            return Optional.of(response);
        });
        APIUriHandler.register("/api/heartbeat", (JsonMessage request) -> {
            JsonMessage response = new JsonMessage();
            response.setSuccess();
            return Optional.of(response);
        });
    }

    static public void register() {
        JsonMessage request = new JsonMessage();
        request.obj.addProperty("name", config.getName());
        request.obj.addProperty("address", config.getAddress());
        request.obj.addProperty("mc_version", FrogMod.mcServer.getMinecraftVersion());
        request.obj.addProperty("mod_port", config.getPort());
        request.obj.addProperty("mod_version", FrogMod.VERSION);
        request.uri = "/api/mcs/register";
        send(request).done((JsonMessage jmsg) -> {
            JsonObject obj = jmsg.obj;
            if (!"success".equals(JsonGetString(obj.get("result")))) {
                FrogMod.logger.error("Failed to register to server: (server report) " + JsonGetString(obj.get("errormsg")));
            } else {
                id = JsonGetString(obj.get("text"));
            }
        }).fail((Exception e) -> {
            FrogMod.logger.error("Failed to register to server: " + e.getMessage());
        });
    }

    static public void update() {
        JsonMessage request = new JsonMessage();
        request.obj.addProperty("name", config.getName());
        request.obj.addProperty("address", config.getAddress());
        request.obj.addProperty("mc_version", FrogMod.mcServer.getMinecraftVersion());
        request.obj.addProperty("mod_port", config.getPort());
        request.obj.addProperty("mod_version", FrogMod.VERSION);
        request.obj.addProperty("serverid", id);
        request.uri = "/api/mcs/update";
        sendWithId(request).fail((Exception e) -> {
            FrogMod.logger.error("Failed to update: " + e.getMessage());
        });
    }

    static public void golive() {
        JsonMessage request = new JsonMessage();
        request.obj.addProperty("serverid", id);
        request.uri = "/api/mcs/golive";
        sendWithId(request).done((JsonMessage jmsg) -> {
            if (!"success".equals(JsonGetString(jmsg.obj.get("result")))) {
                FrogMod.logger.error("Failed to golive: (server report) " + JsonGetString(jmsg.obj.get("errormsg")));
            }
        }).fail((Exception e) -> {
            FrogMod.logger.error("Failed to golive: " + e.getMessage());
        });
    }

    static private int heartbeatFailure = 0;
    static public void heartbeat() {
        JsonMessage request = new JsonMessage();
        request.obj.addProperty("serverid", id);
        request.uri = "/api/mcs/heartbeat";
        sendWithId(request).done((JsonMessage jmsg) -> {
            if (!"success".equals(JsonGetString(jmsg.obj.get("result")))) {
                FrogMod.logger.error("Failed to heartbeat: (server report) " + JsonGetString(jmsg.obj.get("errormsg")));
            }
            heartbeatFailure = 0;
        }).fail((Exception e) -> {
            FrogMod.logger.error("Failed to heartbeat: " + e.getMessage());
            heartbeatFailure++;
            if (heartbeatFailure >= 10) {
                id = null;
                heartbeatFailure = 0;
            }
        });
    }

    static public void closedown(String reason) {
        JsonMessage request = new JsonMessage();
        request.obj.addProperty("name", config.getName());
        request.obj.addProperty("serverid", id);
        request.obj.addProperty("reason", reason);
        request.uri = "/api/server_closedown";
        sendWithId(request);
        id = null;
    }

    private static Timer heartbeatTimer = new Timer();
    static public void enableHeartBeat() {
        heartbeatTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (id != null) {
                    heartbeat();
                }
            }
        }, 0, 60 * 1000);
    }
    static public void disableHeartBeat() {
        heartbeatTimer.cancel();
        heartbeatTimer.purge();
    }
}
