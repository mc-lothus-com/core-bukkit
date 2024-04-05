package com.lothus.bukkit.commands.register.moderation.teleport;

import com.lothus.bukkit.commands.CommandBase;
import com.lothus.core.Core;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.player.group.rank.Rank;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportCommand extends CommandBase {

    public TeleportCommand() {
        super(
                "teleport",
                "",
                "tp");
    }

    @Override
    public boolean execute(CommandSender commandSender, String label, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("§cApenas jogadores podem utilizar este comando!");
            return false;
        }
        Player player = (Player) commandSender;
        LothPlayer hyzePlayer = Core.getPlayerController().get(player.getUniqueId());
        if (!(hyzePlayer.getGroup().getRank().ordinal() <= Rank.TRIAL.ordinal())) {
            if (!hyzePlayer.getGroup().containsPermission("command.teleport")) {
                player.sendMessage(NO_PERMISSION);
                return false;
            }
        }
        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage("§cJogador não encontrado!");
                return false;
            }
            player.sendMessage("§aTeleportando para " + target.getName() + "!");
            player.teleport(target);
            return false;
        } else if (args.length == 2) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage("§cJogador não encontrado!");
                return false;
            }
            Player playerToTeleport = Bukkit.getPlayer(args[1]);
            if (playerToTeleport == null) {
                player.sendMessage("§cJogador não encontrado!");
                return false;
            }
            target.teleport(playerToTeleport);
            player.sendMessage("§aTeleportando " + target.getName() + " para " + playerToTeleport.getName() + "!");
            return false;
        } else if (args.length == 3) {
            if (isInteger(args[0]) && isInteger(args[1]) && isInteger(args[2])) {
                int x = Integer.valueOf(args[0]);
                int y = Integer.valueOf(args[1]);
                int z = Integer.valueOf(args[2]);
                Location location = new Location(player.getWorld(), x, y, z);
                player.sendMessage("§aTeleportando para " + x + ", " + y + ", " + z + "!");
                player.teleport(location);
            } else {
                player.sendMessage("§cUtilize números inteiros!");
            }
        } else {
            player.sendMessage("§cSintaxe incorreta, utilize '/tp [jogador]'.");
            return false;
        }
        return false;
    }


}
