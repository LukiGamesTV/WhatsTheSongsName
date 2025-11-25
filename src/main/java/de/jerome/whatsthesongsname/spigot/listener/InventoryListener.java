package de.jerome.whatsthesongsname.spigot.listener;

import de.jerome.whatsthesongsname.spigot.WTSNMain;
import de.jerome.whatsthesongsname.spigot.object.Messages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryListener implements Listener {

    public InventoryListener() {
        Bukkit.getPluginManager().registerEvents(this, WTSNMain.getInstance());
    }

    @EventHandler
    public void handleInventoryDrag(InventoryDragEvent event) {
        // Checks if this is the ChoseInventory
        if (isNotChoseInventory(event.getInventory())) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void handleInventoryClick(InventoryClickEvent event) {
        // Checks if it is a player and initializes a variable with it
        if (!(event.getWhoClicked() instanceof Player player)) return;

        // Checks if this is the ChoseInventory
        if (isNotChoseInventory(event.getInventory())) return;

        event.setCancelled(true);

        ItemStack currentItem = event.getCurrentItem();
        if (currentItem == null) return;

        if (currentItem.getItemMeta() == null) return;
        String displayName = ChatColor.stripColor(currentItem.getItemMeta().getDisplayName());
        // Checks whether it is a placeholder item
        if (displayName.isEmpty() || displayName.isBlank()) return;

        // Saves the player's answer and closes the inventory
        WTSNMain.getInstance().getGameManager().getPlayerAnswers().put(player, displayName);
        player.sendMessage(WTSNMain.getInstance().getLanguagesManager().getMessage("de_de", Messages.SUBMIT_ANSWER)
                .replaceAll("\\{songTitle}", displayName));

        event.getView().close();
    }

    @EventHandler
    public void handleInventoryClose(InventoryCloseEvent event) {
        // Checks if it is a player and initializes a variable with it
        if (!(event.getPlayer() instanceof Player player)) return;

        // Checks if this is the ChoseInventory
        if (isNotChoseInventory(event.getInventory())) return;

        // Reopens the inventory if no answer has been given yet
        if (!WTSNMain.getInstance().getGameManager().getPlayerAnswers().containsKey(player)
                && !WTSNMain.getInstance().getGameManager().isAllowInventoryClose())
            Bukkit.getScheduler().runTask(WTSNMain.getInstance(), () -> player.openInventory(event.getInventory()));
    }

    private boolean isNotChoseInventory(Inventory event) {
        boolean choseInventory = false;
        int hashCode = event.hashCode();
        for (Inventory inventory : WTSNMain.getInstance().getInventoryManager().getChoseInventories().values())
            if (hashCode == inventory.hashCode()) {
                choseInventory = true;
                break;
            }
        return !choseInventory;
    }

}
