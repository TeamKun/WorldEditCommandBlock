package net.kunmc.lab.wecommandblock.Preset;

import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.*;
import com.sk89q.worldedit.util.Location;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PresetData implements Serializable {
    public String type;
    public String worldName;
    public double[] origin = new double[3];
    public int[] pos1 = new int[3];
    public List<int[]> pos2 = new ArrayList<>();


    public PresetData(String type, String worldName, Location origin, BlockVector3 pos1, Region region) {
        this.type = type;
        this.worldName = worldName;

        this.origin[0] = origin.getX();
        this.origin[1] = origin.getY();
        this.origin[2] = origin.getZ();

        this.pos1[0] = pos1.getX();
        this.pos1[1] = pos1.getY();
        this.pos1[2] = pos1.getZ();

        switch (type) {
            case "cuboid": {
                BlockVector3 pos2 = ((CuboidRegion) region).getPos2();
                int[] v = {pos2.getX(), pos2.getY(), pos2.getZ()};
                this.pos2.add(v);
                break;
            }
            case "2Dx1D polygon": {
                Polygonal2DRegion newRegion = ((Polygonal2DRegion) region);
                newRegion.getPoints().forEach(pos2 -> {
                    int[] v = {pos2.getX(), 0, pos2.getZ()};
                    this.pos2.add(v);
                });
                this.pos2.get(0)[1] = newRegion.getMinimumY();
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
                int[] v1 = {this.pos1[0] + pos2.getX(),  newRegion.getMinimumY(), this.pos1[2] + pos2.getZ()};
                this.pos2.add(v1);
                int[] v2 = {this.pos1[0] + pos2.getX(),  newRegion.getMaximumY(), this.pos1[2] + pos2.getZ()};
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
}
