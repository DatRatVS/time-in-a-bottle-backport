package com.magorage.tiab.api.events;

import com.magorage.tiab.api.HandledState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

@FunctionalInterface
public interface ITimeBottleTickEvent {
    boolean accept(EntityPlayerMP player, ItemStack bottle, HandledState handledState);
}
