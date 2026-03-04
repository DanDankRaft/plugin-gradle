package org.example;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.HSVLike;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

public class PluginCommands {

    public static LiteralArgumentBuilder<CommandSourceStack> menu =
        Commands.literal("menu").executes(ctx -> {
            CommandSender sender = ctx.getSource().getSender();
            if (!(sender instanceof Player)) return 0;

            Player player = (Player) sender;
            player.sendMessage(
                Component.text("You are a player!").color(NamedTextColor.BLUE)
            );
            MyGui gui = new MyGui();
            return 1;
        });

    public static LiteralArgumentBuilder<CommandSourceStack> rainbow =
        Commands.literal("rainbow").executes(ctx -> {
            CommandSender sender = ctx.getSource().getSender();
            if (sender instanceof Player player) {
                BukkitRunnable showTitleRunnable = new BukkitRunnable() {
                    float counter = 0f;

                    @Override
                    public void run() {
                        Title t = Title.title(
                            Component.text("Hello, ").append(
                                Component.text(player.getName()).color(
                                    TextColor.color(
                                        HSVLike.hsvLike(counter % 1.0f, 1, 1)
                                    )
                                )
                            ),
                            Component.empty(),
                            0,
                            3,
                            20
                        );
                        player.showTitle(t);
                        counter += 0.01;
                        if (counter >= 1.0) this.cancel();
                    }
                };
                showTitleRunnable.runTaskTimer(App.thisPlugin, 20, 2);
            }
            return 1;
        });

    public static NamespacedKey playerPlacementSynced = new NamespacedKey(
        App.thisPlugin,
        "PlayerPlacementSynced"
    );

    public static LiteralArgumentBuilder<
        CommandSourceStack
    > toggleBlockPlacementSync = Commands.literal("togglesync").executes(
        ctx -> {
            CommandSender sender = ctx.getSource().getSender();
            if (!(sender instanceof Player)) return 0;

            Player player = (Player) sender;
            PersistentDataContainer pdc = player.getPersistentDataContainer();
            boolean isSynced = pdc.getOrDefault(
                playerPlacementSynced,
                PersistentDataType.BOOLEAN,
                true
            );

            pdc.set(
                playerPlacementSynced,
                PersistentDataType.BOOLEAN,
                !isSynced
            );

            return 1;
        }
    );

    public static class MyGui implements InventoryHolder {

        private final Inventory inventory;

        public static class MyCustomItem extends ItemStack {

            public MyCustomItem() {
                super(Material.COAL_BLOCK);
                // ItemStack coal_block = ItemStack.of(Material.COAL_BLOCK);
                this.editMeta(meta -> {
                    // meta.hasCust
                    meta.displayName(
                        Component.text("frog").decoration(
                            TextDecoration.ITALIC,
                            false
                        )
                    );
                });
            }
        }

        public MyGui() {
            this.inventory = Bukkit.createInventory(
                this,
                InventoryType.CHEST,
                Component.text("My Cool and Awesome GUI")
            );
            ItemStack coal_block = ItemStack.of(Material.COAL_BLOCK);
            coal_block.editMeta(meta -> {
                // meta.hasCust
                meta.displayName(
                    Component.text("frog").decoration(
                        TextDecoration.ITALIC,
                        false
                    )
                );
            });
            coal_block.editPersistentDataContainer(pdc -> {
                pdc.set(
                    new NamespacedKey(App.thisPlugin, "MY_ITEM"),
                    PersistentDataType.BOOLEAN,
                    true
                );
            });
            this.inventory.setItem(13, coal_block);
        }

        @Override
        public Inventory getInventory() {
            return inventory;
        }
    }
}
