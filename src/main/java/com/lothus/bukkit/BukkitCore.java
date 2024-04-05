package com.lothus.bukkit;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.lothus.core.api.hologram.HologramListener;
import com.lothus.core.api.hologram.HologramManager;
import com.lothus.bukkit.commands.loader.BukkitCommandLoader;
import com.lothus.bukkit.listeners.BukkitListener;
import com.lothus.core.Core;
import com.lothus.core.api.loaders.ListenerLoader;
import com.lothus.core.data.app.DataAccountAPP;
import com.lothus.core.data.party.DataParty;
import com.lothus.core.data.player.DataPlayer;
import com.lothus.core.data.server.DataServer;
import com.lothus.core.data.report.DataReport;
import com.lothus.core.event.update.UpdateEvent;
import com.lothus.core.servers.ServerInfo;
import com.lothus.core.servers.type.ServerType;
import com.lothus.core.storage.redis.bukkit.RedisBukkit;
import com.lothus.core.utils.bukkit.commands.UnregisterCommands;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import static com.lothus.core.storage.redis.channels.RedisChannel.*;

public class BukkitCore extends JavaPlugin implements PluginMessageListener {

    @Getter @Setter
    private static BukkitCore instance;

    @Getter @Setter
    private static HologramManager hologramManager;

    @Getter @Setter
    private static ProtocolManager protocolManager;

    @Override
    public void onLoad() {
        setInstance(this);
        saveDefaultConfig();
        Core.setLogger(getLogger());
        Core.setRedis(new RedisBukkit());
        Core.getRedis().start(
                getConfig().getString("redis.host"),
                getConfig().getInt("redis.port"),
                getConfig().getString("redis.password")
        );
        Core.getMongo().start(
                getConfig().getString("mongo.host"),
                getConfig().getInt("mongo.port")
        );
        Core.setDataParty(new DataParty());
        Core.setDataPlayer(new DataPlayer());
        Core.setDataServer(new DataServer());
        Core.setDataReport(new DataReport());
        Core.setDataAccountAPP(new DataAccountAPP());
        setProtocolManager(ProtocolLibrary.getProtocolManager());
    }

    @Override
    public void onEnable() {
        createServerInfo();
        Core.getReportController().loadAll();
        Core.setHologramManager(new HologramManager());
        Bukkit.getPluginManager().registerEvents(new BukkitListener(), this);
        Bukkit.getPluginManager().registerEvents(new HologramListener(), this);
        BukkitCommandLoader.loadCommands(this, "com.lothus.bukkit.commands.register");
        getInstance().getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getInstance().getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
        runAsync();
    }

    @Override
    public void onDisable() {
        Core.getRedis().message(SERVER_STOP.name(), Core.getGson().toJson(Core.getServerInfo()));
        Core.getMongo().stop();
        Core.getRedis().shutdown();
    }


