package com.chef.painter.doughnutPainter.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MsgSender {
    static String msgPrefixString = ChatColor.DARK_PURPLE + "[" + ChatColor.LIGHT_PURPLE + "doughnutPainter" + ChatColor.DARK_PURPLE + "] ";

    public enum MsgLevel {
        NORMAL(ChatColor.GRAY),
        HELP(ChatColor.GOLD),
        NOTICE(ChatColor.GREEN),
        WARNING(ChatColor.YELLOW),
        ERROR(ChatColor.DARK_RED);
        private final ChatColor color;

        MsgLevel(ChatColor c) {
            this.color = c;
        }
    }

    static public void send(Player player, MsgLevel level, String msg) {
        player.sendMessage(msgPrefixString + level.color + msg);
    }

    static public void send(Player player, MsgLevel level, String msg, boolean noPrefix) {
        player.sendMessage(level.color + msg);
    }

//    static public void broadcast(MsgLevel level, String msg) {
//        Bukkit.broadcast(msgPrefixString + level.color + msg);
//        Bukkit.broadcast("");
//    }
}
