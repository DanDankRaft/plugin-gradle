package org.example.level;

import java.util.HashMap;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.block.data.Powerable;
import org.bukkit.entity.Player;

public class LevelState {

    private Player player1;
    private Player player2;
    public Level level;
    public Block player1PressurePlate = null;
    public Block player2PressurePlate = null;

    public boolean areBothPlayersReady() {
        boolean player1Ready =
            player1PressurePlate != null &&
            ((Powerable) player1PressurePlate.getBlockData()).isPowered();
        boolean player2Ready =
            player2PressurePlate != null &&
            ((Powerable) player2PressurePlate.getBlockData()).isPowered();
        return player1Ready && player2Ready;
    }

    public int whichPlayer(Player player) {
        if (player == player1) return 1;
        else if (player == player2) return 2;
        else return 0;
    }

    public LevelState advanceToNextLevel() {
        if (level.nextLevel == null) {
            player1.sendMessage("No more levels!");
            player1.setGameMode(GameMode.CREATIVE);
            player2.sendMessage("No more levels!");
            player2.setGameMode(GameMode.CREATIVE);

            levelStates.remove(player1);
            levelStates.remove(player2);
            return null;
        }

        LevelState newLevel = level.nextLevel.startLevel(player1, player2);
        return newLevel;
    }

    public static HashMap<Player, LevelState> levelStates = new HashMap<
        Player,
        LevelState
    >();

    protected LevelState(Player player1, Player player2, Level level) {
        this.player1 = player1;
        this.player2 = player2;
        this.level = level;
    }
}
