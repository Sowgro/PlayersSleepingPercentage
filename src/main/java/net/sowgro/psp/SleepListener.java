package net.sowgro.psp;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerBedLeaveEvent;

import java.util.HashMap;

import static net.sowgro.psp.PlayersSleepingPercentage.plugin;

public class SleepListener implements Listener {

    private final HashMap<World, Integer> eventIDs = new HashMap<>();

    @EventHandler
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {

        // check that the player successfully entered the bed
        if (event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) {
            return;
        }

        World world = event.getPlayer().getWorld();

        if (enoughPlayersToSleep(world, 1) && eventIDs.get(world) == null) {
            eventIDs.put(world, Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                world.setTime(0);
                world.setStorm(false);
                world.setThundering(false);
                eventIDs.remove(world);
            }, 100L)); // Delay in server ticks before running the task, to ensure it happens after the player is in bed.
        }
    }

    @EventHandler
    public void onPlayerBedLeave(PlayerBedLeaveEvent event) {

        World world = event.getPlayer().getWorld();

        if (!enoughPlayersToSleep(world, -1) && eventIDs.get(world) != null) {
            Bukkit.getServer().getScheduler().cancelTask(eventIDs.get(world));
            eventIDs.remove(world);
        }
    }

    /**
     * Decides if there are enough players sleeping to satisfy the percentage in the config
     * @param world The world to check players in
     * @param playersSleeping Used to offset the number of players counted as sleeping. The player who just entered/left a bed is not recognized as sleeping so 1 / -1 is passed in after those events
     * @return True if there are enough players sleeping
     */
    private boolean enoughPlayersToSleep(World world, int playersSleeping) {
        for (Player p : world.getPlayers()) {
            if (p.isSleeping()) { playersSleeping++; }
        }
        double currentPercent = ((double) playersSleeping / world.getPlayers().size()) * 100;
        double configPercent = plugin.getConfig().getInt("PlayersSleepingPercentage", 100);
        return currentPercent >= configPercent;
    }
}
