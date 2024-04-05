package com.lothus.bukkit.commands.register.player.div;

import com.lothus.bukkit.commands.CommandBase;
import com.lothus.core.Core;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.player.group.rank.Rank;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class DivCommand extends CommandBase {

    private HashMap<UUID, Long> cooldown = new HashMap<>();
    public DivCommand() {
        super(
                "divulgar",
                "live",
                "dv","live"
        );
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player))return true;

        Player player = (Player) sender;
        LothPlayer lothPlayer = Core.getPlayerController().get(player.getUniqueId());

        if (!(lothPlayer.getGroup().getRank().ordinal() <= Rank.MIDIA.ordinal())) {
            if (!lothPlayer.getGroup().containsPermission("command.divulgar")) {
                player.sendMessage(NO_PERMISSION);
                return true;
            }
        }

        if (cooldown.get(player.getUniqueId()) != null && cooldown.get(player.getUniqueId()) > System.currentTimeMillis()) {
            player.sendMessage("§cVocê deve aguardar para executar este comando novamente.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§cSintaxe incorreta, utilize '/divulgar [link]' para continuar.");
            return true;
        }

        if (args.length > 0) {
            String link = args[0];

            if (!link.matches("\\b(?:https?:\\/\\/(?:www\\.)?)?(?:youtube\\.com|youtu\\.be|twitch\\.tv|tiktok\\.com)\\/\\S+\\b")) {
                player.sendMessage("§cVocê só pode inserir links do YouTube, Tiktok ou Twitch.");
                return true;
            }

            cooldown.put(player.getUniqueId(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(60));

            TextComponent accept = new TextComponent("§eClique ");
            TextComponent here = new TextComponent("§6§lAQUI");
            TextComponent toAccept = new TextComponent("§e para assistir.");

            here.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, args[0]));
            here.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Clique para assistir.").create()));

            accept.addExtra(here);
            accept.addExtra(toAccept);

            for (Player a : Bukkit.getOnlinePlayers()) {
                a.sendMessage("");
                a.sendMessage("§6§lALERTA!");
                a.sendMessage(lothPlayer.getGroup().getTag().getColor() + player.getName() + " §eestá divulgando uma live!");
                a.sendMessage(accept);
                a.sendMessage("");
            }
            return true;
        }

        return false;
    }
}
