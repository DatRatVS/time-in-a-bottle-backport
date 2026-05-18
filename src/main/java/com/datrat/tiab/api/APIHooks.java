package com.datrat.tiab.api;

import com.magorage.tiab.api.HandledState;
import com.magorage.tiab.api.TiabAPIHooks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public final class APIHooks {
    private final List<TiabAPIHooks> callbacks = new ArrayList<TiabAPIHooks>();

    public void addEventCallbacks(TiabAPIHooks hooks) {
        if (hooks != null) {
            callbacks.add(hooks);
        }
    }

    public boolean fireCommandEvent(EntityPlayerMP player, int time, boolean isAdd) {
        HandledState state = new HandledState();
        boolean result = false;
        for (TiabAPIHooks hooks : callbacks) {
            if (hooks.getCommandEvent() != null) {
                result |= hooks.getCommandEvent().accept(player, time, isAdd, state);
            }
        }
        return state.isHandled() || result;
    }

    public boolean fireTickEvent(EntityPlayerMP player, ItemStack stack) {
        HandledState state = new HandledState();
        boolean result = false;
        for (TiabAPIHooks hooks : callbacks) {
            if (hooks.getTickEvent() != null) {
                result |= hooks.getTickEvent().accept(player, stack, state);
            }
        }
        return state.isHandled() || result;
    }

    public boolean fireUseEvent(ItemStack bottle, EntityPlayer player, World world, int x, int y, int z) {
        HandledState state = new HandledState();
        boolean result = false;
        for (TiabAPIHooks hooks : callbacks) {
            if (hooks.getUseEvent() != null) {
                result |= hooks.getUseEvent().accept(bottle, player, world, x, y, z, state);
            }
        }
        return state.isHandled() || result;
    }
}
