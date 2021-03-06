package net.kunmc.lab.wecommandblock.Preset;

import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.*;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.World;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PresetData implements Serializable {
    private final double[] origin = new double[3];
    private final int[] pos1 = new int[3];
    private final List<int[]> pos2 = new ArrayList<>();
    public String typeName;
    public String worldName;


    public PresetData(String typeName, String worldName, Location origin, BlockVector3 pos1, Region region) {
        this.typeName = typeName;
        this.worldName = worldName;

        this.origin[0] = origin.getX();
        this.origin[1] = origin.getY();
        this.origin[2] = origin.getZ();

        this.pos1[0] = pos1.getX();
        this.pos1[1] = pos1.getY();
        this.pos1[2] = pos1.getZ();

        setPos2(region);
    }

    private void setPos2(Region region) {
        switch (typeName) {
            case "cuboid": {
                BlockVector3 pos2 = ((CuboidRegion) region).getPos2();
                int[] v = {pos2.getX(), pos2.getY(), pos2.getZ()};
                this.pos2.add(v);
                break;
            }
            case "2Dx1D polygon": {
                Polygonal2DRegion newRegion = ((Polygonal2DRegion) region);
                newRegion.getPoints().forEach(pos2 -> {
                    int[] v = {pos2.getX(), newRegion.getMinimumY(), pos2.getZ()};
                    this.pos2.add(v);
                });
                this.pos2.get(this.pos2.size() - 1)[1] = newRegion.getMaximumY();
                break;
            }
            case "ellipsoid": {
                EllipsoidRegion newRegion = ((EllipsoidRegion) region);
                BlockVector3 pos2 = newRegion.getRadius().toBlockPoint();
                int[] v = {this.pos1[0] + pos2.getX(), this.pos1[1] + pos2.getY(), this.pos1[2] + pos2.getZ()};
                this.pos2.add(v);
                break;
            }
            case "sphere": {
                EllipsoidRegion newRegion = ((EllipsoidRegion) region);
                BlockVector3 pos2 = newRegion.getRadius().toBlockPoint();
                int[] v = {this.pos1[0], this.pos1[1] + pos2.getY(), this.pos1[2]};
                this.pos2.add(v);
                break;
            }
            case "Cylinder": {
                CylinderRegion newRegion = ((CylinderRegion) region);
                BlockVector2 tmp = newRegion.getRadius().toBlockPoint();
                BlockVector3 pos2 = BlockVector3.at(tmp.getX(), newRegion.getHeight(), tmp.getZ());
                int[] v1 = {this.pos1[0] + pos2.getX(), newRegion.getMinimumY(), this.pos1[2] + pos2.getZ()};
                this.pos2.add(v1);
                int[] v2 = {this.pos1[0] + pos2.getX(), newRegion.getMaximumY(), this.pos1[2] + pos2.getZ()};
                this.pos2.add(v2);
                break;
            }
            case "Convex Polyhedron":
                ConvexPolyhedralRegion newRegion = ((ConvexPolyhedralRegion) region);
                newRegion.getVertices().forEach(pos2 -> {
                    int[] v = {pos2.getX(), pos2.getY(), pos2.getZ()};
                    this.pos2.add(v);
                });
                break;
        }
    }

    public Location getOrigin(World w) {
        return new Location(w, origin[0], origin[1], origin[2]);
    }

    public BlockVector3 getPrimaryPosition() {
        return BlockVector3.at(pos1[0], pos1[1], pos1[2]);
    }

    public List<BlockVector3> getSecondarySelections() {
        List<BlockVector3> list = new ArrayList<>();
        pos2.forEach(x -> {
            list.add(BlockVector3.at(x[0], x[1], x[2]));
        });
        return list;
    }
}
