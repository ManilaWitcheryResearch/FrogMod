package manila.frogmod.mcs;

import com.google.gson.JsonObject;
import manila.frogmod.Config;
import manila.frogmod.FrogMod;

/**
 * Created by swordfeng on 16-11-18.
 */
public class API {
    static private Config config = null;
    static {
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
    }
    static public void init() {
        config = Config.getInstance();
        /* also make static code to run */
    }
}
