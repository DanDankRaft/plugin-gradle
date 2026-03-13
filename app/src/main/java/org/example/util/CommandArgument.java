package org.example.util;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import java.util.List;
import org.bukkit.entity.Player;

public class CommandArgument {

    public static List<Player> getPlayers(
        CommandContext<CommandSourceStack> ctx,
        String argumentName
    ) throws CommandSyntaxException {
        return ctx
            .getArgument(argumentName, PlayerSelectorArgumentResolver.class)
            .resolve(ctx.getSource());
    }
}
