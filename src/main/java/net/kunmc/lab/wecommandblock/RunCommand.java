package net.kunmc.lab.wecommandblock;

import com.google.common.base.Joiner;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.event.platform.CommandEvent;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extension.platform.permission.ActorSelectorLimits;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.selector.CylinderRegionSelector;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.World;

import java.util.Arrays;

public class RunCommand {
    WECommandBlock plugin;
    WorldEditPlugin we;

    RunCommand(WECommandBlock plugin) {
        this.plugin = plugin;
        this.we = ((WorldEditPlugin) this.plugin.getServer().getPluginManager().getPlugin("WorldEdit"));
    }

    public void run(Actor actor, World w, String presetname, String[] wecommand) {
        Region r;
        LocalSession session = we.getWorldEdit().getSessionManager().get(actor);
        Clipboard clipboard = new Preset(plugin).load(actor, session, presetname);
        session.setClipboard(new ClipboardHolder(clipboard));
        /*LocalSession session = we.getWorldEdit().getSessionManager().get(actor);*/
        FakePlayer fake = new FakePlayer(w, actor.getSessionKey(), new Location(w, clipboard.getOrigin().toVector3()));

        session.getRegionSelector(w).selectPrimary(clipboard.getMinimumPoint(), ActorSelectorLimits.forActor(actor));
        session.getRegionSelector(w).selectSecondary(clipboard.getMaximumPoint(), ActorSelectorLimits.forActor(actor));
        session.setWorldOverride(w);
        session.setRegionSelector(w, new CylinderRegionSelector(session.getRegionSelector(w)));
        session.setPlaceAtPos1(false);
        session.dispatchCUISelection(actor);

        String arguments = rebuildArguments(wecommand[0], Arrays.copyOfRange(wecommand, 1, wecommand.length));
        plugin.getServer().broadcastMessage(arguments);
        we.getWorldEdit().getEventBus().post(new CommandEvent(fake, arguments));

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
}
