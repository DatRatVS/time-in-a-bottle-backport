package com.datrat.tiab;

import com.datrat.tiab.api.InternalApi;
import com.datrat.tiab.command.CommandTiab;
import com.datrat.tiab.config.TiabConfig;
import com.datrat.tiab.entity.TimeAcceleratorEntity;
import com.datrat.tiab.item.TimeInABottleItem;
import com.datrat.tiab.proxy.CommonProxy;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

@Mod(modid = TimeInABottle.MOD_ID, name = TimeInABottle.MOD_NAME, version = TimeInABottle.VERSION)
public class TimeInABottle {
    public static final String MOD_ID = "tiab";
    public static final String MOD_NAME = "Time In A Bottle";
    public static final String VERSION = "1.7.10-4.0.4";

    @Mod.Instance(MOD_ID)
    public static TimeInABottle instance;

    @SidedProxy(
            clientSide = "com.datrat.tiab.proxy.ClientProxy",
            serverSide = "com.datrat.tiab.proxy.CommonProxy"
    )
    public static CommonProxy proxy;

    public static TimeInABottleItem timeInABottleItem;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        TiabConfig.load(event.getSuggestedConfigurationFile());
        timeInABottleItem = new TimeInABottleItem();
        GameRegistry.registerItem(timeInABottleItem, "time_in_a_bottle");
        EntityRegistry.registerModEntity(TimeAcceleratorEntity.class, "time_accelerator", 1, this, 64, 1, false);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.registerRenderers();
        registerRecipe();
        InternalApi.init();
    }

    @EventHandler
    public void processIMC(FMLInterModComms.IMCEvent event) {
        InternalApi.processIMC(event.getMessages());
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandTiab());
    }

    private void registerRecipe() {
        GameRegistry.addRecipe(new ItemStack(timeInABottleItem),
                "GGG",
                "DCD",
                "LBL",
                'G', Items.gold_ingot,
                'D', Items.diamond,
                'C', Items.clock,
                'B', Items.glass_bottle,
                'L', new ItemStack(Items.dye, 1, 4));
    }
}
