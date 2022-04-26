package fr.pr11dev.getsupport.bungeecord.utils;

import fr.pr11dev.getsupport.bungeecord.data.BungeeTicket;
import fr.pr11dev.getsupport.bungeecord.data.Data;
import fr.pr11dev.getsupport.bungeecord.data.BungeeOfflineTicket;
import fr.pr11dev.getsupport.bungeecord.getsupportBungee;
import fr.pr11dev.getsupport.shared.storage.local.JSON;
import fr.pr11dev.getsupport.shared.storage.mysql.MySQL;

import java.util.UUID;
import java.util.logging.Level;

public class DataManagmentBungee {
    public static void load() {
        if(getsupportBungee.getInstance().config.getBoolean("storage.mysql.enable")) {
            try {
                MySQL.connect(getsupportBungee.getInstance().config.getString("storage.mysql.db"), getsupportBungee.getInstance().config.getString("storage.mysql.ip"), getsupportBungee.getInstance().config.getString("storage.mysql.user"), getsupportBungee.getInstance().config.getString("storage.mysql.pass"), getsupportBungee.getInstance().config.getString("storage.mysql.prefix"));
                getsupportBungee.getInstance().getLogger().log(Level.INFO, "§a[§bGetsupport§a] §bConnexion à la base de donnée réussie !");
            }
            catch (Exception e) {
                getsupportBungee.getInstance().getLogger().log(Level.SEVERE, "§c[§bGetsupport§c] §cImpossible de se connecter à la base de donnée !");
                e.printStackTrace();
            }

            try {
                MySQL.execute("CREATE DATABASE IF NOT EXISTS "+getsupportBungee.getInstance().config.getString("storage.mysql.db")+";",false);
                MySQL.execute("CREATE TABLE IF NOT EXISTS "+getsupportBungee.getInstance().config.getString("storage.mysql.db")+"."+getsupportBungee.getInstance().config.getString("storage.mysql.prefix")+"tickets (uuid VARCHAR(255), message VARCHAR(255), claimed VARCHAR(36), operator VARCHAR(255));",false);
                getsupportBungee.getInstance().getLogger().log(Level.INFO, "§a[§bGetsupport§a] §bVérification de la base de donnée réussie !");
            }
            catch (Exception e) {
                getsupportBungee.getInstance().getLogger().log(Level.SEVERE,"§c[§6GetSupport§c] §7Erreur lors de la création de la base de données");
                e.printStackTrace();
            }

            for(String s : MySQL.getValues("tickets")) {
                try {
                    BungeeOfflineTicket bt = new BungeeOfflineTicket(UUID.fromString(s), MySQL.getString("tickets", "uuid", s, "message"));
                    if(MySQL.getString("tickets", "uuid", s, "claimed").equals("true")) {
                        bt.claim(UUID.fromString(MySQL.getString("tickets", "uuid", s, "operator")));
                    }
                }
                catch (Exception e) {
                    getsupportBungee.getInstance().getLogger().log(Level.SEVERE, "§a[§bGetsupport§a] Erreur lors de la récupération d'un ticket depuis la base de données MySQL");
                    e.printStackTrace();
                }
            }
            MySQL.execute("DELETE FROM "+getsupportBungee.getInstance().config.getString("storage.mysql.prefix")+"tickets;", false);
            getsupportBungee.getInstance().getLogger().log(Level.INFO, "§a[§bGetsupport§a] §bRécupération des tickets de la base de données réussie!");
        }
        else {
            Data.json = new JSON("tickets.json", getsupportBungee.getInstance().getDataFolder()+"/storage");
            JSON json = Data.json;
            for(String s : json.getStringList("tickets")) {
                try {
                    BungeeOfflineTicket t = new BungeeOfflineTicket(UUID.fromString(json.getString(s+".player")), json.getString(s+".message"));
                    if(json.getString(s+".claimed").equals("true")) {
                        t.claim(UUID.fromString(json.getString(s+".operator")));
                    }
                }
                catch (Exception e) {
                    getsupportBungee.getInstance().getLogger().log(Level.SEVERE, "§c[§6GetSupport§c] §7Erreur lors de la récupération  d'un ticket de la base de données");
                    e.printStackTrace();
                }
            }
            json.clear();
            getsupportBungee.getInstance().getLogger().log(Level.INFO, "§c[§6GetSupport§c] §7Récupération des tickets de la base de donnée réussie");
        }
    }

