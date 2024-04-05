package com.lothus.bukkit.commands.register.moderation.chat;

import com.lothus.bukkit.commands.CommandBase;
import com.lothus.core.Core;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.player.group.rank.Rank;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearChatCommand extends CommandBase {

    public ClearChatCommand() {
        super(
                "cc",
                "",
                "clearchat");
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
            if (!hyzePlayer.getGroup().containsPermission("command.clearchat")) {
                player.sendMessage(NO_PERMISSION);
                return false;
            }
        }

        for (int i = 0; i < 100; i++) {
            Bukkit.getOnlinePlayers().forEach(players -> players.sendMessage(""));
        }

        Bukkit.getOnlinePlayers().forEach(players -> players.sendMessage("§eO bate-papo foi limpo por §b" + player.getName() + "§e!"));
        return false;
    }
}
