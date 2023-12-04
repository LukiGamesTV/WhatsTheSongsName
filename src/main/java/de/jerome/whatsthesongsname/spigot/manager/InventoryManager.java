package de.jerome.whatsthesongsname.spigot.manager;

import com.xxmicloxx.NoteBlockAPI.model.Song;
import de.jerome.whatsthesongsname.spigot.WTSNMain;
import de.jerome.whatsthesongsname.spigot.object.Messages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryManager {

    private static final LanguagesManager languagesManager = WTSNMain.getInstance().getLanguagesManager();

    // Must not be final, otherwise the title cannot be changed during a reload.
    private final HashMap<String, Inventory> choseInventories;

    public InventoryManager() {
        choseInventories = new HashMap<>();
        reload();
    }

    public void reload() {
        choseInventories.clear();
        for (String localeCode : WTSNMain.getInstance().getLanguagesManager().getLocaleCodes()) {
            Inventory inventory = Bukkit.createInventory(null, 9, languagesManager.getMessage(localeCode, Messages.INVENTORY_TITLE));
            addItems(inventory);
            choseInventories.put(localeCode, inventory);
        }
    }

    private void addItems(Inventory inventory) {
        inventory.setItem(0, getItem(Material.GRAY_STAINED_GLASS_PANE));
        inventory.setItem(1, getItem(Material.MUSIC_DISC_CHIRP));
        inventory.setItem(2, getItem(Material.GRAY_STAINED_GLASS_PANE));
        inventory.setItem(3, getItem(Material.MUSIC_DISC_CHIRP));
        inventory.setItem(4, getItem(Material.GRAY_STAINED_GLASS_PANE));
        inventory.setItem(5, getItem(Material.MUSIC_DISC_CHIRP));
        inventory.setItem(6, getItem(Material.GRAY_STAINED_GLASS_PANE));
        inventory.setItem(7, getItem(Material.MUSIC_DISC_CHIRP));
        inventory.setItem(8, getItem(Material.GRAY_STAINED_GLASS_PANE));
    }

    public void updateChoseItems(Song chose1, Song chose2, Song chose3, Song chose4) {
        updateChoseItem(1, chose1);
        updateChoseItem(3, chose2);
        updateChoseItem(5, chose3);
        updateChoseItem(7, chose4);
    }

    private void updateChoseItem(int slot, Song song) {
        for (Map.Entry<String, Inventory> entry : choseInventories.entrySet()) {
            ItemStack itemStack = entry.getValue().getItem(slot);
            if (itemStack == null) continue;
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                itemMeta.setDisplayName(ChatColor.getByChar(languagesManager.getMessage(entry.getKey(), Messages.INVENTORY_SONG_ITEMS_DISPLAYNAME_COLOR)) + song.getTitle());

                List<String> lore = new ArrayList<>();
                for (String loreEntry : WTSNMain.getInstance().getLanguagesManager().getStringLists(entry.getKey(), Messages.INVENTORY_SONG_ITEMS_LORE))
                    lore.add(loreEntry.replaceAll("\\{songTitle}", song.getTitle())
                            .replaceAll("\\{songAuthor}", song.getAuthor()));
                itemMeta.setLore(lore);

                itemStack.setItemMeta(itemMeta);
            }
        }
    }

    private @NotNull ItemStack getItem(Material material) {
        ItemStack itemStack = new ItemStack(material);
        itemStack.setAmount(1);

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(" ");
            itemMeta.setLore(new ArrayList<>());
            itemStack.setItemMeta(itemMeta);
        }

        return itemStack;
    }

    /**
     * Get a copy of all ChoseInventories
     *
     * @return a copy of choseInventories
     */
    public @NotNull HashMap<String, Inventory> getChoseInventories() {
        return new HashMap<>(choseInventories);
    }

    public @NotNull Inventory getChoseInventory(String localeCode) {
        return choseInventories.getOrDefault(localeCode, choseInventories.get(WTSNMain.getInstance().getConfigManager().getLanguagesDefault()));
    }
}
