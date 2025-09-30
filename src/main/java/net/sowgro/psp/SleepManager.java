package net.sowgro.psp;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

import static net.sowgro.psp.PlayersSleepingPercentage.plugin;

public class SleepManager {

    private static final HashMap<World, SleepManager> worldListeners = new HashMap<>();

    public static SleepManager fromWorld(World world) {
        worldListeners.putIfAbsent(world, new SleepManager(world));
        return worldListeners.get(world);
    }

    Integer taskID;
    World world;

    public SleepManager(World world) {
        this.world = world;
        taskID = -1;
    }

    void update(int offset, Player player) {
        if (enoughPlayersToSleep(offset, player)) {
            if (taskID == null) {
                taskID = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this::skipNight, 100L);
            }
        } else {
            if (taskID != null) {
                Bukkit.getScheduler().cancelTask(taskID);
                taskID = null;
            }
        }
    }

    public void skipNight() {
        world.setTime(0);
        world.setStorm(false);
        world.setThundering(false);
        taskID = null;
    }

    /**
     * Decides if there are enough players sleeping to satisfy the percentage in the config
     *
     * @param offset Used to offset the number of players counted as sleeping. The player who just entered/left a bed is not recognized as sleeping so 1 / -1 is passed in after those events
     * @return True if there are enough players sleeping
     */
    private boolean enoughPlayersToSleep(int offset, Player player) {
        List<Player> players = world.getPlayers();

        long playersSleeping = players.stream().filter(LivingEntity::isSleeping).count() + offset;
        int playersTotal = world.getPlayers().size();
        int configPercent = plugin.getConfig().getInt("PlayersSleepingPercentage", 100);
        int playersRequired = (int) Math.ceil(configPercent / 100.0 * playersTotal);

        if (playersSleeping >= playersRequired) {
            String s = "Skipping the night";
            players.forEach(p -> sendActionBar(p, s));
        } else if (configPercent > 100) {
            String s = "Skipping the night is disabled";
            sendActionBar(player, s);
        } else if (configPercent == 100) {
            String s = String.format("%s/%s players sleeping", playersSleeping, playersTotal);
            players.forEach(p -> sendActionBar(p, s));
        } else {
            String s = String.format("%s/%s players sleeping (%s required)", playersSleeping, playersTotal, playersRequired);
            players.forEach(p -> sendActionBar(p, s));
        }

        return playersSleeping >= playersRequired;
    }

    void sendActionBar(Player player, String message) {
        if (player == null) return;
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }
}
