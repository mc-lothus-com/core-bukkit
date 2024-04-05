package com.lothus.bukkit.commands.register.player.medal;

import com.lothus.bukkit.commands.CommandBase;
import com.lothus.core.Core;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.player.medal.Medal;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MedalCommand extends CommandBase {

    public MedalCommand() {
        super(
                "medal",
                "",
                "medalha"
        );
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player))return true;

        Player player = (Player) sender;
        LothPlayer lothPlayer = Core.getPlayerController().get(player.getUniqueId());

        if (args.length == 0) {
            TextComponent message = new TextComponent("§aSuas medalhas: ");
            for (Medal tag : getMedal(player)) {
                TextComponent component = new TextComponent(tag.getColor() + "§l" + tag.getSymbol());
                component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{new TextComponent(tag.getColor() + tag.getDisplay() + "\n§eClique para selecionar.")}));
                component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/medal " + tag.name()));
                message.addExtra(component);
                message.addExtra(tag.equals(Medal.CRUZ_DE_MALTA) ? "§f." : "§f, ");
            }
            player.spigot().sendMessage(message);
        }

        if (args.length > 0) {
            if (!Medal.exists(args[0])) {
                player.sendMessage("§cMedalha não encontrada!");
                return false;
            }
            Medal tag = Medal.get(args[0]);
            if (!getMedal(player).contains(tag)) {
                player.sendMessage("§cVocê não tem permissão para utilizar esta tag!");
                return false;
            }

            lothPlayer.setMedal(tag);

            player.sendMessage("§aMedalha alterada para " + tag.getColor() + tag.getDisplay() + "§a!");
            Core.getDataPlayer().update(lothPlayer);
        }

        return false;
    }

    public static List<Medal> getMedal(Player player) {
        List<Medal> tags = new ArrayList<>();
        LothPlayer lothPlayer = Core.getPlayerController().get(player.getUniqueId());
        for (Medal tag : Medal.values()) {
            if (tag.equals(Medal.NENHUM)) {
                continue;
            }
            if (lothPlayer.getGroup().getRank().ordinal() <= tag.getAvailableRank().ordinal()) {
                tags.add(tag);
                continue;
            }

            if (lothPlayer.getGroup().containsPermission(tag.getPermission())) {
                tags.add(tag);
            }
        }
        return tags;
    }
}
