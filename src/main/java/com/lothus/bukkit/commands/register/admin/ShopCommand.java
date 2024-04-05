package com.lothus.bukkit.commands.register.admin;

import com.lothus.bukkit.commands.CommandBase;
import com.lothus.core.Core;
import com.lothus.core.discord.group.Group;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.player.group.rank.Rank;
import com.lothus.core.storage.redis.channels.RedisChannel;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class ShopCommand extends CommandBase {

    public ShopCommand() {
        super(
                "lsvip"
        );
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (sender instanceof Player)return true;

        if (args.length == 0) {
            sender.sendMessage("§cSintaxe incorreta, utilize '/lsvip [player] [grupo] [tempo]'.");
            return true;
        }

        if (args.length > 0) {
            LothPlayer player = Core.getDataPlayer().get(args[0]);
            if (player == null) {
                sender.sendMessage("§cUsuário não encontrado.");
                return true;
            }

            if (args.length > 1) {
                if (args[1].startsWith("rank.")) {
                    player.getGroup().addPermission(args[1], -1L);
                    Core.getDataPlayer().update(player);

                    sender.sendMessage("§aA permissão do jogador " + args[0] + " foi alterado com sucesso.");
                }

                Rank rank = Rank.getRankByName(args[1]);
                if (rank == null) {
                    sender.sendMessage("§cRank não encontrado.");
                    return true;
                }

                if (args.length > 2) {
                    String time = args[2];

                    int days = 0;
                    if (time.endsWith("d")) {
                        days = Integer.parseInt(time.split("d")[0]);
                    }

                    if (player.hasPermission(rank)) {
                        player.getGroup().addPermission("rank." + rank.name().toLowerCase(), System.currentTimeMillis() + TimeUnit.DAYS.toMillis(days));
                    } else {
                        player.getGroup().setRank(rank);
                        player.getGroup().setTag(rank);

                        player.getGroup().setExpires(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(days));
                        player.getGroup().setLastModified(System.currentTimeMillis());
                    }
                    Core.getDataPlayer().update(player);

                    if (player.getSocial().getDiscord() != -1L) {
                        Group group = new Group(player.getUniqueId(), rank);
                        Core.getRedis().message(RedisChannel.DISCORD_UPDATE_GROUP.name(), Core.getGson().toJson(group));
                    }
                    sender.sendMessage("§aO grupo do jogador " + args[0] + " foi alterado com sucesso.");
                }

            }
        }
        return false;
    }
}
