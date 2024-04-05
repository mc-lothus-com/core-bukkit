package com.lothus.bukkit.commands.register.moderation.admin;

import com.lothus.bukkit.commands.CommandBase;
import com.lothus.bukkit.events.commands.AdminChangeEvent;
import com.lothus.core.Core;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.player.group.rank.Rank;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminCommand extends CommandBase {

    public AdminCommand() {
        super(
                "admin",
                "",
                "vanish", "v");
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
            if (!hyzePlayer.getGroup().containsPermission("command.admin")) {
                player.sendMessage(NO_PERMISSION);
                return false;
            }
        }

        Bukkit.getPluginManager().callEvent(new AdminChangeEvent(player, !hyzePlayer.getPrefs().isVanish()));
        return false;
    }
}
