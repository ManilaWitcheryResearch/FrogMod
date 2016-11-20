package manila.frogmod;

import manila.frogmod.dotcommand.DotCommand;
import manila.frogmod.mcs.API.APIChat;
import manila.frogmod.mcs.API.APICommon;
import manila.frogmod.mcs.API.APIMonitor;
import manila.frogmod.mcs.APIUriHandler;
import manila.frogmod.mcs.simpleHttp.SimpleHttpEndpoint;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AchievementEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.*;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

@Mod(modid = FrogMod.MODID, version = FrogMod.VERSION, guiFactory = "manila.frogmod.FrogModGuiFactory",
        serverSideOnly = true, acceptableRemoteVersions = "*")
public class FrogMod {
    public static final String MODID = "FrogMod";
    public static final String VERSION = "0.9";

    public static Logger logger = LogManager.getFormatterLogger(MODID);
    public static Config config;
    public static MinecraftServer mcServer;
    public static long startTime;

    private SimpleHttpEndpoint endpoint;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger.info("load config");
        Config.init(event.getSuggestedConfigurationFile());
        config = Config.getInstance();
        config.syncConfig();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) throws ClassNotFoundException, MalformedURLException {
        logger.info("init event bus");
        MinecraftForge.EVENT_BUS.register(FrogMod.this);
        logger.info("init dot commands");
        DotCommand.initCommands();
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        logger.info("create http endpoint");
        mcServer = event.getServer();

        endpoint = new SimpleHttpEndpoint(config.getPort(), config.getRemoteAddress(), config.getRemotePort(),
                new APIUriHandler());
        logger.info("api init");
        APICommon.init(endpoint);
    }

    @EventHandler
    public void serverStarted(FMLServerStartedEvent event) {
        logger.info("start http endpoing");
        startTime = System.currentTimeMillis();
        try {
            endpoint.start();
        } catch (Exception e) {
            logger.fatal("Cannot start the HTTP server: " + e.getMessage());
            endpoint.stop();
            endpoint = null;
        }
        logger.info("register to air service");
        APIMonitor.register();
        APIMonitor.enableHeartBeat();
    }

    @EventHandler
    public void serverStopping(FMLServerStoppingEvent event) {
        logger.info("server closedown");
        APIMonitor.disableHeartBeat();
        if (APICommon.isRegistered()) {
            APIMonitor.closedown("Server stopped");
        }
        if (endpoint != null) {
            logger.info("destroy http endpoint");
            endpoint.stop();
            endpoint = null;
        }
    }

    @SubscribeEvent
    public void onServerChat(ServerChatEvent event) {
        if (DotCommand.handle(event)) return;

        logger.info("<%s> %s", event.getUsername(), event.getMessage());
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
        logger.info("sync config");
        Config.getInstance().syncConfig();
    }

    @SubscribeEvent
    public void onPlayerAchievement(AchievementEvent event) {
        logger.info("%s's new achievement: %s", event.getEntityPlayer().getDisplayNameString(),
                event.getAchievement().getDescription());
        APIChat.achieveMessage(event.getEntityPlayer().getDisplayNameString(),
                event.getAchievement().getDescription());
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            logger.info("%s died: %s", player.getDisplayNameString(), event.getSource().getDamageType());
            APIChat.deathMessage(player.getDisplayNameString(), event.getSource().getDamageType());
        }
    }

    static {
        String classpath = FrogMod.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String jarpath = classpath.substring(0, classpath.indexOf('!'));
        try {
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(ClassLoader.getSystemClassLoader(), new URL(jarpath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
