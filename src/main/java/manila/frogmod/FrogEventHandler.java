package manila.frogmod;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by swordfeng on 16-11-18.
 */
public class FrogEventHandler {
    public static MinecraftServer mcServer = null;

    @SubscribeEvent
    public void onServerChat(ServerChatEvent event) {
        FrogMod.logger.info("%s says: %s\n", event.getUsername(), event.getMessage());
        event.getPlayer().addChatMessage(new TextComponentString("echo: " + event.getMessage()));
    }

    @SubscribeEvent
    public void onWorld(WorldEvent event) {
        if (event instanceof WorldEvent.Load) {
            if (mcServer == null) mcServer = event.getWorld().getMinecraftServer();
        } else if (event instanceof WorldEvent.Unload) {
        }
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        Config.getInstance().syncConfig();
    }
}