    public static void save() {
        if(getsupportBungee.getInstance().config.getBoolean("storage.mysql.enable")) {
            try {
                for(BungeeTicket bt : Data.tickets) {
                    if(bt.isClaimed()) {
                        MySQL.execute("INSERT INTO "+getsupportBungee.getInstance().config.getString("storage.mysql.prefix")+"tickets (uuid, message, claimed, operator) VALUES ('"+bt.getPlayer().getUniqueId().toString()+"', '"+bt.getMessage()+"', '"+bt.isClaimed()+"', '"+bt.getOperator().getUniqueId().toString()+"');", false );
                    }
                    else {
                        MySQL.execute("INSERT INTO "+getsupportBungee.getInstance().config.getString("storage.mysql.prefix")+"tickets (uuid, message, claimed) VALUES ('"+bt.getPlayer().getUniqueId().toString()+"', '"+bt.getMessage()+"', '"+bt.isClaimed()+"');", false );
                    }
                }
                for(BungeeOfflineTicket bt : Data.offlineTickets) {
                    if(bt.isClaimed()) {
                        MySQL.execute("INSERT INTO "+getsupportBungee.getInstance().config.getString("storage.mysql.prefix")+"tickets (uuid, message, claimed, operator) VALUES ('"+bt.getUuid()+"', '"+bt.getMessage()+"', '"+bt.isClaimed()+"', '"+bt.getUuid_operator()+"');", false );
                    }
                    else {
                        MySQL.execute("INSERT INTO "+getsupportBungee.getInstance().config.getString("storage.mysql.prefix")+"tickets (uuid, message, claimed) VALUES ('"+bt.getUuid()+"', '"+bt.getMessage()+"', '"+bt.isClaimed()+"');", false );
                    }
                }
                getsupportBungee.getInstance().getLogger().log(Level.INFO, "§a[§bGetsupport§a] §bL'enregistrement des tickets dans la base de données a été réussi!");
            }
            catch (Exception e) {
                getsupportBungee.getInstance().getLogger().log(Level.SEVERE, "§a[§bGetsupport§a] Impossible de sauvegarder les tickets dans la base de données MySQL");
                e.printStackTrace();
            }
        }
        else {
            JSON json = Data.json;
            try {
                for(BungeeTicket t : Data.tickets) {
                    json.setString(t.getTicketId() + ".player", t.getPlayer().getUniqueId().toString());
                    json.setString(t.getTicketId() + ".message", t.getMessage());
                    json.setString(t.getTicketId() + ".claimed", t.isClaimed() + "");
                    if(t.isClaimed()) {
                        json.setString(t.getTicketId() + ".operator", t.getOperator().getUniqueId().toString());
                    }
                }
                for(BungeeOfflineTicket t : Data.offlineTickets) {
                    json.setString(t.getTicketId() + ".player", t.getUuid().toString());
                    json.setString(t.getTicketId() + ".message", t.getMessage());
                    json.setString(t.getTicketId() + ".claimed", t.isClaimed() + "");
                    if(t.isClaimed()) {
                        json.setString(t.getTicketId() + ".operator", t.getUuid_operator().toString());
                    }
                }
                getsupportBungee.getInstance().getLogger().log(Level.INFO, "§c[§6GetSupport§c] §7L'enregistrement des tickets dans le fichier json a été réussi");
            }
            catch (Exception e) {
                getsupportBungee.getInstance().getLogger().log(Level.SEVERE, "§c[§6GetSupport§c] §7Erreur lors de la sauvegarde des tickets");
                e.printStackTrace();
            }
        }
    }
}
