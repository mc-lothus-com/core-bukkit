package com.lothus.bukkit.commands.register.player.ping;

import com.lothus.bukkit.commands.CommandBase;
import com.lothus.core.Core;
import com.lothus.core.player.LothPlayer;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Random;

public class PingCommand extends CommandBase {

    public PingCommand() {
        super(
                "ping"
        );
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        if (args.length == 0) {
            CraftPlayer cp = (CraftPlayer)player;
            EntityPlayer ep = cp.getHandle();

            int ping = ep.ping;
            if (ping > 999) {
                ping = 999;
            }

            if (ping > 7 && ping < 13) {
                ping = new Random().nextBoolean() ? 3 : 4;
            } else if (ping > 13 && ping < 20) {
                ping = new Random().nextBoolean() ? 10 : 11;
            }
            player.sendMessage("§eSua latência: §b" + ping + "ms");
            return true;
        } else if (args.length == 1) {
            Player p1 = Bukkit.getPlayer(args[0]);
            if (p1 == null) {
                player.sendMessage("§cJogador não encontrado.");
                return true;
            }

            CraftPlayer cp = (CraftPlayer)p1;
            EntityPlayer ep = cp.getHandle();

            int ping = ep.ping;
            if (ping > 999) {
                ping = 999;
            }

            if (ping > 7 && ping < 13) {
                ping = new Random().nextBoolean() ? 3 : 4;
            } else if (ping > 13 && ping < 20) {
                ping = new Random().nextBoolean() ? 10 : 11;
            }
            player.sendMessage("§eLatência de §b" + p1.getName() + "§e: §b" + ping + "ms");
        }
        return false;
    }
}
