package fr.pr11dev.getsupport.bukkit.commands;

import fr.pr11dev.getsupport.bukkit.data.Data;
import fr.pr11dev.getsupport.bukkit.data.Ticket;
import fr.pr11dev.getsupport.bukkit.utils.AlertStaff;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TicketCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        if(args != null && args[0].equalsIgnoreCase("list")) {
            if(sender.hasPermission("gs.list")) {
                if(Data.tickets != null && Data.tickets.size() > 0) {
                    sender.sendMessage("§c[§6GetSupport§c] §7Liste des tickets");
                    for(Ticket t : Data.tickets) {
                        sender.sendMessage("§c[§6GetSupport§c] §7Ticket §e" + t.getTicketId() + "§7 Demandeur: §e" + t.getPlayer().getDisplayName()+"§7 Claim: §e"+t.isClaimed()+"§7 Ouverture: §e"+t.getFormattedCaseTime());
                    }
                }
            }
            else {
                sender.sendMessage("§c[§6GetSupport§c] §7Vous n'avez pas la permission §egs.list§7.");
            }
        }
        else if(args != null && args[0].equalsIgnoreCase("close")) {
            if(args.length == 2) {
                if(sender.hasPermission("gs.close.other")) {
                    for(Ticket t : Data.tickets) {
                        if(t.getPlayer().getName().equals(args[1])) {
                            t.close();
                            sender.sendMessage("§c[§6GetSupport§c] §7Ticket §e" + t.getTicketId() + "§7 fermé");
                            return false;
                        }
                    }
                    sender.sendMessage("§c[§6GetSupport§c] §7Aucun ticket trouvé pour §e" + args[1]);
                }
                else {
                    sender.sendMessage("§c[§6GetSupport§c] §7Vous n'avez pas la permission de fermer un ticket");
                }
            }
            else {
                if(sender.hasPermission("gs.close")) {
                    for(Ticket t : Data.tickets) {
                        if(t.getPlayer().getUniqueId().equals(sender)) {
                            t.close();
                            sender.sendMessage("§c[§6GetSupport§c] §7Ticket §e" + t.getTicketId() + "§7 fermé");
                            return false;
                        }
                    }
                    sender.sendMessage("§c[§6GetSupport§c] §7Vous n'avez pas de ticket ouvert");
                }
                else {
                    sender.sendMessage("§c[§6GetSupport§c] §7Vous n'avez pas la permission de fermer un ticket");
                }
            }


        }
        else if(args != null && args.length == 2 && args[0].equalsIgnoreCase("claim")) {
            if(sender.hasPermission("gs.claim")) {
                for(Ticket t : Data.tickets) {
                    if(t.getPlayer().getName().equalsIgnoreCase(args[1])) {
                        t.claim((Player) sender);
                        sender.sendMessage("§c[§6GetSupport§c] §7Ticket §e" + t.getTicketId() + "§7 Claimé");
                        return false;
                    }
                }
                sender.sendMessage("§c[§6GetSupport§c] §7Aucun ticket trouvé pour §e" + args[1]);
            }
            else {
                sender.sendMessage("§c[§6GetSupport§c] §7Vous n'avez pas la permission de claim un ticket");
            }
        }
        else if(args != null && args[0].equalsIgnoreCase("create")) {
            if(sender.hasPermission("gs.create")) {
                Ticket newTicket = new Ticket((Player) sender, args[1]);
                Data.tickets.add(newTicket);
                sender.sendMessage("§c[§6GetSupport§c] §7Ticket "+newTicket.getTicketId()+"§7 créé");
                AlertStaff.alert(newTicket);
            }
            else {
                sender.sendMessage("§c[§6GetSupport§c] §7Vous n'avez pas la permission de créer un ticket");
            }
        }
        else if(args != null) {
            if(sender.hasPermission("gs.create")) {
                Ticket newTicket = new Ticket((Player) sender, args[0]);
                Data.tickets.add(newTicket);
                sender.sendMessage("§c[§6GetSupport§c] §7Ticket "+newTicket.getTicketId()+"§7 créé");
                AlertStaff.alert(newTicket);
            }
            else {
                sender.sendMessage("§c[§6GetSupport§c] §7Vous n'avez pas la permission de créer un ticket");
            }
        }
        else {
            sender.sendMessage("§c[§6GetSupport§c] §7Aucune commande n'a été trouvée");
        }


        return false;
    }
}
