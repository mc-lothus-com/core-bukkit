package com.lothus.bukkit.commands.register.admin;

import com.lothus.bukkit.commands.CommandBase;
import com.lothus.core.Core;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.player.group.rank.Rank;
import com.lothus.core.servers.ServerInfo;
import com.lothus.core.servers.status.ServerStatus;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WhitelistCommand extends CommandBase {

    public WhitelistCommand() {
        super("whitelist");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            LothPlayer lothPlayer = Core.getPlayerController().get(player.getUniqueId());

            if (lothPlayer.getGroup().getRank() != Rank.CEO) {
                if (!lothPlayer.getGroup().containsPermission("command.whitelist")) {
                    player.sendMessage(NO_PERMISSION);
                    return true;
                }
            }
        }

        if (args.length == 0) {
            ServerInfo serverInfo = Core.getServerInfo();
            serverInfo.setStatus((serverInfo.getStatus() == ServerStatus.MAINTENANCE_MODE ? ServerStatus.ONLINE : ServerStatus.MAINTENANCE_MODE));
            sender.sendMessage("§eA lista branca foi §b" + (serverInfo.getStatus() == ServerStatus.MAINTENANCE_MODE ? "ativado" : "desativado") + "§e.");
            Core.getDataServer().update(serverInfo);
            return true;
        }
        return false;
    }
}
