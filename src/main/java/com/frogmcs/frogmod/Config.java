package com.frogmcs.frogmod;

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

    public void syncConfig() {
        address = configFile.getString("Server Address", Configuration.CATEGORY_GENERAL, address, "Address notified to the Air Service");
        port = configFile.getInt("Port", Configuration.CATEGORY_GENERAL, port, 0, 65535, "Listening port");

        if(configFile.hasChanged()) {
            configFile.save();
        }
    }
}
