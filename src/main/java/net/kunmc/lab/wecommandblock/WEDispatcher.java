package net.kunmc.lab.wecommandblock;

import com.google.common.base.Joiner;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.event.platform.CommandEvent;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extension.platform.permission.ActorSelectorLimits;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.regions.selector.*;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.World;
import net.kunmc.lab.wecommandblock.Preset.Preset;
import net.kunmc.lab.wecommandblock.Preset.PresetData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WEDispatcher {
    WECommandBlock plugin;
    WorldEditPlugin we;

    WEDispatcher(WECommandBlock plugin) {
        this.plugin = plugin;
        this.we = plugin.we;
    }

    public void run(Actor actor, World w, String filename, String[] wecommand) {
        PresetData data = new Preset(plugin).load(actor, filename);
        String type = data.type;
        Location origin = new Location(w, data.origin[0], data.origin[1], data.origin[2]);
        BlockVector3 pos1 = BlockVector3.at(data.pos1[0], data.pos1[1], data.pos1[2]);
        List<BlockVector3> pos2 = new ArrayList<>();
        data.pos2.forEach(x -> {
            pos2.add(BlockVector3.at(x[0], x[1], x[2]));
        });

        LocalSession session = we.getWorldEdit().getSessionManager().get(actor);
        RegionSelector newSelector = selectRegionSelectorType(session.getRegionSelector(w), type);
        session.setRegionSelector(w, newSelector);
        session.getRegionSelector(w).selectPrimary(pos1, ActorSelectorLimits.forActor(actor));
        pos2.forEach(x -> {
            session.getRegionSelector(w).selectSecondary(x, ActorSelectorLimits.forActor(actor));
        });
        session.setWorldOverride(w);
        session.setPlaceAtPos1(false);
        session.dispatchCUISelection(actor);

        String arguments = rebuildArguments(wecommand[0], Arrays.copyOfRange(wecommand, 1, wecommand.length));
        FakeActor fakeActor = new FakeActor(w, actor.getSessionKey(), origin);
        we.getWorldEdit().getEventBus().post(new CommandEvent(fakeActor, arguments));

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

    private RegionSelector selectRegionSelectorType(RegionSelector oldSelector, String type) {
        RegionSelector newSelector;
        switch (type) {
            case "cuboid":
                newSelector = new CuboidRegionSelector(oldSelector);
                break;
            case "2Dx1D polygon":
                newSelector = new Polygonal2DRegionSelector(oldSelector);
                break;
            case "ellipsoid":
                newSelector = new EllipsoidRegionSelector(oldSelector);
                break;
            case "sphere":
                newSelector = new SphereRegionSelector(oldSelector);
                break;
            case "Cylinder":
                newSelector = new CylinderRegionSelector(oldSelector);
                break;
            case "Convex Polyhedron":
                newSelector = new ConvexPolyhedralRegionSelector(oldSelector);
                break;
            default:
                newSelector = new CuboidRegionSelector(oldSelector);
        }
        return newSelector;
    }

}
