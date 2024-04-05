package com.lothus.bukkit.commands.register.moderation.gamemode;

import com.lothus.bukkit.commands.CommandBase;
import com.lothus.core.Core;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.player.group.rank.Rank;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GamemodeCommand extends CommandBase {

    public GamemodeCommand() {
        super(
                "gamemode",
                "",
                "gm");
    }

    @Override
    public boolean execute(CommandSender commandSender, String label, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("§cApenas jogadores podem utilizar este comando!");
            return false;
        }
        Player player = (Player) commandSender;
        LothPlayer hyzePlayer = Core.getPlayerController().get(player.getUniqueId());
        if (!(hyzePlayer.getGroup().getRank().ordinal() <= Rank.MOD.ordinal())) {
            if (!hyzePlayer.getGroup().containsPermission("command.gamemode")) {
                player.sendMessage(NO_PERMISSION);
                return false;
            }
        }

        if (args.length == 0) {
            player.sendMessage("§cUtilize /gamemode <modo> [jogador]");
            return false;
        }

        GameMode gamemode;
        try {
            gamemode = GameMode.valueOf(args[0].toUpperCase());
        } catch (Exception e) {
            try {
                gamemode = GameMode.getByValue(Integer.parseInt(args[0]));
            } catch (Exception e2) {
                player.sendMessage("§cNão foi possível contrar o modo de jogo.");
                return false;
            }
        }

        if (gamemode == null) {
            player.sendMessage("§cNão foi possível contrar o modo de jogo.");
            return false;
        }

        if (args.length == 1) {
            player.sendMessage("§aVocê alterou seu modo de jogo para " + gamemode.name().toLowerCase() + "!");
            player.setGameMode(gamemode);
        } else if (args.length == 2) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                player.sendMessage("§cNão foi possível encontrar o jogador " + args[1] + "!");
                return false;
            }
            player.sendMessage("§aVocê alterou o modo de jogo de " + target.getName() + " para " + gamemode.name().toLowerCase() + "!");
            target.setGameMode(gamemode);
        }
        return false;
    }


}
