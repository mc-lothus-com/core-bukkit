package com.lothus.bukkit.commands.register.admin;

import com.lothus.core.api.crash.CrashAPI;
import com.lothus.bukkit.commands.CommandBase;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class CrashCommand extends CommandBase {

    private HashMap<UUID, Integer> attempts = new HashMap<>();

    public CrashCommand() {
        super("cpr");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (sender instanceof Player)return true;

        if (args.length == 0) {
            sender.sendMessage("§cSintaxe incorreta, utilize '/crash [player]'.");
            return true;
        }

        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage("§cJogador não encontrado.");
            return true;
        }


        if (attempts.containsKey(player.getUniqueId()) && attempts.get(player.getUniqueId()) >= 10) {
            CrashAPI.crashPlayer(player);
            sender.sendMessage("§cO jogador " + player.getName() + " foi desconectado.");
            attempts.remove(player.getUniqueId());
            return false;
        }

        if (attempts.containsKey(player.getUniqueId())) {
            attempts.put(player.getUniqueId(), attempts.get(player.getUniqueId()) + 1);
        } else {
            attempts.put(player.getUniqueId(), 1);
        }
        return false;
    }
}
