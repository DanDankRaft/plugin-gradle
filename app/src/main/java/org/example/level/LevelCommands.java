package org.example.level;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.entity.Player;

public class LevelCommands {

    public static LiteralArgumentBuilder<
        CommandSourceStack
    > startLevelBuilder() {
        var startlevel = Commands.literal("startlevel").then(
            Commands.argument("level", StringArgumentType.word()).then(
                Commands.argument("player1", ArgumentTypes.player()).then(
                    Commands.argument(
                        "player2",
                        ArgumentTypes.player()
                    ).executes(ctx -> {
                        Player player1 = ctx
                            .getArgument(
                                "player1",
                                PlayerSelectorArgumentResolver.class
                            )
                            .resolve(ctx.getSource())
                            .getFirst();
                        Player player2 = ctx
                            .getArgument(
                                "player2",
                                PlayerSelectorArgumentResolver.class
                            )
                            .resolve(ctx.getSource())
                            .getFirst();

                        String levelString = ctx.getArgument(
                            "level",
                            String.class
                        );

                        Level newLevel = Level.levels.get(levelString);
                        if (newLevel == null) {
                            ctx
                                .getSource()
                                .getSender()
                                .sendMessage("Level name not found");
                            return 1;
                        }

                        newLevel.startLevel(player1, player2);
                        return 0;
                    })
                )
            )
        );

        return startlevel;
    }

    // public static LiteralArgumentBuilder<CommandSourceStack> setLevelBuilder() {
    //     var setlevel = Commands.literal("setlevel").then(
    //         Commands.argument("level", StringArgumentType.word()).executes(
    //             ctx -> {
    //                 if (!(ctx.getSource().getSender() instanceof Player)) {
    //                     ctx
    //                         .getSource()
    //                         .getSender()
    //                         .sendMessage(
    //                             "This command can only be executed by players"
    //                         );
    //                     return 0;
    //                 }

    //                 Player player = (Player) ctx.getSource().getSender();
    //                 String level = ctx.getArgument("level", String.class);
    //                 Level.setPlayerLevel(player, level);
    //                 return 1;
    //             }
    //         )
    //     );

    //     return setlevel;
    // }

    // public static LiteralArgumentBuilder<
    //     CommandSourceStack
    // > clearLevelBuilder() {
    //     var clearLevel = Commands.literal("clearlevel").executes(ctx -> {
    //         if (!(ctx.getSource().getSender() instanceof Player)) {
    //             ctx
    //                 .getSource()
    //                 .getSender()
    //                 .sendMessage(
    //                     "This command can only be executed by players"
    //                 );
    //             return 0;
    //         }

    //         Player player = (Player) ctx.getSource().getSender();
    //         Level.clearPlayerLevel(player);
    //         return 1;
    //     });

    //     return clearLevel;
    // }
}
