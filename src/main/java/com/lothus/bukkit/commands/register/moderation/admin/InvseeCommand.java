package com.lothus.bukkit.commands.register.moderation.admin;

import com.lothus.bukkit.commands.CommandBase;
import com.lothus.core.Core;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.player.group.rank.Rank;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InvseeCommand extends CommandBase {

    public InvseeCommand() {
        super(
                "invsee",
                "",
                "inv", "inventory", "inventorysee");
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
            if (!hyzePlayer.getGroup().containsPermission("command.invsee")) {
                player.sendMessage(NO_PERMISSION);
                return false;
            }
        }
        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage("§cUsuário não encontrado.");
                return false;
            }

            if (target.equals(player)) {
                player.sendMessage("§cVocê não pode ver seu próprio inventário.");
                return false;
            }

            player.openInventory(target.getInventory());
            player.sendMessage("§aInventário de " + target.getName() + " aberto.");
        } else {
            player.sendMessage("§cSintaxe incorreta, utilize '/invsee [jogador]'.");
            return false;
        }
        return false;
    }


}
