package fr.pr11dev.getsupport.bungeecord;

import fr.pr11dev.getsupport.bungeecord.data.BungeeTicket;
import fr.pr11dev.getsupport.bungeecord.data.Data;
import fr.pr11dev.getsupport.bungeecord.manager.Cmd;
import fr.pr11dev.getsupport.bungeecord.utils.BungeeUpdate;
import fr.pr11dev.getsupport.shared.storage.mysql.MySQL;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.bstats.bungeecord.Metrics;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.logging.Level;

public class getsupportBungee extends Plugin {


    private Configuration config;
    static getsupportBungee instance;

    @Override
    public void onEnable() {
        getProxy().getConsole().sendMessage("§a[§bGetsupport§a] §bPlugin §aBungeeCord §bactivé !");
        instance = this;
        Cmd.register();

        new BungeeUpdate(this, 91749).getLastestVersion(version -> {
            if(this.getDescription().getVersion().equals(version)) {
                ProxyServer.getInstance().getLogger().log(Level.INFO,"§c[§6GetSupport§c] §7Vous utilisez la dernière version du plugin");
            } else {
                ProxyServer.getInstance().getLogger().log(Level.INFO,"§c[§6GetSupport§c] §7Vous utilisez la version §6"+this.getDescription().getVersion()+"§7, la dernière version est §6" +version);
            }
        });

        if (!getDataFolder().exists())
            getDataFolder().mkdir();
        File file = new File(getDataFolder(), "config.yml");
        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(config.getBoolean("storage.mysql.enable")) {
            try {
                MySQL.connect(config.getString("storage.mysql.db"), config.getString("storage.mysql.ip"), config.getString("storage.mysql.user"), config.getString("storage.mysql.pass"), config.getString("storage.mysql.prefix"));
                getLogger().log(Level.INFO, "§a[§bGetsupport§a] §bConnexion à la base de donnée réussie !");
            }
            catch (Exception e) {
                getLogger().log(Level.SEVERE, "§c[§bGetsupport§c] §cImpossible de se connecter à la base de donnée !");
                e.printStackTrace();
            }

            try {
                MySQL.execute("CREATE DATABASE IF NOT EXISTS "+config.getString("storage.mysql.db")+";",false);
                MySQL.execute("CREATE TABLE IF NOT EXISTS "+config.getString("storage.mysql.db")+"."+config.getString("storage.mysql.prefix")+"tickets (uuid VARCHAR(255), message VARCHAR(255), claimed VARCHAR(36), operator VARCHAR(255));",false);
                getLogger().log(Level.INFO, "§a[§bGetsupport§a] §bVérification de la base de donnée réussie !");
            }
            catch (Exception e) {
                getLogger().log(Level.SEVERE,"§c[§6GetSupport§c] §7Erreur lors de la création de la base de données");
                e.printStackTrace();
            }

            try {
                for(String s : MySQL.getValues("tickets")) {
                    //NullPointerException
                    //TODO: Fix this
                    BungeeTicket bt = new BungeeTicket(ProxyServer.getInstance().getPlayer(s), MySQL.getString("tickets", "uuid", s, "message"));
                    if(MySQL.getString("tickets", "uuid", s, "claimed").equals("true")) {
                        bt.claim(ProxyServer.getInstance().getPlayer(MySQL.getString("tickets", "uuid", s, "operator")));
                    }
                }
                MySQL.execute("DELETE FROM "+config.getString("storage.mysql.prefix")+"tickets;", false);
                getLogger().log(Level.INFO, "§a[§bGetsupport§a] §bRécupération des tickets de la base de données réussie!");
            }
            catch (Exception e) {
                getLogger().log(Level.SEVERE, "§a[§bGetsupport§a] Erreur lors de la récupération des tickets depuis la base de données MySQL");
                e.printStackTrace();
            }
        }


        new Metrics(this, 15023);
    }

    @Override
    public void onDisable() {
        getProxy().getConsole().sendMessage("§a[§bGetsupport§a] §bPlugin §aBungeeCord §bdesactivé !");


        if(config.getBoolean("storage.mysql.enable")) {
            try {
                for(BungeeTicket bt : Data.tickets) {
                    if(bt.isClaimed()) {
                        MySQL.execute("INSERT INTO "+config.getString("storage.mysql.prefix")+"tickets (uuid, message, claimed, operator) VALUES ('"+bt.getPlayer().getUniqueId().toString()+"', '"+bt.getMessage()+"', '"+bt.isClaimed()+"', '"+bt.getOperator().getUniqueId().toString()+"');", false );
                    }
                    else {
                        MySQL.execute("INSERT INTO "+config.getString("storage.mysql.prefix")+"tickets (uuid, message, claimed) VALUES ('"+bt.getPlayer().getUniqueId().toString()+"', '"+bt.getMessage()+"', '"+bt.isClaimed()+"');", false );
                    }
                }
                getLogger().log(Level.INFO, "§a[§bGetsupport§a] §bEnregistrement des tickets dans la base de données réussie!");
            }
            catch (Exception e) {
                getLogger().log(Level.SEVERE, "§a[§bGetsupport§a] Impossible de sauvegarder les tickets dans la base de données MySQL");
                e.printStackTrace();
            }
        }
    }

    public static getsupportBungee getInstance() {
        return instance;
    }
}
