package net.sowgro.psp;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

public class SleepListener implements Listener {

    @EventHandler
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {
        if (event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) return;

        World world = event.getPlayer().getWorld();
        SleepManager.fromWorld(world).update(1, event.getPlayer());
    }

    @EventHandler
    public void onPlayerBedLeave(PlayerBedLeaveEvent event) {
        World world = event.getPlayer().getWorld();
        SleepManager.fromWorld(world).update(-1, null);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        World world = event.getPlayer().getWorld();
        SleepManager.fromWorld(world).update(0, null);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        World world = event.getPlayer().getWorld();
        SleepManager.fromWorld(world).update(0, null);
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        World world = event.getPlayer().getWorld();
        SleepManager.fromWorld(world).update(0, null);
    }

}
