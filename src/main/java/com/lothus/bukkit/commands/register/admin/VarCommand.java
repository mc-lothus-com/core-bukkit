package com.lothus.bukkit.commands.register.admin;

import com.lothus.bukkit.BukkitCore;
import com.lothus.bukkit.commands.CommandBase;
import com.lothus.bukkit.events.variable.VariableEvent;
import com.lothus.bukkit.events.variable.reason.VariableReason;
import com.lothus.core.Core;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.player.group.rank.Rank;
import com.lothus.core.servers.ServerInfo;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

public class VarCommand extends CommandBase {

    public VarCommand() {
        super(
                "var"
        );
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        LothPlayer lothPlayer = Core.getPlayerController().get(player.getUniqueId());

        if (lothPlayer.getGroup().getRank() != Rank.CEO) {
            if (!lothPlayer.getGroup().containsPermission("command.var")) {
                player.sendMessage(NO_PERMISSION);
                return true;
            }
        }

        if (args.length == 0) {
            player.sendMessage("§cSintaxe incorreta, utilize '/var [variable]' para continuar.");
            return true;
        }

        ServerInfo serverInfo = Core.getServerInfo();

        if (args[0].equalsIgnoreCase("permissions")) {
            if (!(args.length > 1)) {
                player.sendMessage("§cSintaxe incorreta, utilize '/var permissions [add/rem/list]' para continuar.");
                return false;
            }


            if (args[1].equalsIgnoreCase("add")) {
                if (!(args.length > 3)) {
                    player.sendMessage("§cSintaxe incorreta, utilize '/var permissions add [rank] [permission]' para continuar.");
                    return true;
                }

                Rank rank = Rank.getRankByName(args[2]);

                if (rank == null) {
                    player.sendMessage("§cO rank inserido não existe.");
                    return true;
                }


                String permission = args[3];

                if (serverInfo.getConfiguration().getPermissions().hasPermission(rank, permission)) {
                    player.sendMessage("§cA permissão inserida já existe.");
                    return true;
                }

                serverInfo.getConfiguration().getPermissions().addPermission(rank, permission);

                player.sendMessage("§aPermissão adicionada com sucesso.");

                for (Player online : Bukkit.getOnlinePlayers()) {
                    LothPlayer lothOnline = Core.getPlayerController().get(online.getUniqueId());
                    if (lothOnline.hasPermission(rank)) {
                        PermissionAttachment attachment = online.addAttachment(BukkitCore.getInstance());
                        attachment.setPermission(permission, true);
                        online.recalculatePermissions();
                    }
                }

                Core.getDataServer().update(serverInfo);
                return true;
            }

            if (args[1].equalsIgnoreCase("rem")) {
                if (!(args.length > 3)) {
                    player.sendMessage("§cSintaxe incorreta, utilize '/var permissions rem [rank] [permission]' para continuar.");
                    return true;
                }

                Rank rank = Rank.getRankByName(args[2]);

                if (rank == null) {
                    player.sendMessage("§cO rank inserido não existe.");
                    return true;
                }

                if (serverInfo.getConfiguration().getPermissions().findAllByRank(rank) == null) {
                    player.sendMessage("§cO rank inserido não possui nenhuma permissão.");
                    return true;
                }

                String permission = args[3];

                if (!serverInfo.getConfiguration().getPermissions().hasPermission(rank, permission)) {
                    player.sendMessage("§cA permissão inserida não existe.");
                    return true;
                }

                serverInfo.getConfiguration().getPermissions().removePermission(rank, permission);

                player.sendMessage("§aPermissão removida com sucesso.");

                for (Player online : Bukkit.getOnlinePlayers()) {
                    LothPlayer lothOnline = Core.getPlayerController().get(online.getUniqueId());
                    if (lothOnline.hasPermission(rank)) {
                        PermissionAttachment attachment = online.addAttachment(BukkitCore.getInstance());
                        attachment.setPermission(permission, false);
                        online.recalculatePermissions();
                    }
                }

                Core.getDataServer().update(serverInfo);
                return true;
            }

            if (args[1].equalsIgnoreCase("list")) {
                if (!(args.length > 2)) {
                    player.sendMessage("§cSintaxe incorreta, utilize '/var permissions list [rank]' para continuar.");
                    return true;
                }

                Rank rank = Rank.getRankByName(args[2]);

                if (rank == null) {
                    player.sendMessage("§cO rank inserido não existe.");
                    return true;
                }

                if (serverInfo.getConfiguration().getPermissions().findAllByRank(rank) == null) {
                    player.sendMessage("§cO rank inserido não possui nenhuma permissão.");
                    return true;
                }

                TextComponent component = new TextComponent("§aPermissões do rank §f" + rank.getName() + "§a: ");
                {
                    for (String permission : serverInfo.getConfiguration().getPermissions().findAllByRank(rank)) {
                        TextComponent textComponent = new TextComponent("\n§2" + permission);
                        textComponent.setHoverEvent(new TextComponent("§aClique para remover a permissão.").getHoverEvent());
                        textComponent.setClickEvent(new TextComponent("/var permissions rem " + rank.getName() + " " + permission).getClickEvent());
                        component.addExtra(textComponent);
                    }
                }
                player.sendMessage(component);
                return true;
            }
        }

        if (args[0].equalsIgnoreCase("maxplayers")) {
            if (args.length > 1) {
                if (isInteger(args[1])) {
                    serverInfo.getConfiguration().setMaxPlayers(Integer.parseInt(args[1]));
                    player.sendMessage("§aVocê alterou o valor de §fmaxplayers §apara §f" + args[1] + "§a.");
                    Bukkit.getPluginManager().callEvent(new VariableEvent(VariableReason.MAX_PLAYERS));
                } else {
                    player.sendMessage("§cO valor inserido não é um número.");
                    return false;
                }
            }
        } else if (args[0].startsWith("scoreboard_")) {
            if (args.length > 1) {
                if (args[0].endsWith("_title")) {
                    serverInfo.getConfiguration().setScoreboardTitle(args[1].replace("&", "§"));
                    player.sendMessage("§aVocê alterou o valor da variável '" + args[0] + "'.");
                    Bukkit.getPluginManager().callEvent(new VariableEvent(VariableReason.SCOREBOARD_TITLE));
                } else if (args[0].endsWith("_footer")) {
                    serverInfo.getConfiguration().setScoreboardFooter(args[1].replace("&", "§"));
                    player.sendMessage("§aVocê alterou o valor da variável '" + args[0] + "'.");
                    Bukkit.getPluginManager().callEvent(new VariableEvent(VariableReason.SCOREBOARD_FOOTER));
                } else {
                    player.sendMessage("§cSintaxe incorreta, utilize '/var scoreboard_[title/footer] [value]' para continuar.");
                    return true;
                }
            }
        } else if (args[0].equalsIgnoreCase("pvp")) {
            if (args.length > 1) {
                if (isBool(args[1])) {
                    serverInfo.getConfiguration().setPvp(Boolean.parseBoolean(args[1]));
                    player.sendMessage("§aVocê alterou o valor da variável '" + args[0] + "'.");
                    Bukkit.getPluginManager().callEvent(new VariableEvent(VariableReason.PVP));
                } else {
                    player.sendMessage("§cO valor inserido não é um booleano.");
                    return false;
                }
            }
        } else if (args[0].equalsIgnoreCase("chat")) {
            if (args.length > 1) {
                if (isBool(args[1])) {
                    serverInfo.getConfiguration().setChat(Boolean.parseBoolean(args[1]));
                    player.sendMessage("§aVocê alterou o valor da variável '" + args[0] + "'.");
                } else {
                    player.sendMessage("§cO valor inserido não é um booleano.");
                    return false;
                }
            }
        }

        Core.getDataServer().update(
                serverInfo
        );
        return false;
    }
}
