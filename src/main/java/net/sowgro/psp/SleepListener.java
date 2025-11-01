package net.sowgro.psp;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.event.world.TimeSkipEvent;

import java.util.HashMap;
import java.util.stream.Stream;

/**
 * Handles sleep events and stores sleep managers for each world
 */
public class SleepListener implements Listener {

    private final HashMap<World, SleepManager> managers = new HashMap<>();

    private SleepManager getManager(World world) {
        managers.putIfAbsent(world, new SleepManager(world));
        return managers.get(world);
    }

    @EventHandler
    public void onTimeSkip(TimeSkipEvent event) {
        if (event.getSkipReason() != TimeSkipEvent.SkipReason.NIGHT_SKIP) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {
        if (event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) {
            return;
        }

        Player player = event.getPlayer();
        SleepManager manager = getManager(player.getWorld());
        manager.update(player);
    }

    @EventHandler
    public void onPlayerBedLeave(PlayerBedLeaveEvent event) {
        SleepManager manager = getManager(event.getPlayer().getWorld());
        manager.update();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        SleepManager manager = getManager(event.getPlayer().getWorld());
        manager.update();
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        SleepManager manager = getManager(event.getPlayer().getWorld());
        manager.update();
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getTo() == null || event.getFrom().getWorld() == event.getTo().getWorld()) {
            return;
        }

        SleepManager manager1 = getManager(event.getFrom().getWorld());
        manager1.update();
        SleepManager manager2 = getManager(event.getTo().getWorld());
        manager2.update();
    }

}
