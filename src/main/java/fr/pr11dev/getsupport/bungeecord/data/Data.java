package fr.pr11dev.getsupport.bungeecord.data;


import fr.pr11dev.getsupport.shared.storage.local.JSON;

import java.util.ArrayList;

public class Data {
    public static ArrayList<BungeeTicket> tickets = new ArrayList<>();
    public static ArrayList<BungeeOfflineTicket> offlineTickets = new ArrayList<>();
    public static ArrayList<CustomProxiedPlayer> players = new ArrayList<>();
    public static JSON json;
}
