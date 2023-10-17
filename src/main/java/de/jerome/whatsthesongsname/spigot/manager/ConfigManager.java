package de.jerome.whatsthesongsname.spigot.manager;

import de.jerome.whatsthesongsname.spigot.WTSNMain;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ConfigManager {

    private static final FileConfiguration configYAML = WTSNMain.getInstance().getFileManager().getConfig().getFileConfiguration();

    // config.yml
    private String languagesDefault, bungeecordLobby, databaseHost, databaseDatabase, databaseUsername, databasePassword;
    private int databasePort, musicPlayTime, choseTime, rewardVaultCorrect, rewardVaultWrong, roundLimit, rewardSnowballsCorrect;
    private boolean rewardCommandEnabled, rewardVaultEnabled, bungeecordEnable, databaseEnable;
    private List<String> languagesLanguages, rewardCommandCorrect, rewardCommandWrong;


    public ConfigManager() {
        reload();
    }

    public void reload() {
        reloadConfig();
    }

    /**
     * reloads content of config.yml
     */
    public void reloadConfig() {
        musicPlayTime = configYAML.getInt("musicPlayTime");
        choseTime = configYAML.getInt("choseTime");
        roundLimit = configYAML.getInt("roundLimit");

        rewardSnowballsCorrect = configYAML.getInt("reward.snowballs.correct");

        rewardCommandEnabled = configYAML.getBoolean("reward.command.enabled");
        rewardCommandCorrect = configYAML.getStringList("reward.command.correct");
        rewardCommandWrong = configYAML.getStringList("reward.command.wrong");

        rewardVaultEnabled = configYAML.getBoolean("reward.vault.enabled");
        rewardVaultCorrect = configYAML.getInt("reward.vault.correct");
        rewardVaultWrong = configYAML.getInt("reward.vault.wrong");

        languagesDefault = configYAML.getString("languages.default");
        languagesLanguages = configYAML.getStringList("languages.languages");

        bungeecordEnable = configYAML.getBoolean("bungeecord.enable");
        bungeecordLobby = configYAML.getString("bungeecord.lobby");

        databaseEnable = configYAML.getBoolean("database.enable");
        databaseHost = configYAML.getString("database.host");
        databasePort = configYAML.getInt("database.port");
        databaseDatabase = configYAML.getString("database.database");
        databaseUsername = configYAML.getString("database.username");
        databasePassword = configYAML.getString("database.password");
    }

    public String getLanguagesDefault() {
        return languagesDefault;
    }

    public @Nullable String getBungeecordLobby() {
        return bungeecordLobby;
    }

    public @Nullable String getDatabaseHost() {
        return databaseHost;
    }

    public @Nullable String getDatabaseDatabase() {
        return databaseDatabase;
    }

    public @Nullable String getDatabaseUsername() {
        return databaseUsername;
    }

    public @Nullable String getDatabasePassword() {
        return databasePassword;
    }

    public int getDatabasePort() {
        return databasePort;
    }

    public int getMusicPlayTime() {
        return musicPlayTime;
    }

    public int getChoseTime() {
        return choseTime;
    }

    public int getRewardVaultCorrect() {
        return rewardVaultCorrect;
    }

    public int getRewardVaultWrong() {
        return rewardVaultWrong;
    }

    public boolean isRewardVaultEnabled() {
        return rewardVaultEnabled;
    }

    public boolean isBungeecordEnable() {
        return bungeecordEnable;
    }

    public boolean isDatabaseEnable() {
        return databaseEnable;
    }

    public boolean isRewardCommandEnabled() {
        return rewardCommandEnabled;
    }

    public @NotNull List<String> getLanguagesLanguages() {
        return languagesLanguages;
    }

    public @NotNull List<String> getRewardCommandCorrect() {
        return rewardCommandCorrect;
    }

    public @NotNull List<String> getRewardCommandWrong() {
        return rewardCommandWrong;
    }

    public int getRoundLimit() {
        return roundLimit;
    }
    public int getRewardSnowballsCorrect() {
        return rewardSnowballsCorrect;
    }
}
