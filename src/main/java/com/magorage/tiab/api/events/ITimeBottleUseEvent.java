package com.magorage.tiab.api.events;

import com.magorage.tiab.api.HandledState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

@FunctionalInterface
public interface ITimeBottleUseEvent {
    boolean accept(ItemStack bottle, EntityPlayer player, World world, int x, int y, int z, HandledState handledState);
}
