package manila.frogmod.dotcommand;

import net.minecraftforge.event.ServerChatEvent;

import java.util.HashMap;
import java.util.Map;

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

    public static void initCommands() {
        register("info", (args, command, player) -> {});
    }
}
