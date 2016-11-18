package manila.frogmod;

import manila.frogmod.mcs.API;
import manila.frogmod.mcs.APIUriHandler;
import manila.frogmod.mcs.simpleHttp.SimpleHttpServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = FrogMod.MODID, version = FrogMod.VERSION, guiFactory = "manila.frogmod.FrogModGuiFactory")
public class FrogMod {
    public static final String MODID = "FrogMod";
    public static final String VERSION = "0.9";

    /*
    @Mod.Instance("FrogMod")
    public static FrogMod instance;
    */

    public static Logger logger;
    public static Config config;
    private SimpleHttpServer server;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Config.init(event.getSuggestedConfigurationFile());
        config = Config.getInstance();
        config.syncConfig();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        logger = LogManager.getFormatterLogger(MODID);

        MinecraftForge.EVENT_BUS.register(new FrogEventHandler());

        server = new SimpleHttpServer(config.getPort(), new APIUriHandler());
        API.init();
    }

    @EventHandler
    public void serverStarted(FMLServerStartedEvent event) {
        try {
            server.start();
        } catch (Exception e) {
            logger.error("Cannot start the HTTP server");
        }
    }

    @EventHandler
    public void serverStopping(FMLServerStoppingEvent event) {
        server.stop();
    }
}
