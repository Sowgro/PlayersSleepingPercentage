package net.sowgro.psp;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class PlayersSleepingPercentage extends JavaPlugin {

    public static PlayersSleepingPercentage plugin;

    @Override
    public void onEnable() {
        plugin = this;
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new SleepListener(), this);
        Objects.requireNonNull(plugin.getCommand("playersSleepingPercentage")).setExecutor(new Command());
    }
}
