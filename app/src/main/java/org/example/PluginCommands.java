package org.example;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.example.util.CommandArgument;

public class PluginCommands {

    public static NamespacedKey playerPlacementSynced = new NamespacedKey(
        App.thisPlugin,
        "PlayerPlacementSynced"
    );

    static int toggleStateInner(
        CommandContext<CommandSourceStack> ctx,
        List<Player> players,
        Boolean state
    ) {
        CommandSender sender = ctx.getSource().getSender();
        if (players == null) {
            if (sender instanceof Player) {
                players = new ArrayList<Player>();
                players.add((Player) sender);
            } else {
                sender.sendMessage("Can only toggle sync for players");
                sender.sendMessage("try: /togglesync <player> [true|false]");
                return 0;
            }
        }

        for (Player player : players) {
            PersistentDataContainer pdc = player.getPersistentDataContainer();
            Boolean currentState = state;
            if (currentState == null) {
                currentState = pdc.getOrDefault(
                    playerPlacementSynced,
                    PersistentDataType.BOOLEAN,
                    true
                );
                currentState = !currentState;
            }

            pdc.set(
                playerPlacementSynced,
                PersistentDataType.BOOLEAN,
                currentState
            );
            sender.sendMessage(
                player.getName() +
                    " will" +
                    (currentState ? "" : " not") +
                    " sync"
            );
        }

        return Command.SINGLE_SUCCESS;
    }

    public static LiteralArgumentBuilder<
        CommandSourceStack
    > toggleSyncBuilder() {
        var togglesync = Commands.literal("togglesync").executes(ctx ->
            toggleStateInner(ctx, null, null)
        );

        var player = Commands.argument(
            "player",
            ArgumentTypes.players()
        ).executes(ctx ->
            toggleStateInner(
                ctx,
                CommandArgument.getPlayers(ctx, "player"),
                null
            )
        );

        var state = Commands.argument(
            "state",
            BoolArgumentType.bool()
        ).executes(ctx ->
            toggleStateInner(
                ctx,
                CommandArgument.getPlayers(ctx, "player"),
                ctx.getArgument("state", boolean.class)
            )
        );

        togglesync.then(player.then(state));
        return togglesync;
    }

    static int tpWorldInner(
        CommandContext<CommandSourceStack> ctx,
        String world
    ) {
        CommandSender sender = ctx.getSource().getSender();
        if (!(sender instanceof Player)) {
            sender.sendMessage(
                Component.text("Only players can use this command.")
            );
            return 0;
        }
        Player player = (Player) sender;
        Location mirrorLocation = player.getLocation();
        mirrorLocation.setWorld(Bukkit.getWorld(world));
        player.teleport(mirrorLocation);

        return Command.SINGLE_SUCCESS;
    }

    public static LiteralArgumentBuilder<CommandSourceStack> tpMirrorBuilder() {
        var tpmirror = Commands.literal("tpmirror").executes(ctx ->
            tpWorldInner(ctx, "mirror_world")
        );
        return tpmirror;
    }

    public static LiteralArgumentBuilder<
        CommandSourceStack
    > tpOverworldBuilder() {
        var tpmirror = Commands.literal("tpoverworld").executes(ctx ->
            tpWorldInner(ctx, "world")
        );
        return tpmirror;
    }
}
