package com.chef.painter.doughnutPainter;

import com.chef.painter.doughnutPainter.commands.MainCommands;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class PluginMain extends JavaPlugin {
    public static Logger logger = PluginLogger.getLogger("doughnutPainter");
    @Override
    public void onEnable() {
        // Plugin startup logic
        logger.info("=============[doughnutPainter enable]=============");
        getCommand("cep").setExecutor(new MainCommands());
        getCommand("cep").setTabCompleter(new MainCommands());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
