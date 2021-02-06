package net.kunmc.lab.wecommandblock;

import com.sk89q.worldedit.blocks.BaseItemStack;
import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.extension.platform.AbstractPlayerActor;
import com.sk89q.worldedit.extent.inventory.BlockBag;
import com.sk89q.worldedit.session.SessionKey;
import com.sk89q.worldedit.util.HandSide;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.world.World;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.UUID;

public class FakeActor extends AbstractPlayerActor {
    World world;
    SessionKey sessionKey;
    Location location;
    String name = "Fake";

    FakeActor(World world, SessionKey sessionKey, Location location) {
        this.world = world;
        this.sessionKey = sessionKey;
        this.location = location;
    }

    @Override
    public World getWorld() {
        return this.world;
    }

    @Override
    public BaseItemStack getItemInHand(HandSide handSide) {
        return null;
    }

    @Override
    public void giveItem(BaseItemStack itemStack) {

    }

    @Override
    public BlockBag getInventoryBlockBag() {
        return null;
    }

    @Nullable
    @Override
    public BaseEntity getState() {
        return null;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void printRaw(String msg) {

    }

    @Override
    public void printDebug(String msg) {

    }

    @Override
    public void print(String msg) {

    }

    @Override
    public void printError(String msg) {

    }

    @Override
    public void print(Component component) {

    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    @Override
    public boolean setLocation(Location location) {
        this.location = location;
        return true;
    }

    @Override
    public SessionKey getSessionKey() {
        return this.sessionKey;
    }

    @Nullable
    @Override
    public <T> T getFacet(Class<? extends T> cls) {
        return null;
    }

    @Override
    public UUID getUniqueId() {
        return null;
    }

    @Override
    public String[] getGroups() {
        return new String[0];
    }

    @Override
    public boolean hasPermission(String permission) {
        return true;
    }
}
