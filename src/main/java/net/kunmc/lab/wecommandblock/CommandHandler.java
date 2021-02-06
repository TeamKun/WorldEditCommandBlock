package net.kunmc.lab.wecommandblock;

import com.google.common.base.Joiner;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.world.World;
import net.kunmc.lab.wecommandblock.Preset.Preset;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandHandler implements CommandExecutor, TabCompleter {
    WECommandBlock plugin;
    WorldEditPlugin we;

    CommandHandler() {
    }

    CommandHandler(WECommandBlock plugin) {
        this.plugin = plugin;
        this.we = plugin.we;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) return false;
        if (!(sender instanceof BlockCommandSender || sender instanceof Player)) return false;
        Actor actor = BukkitAdapter.adapt(sender);
        World w = getWorldFromCommandSender(sender);

        String subcmd = args[0].toLowerCase();
        switch (subcmd) {
            case "run":
                if (args.length < 3) return false;
                new RunCommand(this.plugin).run(actor, w, args[1], Arrays.copyOfRange(args, 2, args.length));
                return true;
            case "save":
                if (args.length < 2) return false;
                new Preset(this.plugin).save(actor, w, args[1]);
                return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> list = new ArrayList<String>();
        switch (args.length) {
            case 0:
                return null;
            case 1:
                list.add("run");
                list.add("save");
                return list;
            case 2:
                Arrays.asList(we.getWorldEdit().getWorkingDirectoryPath(plugin.saveDir).toFile().list()).stream().forEach(x -> {
                    list.add(x.replaceFirst(".schem", ""));
                });
                return list;
            default:
                return list;
        }
    }

    private String rebuildArguments(String commandLabel, String[] args) {
        int plSep = commandLabel.indexOf(":");
        if (plSep >= 0 && plSep < commandLabel.length() + 1) {
            commandLabel = commandLabel.substring(plSep + 1);
        }

        StringBuilder sb = new StringBuilder("/").append(commandLabel);
        if (args.length > 0) {
            sb.append(" ");
        }
        return Joiner.on(" ").appendTo(sb, args).toString();
    }

    public World getWorldFromCommandSender(CommandSender sender) {
        if (sender instanceof Player) {
            return BukkitAdapter.adapt((Player) sender).getWorld();
        } else if (sender instanceof BlockCommandSender) {
            return BukkitAdapter.adapt(((BlockCommandSender) sender).getBlock().getWorld());
        }
        return null;
    }
}
