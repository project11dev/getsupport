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
            MySQL.connect(config.getString("storage.mysql.db"), config.getString("storage.mysql.ip"), config.getString("storage.mysql.user"), config.getString("storage.mysql.pass"), config.getString("storage.mysql.prefix"));

            try {
                MySQL.execute("CREATE DATABASE IF NOT EXISTS "+config.getString("storage.mysql.db")+";",true);
                MySQL.execute("CREATE TABLE IF NOT EXISTS "+config.getString("storage.mysql.db")+"."+config.getString("storage.mysql.prefix")+"tickets (uuid VARCHAR(255), message VARCHAR(255), claimed BOOLEAN, operator VARCHAR(255));",true);

            }
            catch (Exception e) {
                getLogger().log(Level.SEVERE,"§c[§6GetSupport§c] §7Erreur lors de la création de la base de données");
            }

            try {
                for(String s : MySQL.getValues("tickets")) {
                    BungeeTicket bt = new BungeeTicket(ProxyServer.getInstance().getPlayer(s), MySQL.getString("tickets", "uuid", s, "message"));
                    if(MySQL.getString("tickets", "uuid", s, "claimed").equals("true")) {
                        bt.claim(ProxyServer.getInstance().getPlayer(MySQL.getString("tickets", "uuid", s, "operator")));
                    }
                }
                MySQL.execute("DELETE FROM tickets;", true);
            }
            catch (Exception e) {
                getLogger().log(Level.SEVERE, "§a[§bGetsupport§a] Erreur lors de la récupération des tickets depuis la base de données MySQL");
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
                    MySQL.execute("INSERT INTO tickets (uuid, message, claimed, operator) VALUES ('"+bt.getPlayer().getUniqueId().toString()+"', '"+bt.getMessage()+"', '"+bt.isClaimed()+"', '"+bt.getOperator().getUniqueId().toString()+"')", true );
                }
            }
            catch (Exception e) {
                getLogger().log(Level.SEVERE, "§a[§bGetsupport§a] Impossible de sauvegarder les tickets dans la base de données MySQL");
            }
        }
    }

    public static getsupportBungee getInstance() {
        return instance;
    }
}
