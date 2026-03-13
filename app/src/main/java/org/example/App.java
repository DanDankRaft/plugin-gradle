package org.example;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.Powerable;
import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.example.level.Level;
import org.example.level.LevelCommands;
import org.example.level.LevelState;

public class App extends JavaPlugin implements Listener {

    public static App thisPlugin;
    public static World mirror_world;
    public static World overworld;

    @Override
    public void onEnable() {
        thisPlugin = this;

        Bukkit.getPluginManager().registerEvents(this, this);

        this.getLifecycleManager().registerEventHandler(
            LifecycleEvents.COMMANDS,
            commands -> {
                Commands registrar = commands.registrar();

                registrar.register(PluginCommands.toggleSyncBuilder().build());
                registrar.register(PluginCommands.tpMirrorBuilder().build());
                registrar.register(PluginCommands.tpOverworldBuilder().build());
                // registrar.register(LevelCommands.setLevelBuilder().build());
                // registrar.register(LevelCommands.clearLevelBuilder().build());
                registrar.register(LevelCommands.startLevelBuilder().build());
            }
        );

        mirror_world = new WorldCreator(new NamespacedKey(this, "mirror_world"))
            .environment(World.Environment.NORMAL)
            .type(WorldType.FLAT)
            .createWorld();

        overworld = Bukkit.getWorld("world");

        Level.initialize();
    }

    @Override
    public void onDisable() {
        this.getServer().unloadWorld(mirror_world, true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        LevelState levelState = LevelState.levelStates.get(player);

        if (levelState == null) return;

        if (event.getAction() == Action.PHYSICAL) {
            Block block = event.getClickedBlock();
            if (Tag.PRESSURE_PLATES.isTagged(block.getType())) {
                Powerable plate = (Powerable) block.getBlockData();

                //necessary for areBothPlayersReady to work
                plate.setPowered(true);
                block.setBlockData(plate);

                if (levelState.whichPlayer(player) == 1) {
                    levelState.player1PressurePlate = block;
                } else if (levelState.whichPlayer(player) == 2) {
                    levelState.player2PressurePlate = block;
                }

                player.sendMessage(
                    "Are both players ready? " +
                        levelState.areBothPlayersReady()
                );
                if (levelState.areBothPlayersReady()) {
                    levelState.advanceToNextLevel();
                }
            }
        }

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();

            if (
                block.getBlockData() instanceof Openable door &&
                block.getType() != Material.IRON_DOOR
            ) {
                Location otherDoorLocation = block.getLocation();
                otherDoorLocation.setWorld(
                    player.getWorld() == overworld ? mirror_world : overworld
                );
                Block otherDoorBlock = otherDoorLocation.getBlock();
                Openable otherDoor = (Openable) otherDoorBlock.getBlockData();

                otherDoor.setOpen(!door.isOpen());
                otherDoorBlock.setBlockData(otherDoor);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        LevelState levelState = LevelState.levelStates.get(player);

        if (levelState == null) return;

        if (event.getTo().getY() <= -10f) {
            player.sendMessage("Teleporting!");
            Location loc_normalized = levelState.level.spawnPoint.getLocation();
            loc_normalized.setWorld(player.getWorld());

            player.teleport(loc_normalized);
        }
    }

    @EventHandler
    public void onBlockDestroy(BlockBreakEvent event) {
        PersistentDataContainer pdc = event
            .getPlayer()
            .getPersistentDataContainer();
        boolean isSynced = pdc.getOrDefault(
            PluginCommands.playerPlacementSynced,
            PersistentDataType.BOOLEAN,
            true
        );
        if (!isSynced) return;

        World other_world = event
            .getBlock()
            .getWorld()
            .getName()
            .equals("world")
            ? Bukkit.getWorld("mirror_world")
            : Bukkit.getWorld("world");

        Location new_location = event.getBlock().getLocation().clone();
        new_location.setWorld(other_world);

        new_location.getBlock().setType(Material.AIR);
    }

    @EventHandler
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        PersistentDataContainer pdc = event
            .getPlayer()
            .getPersistentDataContainer();
        boolean isSynced = pdc.getOrDefault(
            PluginCommands.playerPlacementSynced,
            PersistentDataType.BOOLEAN,
            true
        );
        if (!isSynced) return;

        World other_world = event
            .getBlock()
            .getWorld()
            .getName()
            .equals("world")
            ? Bukkit.getWorld("mirror_world")
            : Bukkit.getWorld("world");

        Location new_location = event.getBlock().getLocation().clone();
        new_location.setWorld(other_world);

        new_location.getBlock().setType(Material.AIR);
    }

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        PersistentDataContainer pdc = event
            .getPlayer()
            .getPersistentDataContainer();
        boolean isSynced = pdc.getOrDefault(
            PluginCommands.playerPlacementSynced,
            PersistentDataType.BOOLEAN,
            true
        );
        if (!isSynced) return;

        Material bucket = event.getBucket();
        Material liquid = Material.AIR;
        if (bucket == Material.WATER_BUCKET) {
            liquid = Material.WATER;
        } else if (bucket == Material.LAVA_BUCKET) {
            liquid = Material.LAVA;
        }

        World other_world = event
            .getBlock()
            .getWorld()
            .getName()
            .equals("world")
            ? Bukkit.getWorld("mirror_world")
            : Bukkit.getWorld("world");

        Location new_location = event.getBlock().getLocation().clone();
        new_location.setWorld(other_world);

        new_location.getBlock().setType(liquid);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        PersistentDataContainer pdc = event
            .getPlayer()
            .getPersistentDataContainer();
        boolean isSynced = pdc.getOrDefault(
            PluginCommands.playerPlacementSynced,
            PersistentDataType.BOOLEAN,
            true
        );
        if (!isSynced) return;

        World other_world = event
            .getBlock()
            .getWorld()
            .getName()
            .equals("world")
            ? Bukkit.getWorld("mirror_world")
            : Bukkit.getWorld("world");

        Location new_location = event.getBlock().getLocation().clone();
        new_location.setWorld(other_world);

        new_location.getBlock().setBlockData(event.getBlock().getBlockData());
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        PersistentDataContainer pdc = event
            .getPlayer()
            .getPersistentDataContainer();
        boolean isSynced = pdc.getOrDefault(
            PluginCommands.playerPlacementSynced,
            PersistentDataType.BOOLEAN,
            true
        );
        if (!isSynced) return;

        World other_world = event
            .getBlock()
            .getWorld()
            .getName()
            .equals("world")
            ? Bukkit.getWorld("mirror_world")
            : Bukkit.getWorld("world");

        Location new_location = event.getBlock().getLocation().clone();
        new_location.setWorld(other_world);

        List<Component> lines = event.lines();
        Sign new_sign = (Sign) new_location.getBlock().getState();
        SignSide new_signside = new_sign.getSide(event.getSide());
        for (int i = 0; i < lines.size(); i++) {
            new_signside.line(i, lines.get(i));
        }
        new_sign.update();
    }
}
