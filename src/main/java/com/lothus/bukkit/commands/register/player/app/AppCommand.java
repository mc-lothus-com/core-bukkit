package com.lothus.bukkit.commands.register.player.app;

import com.lothus.bukkit.commands.CommandBase;
import com.lothus.core.Core;
import com.lothus.core.app.AccountAPP;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AppCommand extends CommandBase {

    public AppCommand() {
        super("app", "", "registrarapp", "registerapp");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player))return true;

        Player player = (Player) sender;
        AccountAPP accountAPP;

        if (args.length == 0) {
            player.sendMessage("§cSintaxe incorreta, utilize '/app [senha]' para registrar.");
            return true;
        }

        if (args.length > 0) {
            String password = args[0];

            if (Core.getDataAccountAPP().get(player.getName()) != null) {
                player.sendMessage(" §cVocê já possui uma conta no aplicativo.");
                return true;
            }

            player.sendMessage("");
            player.sendMessage("§eA sua conta foi criada com sucesso.");
            player.sendMessage("§eAcesse sua conta utilizando seu §bnickname §ee sua §bsenha§e.");
            player.sendMessage("");
            accountAPP = new AccountAPP(player.getName(), password);
            Core.getDataAccountAPP().create(accountAPP);
        }
        return false;
    }
}
