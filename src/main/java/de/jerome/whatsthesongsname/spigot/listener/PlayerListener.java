package de.jerome.whatsthesongsname.spigot.listener;

import com.xxmicloxx.NoteBlockAPI.event.SongNextEvent;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import de.jerome.whatsthesongsname.spigot.WTSNMain;
import de.jerome.whatsthesongsname.spigot.object.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    public PlayerListener() {
        Bukkit.getPluginManager().registerEvents(this, WTSNMain.getInstance());
    }

    @EventHandler
    public void handlePlayerJoin(PlayerJoinEvent event) {
        if (!WTSNMain.getInstance().getConfigManager().isBungeecordEnable()) return;
        Player player = event.getPlayer();

        // Does he have the permissions?
        if (!player.hasPermission("wtsn.play")) return;

        WTSNMain.getInstance().getGameManager().joinGame(player);
        player.sendMessage(WTSNMain.getInstance().getLanguagesManager().getMessage("de_de", Messages.JOIN_JOINED));
    }

    @EventHandler
    public void handlePlayerQuit(PlayerQuitEvent event) {
        WTSNMain.getInstance().getGameManager().leaveGame(event.getPlayer());
        WTSNMain.getInstance().getPlayerManager().getPlayer(event.getPlayer()).save();
    }

    @EventHandler
    public void handleNextSong(SongNextEvent event) {
        if (!(event.getSongPlayer() instanceof RadioSongPlayer radioSongPlayer)) return;
        if (radioSongPlayer != WTSNMain.getInstance().getSongManager().getRadioSongPlayer()) return;
        if (WTSNMain.getInstance().getGameManager().getWaitingPlayers() == null || WTSNMain.getInstance().getGameManager().getWaitingPlayers().isEmpty())
            return;
        for (Player waitingPlayer : WTSNMain.getInstance().getGameManager().getWaitingPlayers()) {
            radioSongPlayer.addPlayer(waitingPlayer);
            WTSNMain.getInstance().getGameManager().removeWaitingPlayer(waitingPlayer);
            WTSNMain.getInstance().getGameManager().addGamePlayer(waitingPlayer);
        }
    }
}
