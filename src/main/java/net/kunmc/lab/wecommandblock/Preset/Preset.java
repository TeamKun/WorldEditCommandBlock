package net.kunmc.lab.wecommandblock.Preset;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extension.platform.Locatable;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.formatting.component.ErrorFormat;
import com.sk89q.worldedit.util.formatting.component.MessageBox;
import com.sk89q.worldedit.util.io.file.FilenameException;
import com.sk89q.worldedit.world.World;
import net.kunmc.lab.wecommandblock.WECommandBlock;

import java.io.*;
import java.nio.file.Path;

public class Preset {
    public static String Ext = "preset";
    WECommandBlock plugin;
    WorldEditPlugin we;

    public Preset(WECommandBlock plugin) {
        this.plugin = plugin;
        this.we = plugin.we;
    }

    public void save(Actor actor, String filename, boolean overwrite) {
        if (!actor.isPlayer()) {
            actor.print(ErrorFormat.wrap("This command is only for Players"));
            return;
        }

        World w = ((Player) actor).getWorld();
        LocalSession session = we.getWorldEdit().getSessionManager().get(actor);
        RegionSelector selector = session.getRegionSelector(w);

        String type = selector.getTypeName();
        String worldName = w.getName();
        Location origin = ((Locatable) actor).getLocation();
        BlockVector3 pos1;
        Region region;
        try {
            region = selector.getRegion();
            pos1 = selector.getPrimaryPosition();
        } catch (IncompleteRegionException e) {
            actor.print(ErrorFormat.wrap("Make a region selection first"));
            return;
        }
        PresetData data = new PresetData(type, worldName, origin, pos1, region);

        Path dir = we.getWorldEdit().getWorkingDirectoryPath(plugin.saveDir);
        ObjectOutputStream stream;
        try {
            File f = we.getWorldEdit().getSafeSaveFile(actor, dir.toFile(), filename, Ext);
            if (f.exists() && !overwrite) {
                actor.print(ErrorFormat.wrap(filename + " is already exists"));
                return;
            }
            stream = new ObjectOutputStream(new FileOutputStream(f));
        } catch (FilenameException | IOException e) {
            actor.print(ErrorFormat.wrap(filename + " is invalid name"));
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

    public PresetData load(Actor actor, String filename) throws FilenameException, IOException, ClassNotFoundException {
        PresetData data;
        ObjectInputStream stream = null;
        try {
            Path dir = we.getWorldEdit().getWorkingDirectoryPath(plugin.saveDir);
            File f = we.getWorldEdit().getSafeSaveFile(actor, dir.toFile(), filename, Ext);
            if (!f.exists()) {
                actor.print(ErrorFormat.wrap(filename + " is not exists"));
                return null;
            }
            stream = new ObjectInputStream(new FileInputStream(f));
            data = ((PresetData) stream.readObject());
        } finally {
            if (stream != null) stream.close();
        }

        return data;
    }
}
