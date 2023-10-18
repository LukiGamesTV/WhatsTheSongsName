package de.jerome.whatsthesongsname.spigot.command;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.jerome.whatsthesongsname.spigot.WTSNMain;
import de.jerome.whatsthesongsname.spigot.manager.LanguagesManager;
import de.jerome.whatsthesongsname.spigot.object.Messages;
import de.jerome.whatsthesongsname.spigot.object.WTSNPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class WTSNCommand implements CommandExecutor, TabExecutor {

    private static final LanguagesManager languagesManager = WTSNMain.getInstance().getLanguagesManager();

    /*
     * help - / - Shows all commands
     * join - wtsn.play - Join the game
     * leave - wtsn.play - Leave the game
     * reload - wtsn.reload - Reloads the songs and the configuration
     * stats - wtsn.stats - Shows the statistics
     * stats [Spielername] - wtsn.stats.other - Shows the statistics of a player
     */

    public WTSNCommand() {
        PluginCommand pluginCommand = WTSNMain.getInstance().getCommand("whatsthesongsname");
        if (pluginCommand == null) return;
        pluginCommand.setExecutor(this);
        pluginCommand.setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String commandName, @NotNull String[] args) {
        String localeCode = null;
        if (commandSender instanceof Player player) localeCode = player.getLocale();

        if (args.length == 0) {
            commandSender.sendMessage(languagesManager.getMessage(localeCode, Messages.INFO)
                    .replaceAll("\\{pluginAuthors}", WTSNMain.getInstance().getDescription().getAuthors().toString())
                    .replaceAll("\\{commandName}", commandName));
            return true;
        }

        if (args.length == 1) {
            // Subcommand "join"
            if (args[0].equalsIgnoreCase("join")) {
                // Does he have the permissions?
                if (!commandSender.hasPermission("wtsn.play")) {
                    sendSyntaxMessage(commandSender, commandName);
                    return true;
                }

                // Is he a player?
                if (!(commandSender instanceof Player player)) {
                    sendSyntaxMessage(commandSender, commandName);
                    return true;
                }

                // Is he already in the game? If not, he enters it
                if (!WTSNMain.getInstance().getGameManager().joinGame(player)) {
                    commandSender.sendMessage(languagesManager.getMessage(localeCode, Messages.JOIN_ALREADY_IN_GAME));
                    return true;
                }

                commandSender.sendMessage(languagesManager.getMessage(localeCode, Messages.JOIN_JOINED));
                return true;
            }

            // Subcommand "leave"
            if (args[0].equalsIgnoreCase("leave")) {
                // Does he have the permissions?
                if (!commandSender.hasPermission("wtsn.play")) {
                    sendSyntaxMessage(commandSender, commandName);
                    return true;
                }

                // Is he a player?
                if (!(commandSender instanceof Player player)) {
                    sendSyntaxMessage(commandSender, commandName);
                    return true;
                }

                // Is he in game? If so, he leaves
                if (!WTSNMain.getInstance().getGameManager().leaveGame(player)) {
                    commandSender.sendMessage(languagesManager.getMessage(localeCode, Messages.LEAVE_NOT_IN_GAME));
                    return true;
                }

                commandSender.sendMessage(languagesManager.getMessage(localeCode, Messages.LEAVE_LEFT));

                if (WTSNMain.getInstance().getConfigManager().isBungeecordEnable() && WTSNMain.getInstance().getConfigManager().getBungeecordLobby() != null) {
                    ByteArrayDataOutput out = ByteStreams.newDataOutput();
                    out.writeUTF("Connect");
                    out.writeUTF(WTSNMain.getInstance().getConfigManager().getBungeecordLobby());
                    player.sendPluginMessage(WTSNMain.getInstance(), "BungeeCord", out.toByteArray());
                }
                return true;
            }

            if (args[0].equalsIgnoreCase("reload")) {
                // Does he have the permissions?
                if (!commandSender.hasPermission("wtsn.reload")) {
                    sendSyntaxMessage(commandSender, commandName);
                    return true;
                }

                if (!WTSNMain.getInstance().reload()) {
                    commandSender.sendMessage(languagesManager.getMessage(localeCode, Messages.RELOAD_FAILED));
                }

                commandSender.sendMessage(languagesManager.getMessage(localeCode, Messages.RELOAD_SUCCESS));
                return true;
            }

            if (args[0].equalsIgnoreCase("stats")) {
                // Does he have the permissions?
                if (!commandSender.hasPermission("wtsn.stats")) {
                    sendSyntaxMessage(commandSender, commandName);
                    return true;
                }

                // Is he a player?
                if (!(commandSender instanceof Player player)) {
                    sendSyntaxMessage(commandSender, commandName);
                    return true;
                }

                WTSNPlayer WTSNPlayer = WTSNMain.getInstance().getPlayerManager().getPlayer(player);

                // The Stats of the player
                commandSender.sendMessage(languagesManager.getMessage(localeCode, Messages.STATS_OWN)
                        .replaceAll("\\{playerPoints}", String.valueOf(WTSNPlayer.getPoints()))
                        .replaceAll("\\{playerGuessedCorrectly}", String.valueOf(WTSNPlayer.getGuessedCorrectly()))
                        .replaceAll("\\{playerGuessedWrong}", String.valueOf(WTSNPlayer.getGuessedWrong()))
                        .replaceAll("\\{plays}", String.valueOf(WTSNPlayer.getPlays())));
                return true;
            }
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("stats")) {
                // Does he have the permissions?
                if (!commandSender.hasPermission("wtsn.stats.other")) {
                    sendSyntaxMessage(commandSender, commandName);
                    return true;
                }

                WTSNPlayer wtsnTarget = WTSNMain.getInstance().getPlayerManager().getPlayer(args[1]);

                // Is there the player from whom the stats are requested?
                if (wtsnTarget == null) {
                    commandSender.sendMessage(languagesManager.getMessage(localeCode, Messages.STATS_OTHER_PLAYER_NOT_FOUND)
                            .replaceAll("\\{playerName}", args[1]));
                    return true;
                }

                // The Stats of the player
                commandSender.sendMessage(languagesManager.getMessage(localeCode, Messages.STATS_OTHER_SUCCESS)
                        .replaceAll("\\{playerName}", String.valueOf(wtsnTarget.getName()))
                        .replaceAll("\\{playerPoints}", String.valueOf(wtsnTarget.getPoints()))
                        .replaceAll("\\{playerGuessedCorrectly}", String.valueOf(wtsnTarget.getGuessedCorrectly()))
                        .replaceAll("\\{playerGuessedWrong}", String.valueOf(wtsnTarget.getGuessedWrong()))
                        .replaceAll("\\{plays}", String.valueOf(wtsnTarget.getPlays())));
                return true;
            }
        }

        sendSyntaxMessage(commandSender, commandName);
        return true;
    }

    /*
     * help - / - Shows all commands
     * join - wtsn.play - Join the game
     * leave - wtsn.play - Leave the game
     * reload - wtsn.reload - Reloads the songs and the configuration
     * stats - wtsn.stats - Shows the statistics
     * stats [Spielername] - wtsn.stats.other - Shows the statistics of a player
     */

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String commandName, @NotNull String[] args) {
        List<String> tab = new ArrayList<>();

        if (args.length == 1) {
            tab.add("help");
            if (commandSender.hasPermission("wtsn.play")) {
                tab.add("join");
                tab.add("leave");
            }
            if (commandSender.hasPermission("wtsn.reload")) tab.add("reload");
            if (commandSender.hasPermission("wtsn.stats")) tab.add("stats");
        }

        if (args.length == 2)

            if (commandSender.hasPermission("wtsn.stats.other"))
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) tab.add(onlinePlayer.getName());

        return tab;
    }

    private void sendSyntaxMessage(@NotNull CommandSender commandSender, @NotNull String commandName) {
        String localeCode = null;
        boolean player = false;
        if (commandSender instanceof Player tempPlayer) {
            localeCode = tempPlayer.getLocale();
            player = true;
        }

        boolean minOnePermission = false;

        commandSender.sendMessage(languagesManager.getMessage(localeCode, Messages.SYNTAX_INFO));
        if (player && commandSender.hasPermission("wtsn.play")) {
            minOnePermission = true;
            commandSender.sendMessage(languagesManager.getMessage(localeCode, Messages.SYNTAX_JOIN).replaceAll("\\{commandName}", commandName));
            commandSender.sendMessage(languagesManager.getMessage(localeCode, Messages.SYNTAX_LEAVE).replaceAll("\\{commandName}", commandName));
        }

        if (commandSender.hasPermission("wtsn.reload")) {
            minOnePermission = true;
            commandSender.sendMessage(languagesManager.getMessage(localeCode, Messages.SYNTAX_RELOAD).replaceAll("\\{commandName}", commandName));
        }

        if (commandSender.hasPermission("wtsn.stats.other")) {
            minOnePermission = true;
            commandSender.sendMessage(languagesManager.getMessage(localeCode, Messages.SYNTAX_STATS_OTHER).replaceAll("\\{commandName}", commandName));
        } else if (player && commandSender.hasPermission("wtsn.stats")) {
            minOnePermission = true;
            commandSender.sendMessage(languagesManager.getMessage(localeCode, Messages.SYNTAX_STATS_OWN).replaceAll("\\{commandName}", commandName));
        }

        if (!minOnePermission)
            commandSender.sendMessage(languagesManager.getMessage(localeCode, Messages.SYNTAX_NO_PERMISSION).replaceAll("\\{commandName}", commandName));
    }
}
