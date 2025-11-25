package de.jerome.whatsthesongsname.spigot.command;

import de.jerome.whatsthesongsname.spigot.WTSNMain;
import de.jerome.whatsthesongsname.spigot.manager.LanguagesManager;
import de.jerome.whatsthesongsname.spigot.object.Messages;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SongratenCommand implements CommandExecutor {
    private static final LanguagesManager languagesManager = WTSNMain.getInstance().getLanguagesManager();
    public SongratenCommand() {
        PluginCommand pluginCommand = WTSNMain.getInstance().getCommand("songraten");
        if (pluginCommand == null) return;
        pluginCommand.setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String commandName, @NotNull String[] strings) {
        String localeCode = "de_de";

        // Is he a player?
        if (!(commandSender instanceof Player player)) {
            sendSyntaxMessage(commandSender, commandName);
            return true;
        }

        if(WTSNMain.getInstance().getPlayerManager().getPlayer(player).getPlays() >= WTSNMain.getInstance().getConfigManager().getRoundLimit()){
            player.sendMessage(languagesManager.getMessage(localeCode, Messages.LEAVE_PLAYS_EXCEEDED));
            return true;
        }

        boolean joined = WTSNMain.getInstance().getGameManager().joinGame(player);

        // Is he already in the game? If not, he enters it
        if (!joined) {
            WTSNMain.getInstance().getGameManager().leaveGame(player);
            commandSender.sendMessage(languagesManager.getMessage(localeCode, Messages.LEAVE_LEFT));
        } else {
            if (WTSNMain.getInstance().getGameManager().getWaitingPlayers().contains(player)) {
                commandSender.sendMessage(languagesManager.getMessage(localeCode, Messages.JOIN_WAITING));
            } else {
                commandSender.sendMessage(languagesManager.getMessage(localeCode, Messages.JOIN_JOINED));
            }
        }
        return true;
    }

    private void sendSyntaxMessage(@NotNull CommandSender commandSender, @NotNull String commandName) {
        String localeCode = null;
        if (commandSender instanceof Player tempPlayer) {
            localeCode = tempPlayer.getLocale();
        }

        boolean minOnePermission = false;

        if (!minOnePermission)
            commandSender.sendMessage(languagesManager.getMessage(localeCode, Messages.SYNTAX_NO_PERMISSION).replaceAll("\\{commandName}", commandName));
    }
}
