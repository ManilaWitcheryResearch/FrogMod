package com.frogmcs.frogmod;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = FrogMod.MODID, version = FrogMod.VERSION)
public class FrogMod
{
    public static final String MODID = "FrogMod";
    public static final String VERSION = "0.9";
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(new FrogEventHandler());
    }
}
