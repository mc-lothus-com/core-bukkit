package com.lothus.bukkit.listeners;

import com.lothus.bukkit.BukkitCore;
import com.lothus.core.Core;
import com.lothus.core.api.tag.TagManager;
import com.lothus.bukkit.events.chat.CoreChatEvent;
import com.lothus.bukkit.events.variable.VariableEvent;
import com.lothus.core.event.update.UpdateEvent;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.player.booster.GameBooster;
import com.lothus.core.player.booster.status.BoosterStatus;
import com.lothus.core.player.group.perm.Permission;
import com.lothus.core.player.group.rank.Rank;
import com.lothus.core.player.medal.Medal;
import com.lothus.core.player.skin.Skin;
import com.lothus.core.punish.PunishesInfo;
import com.lothus.core.servers.ServerInfo;
import com.lothus.core.servers.configuration.permissions.ServerPermissions;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.permissions.PermissionAttachment;

import java.io.File;
import java.util.UUID;

import static com.lothus.bukkit.events.variable.reason.VariableReason.MAX_PLAYERS;
import static com.lothus.bukkit.events.variable.reason.VariableReason.PVP;

public class BukkitListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        LothPlayer lothPlayer = Core.getDataPlayer().get(event.getUniqueId());

        if (lothPlayer == null) {
            event.disallow(PlayerPreLoginEvent.Result.KICK_FULL, "§cNão foi possível carregar sua conta.");
            return;
        }

        if (!(lothPlayer.getGroup().getRank().ordinal() <= Rank.TRIAL.ordinal())) {
            lothPlayer.getPrefs().setVanish(false);
        }

        if (!(lothPlayer.getGroup().getRank().ordinal() <= Rank.VIP.ordinal())) {
            lothPlayer.getPrefs().setFly(false);
        }

        if (lothPlayer.getSkin() == null) {
            lothPlayer.setSkin(new Skin(event.getName(), event.getUniqueId(), "", ""));
        }

        if (lothPlayer.getGroup().getRank() == null) {
            lothPlayer.getGroup().setRank(Rank.MEMBRO);
        }

        if (lothPlayer.getGroup().getTag() == null) {
            lothPlayer.getGroup().setTag(lothPlayer.getGroup().getRank());
        }

        if (Core.getServerInfo().getConfiguration().getPermissions() == null) {
            Core.getServerInfo().getConfiguration().setPermissions(new ServerPermissions());
            Core.getDataServer().update(Core.getServerInfo());
        }

        Core.getPlayerController().load(lothPlayer);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        LothPlayer lothPlayer = Core.getPlayerController().get(player.getUniqueId());

        for (Rank r : Rank.values()) {
            if (!lothPlayer.hasPermission(r)) continue;
            if (Core.getServerInfo().getConfiguration().getPermissions().findAllByRank(r) == null) continue;
            for (String permission : Core.getServerInfo().getConfiguration().getPermissions().findAllByRank(lothPlayer.getGroup().getRank())) {
                if (player.hasPermission(permission)) continue;

                PermissionAttachment attachment = player.addAttachment(BukkitCore.getInstance());
                attachment.setPermission(permission, true);
                player.recalculatePermissions();
            }
        }


        if (lothPlayer.hasPermission("lothus.bypass")) {
            player.setOp(true);
        }

        try {
            if (lothPlayer.getMedal() == null) {
                lothPlayer.setMedal(Medal.NENHUM);
            }
            TagManager.setTag(player, lothPlayer.getGroup().getTag());

            Core.getDataPlayer().update(lothPlayer);
            event.setJoinMessage(null);
        } catch (Exception e) {
            player.kickPlayer("§cOcorreu um erro ao conectar-se.");
            return;
        }
    }

    @EventHandler
    public void onVariable(VariableEvent event) {
        if (event.getReason() == PVP) {
            Bukkit.getServer().getWorlds().forEach(world -> world.setPVP(Core.getServerInfo().getConfiguration().isPvp()));
            Bukkit.getServer().spigot().getSpigotConfig().set("pvp", Core.getServerInfo().getConfiguration().isPvp());
        } else if (event.getReason() == MAX_PLAYERS) {
            File f = new File("/home/container/server.properties");
            YamlConfiguration config = Bukkit.getServer().spigot().getSpigotConfig();
            config.set("max-players", Core.getServerInfo().getConfiguration().getMaxPlayers());
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onUpdate(UpdateEvent event) {
        LothPlayer player = Core.getPlayerController().get(event.getPlayer().getUniqueId());
        ServerInfo serverInfo = Core.getServerInfo();

        serverInfo.setPlayers(Bukkit.getOnlinePlayers().size());

        if (player == null) {
            return;
        }

        if (player.getBoosters() != null) {
            for (GameBooster booster : player.getBoosters()) {
                if (booster.getStatus() == BoosterStatus.ACTIVE) {
                    if (booster.getDuration().getExpires() == 0L)continue;
                    if (booster.getDuration().getExpires() == -1L)continue;
                    if (booster.getDuration().getExpires() <= System.currentTimeMillis()) {
                        event.getPlayer().sendMessage("§cO booster de " + booster.getType().getName() + " " + booster.getGameType().getName() + " §cexpirou.");
                        booster.setStatus(BoosterStatus.EXPIRED);
                    }
                }
            }

            player.getBoosters().removeIf(booster -> booster.getStatus() == BoosterStatus.EXPIRED);
            Core.getDataPlayer().update(player);
        }

        if (!Core.getServerInfo().getType().name().startsWith("ROOM_")) {
            if (player.getSocial().getFake() != null && !player.getSocial().getFake().getName().equalsIgnoreCase(player.getName())) {
                TagManager.setTag(event.getPlayer(), player.getSocial().getFake().getRank());
            } else {
                if (player.getMedal() == null) {
                    player.setMedal(Medal.NENHUM);
                }
                TagManager.setTag(event.getPlayer(), player.getGroup().getTag());
            }
        }

        if (player.getGroup().getExpires() != -1L) {
            if (player.getGroup().getExpires() <= System.currentTimeMillis()) {
                event.getPlayer().sendMessage("§aO rank " + player.getGroup().getRank().getColor() + player.getGroup().getRank().getName() + " §aexpirou.");
                player.getGroup().setRank(Rank.MEMBRO);
                player.getGroup().setTag(Rank.MEMBRO);
                player.getGroup().setExpires(-1L);
                player.getGroup().setLastModified(System.currentTimeMillis());
                Core.getDataPlayer().update(player);
            }
        }

        if (!player.getGroup().getPermissions().isEmpty()) {
            for (Permission permission : player.getGroup().getPermissions()) {
                if (permission.getExpires() != -1L) {
                    if (permission.getExpires() <= System.currentTimeMillis()) {
                        player.getGroup().getPermissions().remove(permission);
                        event.getPlayer().sendMessage("§aA permissão §f" + permission.getPermission() + " §aexpirou.");
                        Core.getDataPlayer().update(player);
                        return;
                    }
                }
            }
        }

        for (PunishesInfo p : player.getPunishes().values()) {
            if (p.getReason() == null)continue;
            if (!((p.getReason().getTimeInDays()) == 99999)) {
                if (p.getExpires() <= System.currentTimeMillis()) {
                    p.setExpired(true);
                    player.getPunishes().replace(p.getId(), p);
                    if (Core.getPlayerController().get(player.getUniqueId()) != null) {
                        Core.getPlayerController().replace(player);
                    }
                    Core.getDataPlayer().update(player);
                }
            }
        }
    }


    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        Bukkit.getPluginManager().callEvent(new CoreChatEvent(event.getPlayer(), Core.getPlayerController().get(event.getPlayer().getUniqueId()), event.getMessage()));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Core.getPlayerController().unload(player.getUniqueId());
        event.setQuitMessage(null);
    }

    private LothPlayer get(UUID uniqueId) {
        return Core.getGson().fromJson(Core.getRedis().get("player:" + uniqueId.toString()), LothPlayer.class);
    }

}
