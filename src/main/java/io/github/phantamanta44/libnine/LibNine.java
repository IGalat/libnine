package io.github.phantamanta44.libnine;

import io.github.phantamanta44.libnine.util.nullity.LateInitialization;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = L9Const.MOD_ID, version = L9Const.VERSION, useMetadata = true)
public class LibNine {

    @Mod.Instance(L9Const.MOD_ID)
    @LateInitialization
    public static LibNine INSTANCE;

    @SidedProxy(
            serverSide = "io.github.phantamanta44.libnine.client.L9ServerProxy",
            clientSide = "io.github.phantamanta44.libnine.client.L9ClientProxy"
    )
    @LateInitialization
    public static L9CommonProxy PROXY;

    @LateInitialization
    public static Logger LOGGER;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        LOGGER = event.getModLog();
        PROXY.onPreInit(event);
    }

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        PROXY.onInit(event);
    }

    @Mod.EventHandler
    public void onPostInit(FMLPostInitializationEvent event) {
        PROXY.onPostInit(event);
    }

    @Mod.EventHandler
    public void onLoadComplete(FMLLoadCompleteEvent event) {
        PROXY.onLoadComplete(event);
    }

}
