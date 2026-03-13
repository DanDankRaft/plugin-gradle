package org.example.level;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SpawnPoint {

    private Location loc1;
    private Location loc2;

    public SpawnPoint(Location loc) {
        loc1 = loc.clone();
        this.loc1.setWorld(null);
        loc2 = loc.clone();
        this.loc2.setWorld(null);
    }

    public SpawnPoint(Location loc1, Location loc2) {
        this.loc1 = loc1.clone();
        this.loc1.setWorld(null);
        this.loc2 = loc2.clone();
        this.loc2.setWorld(null);
    }

    /**
     * Returns the spawn point's location. Location.world will always be null, as these locations are supposed to be world-agnostic
     * @return
     */
    public Location getLocation() {
        return loc1;
    }

    /**
     * Returns the spawn point's location. Location.world will always be null, as these locations are supposed to be world-agnostic
     * @return
     */
    public Location getLocation(int player) {
        switch (player) {
            case 1:
                return loc1;
            case 2:
                return loc2;
            default:
                return null;
        }
    }

    public boolean teleportPlayer(Player player, int which_player) {
        Location loc;
        switch (which_player) {
            case 1:
                loc = loc1.clone();
                break;
            case 2:
                loc = loc2.clone();
                break;
            default:
                return false;
        }
        loc.setWorld(player.getWorld());
        player.teleport(loc);
        return true;
    }

    /***
     * Sets the location of the spawn point for a given player.
     * <br>
     * Silently fails if player != 1 or 2
     */
    public void setLocation(Location loc, int player) {
        switch (player) {
            case 1:
                loc1 = loc;
                break;
            case 2:
                loc2 = loc;
                break;
        }
    }

    public void setLocation(Location loc) {
        loc1 = loc;
        loc2 = loc;
    }
}
