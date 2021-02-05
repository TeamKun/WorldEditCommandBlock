package net.kunmc.lab.wecommandblock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
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
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.formatting.component.ErrorFormat;
import com.sk89q.worldedit.world.World;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
        ((Locatable) actor).getLocation();
        session.getRegionSelector(w).getRegion();
        session.getRegionSelector(w).getTypeName();
        session.getRegionSelector(w).getPrimaryPosition();

        try {
            Region r = session.getRegionSelector(w).getRegion();
            BlockArrayClipboard clipboard = new BlockArrayClipboard(r);
            EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(w).build();
            ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(
                    editSession, r, clipboard, r.getMinimumPoint()
            );
            Operations.complete(forwardExtentCopy);

            Path dir = we.getWorldEdit().getWorkingDirectoryPath(plugin.saveDir);
            File f = we.getWorldEdit().getSafeSaveFile(actor, dir.toFile(), filename, "schem");
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json = ow.writeValue(f)
            ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(f);
            writer.write(clipboard);
            // schem.save(actor, session, filename, "sponge", true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*Region r;
        try {
            r = session.getRegionSelector(w).getRegion();
        } catch (IncompleteRegionException e) {
            plugin.getServer().broadcastMessage(e.toString());
            return;
        }
        plugin.getServer().broadcastMessage(r.toString());
        r.forEach(x -> {
            plugin.getServer().broadcastMessage(x.toString());
        });

        try {
            Path dir = we.getWorldEdit().getWorkingDirectoryPath(plugin.saveDir);
            FileOutputStream f = new FileOutputStream(we.getWorldEdit().getSafeSaveFile(actor, dir.toFile(), filename,"region"));
            ObjectOutputStream o = new ObjectOutputStream(f);
            o.writeObject(r);
            o.close();
        } catch (IOException | FilenameException e) {
            plugin.getServer().broadcastMessage(e.toString());
        }*/
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
}
