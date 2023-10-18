package de.jerome.whatsthesongsname.spigot.manager;

import com.xxmicloxx.NoteBlockAPI.model.Playlist;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import de.jerome.whatsthesongsname.spigot.WTSNMain;
import de.jerome.whatsthesongsname.spigot.object.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public class GameManager {

    private static final InventoryManager inventoryManager = WTSNMain.getInstance().getInventoryManager();
    private static final LanguagesManager languagesManager = WTSNMain.getInstance().getLanguagesManager();
    private static final SongManager songManager = WTSNMain.getInstance().getSongManager();

    private final ArrayList<Player> gamePlayers;
    private final HashMap<Player, String> playerAnswers;
    private boolean ready, running, allowInventoryClose;

    private BukkitTask stopMusicTask;


    public GameManager() {
        gamePlayers = new ArrayList<>();
        playerAnswers = new HashMap<>();

        reload();
    }

    public void reload() {
        stopGame();

        if (songManager.getPlaylist() == null || songManager.getRadioSongPlayer() == null) {
            ready = false;
            return;
        }

        ready = songManager.getPlaylist().getSongList().size() >= 4;

        // Checks if there are at least 4 songs
        if (!ready)
            WTSNMain.getInstance().getLogger().log(Level.WARNING, "At least 4 songs are required for the game to start. Please add more!");
    }

    public void startGame() {
        if (!ready) return;
        running = true;
        startMusic();
    }

    public void stopGame() {
        running = false;
        allowInventoryClose = false;
        gamePlayers.clear();
        playerAnswers.clear();

        if (stopMusicTask != null) {
            stopMusicTask.cancel();
            stopMusicTask = null;
        }

        stopMusic();
    }

    private void startMusic() {
        for(int i = 0; i < gamePlayers.size(); i++){
            Player player = gamePlayers.get(i);
            if(WTSNMain.getInstance().getPlayerManager().getPlayer(player).getPlays() >= WTSNMain.getInstance().getConfigManager().getRoundLimit()){
                player.sendMessage(languagesManager.getMessage(player.getLocale(), Messages.LEAVE_PLAYS_EXCEEDED));
                leaveGame(player);
            }
        }
        RadioSongPlayer radioSongPlayer = WTSNMain.getInstance().getSongManager().getRadioSongPlayer();
        if (radioSongPlayer == null) return;

        radioSongPlayer.setPlaying(true);
        autoStopMusic();
    }

    private void stopMusic() {
        RadioSongPlayer radioSongPlayer = WTSNMain.getInstance().getSongManager().getRadioSongPlayer();
        if (radioSongPlayer == null) return;

        // Pause song playback
        radioSongPlayer.setPlaying(false);

        // Selects the next song
        radioSongPlayer.playNextSong();
    }

    private void autoStopMusic() {
        // Pauses song playback after a variable number of seconds
        stopMusicTask = Bukkit.getScheduler().runTaskLater(WTSNMain.getInstance(), () -> {
            if (!running) return;

            // stops the music
            stopMusic();

            // opens the inventory for selecting the song
            startInventoryChose();

            stopMusicTask = null;
        }, 20L * WTSNMain.getInstance().getConfigManager().getMusicPlayTime());
    }

    private void startInventoryChose() {
        // Inventory setup

        // A temporary list containing the songs available for selection in the inventory
        List<Song> choseSongs = new ArrayList<>();

        // The correct title is added
        RadioSongPlayer radioSongPlayer = WTSNMain.getInstance().getSongManager().getRadioSongPlayer();
        if (radioSongPlayer == null) return;
        choseSongs.add(radioSongPlayer.getSong());

        // List of all songs so that some of them can be randomly added to the inventory
        Playlist playlist = WTSNMain.getInstance().getSongManager().getPlaylist();
        if (playlist == null) return;
        List<Song> songs = playlist.getSongList();

        // Iterates through the "songs" list and randomly adds 3 more to "chooseTitles"
        Collections.shuffle(songs);
        for (int i = 0; i < 3; i++) {
            choseSongs.add(songs.get(i));
        }

        // Shuffles the selection so that the first item is not always the right one
        Collections.shuffle(choseSongs);

        // Renames the items in the ChoseInventory
        inventoryManager.updateChoseItems(choseSongs.get(0), choseSongs.get(1), choseSongs.get(2), choseSongs.get(3));

        // Opens ChoseInventory
        for (Player gamePlayer : gamePlayers)
            gamePlayer.openInventory(inventoryManager.getChoseInventory(gamePlayer.getLocale()));

        // Starts evaluating the songs after a variable time
        Bukkit.getScheduler().runTaskLater(WTSNMain.getInstance(), this::evaluateChoosing, 20L * WTSNMain.getInstance().getConfigManager().getChoseTime());
    }

    private void evaluateChoosing() {
        // Gets the correct song
        RadioSongPlayer radioSongPlayer = WTSNMain.getInstance().getSongManager().getRadioSongPlayer();
        if (radioSongPlayer == null) return;
        Song song = radioSongPlayer.getSong();

        // So that the inventory is not reopened by the InventoryCloseEvent
        allowInventoryClose = true;

        for (Player gamePlayer : gamePlayers) {
            // Close ChoseInventory
            int hashCode = gamePlayer.getOpenInventory().getTopInventory().hashCode();
            for (Inventory inventory : WTSNMain.getInstance().getInventoryManager().getChoseInventories().values())
                if (hashCode == inventory.hashCode()) {
                    gamePlayer.closeInventory();
                    break;
                }

            WTSNMain.getInstance().getPlayerManager().getPlayer(gamePlayer).addPlay();

            gamePlayer.sendMessage(languagesManager.getMessage(gamePlayer.getLocale(), Messages.CHOSE_EVALUATION_SONG_NAME)
                    .replaceAll("\\{songTitle}", song.getTitle())
                    .replaceAll("\\{songAuthor}", song.getAuthor()));

            // Evaluate song selection
            if (!playerAnswers.containsKey(gamePlayer)) {
                // No answer by player
                gamePlayer.sendMessage(languagesManager.getMessage(gamePlayer.getLocale(), Messages.CHOSE_EVALUATION_NO_ANSWER));
                continue;
            }

            if (!playerAnswers.get(gamePlayer).equals(song.getTitle())) {
                // Wrong answer
                WTSNMain.getInstance().getPlayerManager().getPlayer(gamePlayer).addGuessedWrong();
                gamePlayer.sendMessage(languagesManager.getMessage(gamePlayer.getLocale(), Messages.CHOSE_EVALUATION_WRONG_ANSWER));
                continue;
            }

            // Correct answer
            WTSNMain.getInstance().getPlayerManager().getPlayer(gamePlayer).addGuessedCorrectly();
            gamePlayer.sendMessage(languagesManager.getMessage(gamePlayer.getLocale(), Messages.CHOSE_EVALUATION_CORRECT_ANSWER).replaceAll("\\{snowballs}", WTSNMain.getInstance().getConfigManager().getRewardSnowballsCorrect() + ""));
        }

        // Disables inventory closing again
        allowInventoryClose = false;

        // Clears players' responses
        playerAnswers.clear();

        // Stats Musik and repeat all

        startMusic();
    }

    /**
     * Have a player enter the game
     *
     * @param player the player who wants to enter
     * @return if success
     */
    public boolean joinGame(@NotNull Player player) {
        // Checks if there are at least 4 songs
        if (!ready) {
            player.sendMessage("§cAt least 4 songs are required for the game to start!");
            return true;
        }

        // Checks if the player is already in the game
        if (gamePlayers.contains(player)) return false;
        gamePlayers.add(player);

        // Adds the player to the RadioSongPlayer
        RadioSongPlayer radioSongPlayer = WTSNMain.getInstance().getSongManager().getRadioSongPlayer();
        if (radioSongPlayer == null) return false;
        radioSongPlayer.addPlayer(player);

        // Starts the game if it's not already started
        if (!running) startGame();
        return true;
    }

    /**
     * Have a player leave the game
     *
     * @param player the player who wants to leave
     * @return if success
     */
    public boolean leaveGame(@NotNull Player player) {
        // Checks if the player is even in the game
        if (!gamePlayers.contains(player)) return false;
        gamePlayers.remove(player);

        // Delete the player's answer
        playerAnswers.remove(player);

        // Removes the player from the RadioSongPlayer so that it no longer hears the music
        RadioSongPlayer radioSongPlayer = WTSNMain.getInstance().getSongManager().getRadioSongPlayer();
        if (radioSongPlayer == null) return false;
        radioSongPlayer.removePlayer(player);

        // Stop the game if there are no more players in it
        if (running && gamePlayers.isEmpty()) stopGame();
        return true;
    }

    /**
     * Indicates whether the game can be started and all the conditions provided for it are met
     *
     * @return true if all requirements for the game are met
     */
    public boolean isReady() {
        return ready;
    }

    /**
     * Get the status of the game
     *
     * @return true when the game is running
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Indicates whether the ChoseInventory is allowed to be closed. Is only intended for the internal InventoryCloseEvent!
     *
     * @return true: if the inventory is allowed to be closed
     */
    public boolean isAllowInventoryClose() {
        return allowInventoryClose;
    }

    /**
     * Get all players that are in game
     *
     * @return a copy of gamePlayers
     */
    public @NotNull List<Player> getGamePlayers() {
        return new ArrayList<>(gamePlayers);
    }

    /**
     * A list of all player responses
     *
     * @return the responses submitted by players
     */
    public @NotNull HashMap<Player, String> getPlayerAnswers() {
        return playerAnswers;
    }
}
