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
    String remoteAddress = "";
    int remotePort = 8123;

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

    public String getRemoteAddress() { return remoteAddress; }
    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
        syncConfig();
    }

    public int getRemotePort() {
        return remotePort;
    }
    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
        syncConfig();
    }

    public void syncConfig() {
        name = configFile.getString("Minecraft Server Name", Configuration.CATEGORY_GENERAL,
                name, "Name of this server");
        address = configFile.getString("Minecraft Server Address", Configuration.CATEGORY_GENERAL,
                address, "Address sent to the Air Service");
        port = configFile.getInt("Minecraft Server Port", Configuration.CATEGORY_GENERAL,
                port, 0, 65535, "Listening port");
        remoteAddress = configFile.getString("Air Server Address", Configuration.CATEGORY_GENERAL,
                remoteAddress, "Air Service Address");
        remotePort = configFile.getInt("Air Server Port", Configuration.CATEGORY_GENERAL,
                remotePort, 0, 65535, "Air Service port");

        if(configFile.hasChanged()) {
            configFile.save();
        }
    }
}
