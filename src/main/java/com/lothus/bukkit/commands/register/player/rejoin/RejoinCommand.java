package com.lothus.bukkit.commands.register.player.rejoin;

import com.lothus.bukkit.commands.CommandBase;
import com.lothus.core.Core;
import com.lothus.core.games.GameInfo;
import com.lothus.core.player.rejoin.Rejoin;
import com.lothus.core.servers.ServerInfo;
import com.lothus.core.utils.bukkit.player.PlayerUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class RejoinCommand extends CommandBase {

    private HashMap<UUID, Long> cooldown = new HashMap<>();

    public RejoinCommand() {
        super("rejoin", "", "reconectar");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if(!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        if (cooldown.containsKey(player.getUniqueId()) && cooldown.get(player.getUniqueId()) > System.currentTimeMillis()) {
            int sec = (int) TimeUnit.MILLISECONDS.toSeconds(cooldown.get(player.getUniqueId()) - System.currentTimeMillis());
            player.sendMessage("§cAguarde " + sec + " segundo"+ (sec <= 1 ? "" : "s") + " para reconectar novamente.");
            return true;
        }

        cooldown.put(player.getUniqueId(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(5));

        Rejoin rejoin = Core.getRejoinController().getRejoin(player.getUniqueId());
        if (rejoin == null) {
            player.sendMessage("§cVocê não tem uma reconexão disponível.");
            return true;
        }

        List<GameInfo> gameInfos = Core.getGameController().getAll(rejoin.getGameType(), rejoin.getRoomType());
        if (gameInfos.isEmpty()) {
            player.sendMessage("§cA lista de partidas " + rejoin.getGameType().getName() + " " + rejoin.getRoomType().getName() + " está vazia.");
            return true;
        }

        GameInfo gameInfo = gameInfos.stream().filter(g -> g.getName().equals(rejoin.getArenaName())).findFirst().orElse(null);
        if (gameInfo == null) {
            player.sendMessage("§cA partida " + rejoin.getArenaName() + " não está disponível.");
            return true;
        }

        ServerInfo serverInfo = Core.getServerController().get(gameInfo.getName());

        if (serverInfo == null) {
            player.sendMessage("§cA partida " + rejoin.getArenaName() + " não está disponível.");
            return true;
        }

        PlayerUtil.connect(player.getUniqueId(), serverInfo);
        Core.getRejoinController().unload(player.getUniqueId());
        return false;
    }
}
