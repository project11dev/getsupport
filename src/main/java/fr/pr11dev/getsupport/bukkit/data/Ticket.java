package fr.pr11dev.getsupport.bukkit.data;

import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

public class Ticket {
    public Ticket(Player player, String message) {
        this.player = player;
        this.message = message;
        this.claimed = false;
        this.caseOpeningTime = LocalDateTime.now();
        this.ticket_id = "ticket_"+player.getUniqueId().toString()+"_"+getFormattedCaseTime();
    }

    public void claim(Player player) {
        this.operator = player;
        this.claimed = true;
        this.claimedTime = LocalDateTime.now();
    }

    public Player getPlayer() {
        return player;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getCaseTime () {
        return caseOpeningTime;
    }

    public String getFormattedCaseTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(caseOpeningTime);
    }

    public LocalDateTime getClaimedTime() {
        return claimedTime;
    }

    public String getFormattedClaimedTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(claimedTime);
    }

    public Player getOperator() {
        return operator;
    }

    public boolean isClaimed() {
        return claimed;
    }

    public String getTicketId() {
        return ticket_id;
    }


    public void close() {
        this.claimedTime = null;
        this.message = null;
        this.caseOpeningTime = null;
        this.operator = null;
        this.player = null;
        this.claimed = false;
    }

        private Player player;
        private String message;
        private LocalDateTime caseOpeningTime;
        private LocalDateTime claimedTime;
        private Player operator;
        private boolean claimed;
        private String ticket_id;
}
