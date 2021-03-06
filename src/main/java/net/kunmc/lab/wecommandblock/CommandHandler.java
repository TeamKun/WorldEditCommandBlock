package net.kunmc.lab.wecommandblock;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.util.formatting.component.ErrorFormat;
import com.sk89q.worldedit.util.io.file.FilenameException;
import net.kunmc.lab.wecommandblock.Preset.Preset;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandHandler implements CommandExecutor, TabCompleter {
    WECommandBlock plugin;
    WorldEditPlugin we;

    CommandHandler(WECommandBlock plugin) {
        this.plugin = plugin;
        this.we = plugin.we;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) return false;

        Actor actor = BukkitAdapter.adapt(sender);
        String subcmd = args[0].toLowerCase();
        switch (subcmd) {
            case "run": {
                if (args.length < 3) return false;
                String filename = args[1];
                String[] wecommand = Arrays.copyOfRange(args, 2, args.length);
                new WEDispatcher(this.plugin).run(actor, filename, wecommand);
                return true;
            }
            case "save":
                if (args.length < 2) return false;

                String filename = args[1];
                boolean overwrite = args[args.length - 1].equalsIgnoreCase("-f");
                try {
                    new Preset(this.plugin).save(actor, filename, overwrite);
                } catch (IncompleteRegionException e) {
                    actor.print(ErrorFormat.wrap("Make a region selection first"));
                } catch (FilenameException e) {
                    actor.print(ErrorFormat.wrap(filename + " is invalid filename"));
                } catch (Exception e) {
                    actor.print(ErrorFormat.wrap(e.getMessage()));
                }
                return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> list = new ArrayList<>();
        switch (args.length) {
            case 0:
                return null;
            case 1:
                list.add("run");
                list.add("save");
                return list;
            case 2:
                Arrays.asList(we.getWorldEdit().getWorkingDirectoryPath(plugin.saveDir).toFile().list()).stream().forEach(x -> {
                    list.add(x.replaceFirst("." + Preset.Ext, ""));
                });
                return list;
            default:
                return list;
        }
    }
}
