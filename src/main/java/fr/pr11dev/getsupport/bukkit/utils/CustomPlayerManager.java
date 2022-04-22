package fr.pr11dev.getsupport.bukkit.utils;

import fr.pr11dev.getsupport.bukkit.data.CustomPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CustomPlayerManager {
    public static CustomPlayer getCustomPlayer(String name) {
        if(Bukkit.getServer().getPlayer(name) != null && !Bukkit.getServer().getPlayer(name).isEmpty()) {
            return new CustomPlayer(Bukkit.getServer().getPlayer(name));
        }
        return null;
    }

    public static CustomPlayer getCustomPlayerFromSender(CommandSender sender){
        if(sender instanceof Player){
            return new CustomPlayer((Player) sender);
        }
        return null;
    }

    public static CustomPlayer getCustomPlayerFromPlayer(Player player) {
        return new CustomPlayer(player);
    }
}
