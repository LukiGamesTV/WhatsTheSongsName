package de.jerome.whatsthesongsname.spigot;

import de.jerome.whatsthesongsname.spigot.command.SongratenCommand;
import de.jerome.whatsthesongsname.spigot.command.WTSNCommand;
import de.jerome.whatsthesongsname.spigot.listener.InventoryListener;
import de.jerome.whatsthesongsname.spigot.listener.PlayerListener;
import de.jerome.whatsthesongsname.spigot.manager.*;
import de.jerome.whatsthesongsname.spigot.object.Messages;
import de.jerome.whatsthesongsname.spigot.util.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

public class WTSNMain extends JavaPlugin {

    private static WTSNMain instance;

    private ConfigManager configManager;
    private DatabaseManager databaseManager;
    private FileManager fileManager;
    private GameManager gameManager;
    private InventoryManager inventoryManager;
    private LanguagesManager languagesManager;
    private PlayerManager playerManager;
    private SongManager songManager;
    private UUIDFetcher uuidFetcher;

    private VaultManager vaultManager;

    private LocalDate today;

    public static @NotNull WTSNMain getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        makeInstances();
        registerCommands();
        registerListeners();
        registerChannel();

        Bukkit.getAsyncScheduler().runAtFixedRate(getInstance(), scheduledTask -> {
            LocalDate now = LocalDate.now();
            if(today.isBefore(now)){
                today = now;
                WTSNMain.getInstance().getPlayerManager().loadPlayers();
                WTSNMain.getInstance().getPlayerManager().resetPlaysForAll();
                WTSNMain.getInstance().getPlayerManager().unloadAllOfflinePlayers();
                Bukkit.getConsoleSender().sendMessage(getLanguagesManager().getMessage("de_de", Messages.STATS_PLAYS_RESET));
            }
        }, 1L, 1L, TimeUnit.MINUTES);
    }

    @Override
    public void onDisable() {
        playerManager.saveAllPlayers();

        if (configManager.isDatabaseEnable())
            databaseManager.disconnect();
        else fileManager.save();

        Bukkit.getMessenger().unregisterOutgoingPluginChannel(this);
    }

    public boolean reload() {
        if (gameManager.isRunning()) gameManager.stopGame();
        playerManager.saveAllPlayers();

        boolean success = fileManager.reload();
        if (success) {
            configManager.reload();
            databaseManager.reload();
            inventoryManager.reload();
        }
        success = success && songManager.reload();
        if (success) gameManager.reload();
        return success;
    }

    private void makeInstances() {
        instance = this;
        today = LocalDate.now();
        uuidFetcher = new UUIDFetcher();
        fileManager = new FileManager();
        configManager = new ConfigManager(); // FileManager
        databaseManager = new DatabaseManager(); // FileManager, ConfigManager
        languagesManager = new LanguagesManager(); // FileManager
        vaultManager = new VaultManager(); // ConfigManager
        playerManager = new PlayerManager(); // ConfigManager
        inventoryManager = new InventoryManager(); // ConfigManager
        songManager = new SongManager(); // ConfigManager
        gameManager = new GameManager(); // ConfigManager, SongManager
    }

    private void registerCommands() {
        new WTSNCommand();
        new SongratenCommand();
    }

    private void registerListeners() {
        new InventoryListener();
        new PlayerListener();
    }

    private void registerChannel() {
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    public @NotNull ConfigManager getConfigManager() {
        return configManager;
    }

    public @NotNull DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public @NotNull FileManager getFileManager() {
        return fileManager;
    }

    public @NotNull GameManager getGameManager() {
        return gameManager;
    }

    public @NotNull InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public @NotNull LanguagesManager getLanguagesManager() {
        return languagesManager;
    }

    public @NotNull PlayerManager getPlayerManager() {
        return playerManager;
    }

    public @NotNull SongManager getSongManager() {
        return songManager;
    }

    public @NotNull UUIDFetcher getUuidFetcher() {
        return uuidFetcher;
    }

    public @NotNull VaultManager getVaultManager() {
        return vaultManager;
    }
}