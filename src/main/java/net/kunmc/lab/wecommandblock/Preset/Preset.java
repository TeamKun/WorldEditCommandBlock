package net.kunmc.lab.wecommandblock.Preset;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extension.platform.Locatable;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.formatting.component.ErrorFormat;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.io.file.FilenameException;
import com.sk89q.worldedit.world.World;
import net.kunmc.lab.wecommandblock.WECommandBlock;

import java.io.*;
import java.nio.file.Path;

public class Preset {
    WECommandBlock plugin;
    WorldEditPlugin we;
    public static String Ext = "preset";

    public Preset(WECommandBlock plugin) {
        this.plugin = plugin;
        this.we = plugin.we;
    }

    public void save(Actor actor, World w, boolean overwrite, String filename) {
        if (!actor.isPlayer()) {
            actor.print(ErrorFormat.wrap("should call this command from player"));
        }
        LocalSession session = we.getWorldEdit().getSessionManager().get(actor);
        Region region;
        Location origin = ((Locatable) actor).getLocation();
        BlockVector3 pos1;
        try {
            region = session.getRegionSelector(w).getRegion();
            pos1 = session.getRegionSelector(w).getPrimaryPosition();
        } catch (IncompleteRegionException e) {
            actor.printError("Make a region selection first");
            return;
        }
        String type = session.getRegionSelector(w).getTypeName();
        PresetData data = new PresetData(type, origin, pos1, region);

        Path dir = we.getWorldEdit().getWorkingDirectoryPath(plugin.saveDir);
        ObjectOutputStream stream;
        try {
            File f = we.getWorldEdit().getSafeSaveFile(actor, dir.toFile(), filename, Ext);
            if (f.exists() && !overwrite) {
                actor.print(ErrorFormat.wrap(filename+" is already exists"));
                return;
            };
            stream = new ObjectOutputStream(new FileOutputStream(f));

        } catch (FilenameException | IOException e) {
            e.printStackTrace();
            return;
        }

        try {
            stream.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        actor.print(filename + " saved");
    }

    public PresetData load(Actor actor, String filename) {
        PresetData data;

        Path dir = we.getWorldEdit().getWorkingDirectoryPath(plugin.saveDir);
        try {
            File f = we.getWorldEdit().getSafeSaveFile(actor, dir.toFile(), filename, Ext);
            if (!f.exists()) {
                actor.print(ErrorFormat.wrap(filename+" is not exists"));
                return null;
            }
            ObjectInputStream stream = new ObjectInputStream(new FileInputStream(f));
            data = ((PresetData) stream.readObject());
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return data;
    }
}
