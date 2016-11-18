package manila.frogmod;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

/**
 * Created by swordfeng on 16-11-18.
 */
public class Config {
    public Configuration configFile;
    private static Config _instance = null;

    public static void init(File f) {
        _instance = new Config(f);
        _instance.syncConfig();
    }

    public static Config getInstance() {
        return _instance;
    }

    private Config(File f) {
        configFile = new Configuration(f);
    }

    String address = "";
    int port = 8123;
    String name = "Minecraft Server";

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
        syncConfig();
    }

    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
        syncConfig();
    }

    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
        syncConfig();
    }

    public void syncConfig() {
        name = configFile.getString("Server Name", Configuration.CATEGORY_GENERAL, name, "Name of this server");
        address = configFile.getString("Server Address", Configuration.CATEGORY_GENERAL, address, "Address notified to the Air Service");
        port = configFile.getInt("Port", Configuration.CATEGORY_GENERAL, port, 0, 65535, "Listening port");

        if(configFile.hasChanged()) {
            configFile.save();
        }
    }
}
