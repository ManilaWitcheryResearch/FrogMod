package com.example.examplemod;

import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by swordfeng on 16-11-18.
 */
public class SimpleEventHandler {
    @SubscribeEvent
    public void onServerChat(ServerChatEvent event) {
        System.out.printf("%s says: %s\n", event.getUsername(), event.getMessage());
        event.getPlayer().addChatMessage(new TextComponentString("echo: " + event.getMessage()));
    }
}
