package fr.pr11dev.getsupport.bukkit.events;

import fr.pr11dev.getsupport.bukkit.data.Data;
import fr.pr11dev.getsupport.bukkit.data.OfflineTicket;
import fr.pr11dev.getsupport.bukkit.data.Ticket;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static org.bukkit.Bukkit.getServer;

public class PlayerJoin implements Listener {
    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent e) {
        for(OfflineTicket ot : Data.offlineTickets) {
            if(ot.getUuid().equals(e.getPlayer().getUniqueId())) {
                final Ticket t = new Ticket(getServer().getPlayer(ot.getUuid()), ot.getMessage());
                    if(ot.isClaimed()) {
                        t.claim(getServer().getPlayer(ot.getUuid_operator()));
                    }
                    ot.remove();
                    return;

            }
        }
    }
}
