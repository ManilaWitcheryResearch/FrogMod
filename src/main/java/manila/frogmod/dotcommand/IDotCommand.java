package manila.frogmod.dotcommand;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.ServerChatEvent;

/**
 * Created by swordfeng on 16-11-18.
 */
public interface IDotCommand {
    void handleCommand(String args, String command, ServerChatEvent event);
}
