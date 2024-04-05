package com.lothus.bukkit.commands.register.player.tag;

import com.lothus.core.Core;
import com.lothus.core.api.tag.TagManager;
import com.lothus.bukkit.commands.CommandBase;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.player.group.rank.Rank;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TagCommand extends CommandBase {

    public TagCommand() {
        super(
                "tag",
                "",
                "tags");
    }

    @Override
    public boolean execute(CommandSender commandSender, String label, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("§cApenas jogadores podem utilizar este comando!");
            return false;
        }
        Player player = (Player) commandSender;
        LothPlayer hyzePlayer = Core.getPlayerController().get(player.getUniqueId());
        if (args.length == 0) {
            TextComponent message = new TextComponent("§aSuas tags: ");
            for (Rank tag : getTags(player)) {
                TextComponent component = new TextComponent(tag.getColor() + tag.getName());
                component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{new TextComponent(
                        "§fPré-visualização: " + (tag == Rank.MEMBRO ? tag.getColor() : tag.getColor() + "§l" +
                                tag.getName().toUpperCase() + tag.getColor() + " " + hyzePlayer.getName() + "\n" +
                                "§eClique para selecionar."))}));
                component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tag " + tag.name()));
                message.addExtra(component);
                message.addExtra(tag.equals(Rank.MEMBRO) ? "§f." : "§f, ");
            }
            player.spigot().sendMessage(message);
        }
        if (args.length > 0) {
            if (Core.getServerInfo().getType().name().startsWith("ROOM_")) {
                player.sendMessage("§cVocê não pode alterar sua tag enquanto estiver em uma sala!");
                return false;
            }

            if (!Rank.exists(args[0])) {
                player.sendMessage("§cTag não encontrada!");
                return false;
            }

            Rank tag = Rank.getRankByName(args[0]);
            if (!getTags(player).contains(tag)) {
                player.sendMessage("§cVocê não tem permissão para utilizar esta tag!");
                return false;
            }

            if (hyzePlayer.getSocial().getFake() == null || hyzePlayer.getSocial().getFake().getName().equalsIgnoreCase(hyzePlayer.getName())) {
                hyzePlayer.getGroup().setTag(tag);
            } else  {
                hyzePlayer.getSocial().getFake().setRank(tag);
            }

            TagManager.setTag(player, tag);
            player.sendMessage("§aTag alterada para " + tag.getColor() + tag.getName() + "§a!");
            Core.getDataPlayer().update(hyzePlayer);
        }
        return false;
    }

    public static List<Rank> getTags(Player player) {
        List<Rank> tags = new ArrayList<>();
        LothPlayer lothPlayer = Core.getPlayerController().get(player.getUniqueId());
        for (Rank tag : Rank.values()) {
            if (tag.equals(Rank.MEMBRO)) {
                continue;
            }

            if (tag.isOnlyPermission()) {
                if (lothPlayer.getGroup().getRank() == tag) {
                    tags.add(tag);
                    continue;
                }
                if (lothPlayer.getGroup().containsPermission("rank." + tag.name().toLowerCase())) {
                    tags.add(tag);
                    continue;
                }
                continue;
            }

            if (lothPlayer.hasPermission(tag)) {
                tags.add(tag);
                continue;
            }

            if (lothPlayer.getGroup().containsPermission("rank." + tag.name().toLowerCase())) {
                tags.add(tag);
            }
        }
        tags.add(Rank.MEMBRO);
        return tags;
    }
}
