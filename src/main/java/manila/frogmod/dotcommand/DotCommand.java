package manila.frogmod.dotcommand;

import manila.frogmod.mcs.API.APIMonitor;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.ServerChatEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by swordfeng on 16-11-18.
 */
public class DotCommand {
    private static Map<String, IDotCommand> handlers = new HashMap<>();

    public static void register(String command, IDotCommand handler) {
        handlers.put(command, handler);
    }

    public static boolean handle(ServerChatEvent event) {
        String msg = event.getMessage();
        if (msg.length() == 0 || msg.charAt(0) != '.') {
            return false;
        }
        int spacePos = msg.indexOf(' ');
        String command;
        String args;
        if (spacePos == -1) {
            command = msg.substring(1);
            args = null;
        } else {
            command = msg.substring(1, spacePos);
            args = msg.substring(spacePos + 1);
        }
        IDotCommand handler = handlers.get(command);
        if (handler == null) return false;
        event.setCanceled(true);
        handler.handleCommand(args, command, event);
        return true;
    }

    static private Optional<TileEntityChest> chest = Optional.empty();

    public static void initCommands() {
        register("info", (args, command, event) -> {
            event.getPlayer().sendMessage(new TextComponentString(String.format(
                    "name: %s\n" +
                    "ip: %s\n",
                    event.getPlayer().getDisplayNameString(),
                    event.getPlayer().getPlayerIP()
            )));
        });
        register("suicide", (args, command, event) -> {
            event.getPlayer().setHealth(-1);
        });
        register("register", (args, command, event) -> {
            APIMonitor.register();
        });

        register("showgui", (args, command, event) -> {
            synchronized (chest) {
                if (chest.isPresent() == false) chest = Optional.of(new TileEntityChest());
            }
            event.getPlayer().displayGui(chest.get());
        });
    }
}
