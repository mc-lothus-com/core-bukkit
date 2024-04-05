package com.lothus.bukkit.commands.register.moderation.teleport;

import com.lothus.bukkit.commands.CommandBase;
import com.lothus.core.Core;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.player.group.rank.Rank;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportAllCommand extends CommandBase {

    public TeleportAllCommand() {
        super(
                "teleportall",
                "",
                "tpall");
    }

    @Override
    public boolean execute(CommandSender commandSender, String label, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("§cApenas jogadores podem utilizar este comando!");
            return false;
        }
        Player player = (Player) commandSender;
        LothPlayer hyzePlayer = Core.getPlayerController().get(player.getUniqueId());
        if (!(hyzePlayer.getGroup().getRank().ordinal() <= Rank.GER.ordinal())) {
            if (!hyzePlayer.getGroup().containsPermission("command.teleportall")) {
                player.sendMessage(NO_PERMISSION);
                return false;
            }
        }
        teleport(player);
        player.sendMessage("§aTeleportando todos os jogadores para você!");
        return false;
    }

    public void teleport(Player player) {
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (player.equals(target))
                continue;

            target.teleport(player);
        }
    }

}