    private void runAsync() {
        Thread thread = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        getServer().getScheduler().runTaskAsynchronously(instance, new Runnable() {
                            @Override
                            public void run() {
                                Core.getRedis().subscribe(new com.lothus.bukkit.redis.BukkitListener(),
                                        SERVER_START.name(),
                                        SERVER_STOP.name(),
                                        SERVER_UPDATE.name(),
                                        PLAYER_ACCOUNT_UPDATE.name(),
                                        GAME_START.name(),
                                        GAME_UPDATE.name(),
                                        GAME_STOP.name(),
                                        MAINTENANCE.name(),
                                        REJOIN.name());
                                Core.getRedis().message(SERVER_START.name(), Core.getGson().toJson(Core.getServerInfo()));

                                getServer().getScheduler().scheduleAsyncRepeatingTask(instance, () -> {
                                    Core.getServerInfo().setPlayers(Bukkit.getOnlinePlayers().size());
                                    Core.getRedis().message(SERVER_UPDATE.name(), Core.getGson().toJson(Core.getServerInfo()));
                                    Bukkit.getOnlinePlayers().forEach(player -> {
                                        Bukkit.getPluginManager().callEvent(new UpdateEvent(player));
                                    });
                                }, 0L, 20L);
                            }
                        });
                        try {
                            getServer().getScheduler().runTaskLater(instance, () -> UnregisterCommands.unregister(
                                    "icanhasbukkit", "?", "about", "help", "ban", "ban-ip", "banlist", "clear", "deop",
                                    "op", "difficulty", "effect", "enchant", "give", "kick", "list", "me",
                                    "scoreboard", "seed", "spawnpoint", "spreadplayers", "summon", "tell", "tellraw", "testfor",
                                    "testforblocks", "weather", "xp", "reload", "rl", "worldborder", "achievement",
                                    "blockdata", "clone", "debug", "defaultgamemode", "entitydata", "execute", "fill", "gamemode",
                                    "pardon", "pardon-ip", "replaceitem", "setidletimeout", "testforblock", "title",
                                    "trigger", "viaver", "ps", "holograms", "hd", "holo", "hologram", "restart", "filter",
                                    "packetlog", "?", "minecraft:gamerule", "minecraft:gm", "minecraft:gr",
                                    "minecraft:kill", "minecraft:pl", "minecraft:plugin", "minecraft:plugins", "minecraft:save-all",
                                    "minecraft:save-off", "minecraft:save-on", "minecraft:setblock", "minecraft:setworldspawn",
                                    "minecraft:time", "ver", "version", "minecraft:timings", "minecraft:stop", "minecraft:toggledownfall",
                                    "minecraft:ver", "minecraft:version", "minecraft:playsound", "playsound", "particle", "minecraft:particle",
                                    "packet", "packet_filter", "pl", "plugin", "plugins", "list", "minecraft:list",
                                    "protocol", "protocollib:packet", "protocollib:packet_filter", "protocollib:protocol",
                                    "viaversion:viaversion", "viaversion", "viaversion:vvbukkit", "vvbukkit",
                                    "timings", "save-on", "save-off", "setblock", "bukkit:timings", "setworldspawn", "say", "minecraft:say",
                                    "dyspigot:setblock", "dyspigot:say", "dyspigot:save-off", "dyspigot:save-all", "dyspigot:setworldspawn",
                                    "dyspigot:stop", "dyspigot:clear", "dyspigot:deop", "op", "dyspigot:op", "dyspigot:give", "dyspigot:kill",
                                    "dyspigot:difficulty", "dyspigot:gamemode", "dyspigot:gamerule", "/calc", "calc"), 3L);
                        } catch (Exception ignore) {}
                    }
                }
        );
        thread.start();
    }

    private void createServerInfo() {
        ServerInfo serverInfo = Core.getDataServer().get(getConfig().getString("server.name"));
        if (serverInfo == null) {
            serverInfo = new ServerInfo(
                    getConfig().getInt("server.id"),
                    getConfig().getString("server.name"),
                    ServerType.getByName(getConfig().getString("server.type")),
                    getServer().getPort()
            );
            Core.getDataServer().create(serverInfo);
        }

        serverInfo.setId(getConfig().getInt("server.id"));
        serverInfo.setType(ServerType.valueOf(getConfig().getString("server.type")));
        serverInfo.setAddress("172.18.0.1");
        serverInfo.setPort(getServer().getPort());
        serverInfo.setPlayers(0);
        serverInfo.getConfiguration().setMaxPlayers(getConfig().getInt("server.maxPlayers"));

        Core.setServerInfo(serverInfo);
        Core.getServerController().load(serverInfo);
        Core.getLogger().info("SERVER_INFO --> As informações servidor foram carregadas com sucesso.");
    }

    public Integer getOnlineCount(ServerType serverType) {
        int players = 0;
        for (ServerInfo serverInfo : Core.getServerController().getAll()) {
            if (serverInfo == null) {
                continue;
            }
            if (serverInfo.getType() == serverType) {
                players += serverInfo.getPlayers();
            }
        }
        return players;
    }

    @Override
    public void onPluginMessageReceived(String s, Player player, byte[] bytes) {

    }
}
