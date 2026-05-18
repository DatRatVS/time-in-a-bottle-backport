package com.magorage.tiab.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

import java.util.function.Function;

public interface ITimeInABottleAPI {
    class IMC {
        public static final String GET_API = "get_api";
        public static final String MOD_ID = "tiab";
    }

    Item getItem();

    int getTotalTime(ItemStack bottle);

    int getStoredTime(ItemStack bottle);

    String getModID();

    void setStoredTime(ItemStack bottle, int time);

    void setTotalTime(ItemStack bottle, int time);

    int processCommand(Function<EntityPlayerMP, ItemStack> itemStackFunction, EntityPlayerMP player, String messageValue, boolean isAdd);

    IChatComponent getTotalTimeTranslated(ItemStack stack);

    IChatComponent getStoredTimeTranslated(ItemStack stack);

    void playSound(World world, int x, int y, int z, int nextRate);

    void applyDamage(ItemStack stack, int damage);

    int getEnergyCost(int timeRate);

    boolean canUse();

    boolean callCommandEvent(EntityPlayerMP player, int time, boolean isAdd);

    boolean callTickEvent(EntityPlayerMP player, ItemStack stack);

    boolean callUseEvent(ItemStack bottle, EntityPlayer player, World world, int x, int y, int z);

    boolean accelerateBlock(ITimeInABottleAPI api, ItemStack stack, EntityPlayer player, World world, int x, int y, int z);
}
