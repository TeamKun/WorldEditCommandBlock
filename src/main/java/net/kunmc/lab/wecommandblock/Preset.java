package net.kunmc.lab.wecommandblock;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.command.SchematicCommands;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extension.platform.Locatable;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.*;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.selector.RegionSelectorType;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.formatting.component.ErrorFormat;
import com.sk89q.worldedit.util.io.file.FilenameException;
import com.sk89q.worldedit.world.World;

import java.io.*;
import java.nio.file.Path;

public class Preset {
    WECommandBlock plugin;
    WorldEditPlugin we;
    SchematicCommands schem;

    Preset(WECommandBlock plugin) {
        this.plugin = plugin;
        this.we = plugin.we;
        this.schem = new SchematicCommands(this.we.getWorldEdit());
    }

    public void save(Actor actor, World w, String filename) {
        if (!actor.isPlayer()) {
            actor.print(ErrorFormat.wrap("should call this command from player"));
        }
        LocalSession session = we.getWorldEdit().getSessionManager().get(actor);
        BlockArrayClipboard clipboard;
        try {
            Region region = session.getRegionSelector(w).getRegion();
            clipboard = new BlockArrayClipboard(region);
            clipboard.setOrigin(((Locatable) actor).getLocation().toVector().toBlockPoint());
            EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(w).actor(actor).build();
            ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(
                    editSession, region, clipboard, region.getMinimumPoint()
            );
            Operations.complete(forwardExtentCopy);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        Path dir = we.getWorldEdit().getWorkingDirectoryPath(plugin.saveDir);
        FileOutputStream f;
        String regionSelectorType = session.getRegionSelector(w).getTypeName();
        try {
            f = new FileOutputStream(we.getWorldEdit().getSafeSaveFile(actor, dir.toFile(), filename + "-" + regionSelectorType, "schem"));
        } catch (FilenameException | FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        try (ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(f)) {
            writer.write(clipboard);
            f.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Clipboard load(Actor actor, LocalSession session, String filename) {
        Clipboard clipboard;
        try {
            Path dir = we.getWorldEdit().getWorkingDirectoryPath(plugin.saveDir);
            File f = we.getWorldEdit().getSafeSaveFile(actor, dir.toFile(), filename, "schem");
            ClipboardFormat format = ClipboardFormats.findByFile(f);
            ClipboardReader reader = format.getReader(new FileInputStream(f));
            clipboard = reader.read();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return clipboard;
    }

    class Pre {
        Location location;
        Region region;
        String selectorTypeName;
        BlockVector3 primaryPos;

        Pre(Location location, Region region, String selectorTypeName, BlockVector3 primaryPos) {
            this.location = location;
            this.region = region;
            this.selectorTypeName = selectorTypeName;
            this.primaryPos = primaryPos;
        }
    }
}
