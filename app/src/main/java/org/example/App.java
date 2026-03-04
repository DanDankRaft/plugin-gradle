package org.example;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class App extends JavaPlugin implements Listener {

    public static App thisPlugin;

    @Override
    public void onEnable() {
        thisPlugin = this;

        Bukkit.getPluginManager().registerEvents(this, this);

        this.getLifecycleManager().registerEventHandler(
            LifecycleEvents.COMMANDS,
            commands -> {
                commands.registrar().register(PluginCommands.menu.build());
                commands.registrar().register(PluginCommands.rainbow.build());
                commands
                    .registrar()
                    .register(PluginCommands.toggleBlockPlacementSync.build());
            }
        );
    }

    @Override
    public void onDisable() {
        this.getLogger().info("Plugin Disabled");
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) return;

        event.getWhoClicked().sendMessage(event.getCurrentItem().displayName());
        if (
            event
                .getCurrentItem()
                .getPersistentDataContainer()
                .has(new NamespacedKey(App.thisPlugin, "MY_ITEM"))
        ) {
            event.setCancelled(true);
            // event.getView().close();
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
            ? Bukkit.getWorld("world_nether")
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
            ? Bukkit.getWorld("world_nether")
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
            ? Bukkit.getWorld("world_nether")
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
            ? Bukkit.getWorld("world_nether")
            : Bukkit.getWorld("world");

        Location new_location = event.getBlock().getLocation().clone();
        new_location.setWorld(other_world);

        new_location.getBlock().setBlockData(event.getBlock().getBlockData());
    }
}
