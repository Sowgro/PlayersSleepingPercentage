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
        if (enoughPlayersToSleep(caused)) {
            if (taskID == null) {
                String s = "Skipping the night";
                world.getPlayers().forEach(p -> sendActionBar(p, s));
                taskID = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this::skipNightTask, 100L);
            }
        } else {
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

    /**
     * Decides if there are enough players sleeping to satisfy the percentage in the config
     *
     * @param caused event sender
     * @return True if there are enough players sleeping
     */
    private boolean enoughPlayersToSleep(Player caused) {
        List<Player> players = world.getPlayers();

        int nPlayersSleeping = (int) players.stream().filter(LivingEntity::isSleeping).count();
        int nPlayersTotal = players.size();
        int configPercent = plugin.getConfig().getInt("PlayersSleepingPercentage", 100);
        int nPlayersRequired = (int) Math.ceil(configPercent / 100.0 * nPlayersTotal);

        if ((nPlayersSleeping == 0 && prevPlayersSleeping == 0) || isDay(world.getTime())) {
            return false;
        }
        prevPlayersSleeping = nPlayersSleeping;

        if (nPlayersSleeping >= nPlayersRequired) {
            return true;
        }

        if (configPercent > 100) {
            String s = "Skipping the night is disabled";
            sendActionBar(caused, s);
        } else if (configPercent == 100) {
            String s = String.format("%s/%s players sleeping", nPlayersSleeping, nPlayersTotal);
            players.forEach(p -> sendActionBar(p, s));
        } else {
            String s = String.format("%s/%s players sleeping (%s required)", nPlayersSleeping, nPlayersTotal, nPlayersRequired);
            players.forEach(p -> sendActionBar(p, s));
        }

        return false;
    }

    void sendActionBar(Player player, String message) {
        if (player == null) {
            return;
        }

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }

    boolean isDay(long time) {
        return time < 12300 || time > 23850;
    }
}
