package com.lothus.bukkit.commands.register.moderation.booster;

import com.lothus.bukkit.commands.CommandBase;
import com.lothus.core.Core;
import com.lothus.core.games.type.GameType;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.player.booster.GameBooster;
import com.lothus.core.player.booster.duration.BoosterDuration;
import com.lothus.core.player.booster.duration.type.BoosterDurationType;
import com.lothus.core.player.booster.status.BoosterStatus;
import com.lothus.core.player.booster.type.BoosterType;
import com.lothus.core.player.group.rank.Rank;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class BoosterCommand extends CommandBase {

    public BoosterCommand() {
        super(
                "booster"
        );
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            LothPlayer lothPlayer = Core.getPlayerController().get(player.getUniqueId());

            if (!lothPlayer.hasPermission(Rank.GER)) {
                if (!lothPlayer.hasPermission("lothus.booster")) {
                    player.sendMessage(NO_PERMISSION);
                    return true;
                }
            }
        }

        if (args.length == 0) {
            sender.sendMessage("§cSintaxe incorreta, utilize '/booster [jogador] [adicionar] [tipo] [múltiplo] [tempo]'");
            return true;
        }

        Player player = Bukkit.getPlayer(args[0]);

        LothPlayer lp = (player == null ? Core.getDataPlayer().get(args[0]) : Core.getPlayerController().get(player.getUniqueId()));

        if (args[1].equalsIgnoreCase("adicionar")) {
            if (args.length == 2 || args.length == 3 || args.length == 4) {
                sender.sendMessage("§cSintaxe incorreta, utilize '/booster " + args[0] + " adicionar [game] [tipo] [múltiplo] [1h/1d]'");
                return true;
            }

            GameType gameType = GameType.getByName(args[2].toLowerCase());
            BoosterType type = BoosterType.getByName(args[3].toLowerCase());

            if (gameType == null) {
                sender.sendMessage("§cJogo inválido.");
                return true;
            }

            if (type == null) {
                sender.sendMessage("§cTipo de booster inválido.");
                return true;
            }

            double multiple = Double.parseDouble(args[4]);

            int timeType = 0;

            if (args[5].endsWith("h")) {
                timeType = 1;
            } else if (args[5].endsWith("d")) {
                timeType = 2;
            }

            if (timeType != 1 && timeType != 2) {
                sender.sendMessage("§cTipo de tempo inválido, utilize 'h' no final para horas ou 'd' no final para dias.");
                return true;
            }

            int time = Integer.parseInt(args[5].split(timeType == 1 ? "h" : "d")[0]);

            GameBooster booster = new GameBooster(
                    gameType,
                    type,
                    BoosterStatus.INACTIVE,
                    multiple,
                    new BoosterDuration(
                            (timeType == 1 ? BoosterDurationType.HOURS : BoosterDurationType.DAYS),
                            time,
                            -1L
                    )
            );

            if (lp.getBoosters() == null) {
                lp.setBoosters(new ArrayList<>());
            }
            lp.addBooster(booster);

            Core.getDataPlayer().update(lp);
            sender.sendMessage("§aBooster adicionado com sucesso!");
        }
        return false;
    }
}
