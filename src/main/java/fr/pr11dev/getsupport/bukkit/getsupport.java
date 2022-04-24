package fr.pr11dev.getsupport.bukkit;

import fr.pr11dev.getsupport.bukkit.data.Data;
import fr.pr11dev.getsupport.bukkit.data.Ticket;
import fr.pr11dev.getsupport.bukkit.manager.Commands;
import fr.pr11dev.getsupport.bukkit.utils.Update;
import fr.pr11dev.getsupport.bungeecord.data.BungeeTicket;
import fr.pr11dev.getsupport.bungeecord.getsupportBungee;
import fr.pr11dev.getsupport.shared.storage.mysql.MySQL;
import net.md_5.bungee.api.ProxyServer;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class getsupport extends JavaPlugin {

    private final File config = new File("./plugins/getsupport/config.yml");
    static getsupport instance;

    public void onEnable() {
        Bukkit.getServer().getConsoleSender().sendMessage("§c[§6GetSupport§c] §7Le plugin s'allume");

        Data.tickets = new ArrayList<>();
        instance = this;
        Commands.register();

        new Update(this, 91749).getLastestVersion(version -> {
            if(this.getDescription().getVersion().equals(version)) {
                Bukkit.getLogger().log(Level.INFO,"§c[§6GetSupport§c] §7Vous utilisez la dernière version du plugin");
            } else {
                Bukkit.getLogger().log(Level.INFO,"§c[§6GetSupport§c] §7Vous utilisez la version §6"+this.getDescription().getVersion()+"§7, la dernière version est §6" +version);
            }
        });

        if(!config.exists()) {
            saveDefaultConfig();
        }

        if(getConfig().getBoolean("storage.mysql.enable")) {

            try {
            MySQL.connect(getConfig().getString("storage.mysql.db"), getConfig().getString("storage.mysql.ip"), getConfig().getString("storage.mysql.user"), getConfig().getString("storage.mysql.pass"), getConfig().getString("storage.mysql.prefix"));
            }
            catch (Exception e) {
                getLogger().log(Level.SEVERE, "§c[§6GetSupport§c] §7Erreur lors de la connexion à la base de données");
            }


            try {
                MySQL.execute("CREATE DATABASE IF NOT EXISTS "+getConfig().getString("storage.mysql.db")+";",true);
                MySQL.execute("CREATE TABLE IF NOT EXISTS "+getConfig().getString("storage.mysql.db")+"."+getConfig().getString("storage.mysql.prefix")+"tickets (uuid VARCHAR(255), message VARCHAR(255), claimed BOOLEAN, operator VARCHAR(255));",true);

            }
            catch (Exception e) {
                getLogger().log(Level.SEVERE,"§c[§6GetSupport§c] §7Erreur lors de la création de la base de données");
            }

            try {
                for(String s : MySQL.getValues("tickets")) {
                    Ticket t = new Ticket(getServer().getPlayer(s), MySQL.getString("tickets", "uuid", s, "message"));
                    if(MySQL.getString("tickets", "uuid", s, "claimed").equals("true")) {
                        t.claim(getServer().getPlayer(MySQL.getString("tickets", "uuid", s, "operator")));
                    }
                }
                MySQL.execute("DELETE FROM tickets;", true);
            }
            catch (Exception e) {
                getLogger().log(Level.SEVERE, "§c[§6GetSupport§c] §7Erreur lors de la récupération des tickets de la base de données");
            }
        }

        new Metrics(this, 15022);
    }

    public void onDisable() {
        Bukkit.getServer().getConsoleSender().sendMessage("§c[§6GetSupport§c] §7Le plugin s'arrete");

        if(getConfig().getBoolean("storage.mysql.enable")) {
            try {
                for(Ticket t: Data.tickets) {
                    MySQL.execute("INSERT INTO tickets (uuid, message, claimed, operator) VALUES ('"+t.getPlayer().getUniqueId().toString()+"', '"+t.getMessage()+"', '"+t.isClaimed()+"', '"+t.getOperator().getUniqueId().toString()+"');", true );
                }
            }
            catch (Exception e) {
                getLogger().log(Level.SEVERE, "§c[§6GetSupport§c] §7Erreur lors de la sauvegarde des tickets");
            }
        }
    }
}
