package de.jerome.whatsthesongsname.spigot.manager;

import de.jerome.whatsthesongsname.spigot.WTSNMain;
import de.jerome.whatsthesongsname.spigot.object.WTSNPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.UUID;

public class PlayerManager {

    private final HashMap<UUID, WTSNPlayer> players;

    public PlayerManager() {
        players = new HashMap<>();
    }

    public void loadPlayers() {
        for (String playerUUID : WTSNMain.getInstance().getFileManager().getPlayers().getFileConfiguration().getKeys(false))
            getPlayer(UUID.fromString(playerUUID));
    }

    public void saveAllPlayers() {
        for (WTSNPlayer player : players.values()) player.save();
    }

    public @NotNull WTSNPlayer getPlayer(@NotNull UUID uuid) {
        if (players.containsKey(uuid)) return players.get(uuid);
        WTSNPlayer WTSNPlayer = new WTSNPlayer(uuid);
        players.put(uuid, WTSNPlayer);
        return WTSNPlayer;
    }

    public @NotNull WTSNPlayer getPlayer(@NotNull Player player) {
        return getPlayer(player.getUniqueId());
    }

    public @Nullable WTSNPlayer getPlayer(@NotNull String name) {
        UUID uuid = WTSNMain.getInstance().getUuidFetcher().getUUID(name);
        if (uuid == null) return null;
        return getPlayer(uuid);
    }

    public void unloadAllOfflinePlayers() {
        for (WTSNPlayer wtsnPlayer : players.values()) {
            if (Bukkit.getOfflinePlayer(wtsnPlayer.getUuid()).isOnline()) continue;
            wtsnPlayer.save();
            players.remove(wtsnPlayer.getUuid());
        }
    }

    public void resetPlaysForAll(){
        for(WTSNPlayer wtsnPlayer : players.values())
            wtsnPlayer.resetPlays();
    }
}
