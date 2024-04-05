package com.lothus.bukkit.redis;

import com.lothus.core.Core;
import com.lothus.core.games.GameInfo;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.player.rejoin.Rejoin;
import com.lothus.core.servers.ServerInfo;
import com.lothus.core.storage.redis.channels.RedisChannel;
import io.lettuce.core.pubsub.RedisPubSubAdapter;

public class BukkitListener extends RedisPubSubAdapter<String,String> {

    @Override
    public void message(String channel, String message) {
        Rejoin rejoin;
        GameInfo gameInfo;
        ServerInfo serverInfo;
        LothPlayer lothPlayer;
        RedisChannel redisChannel = RedisChannel.valueOf(channel);

        switch (redisChannel) {
            case PLAYER_ACCOUNT_UPDATE:
                lothPlayer = Core.getGson().fromJson(message, LothPlayer.class);

                if (lothPlayer == null) return;

                Core.getPlayerController().replace(lothPlayer);
                break;
            case SERVER_START:
                serverInfo = Core.getGson().fromJson(message, ServerInfo.class);

                if (serverInfo == null) return;
                Core.getServerController().load(serverInfo);
                break;
            case SERVER_UPDATE:
                serverInfo = Core.getGson().fromJson(message, ServerInfo.class);

                if (serverInfo == null) return;
                Core.getServerController().update(serverInfo);
                break;
            case SERVER_STOP:
                serverInfo = Core.getGson().fromJson(message, ServerInfo.class);

                if (serverInfo == null) return;
                Core.getServerController().unload(serverInfo.getName());
                break;
            case GAME_START:
                gameInfo = Core.getGson().fromJson(message, GameInfo.class);

                if (gameInfo == null) return;
                Core.getGameController().load(gameInfo);
                break;
            case GAME_UPDATE:
                gameInfo = Core.getGson().fromJson(message, GameInfo.class);

                if (gameInfo == null) return;
                Core.getGameController().update(gameInfo);
                break;
            case GAME_STOP:
                gameInfo = Core.getGson().fromJson(message, GameInfo.class);

                if (gameInfo == null) return;
                Core.getGameController().unload(gameInfo.getId());
                break;
            case MAINTENANCE:
                serverInfo = Core.getGson().fromJson(message, ServerInfo.class);

                if (serverInfo == null) return;

                Core.getServerController().unload(serverInfo.getName());
                Core.getServerController().load(serverInfo);
                break;
            case REJOIN:
                rejoin = Core.getGson().fromJson(message, Rejoin.class);

                if (rejoin == null)return;

                Core.getRejoinController().load(rejoin);
                break;
        }
    }
}