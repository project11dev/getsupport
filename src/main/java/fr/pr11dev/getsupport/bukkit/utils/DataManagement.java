package fr.pr11dev.getsupport.bukkit.utils;

import fr.pr11dev.getsupport.bukkit.data.Data;
import fr.pr11dev.getsupport.bukkit.data.OfflineTicket;
import fr.pr11dev.getsupport.bukkit.data.Ticket;
import fr.pr11dev.getsupport.bukkit.getsupport;
import fr.pr11dev.getsupport.shared.storage.local.JSON;
import fr.pr11dev.getsupport.shared.storage.mysql.MySQL;

import java.util.UUID;
import java.util.logging.Level;

import static org.bukkit.Bukkit.getLogger;

public class DataManagement {
    public static void load() {
        if(getsupport.getInstance().getConfig().getBoolean("storage.mysql.enable")) {
            try {
                MySQL.connect(getsupport.getInstance().getConfig().getString("storage.mysql.db"), getsupport.getInstance().getConfig().getString("storage.mysql.ip"), getsupport.getInstance().getConfig().getString("storage.mysql.user"), getsupport.getInstance().getConfig().getString("storage.mysql.pass"), getsupport.getInstance().getConfig().getString("storage.mysql.prefix"));
                getLogger().log(Level.INFO, "�c[�6GetSupport�c] �7Connexion � la base de donn�es r�ussie");
            }
            catch (Exception e) {
                getLogger().log(Level.SEVERE, "�c[�6GetSupport�c] �7Erreur lors de la connexion � la base de donn�es");
                e.printStackTrace();
            }


            try {
                MySQL.execute("CREATE DATABASE IF NOT EXISTS "+getsupport.getInstance().getConfig().getString("storage.mysql.db")+";",false);
                MySQL.execute("CREATE TABLE IF NOT EXISTS "+getsupport.getInstance().getConfig().getString("storage.mysql.db")+"."+getsupport.getInstance().getConfig().getString("storage.mysql.prefix")+"tickets (uuid VARCHAR(255), message VARCHAR(255), claimed VARCHAR(36), operator VARCHAR(255));",false);
                getLogger().log(Level.INFO, "�c[�6GetSupport�c] �7V�rification de la base de donn�es r�ussie");
            }
            catch (Exception e) {
                getLogger().log(Level.SEVERE,"�c[�6GetSupport�c] �7Erreur lors de la cr�ation de la base de donn�es");
                e.printStackTrace();
            }

            for(String s : MySQL.getValues("tickets")) {
                try {
                    OfflineTicket t = new OfflineTicket(UUID.fromString(s), MySQL.getString("tickets", "uuid", s, "message"));
                    if(MySQL.getString("tickets", "uuid", s, "claimed").equals("true")) {
                        t.claim(UUID.fromString(MySQL.getString("tickets", "uuid", s, "operator")));
                    }
                }
                catch (Exception e) {
                    getLogger().log(Level.SEVERE, "�c[�6GetSupport�c] �7Erreur lors de la r�cup�ration  d'un ticket de la base de donn�es");
                    e.printStackTrace();
                }
            }
            MySQL.execute("DELETE FROM "+getsupport.getInstance().getConfig().getString("storage.mysql.prefix")+"tickets;", false);
            getLogger().log(Level.INFO, "�c[�6GetSupport�c] �7R�cup�ration des tickets de la base de donn�e r�ussie");

        }
        else {
            Data.json = new JSON("tickets.json",getsupport.getInstance().getDataFolder()+"/storage");
            JSON json = Data.json;
            for(String s : json.getStringList("tickets")) {
                try {
                    OfflineTicket t = new OfflineTicket(UUID.fromString(json.getString(s+".player")), json.getString(s+".message"));
                    if(json.getString(s+".claimed").equals("true")) {
                        t.claim(UUID.fromString(json.getString(s+".operator")));
                    }
                }
                catch (Exception e) {
                    getLogger().log(Level.SEVERE, "�c[�6GetSupport�c] �7Erreur lors de la r�cup�ration  d'un ticket de la base de donn�es");
                    e.printStackTrace();
                }
            }
            json.clear();
            getLogger().log(Level.INFO, "�c[�6GetSupport�c] �7R�cup�ration des tickets de la base de donn�e r�ussie");
        }
    }

    public static void save() {
        if(getsupport.getInstance().getConfig().getBoolean("storage.mysql.enable")) {
            try {
                for(Ticket t: Data.tickets) {
                    if(t.isClaimed()) {
                        MySQL.execute("INSERT INTO "+getsupport.getInstance().getConfig().getString("storage.mysql.prefix")+"tickets (uuid, message, claimed, operator) VALUES ('"+t.getPlayer().getUniqueId().toString()+"', '"+t.getMessage()+"', '"+t.isClaimed()+"', '"+t.getOperator().getUniqueId().toString()+"');", false );
                    }
                    else {
                        MySQL.execute("INSERT INTO "+getsupport.getInstance().getConfig().getString("storage.mysql.prefix")+"tickets (uuid, message, claimed) VALUES ('"+t.getPlayer().getUniqueId().toString()+"', '"+t.getMessage()+"', '"+t.isClaimed()+"');", false );
                    }
                }
                for(OfflineTicket t : Data.offlineTickets) {
                    if(t.isClaimed()) {
                        MySQL.execute("INSERT INTO "+getsupport.getInstance().getConfig().getString("storage.mysql.prefix")+"tickets (uuid, message, claimed, operator) VALUES ('"+t.getUuid()+"', '"+t.getMessage()+"', '"+t.isClaimed()+"', '"+t.getUuid_operator()+"');", false );
                    }
                    else {
                        MySQL.execute("INSERT INTO "+getsupport.getInstance().getConfig().getString("storage.mysql.prefix")+"tickets (uuid, message, claimed) VALUES ('"+t.getUuid()+"', '"+t.getMessage()+"', '"+t.isClaimed()+"');", false );
                    }
                }
                getLogger().log(Level.INFO, "�c[�6GetSupport�c] �7L'enregistrement des tickets dans la base de donn�es a �t� r�ussi");
            }
            catch (Exception e) {
                getLogger().log(Level.SEVERE, "�c[�6GetSupport�c] �7Erreur lors de la sauvegarde des tickets");
                e.printStackTrace();
            }
        }
        else {
            JSON json = Data.json;
            try {
                for(Ticket t : Data.tickets) {
                    json.setString(t.getTicketId() + ".player", t.getPlayer().getUniqueId().toString());
                    json.setString(t.getTicketId() + ".message", t.getMessage());
                    json.setString(t.getTicketId() + ".claimed", t.isClaimed() + "");
                    if(t.isClaimed()) {
                        json.setString(t.getTicketId() + ".operator", t.getOperator().getUniqueId().toString());
                    }
                }
                for(OfflineTicket t : Data.offlineTickets) {
                    json.setString(t.getTicketId() + ".player", t.getUuid().toString());
                    json.setString(t.getTicketId() + ".message", t.getMessage());
                    json.setString(t.getTicketId() + ".claimed", t.isClaimed() + "");
                    if(t.isClaimed()) {
                        json.setString(t.getTicketId() + ".operator", t.getUuid_operator().toString());
                    }
                }
                getLogger().log(Level.INFO, "�c[�6GetSupport�c] �7L'e nregistrement des tickets dans le fichier json a �t� r�ussi");
            }
            catch (Exception e) {
                getLogger().log(Level.SEVERE, "�c[�6GetSupport�c] �7Erreur lors de la sauvegarde des tickets");
                e.printStackTrace();
            }
        }
    }

}
