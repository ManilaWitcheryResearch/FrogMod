package manila.frogmod.mcs.API;

import com.google.gson.JsonObject;
import manila.frogmod.Config;
import manila.frogmod.FrogMod;
import manila.frogmod.mcs.APIUriHandler;
import manila.frogmod.mcs.JsonMessage;
import manila.frogmod.mcs.simpleHttp.SimpleHttpEndpoint;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by swordfeng on 16-11-18.
 */
public class APIMonitor {
    static private Config config = null;
    static public SimpleHttpEndpoint endpoint = null;

    static public void init(SimpleHttpEndpoint endpoint) {
        config = Config.getInstance();
        APIMonitor.endpoint = endpoint;
        /* also make static code to run */
        APIUriHandler.register("/api/status", (JsonMessage request) -> {
            JsonMessage response = new JsonMessage();
            response.obj.addProperty("name", config.getName());
            response.obj.addProperty("address", config.getAddress());
            response.obj.addProperty("mc_version", FrogMod.mcServer.getMinecraftVersion());
            response.obj.addProperty("mod_port", config.getPort());
            response.obj.addProperty("mod_version", FrogMod.VERSION);
            response.obj.addProperty("onlines", FrogMod.mcServer.getCurrentPlayerCount());
            response.obj.addProperty("running_time", System.currentTimeMillis() - FrogMod.startTime);
            response.setSuccess();
            return response;
        });
        APIUriHandler.register("/api/heartbeat", (JsonMessage request) -> {
            JsonMessage response = new JsonMessage();
            response.setSuccess();
            return response;
        });

        APIChat.init();
    }

    private static String id;

    static public void register() {
        JsonMessage request = new JsonMessage();
        request.obj.addProperty("name", config.getName());
        request.obj.addProperty("address", config.getAddress());
        request.obj.addProperty("mc_version", FrogMod.mcServer.getMinecraftVersion());
        request.obj.addProperty("mod_port", config.getPort());
        request.obj.addProperty("mod_version", FrogMod.VERSION);
        request.uri = "/api/mcs/register";
        endpoint.send(request).done((JsonMessage jmsg) -> {
            JsonObject obj = jmsg.obj;
            String result = obj.get("result").getAsString();
            if (!"success".equals(result)) {
                FrogMod.logger.error("Failed to register to server: (server report) " + obj.get("errormsg").getAsString());
            } else {
                id = obj.get("text").getAsString();
            }
        }).fail((Exception e) -> {
            FrogMod.logger.error("Failed to register to server: " + e.getMessage());
        });
    }

    static public void update() {
        if (id == null) {
            throw new RuntimeException("Server is not registered");
        }
        JsonMessage request = new JsonMessage();
        request.obj.addProperty("name", config.getName());
        request.obj.addProperty("address", config.getAddress());
        request.obj.addProperty("mc_version", FrogMod.mcServer.getMinecraftVersion());
        request.obj.addProperty("mod_port", config.getPort());
        request.obj.addProperty("mod_version", FrogMod.VERSION);
        request.obj.addProperty("serverid", id);
        request.uri = "/api/mcs/update";
        endpoint.send(request).fail((Exception e) -> {
            FrogMod.logger.error("Failed to update: " + e.getMessage());
        });
    }

    static public void golive() {
        if (id == null) {
            throw new RuntimeException("Server is not registered");
        }
        JsonMessage request = new JsonMessage();
        request.obj.addProperty("serverid", id);
        request.uri = "/api/mcs/golive";
        endpoint.send(request).done((JsonMessage jmsg) -> {
            if (!"success".equals(jmsg.obj.get("result").getAsString())) {
                FrogMod.logger.error("Failed to golive: (server report) " + jmsg.obj.get("errormsg").getAsString());
            }
        }).fail((Exception e) -> {
            FrogMod.logger.error("Failed to golive: " + e.getMessage());
        });
    }

    static public void heartbeat() {
        if (id == null) {
            throw new RuntimeException("Server is not registered");
        }
        JsonMessage request = new JsonMessage();
        request.obj.addProperty("serverid", id);
        request.uri = "/api/mcs/heartbeat";
        endpoint.send(request).done((JsonMessage jmsg) -> {
            if (!"success".equals(jmsg.obj.get("result").getAsString())) {
                FrogMod.logger.error("Failed to heartbeat: (server report) " + jmsg.obj.get("errormsg").getAsString());
            }
        }).fail((Exception e) -> {
            FrogMod.logger.error("Failed to heartbeat: " + e.getMessage());
        });
    }

    static public void shutdown(String reason) {
        if (id == null) {
            throw new RuntimeException("Server is not registered");
        }
        JsonMessage request = new JsonMessage();
        request.obj.addProperty("name", config.getName());
        request.obj.addProperty("serverid", id);
        request.obj.addProperty("reason", reason);
        request.uri = "/api/server_closedown";
        endpoint.send(request);
        id = null;
    }

    static public boolean isRegistered() {
        return id != null;
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
