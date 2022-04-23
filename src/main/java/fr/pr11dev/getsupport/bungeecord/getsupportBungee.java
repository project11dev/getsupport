package fr.pr11dev.getsupport.bungeecord;

import fr.pr11dev.getsupport.bungeecord.manager.Cmd;
import net.md_5.bungee.api.plugin.Plugin;

public class getsupportBungee extends Plugin {

    static getsupportBungee instance;

    @Override
    public void onEnable() {
        getProxy().getConsole().sendMessage("§a[§bGetsupport§a] §bPlugin §aBungeeCord §bactivé !");
        instance = this;
        Cmd.register();
    }

    @Override
    public void onDisable() {
        getProxy().getConsole().sendMessage("§a[§bGetsupport§a] §bPlugin §aBungeeCord §bdesactivé !");
    }

    public static getsupportBungee getInstance() {
        return instance;
    }
}
