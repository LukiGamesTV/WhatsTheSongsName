package de.jerome.whatsthesongsname.spigot.listener;

import de.jerome.whatsthesongsname.spigot.WITSNMain;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class NoteBlockAPIListener implements Listener {

    public NoteBlockAPIListener() {
        Bukkit.getPluginManager().registerEvents(this, WITSNMain.getInstance());
    }

}
