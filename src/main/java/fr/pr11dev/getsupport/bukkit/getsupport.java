package fr.pr11dev.getsupport.bukkit;

import fr.pr11dev.getsupport.bukkit.data.Data;
import fr.pr11dev.getsupport.bukkit.manager.Commands;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class getsupport extends JavaPlugin {
    public void onEnable() {
        Bukkit.getServer().getConsoleSender().sendMessage("§c[§6GetSupport§c] §7Le plugin s'allume");

        Data.tickets = new ArrayList<>();
        Commands.register();
    }

    public void onDisable() {
        Bukkit.getServer().getConsoleSender().sendMessage("§c[§6GetSupport§c] §7Le plugin s'arrete");
    }
}
