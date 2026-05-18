package com.datrat.tiab.util;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

public final class Chat {
    private Chat() {
    }

    public static void send(EntityPlayer player, String message) {
        if (player != null) {
            player.addChatMessage(new ChatComponentText(message));
        }
    }

    public static void send(ICommandSender sender, String message) {
        if (sender != null) {
            sender.addChatMessage(new ChatComponentText(message));
        }
    }
}
