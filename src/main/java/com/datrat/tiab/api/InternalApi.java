package com.datrat.tiab.api;

import com.datrat.tiab.TimeInABottle;
import com.datrat.tiab.command.CommandTiab;
import com.datrat.tiab.config.TiabConfig;
import com.datrat.tiab.item.TimeInABottleItem;
import com.datrat.tiab.util.TimeFormat;
import com.magorage.tiab.api.ITimeInABottleAPI;
import com.magorage.tiab.api.TiabProvider;
import cpw.mods.fml.common.event.FMLInterModComms;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public final class InternalApi implements ITimeInABottleAPI {
    private static final APIHooks HOOKS = new APIHooks();
    private static final Set<String> PROCESSED_MODS = new HashSet<String>();
    private static final InternalApi INTERNAL_API = new InternalApi(TimeInABottle.MOD_ID);

    private final String apiModId;

    private InternalApi(String apiModId) {
        this.apiModId = apiModId;
    }

    public static void init() {
        // Forces class initialization after the item has been registered.
    }

    public static ITimeInABottleAPI getInternalApi() {
        return INTERNAL_API;
    }

    public static APIHooks getHooks() {
        return HOOKS;
    }

    public static void processIMC(Iterable<FMLInterModComms.IMCMessage> messages) {
        for (FMLInterModComms.IMCMessage message : messages) {
            if (!ITimeInABottleAPI.IMC.GET_API.equals(message.key) || !message.isStringMessage()) {
                continue;
            }

            String modId = message.getStringValue();
            if (PROCESSED_MODS.contains(modId)) {
                throw new IllegalStateException("Mod requested the TIAB API more than once: " + modId);
            }

            TiabProvider provider = TiabProvider.takePending(modId);
            if (provider != null) {
                provider.setAPI(new InternalApi(provider.getModID()));
                HOOKS.addEventCallbacks(provider.getHooks());
                provider.setFinalized();
                PROCESSED_MODS.add(modId);
            }
        }
    }

    @Override
    public Item getItem() {
        return TimeInABottle.timeInABottleItem;
    }

    @Override
    public int getTotalTime(ItemStack bottle) {
        return isBottle(bottle) ? item().getTotalTime(bottle) : 0;
    }

    @Override
    public int getStoredTime(ItemStack bottle) {
        return isBottle(bottle) ? item().getStoredTime(bottle) : 0;
    }

    @Override
    public String getModID() {
        return TimeInABottle.MOD_ID;
    }

    @Override
    public void setStoredTime(ItemStack bottle, int time) {
        if (canUse() && isBottle(bottle)) {
            item().setStoredTime(bottle, time);
        }
    }

    @Override
    public void setTotalTime(ItemStack bottle, int time) {
        if (canUse() && isBottle(bottle)) {
            item().setTotalTime(bottle, time);
        }
    }

    @Override
    public int processCommand(Function<EntityPlayerMP, ItemStack> itemStackFunction, EntityPlayerMP player, String messageValue, boolean isAdd) {
        if (!canUse()) {
            return 0;
        }
        ItemStack stack = itemStackFunction.apply(player);
        if (stack == null || !isBottle(stack)) {
            return 0;
        }
        return CommandTiab.handleBottle(player, stack, messageValue, isAdd);
    }

    @Override
    public IChatComponent getTotalTimeTranslated(ItemStack stack) {
        return new ChatComponentText("Total accumulated time " + TimeFormat.formatTicks(getTotalTime(stack)));
    }

    @Override
    public IChatComponent getStoredTimeTranslated(ItemStack stack) {
        return new ChatComponentText("Stored time " + TimeFormat.formatTicks(getStoredTime(stack)));
    }

    @Override
    public void playSound(World world, int x, int y, int z, int nextRate) {
        if (canUse()) {
            item().playSound(world, x, y, z, nextRate);
        }
    }

    @Override
    public void applyDamage(ItemStack stack, int damage) {
        if (canUse() && isBottle(stack)) {
            item().applyDamage(stack, damage);
        }
    }

    @Override
    public int getEnergyCost(int timeRate) {
        return item().getEnergyCost(timeRate);
    }

    @Override
    public boolean canUse() {
        return !TiabConfig.isApiBlacklisted(apiModId);
    }

    @Override
    public boolean callCommandEvent(EntityPlayerMP player, int time, boolean isAdd) {
        return TimeInABottle.MOD_ID.equals(apiModId) && HOOKS.fireCommandEvent(player, time, isAdd);
    }

    @Override
    public boolean callTickEvent(EntityPlayerMP player, ItemStack stack) {
        return TimeInABottle.MOD_ID.equals(apiModId) && HOOKS.fireTickEvent(player, stack);
    }

    @Override
    public boolean callUseEvent(ItemStack bottle, EntityPlayer player, World world, int x, int y, int z) {
        return TimeInABottle.MOD_ID.equals(apiModId) && HOOKS.fireUseEvent(bottle, player, world, x, y, z);
    }

    @Override
    public boolean accelerateBlock(ITimeInABottleAPI api, ItemStack stack, EntityPlayer player, World world, int x, int y, int z) {
        return item().accelerateBlock(api, stack, player, world, x, y, z);
    }

    private static boolean isBottle(ItemStack stack) {
        return stack != null && stack.getItem() instanceof TimeInABottleItem;
    }

    private static TimeInABottleItem item() {
        return TimeInABottle.timeInABottleItem;
    }
}
