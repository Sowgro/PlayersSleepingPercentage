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
        SleepManager.UpdateContext cxt = manager.new UpdateContext();

        cxt.playerThatCaused = player;
        cxt.playersSleeping.add(player);
        manager.update(cxt);
    }

    @EventHandler
    public void onPlayerBedLeave(PlayerBedLeaveEvent event) {
        Player player = event.getPlayer();
        SleepManager manager = getManager(player.getWorld());
        SleepManager.UpdateContext cxt = manager.new UpdateContext();

        cxt.playersSleeping.remove(player);
        manager.update(cxt);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        SleepManager manager = getManager(player.getWorld());
        SleepManager.UpdateContext cxt = manager.new UpdateContext();

        manager.update(cxt);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        SleepManager manager = getManager(player.getWorld());
        SleepManager.UpdateContext cxt = manager.new UpdateContext();

        cxt.playersTotal.remove(player);
        cxt.playersSleeping.remove(player);
        manager.update(cxt);
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getTo() == null || event.getFrom().getWorld() == event.getTo().getWorld()) {
            return;
        }

        Player player = event.getPlayer();
        {
            SleepManager manager = getManager(event.getFrom().getWorld());
            SleepManager.UpdateContext cxt = manager.new UpdateContext();

            cxt.playersTotal.remove(player);
            cxt.playersSleeping.remove(player);
            manager.update(cxt);
        }
        {
            SleepManager manager = getManager(event.getTo().getWorld());
            SleepManager.UpdateContext cxt = manager.new UpdateContext();

            cxt.playersTotal.add(player);
            if (player.isSleeping()) {
                cxt.playersSleeping.add(player);
            }
            manager.update(cxt);
        }
    }

}
