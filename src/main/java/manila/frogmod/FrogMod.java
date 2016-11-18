package manila.frogmod;

import manila.frogmod.mcs.API;
import manila.frogmod.mcs.APIUriHandler;
import manila.frogmod.mcs.simpleHttp.SimpleHttpServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.*;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = FrogMod.MODID, version = FrogMod.VERSION, guiFactory = "manila.frogmod.FrogModGuiFactory")
public class FrogMod {
    public static final String MODID = "FrogMod";
    public static final String VERSION = "0.9";

    public static Logger logger;
    public static Config config;
    public static MinecraftServer mcServer;
    public static long startTime;

    private SimpleHttpServer httpServer;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Config.init(event.getSuggestedConfigurationFile());
        config = Config.getInstance();
        config.syncConfig();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        logger = LogManager.getFormatterLogger(MODID);

        MinecraftForge.EVENT_BUS.register(FrogMod.this);

        httpServer = new SimpleHttpServer(config.getPort(), new APIUriHandler());
        API.init();
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        mcServer = event.getServer();
    }


    @EventHandler
    public void serverStarted(FMLServerStartedEvent event) {
        startTime = System.currentTimeMillis();
        try {
            httpServer.start();
        } catch (Exception e) {
            logger.fatal("Cannot start the HTTP server");
            httpServer = null;
        }
    }

    @EventHandler
    public void serverStopping(FMLServerStoppingEvent event) {
        if (httpServer != null) {
            httpServer.stop();
            httpServer = null;
        }
    }

    @SubscribeEvent
    public void onServerChat(ServerChatEvent event) {
        FrogMod.logger.info("%s says: %s\n", event.getUsername(), event.getMessage());
        event.getPlayer().addChatMessage(new TextComponentString("echo: " + event.getMessage()));
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        Config.getInstance().syncConfig();
    }

}
