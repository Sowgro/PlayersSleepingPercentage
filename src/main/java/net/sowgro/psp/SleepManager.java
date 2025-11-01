package net.sowgro.psp;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

import static net.sowgro.psp.PlayersSleepingPercentage.plugin;

/**
 * Manages the players sleeping for a specific world
 */
public class SleepManager {

    Integer taskID;
    World world;
    long prevPlayersSleeping = 0;

    public SleepManager(World world) {
        this.world = world;
        taskID = -1;
    }

    public void update() {
        update(null);
    }

    public void update(Player caused) {
        Bukkit.getScheduler().runTask(plugin, () -> updateTask(caused));
    }

    private synchronized void updateTask(Player caused) {
        SleepCalculator sc = new SleepCalculator();

        if (sc.enoughPlayersToSleep) {
            if (taskID == null) {
                //message
                sendActionBar(world.getPlayers(), "Skipping the night");

                //update task
                taskID = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this::skipNightTask, 100L);
            }
        } else {
            //message
            if (sc.isRelevant) {
                if (sc.configPercent > 100) {
                    sendActionBar(caused, "Skipping the night is disabled");
                } else if (sc.configPercent == 100) {
                    String s = String.format("%s/%s players sleeping", sc.nPlayersSleeping, sc.nPlayersTotal);
                    sendActionBar(world.getPlayers(), s);
                } else {
                    String s = String.format("%s/%s players sleeping (%s required)", sc.nPlayersSleeping, sc.nPlayersTotal, sc.nPlayersRequired);
                    sendActionBar(world.getPlayers(), s);
                }
            }

            //update task
            if (taskID != null) {
                Bukkit.getScheduler().cancelTask(taskID);
                taskID = null;
            }
        }
    }

    private synchronized void skipNightTask() {
        world.setTime(0);
        world.setStorm(false);
        world.setThundering(false);
        taskID = null;
    }

    private void sendActionBar(List<Player> player, String message) {
        player.forEach(p -> sendActionBar(p, message));
    }

    private void sendActionBar(Player player, String message) {
        if (player == null) {
            return;
        }

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }

    private class SleepCalculator {
        final int nPlayersSleeping = (int) world.getPlayers().stream().filter(LivingEntity::isSleeping).count();
        final int nPlayersTotal = world.getPlayers().size();
        final int configPercent = plugin.getConfig().getInt("PlayersSleepingPercentage", 100);
        final int nPlayersRequired = (int) Math.ceil(configPercent / 100.0 * nPlayersTotal);
        final boolean isDay = 12300 > world.getTime() || world.getTime() > 23850;
        boolean isRelevant = !(nPlayersSleeping == 0 && prevPlayersSleeping == 0) && !isDay;
        boolean enoughPlayersToSleep = nPlayersSleeping >= nPlayersRequired && !isDay;

        SleepCalculator() {
            prevPlayersSleeping = nPlayersSleeping;
        }
    }
}
