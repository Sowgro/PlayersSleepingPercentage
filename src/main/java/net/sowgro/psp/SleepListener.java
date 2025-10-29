package net.sowgro.psp;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import java.util.HashMap;

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
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {
        if (event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) {
            return;
        }

        World world = event.getPlayer().getWorld();
        getManager(world).update(1, event.getPlayer());
    }

    @EventHandler
    public void onPlayerBedLeave(PlayerBedLeaveEvent event) {
        World world = event.getPlayer().getWorld();
        getManager(world).update(-1, null);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        World world = event.getPlayer().getWorld();
        getManager(world).update(0, null);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        World world = event.getPlayer().getWorld();
        getManager(world).update(0, null);
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        World world = event.getPlayer().getWorld();
        getManager(world).update(0, null);
    }

}
