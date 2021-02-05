package net.kunmc.lab.wecommandblock;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class WECommandBlock extends JavaPlugin {
    public final String saveDir = "wecommandblock";
    public WorldEditPlugin we;

    @Override
    public void onEnable() {
        this.we = ((WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit"));
        getDataFolder().mkdirs();
        getServer().getPluginCommand("weblock").setExecutor(new CommandHandler(this));
        getServer().getPluginCommand("weblock").setTabCompleter(new CommandHandler(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
