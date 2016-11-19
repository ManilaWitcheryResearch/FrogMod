package manila.frogmod;

import manila.frogmod.dotcommand.DotCommand;
import manila.frogmod.mcs.API.APIChat;
import manila.frogmod.mcs.API.APICommon;
import manila.frogmod.mcs.API.APIMonitor;
import manila.frogmod.mcs.APIUriHandler;
import manila.frogmod.mcs.simpleHttp.SimpleHttpEndpoint;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.*;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

@Mod(modid = FrogMod.MODID, version = FrogMod.VERSION, guiFactory = "manila.frogmod.FrogModGuiFactory")
public class FrogMod {
    public static final String MODID = "FrogMod";
    public static final String VERSION = "0.9";

    public static Logger logger;
    public static Config config;
    public static MinecraftServer mcServer;
    public static long startTime;

    private SimpleHttpEndpoint endpoint;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Config.init(event.getSuggestedConfigurationFile());
        config = Config.getInstance();
        config.syncConfig();
        LoggerFactory.getLogger("test");
    }

    @EventHandler
    public void init(FMLInitializationEvent event) throws ClassNotFoundException, MalformedURLException {
        logger = LogManager.getFormatterLogger(MODID);

        MinecraftForge.EVENT_BUS.register(FrogMod.this);;

        DotCommand.initCommands();
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        mcServer = event.getServer();

        endpoint = new SimpleHttpEndpoint(config.getPort(), config.getRemoteAddress(), config.getRemotePort(),
                new APIUriHandler());
        APICommon.init(endpoint);
    }

    @EventHandler
    public void serverStarted(FMLServerStartedEvent event) {
        startTime = System.currentTimeMillis();
        try {
            endpoint.start();
        } catch (Exception e) {
            logger.fatal("Cannot start the HTTP server: " + e.getMessage());
            endpoint.stop();
            endpoint = null;
        }
        APIMonitor.register();
        APIMonitor.enableHeartBeat();
    }

    @EventHandler
    public void serverStopping(FMLServerStoppingEvent event) {
        APIMonitor.disableHeartBeat();
        if (APICommon.isRegistered()) {
            APIMonitor.shutdown("Server stopped");
        }
        if (endpoint != null) {
            endpoint.stop();
            endpoint = null;
        }
    }

    @SubscribeEvent
    public void onServerChat(ServerChatEvent event) {
        FrogMod.logger.info("%s says: %s\n", event.getUsername(), event.getMessage());
        event.getPlayer().addChatMessage(new TextComponentString("echo: " + event.getMessage()));

        if (DotCommand.handle(event)) return;

        APIChat.chatMessage(event.getUsername(), event.getMessage());
    }

    @SubscribeEvent
    public void onPlayer(PlayerEvent event) {
        if (event instanceof PlayerEvent.PlayerLoggedInEvent) {
            FrogMod.logger.info("%s logged in", event.player.getDisplayNameString());
            APIChat.loginMessage(event.player.getDisplayNameString(), true);
        } else if (event instanceof PlayerEvent.PlayerLoggedOutEvent) {
            FrogMod.logger.info("%s logged out", event.player.getDisplayNameString());
            APIChat.loginMessage(event.player.getDisplayNameString(), false);
        }
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        Config.getInstance().syncConfig();
    }


    static {
        String classpath = FrogMod.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String jarpath = classpath.substring(0, classpath.indexOf('!'));
        try {
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
            method.setAccessible(true);
            method.invoke(ClassLoader.getSystemClassLoader(), new Object[] { new URL(jarpath) });
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
