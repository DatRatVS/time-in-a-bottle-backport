package com.datrat.tiab.command;

import com.datrat.tiab.TimeInABottle;
import com.datrat.tiab.api.InternalApi;
import com.datrat.tiab.config.TiabConfig;
import com.datrat.tiab.item.TimeInABottleItem;
import com.datrat.tiab.util.Chat;
import com.magorage.tiab.api.ITimeInABottleAPI;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import java.util.List;

public class CommandTiab extends CommandBase {
    @Override
    public String getCommandName() {
        return TimeInABottle.MOD_ID;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/tiab <addTime|removeTime> <seconds>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length != 2) {
            throw new WrongUsageException(getCommandUsage(sender));
        }

        boolean add;
        if ("addTime".equals(args[0])) {
            add = true;
        } else if ("removeTime".equals(args[0])) {
            add = false;
        } else {
            throw new WrongUsageException(getCommandUsage(sender));
        }

        EntityPlayerMP player = getCommandSenderAsPlayer(sender);
        handle(player, args[1], add);
    }

    public static int handle(EntityPlayerMP player, String value, boolean add) {
        return handleBottle(player, findBottle(player), value, add);
    }

    public static int handleBottle(EntityPlayerMP player, ItemStack bottle, String value, boolean add) {
        ITimeInABottleAPI api = InternalApi.getInternalApi();
        if (bottle == null) {
            Chat.send(player, "No Time in a bottle item in inventory");
            return 0;
        }

        int seconds;
        try {
            seconds = Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            Chat.send(player, "Invalid time parameter! (is the number too big?)");
            return 0;
        }

        if (seconds < 0) {
            Chat.send(player, "Invalid time parameter! Time must not be negative.");
            return 0;
        }

        int maxSeconds = TiabConfig.maxStoredTime / 20;
        if (seconds > maxSeconds) {
            seconds = maxSeconds;
        }

        int current = api.getStoredTime(bottle);
        if (!add) {
            int currentSeconds = current / 20;
            if (seconds > currentSeconds) {
                seconds = currentSeconds;
            }
            seconds = -seconds;
        }

        if (api.canUse()) {
            api.setStoredTime(bottle, current + seconds * 20);
            Chat.send(player, String.format("%s %d seconds", add ? "Added" : "Removed", Math.abs(seconds)));
            return 1;
        }

        if (!api.callCommandEvent(player, seconds, add)) {
            Chat.send(player, "TIAB has had its API access revoked.");
        }
        return 0;
    }

    public static ItemStack findBottle(EntityPlayerMP player) {
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (stack != null && stack.getItem() instanceof TimeInABottleItem) {
                return stack;
            }
        }
        return null;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, "addTime", "removeTime") : null;
    }
}
