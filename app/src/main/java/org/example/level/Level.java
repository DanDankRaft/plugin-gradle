package org.example.level;

import java.util.HashMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.example.App;
import org.jetbrains.annotations.Nullable;

public class Level {

    public static HashMap<String, Level> levels = new HashMap<String, Level>();

    //used for debug purposes, sets up the levels hashmap with some default values
    public static void initialize() {
        Level level1 = new Level(
            new SpawnPoint(new Location(null, -1.5, 8.0, 3.5, 180.0f, 0.0f))
        );
        level1.levelCaption = "Level 1";
        level1.player1Inventory = new ItemStack[] {
            new ItemStack(Material.BLUE_CONCRETE, 2),
            new ItemStack(Material.LADDER, 3),
        };
        level1.player2Inventory = new ItemStack[] {
            new ItemStack(Material.RED_CONCRETE, 3),
            new ItemStack(Material.LADDER, 3),
        };

        Level level2 = new Level(
            new SpawnPoint(new Location(null, -1.5, 9.0, 103.5, 0f, 13.5f))
        );
        level2.levelCaption = "Level 2";
        level2.player1Inventory = new ItemStack[] {
            new ItemStack(Material.WATER_BUCKET),
            new ItemStack(Material.WATER_BUCKET),
            new ItemStack(Material.WATER_BUCKET),
            new ItemStack(Material.WATER_BUCKET),
            new ItemStack(Material.WATER_BUCKET),
        };
        level2.player2Inventory = new ItemStack[] {
            new ItemStack(Material.LAVA_BUCKET),
            new ItemStack(Material.LAVA_BUCKET),
            new ItemStack(Material.LAVA_BUCKET),
            new ItemStack(Material.LAVA_BUCKET),
            new ItemStack(Material.LAVA_BUCKET),
        };
        level1.nextLevel = level2;
        levels.put("level1", level1);
        levels.put("level2", level2);
    }

    public SpawnPoint spawnPoint;

    @Nullable
    public String levelCaption;

    @Nullable
    public Level nextLevel;

    public ItemStack[] player1Inventory;
    public ItemStack[] player2Inventory;

    //TODO: implement Region and world-resetting functionality
    //private Region region1;
    //private Region region2

    //private inventory1
    //private inventory2

    //public startLevel(Player player1, Player player2)

    public Level(SpawnPoint spawnPoint) {
        this.spawnPoint = spawnPoint;
    }

    public LevelState startLevel(Player player1, Player player2) {
        LevelState state = new LevelState(player1, player2, this);
        LevelState.levelStates.put(player1, state);
        LevelState.levelStates.put(player2, state);

        Location location1 = spawnPoint.getLocation(1);
        Location location2 = spawnPoint.getLocation(2);
        location1.setWorld(App.overworld);
        location2.setWorld(App.mirror_world);

        player1.teleport(location1);
        player2.teleport(location2);

        player1.getInventory().setContents(player1Inventory);
        player2.getInventory().setContents(player2Inventory);

        player1.showTitle(
            Title.title(
                Component.text(levelCaption).color(NamedTextColor.BLUE),
                Component.text("don't forget to have fun Player 1")
            )
        );

        player2.showTitle(
            Title.title(
                Component.text(levelCaption).color(NamedTextColor.RED),
                Component.text("don't forget to have fun Player 2")
            )
        );

        return state;
    }

    public static NamespacedKey currentLevelKey = new NamespacedKey(
        App.thisPlugin,
        "CurrentLevel"
    );

    //TODO: change to non-static later..
    // public static void setPlayerLevel(Player player, String level) {
    //     PersistentDataContainer pdc = player.getPersistentDataContainer();
    //     pdc.set(currentLevelKey, PersistentDataType.STRING, level);
    // }

    // public static void clearPlayerLevel(Player player) {
    //     PersistentDataContainer pdc = player.getPersistentDataContainer();
    //     pdc.remove(currentLevelKey);
    // }
}
