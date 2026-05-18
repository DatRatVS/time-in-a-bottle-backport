package com.magorage.tiab.api.events;

import com.magorage.tiab.api.HandledState;
import net.minecraft.entity.player.EntityPlayerMP;

@FunctionalInterface
public interface ITimeCommandEvent {
    boolean accept(EntityPlayerMP player, int time, boolean isAdd, HandledState handledState);
}
