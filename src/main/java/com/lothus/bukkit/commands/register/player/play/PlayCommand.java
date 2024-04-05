package com.lothus.bukkit.commands.register.player.play;

import com.lothus.bukkit.commands.CommandBase;
import com.lothus.core.Core;
import com.lothus.core.games.GameInfo;
import com.lothus.core.games.room.RoomType;
import com.lothus.core.games.state.GameState;
import com.lothus.core.games.type.GameType;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.player.group.rank.Rank;
import com.lothus.core.player.party.Party;
import com.lothus.core.servers.ServerInfo;
import com.lothus.core.servers.type.ServerType;
import com.lothus.core.utils.bukkit.player.PlayerUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.lothus.core.games.state.GameState.INICIANDO;
import static com.lothus.core.servers.status.ServerStatus.MAINTENANCE_MODE;

public class PlayCommand extends CommandBase {

    private HashMap<UUID, Long> c = new HashMap<>();

    public PlayCommand() {
        super("play");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage("§cSintaxe incorreta, utilize '/play [swsolo]'");
            return true;
        }

        if (args.length > 0) {
            GameInfo gameInfo = game(player, (args[0].startsWith("sw") ? GameType.SKY_WARS : GameType.BED_WARS), (args[0].endsWith("solo") ? RoomType.SOLO : args[0].endsWith("team") ? RoomType.DUPLAS : args[0].endsWith("trio") ? RoomType.TRIOS : args[0].endsWith("quarteto") ? RoomType.QUARTETOS : RoomType.RANQUEADO));

            if (gameInfo == null) {
                player.sendMessage("§cNossas salas estão indisponíveis no momento.");
                return true;
            }

            ServerInfo serverInfo = Core.getServerController().get(gameInfo.getName());

            if (serverInfo == null) {
                player.sendMessage("§cO servidor desta arena é igual a nulo.");
                return true;
            }

            LothPlayer lothPlayer = Core.getPlayerController().get(player.getUniqueId());

            if (!(lothPlayer.getGroup().getRank().ordinal() <= Rank.TRIAL.ordinal())) {
                if (!lothPlayer.getGroup().containsPermission("maintenance.bypass")) {
                    if (serverInfo.getType() == ServerType.ROOM_SKYWARS) {
                        if (maintenance(ServerType.LOBBY_SKYWARS)) {
                            player.sendMessage("§cAs salas de Sky Wars estão indisponíveis no momento.");
                            return true;
                        }
                    } else if (serverInfo.getType() == ServerType.ROOM_BEDWARS) {
                        if (maintenance(ServerType.LOBBY_BEDWARS)) {
                            player.sendMessage("§cAs salas de Bed Wars estão indisponíveis no momento.");
                            return true;
                        }
                    }
                }
            }

            if (c.get(player.getUniqueId()) != null && c.get(player.getUniqueId()) > System.currentTimeMillis()) {
                player.sendMessage("§cVocê deve aguardar para conectar-se novamente.");
                return true;
            }

            Party party = Core.getDataParty().get(player.getUniqueId());

            if (party != null) {
                if (!party.isLeader(player.getUniqueId())) {
                    player.sendMessage("§cApenas o líder do grupo pode entrar em uma partida.");
                    return true;
                }

                if (party.size() > gameInfo.getMaxPlayers()) {
                    player.sendMessage("§cO grupo não pode entrar nessa partida, pois excede o limite de jogadores.");
                    return true;
                }

                if (party.size() > (gameInfo.getMaxPlayers() - gameInfo.getPlayers())) {
                    player.sendMessage("§cO grupo não pode entrar nessa partida, pois excede o limite de jogadores.");
                    return true;
                }
            }

            c.put(player.getUniqueId(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(5));

            switch (PlayerUtil.connect(player.getUniqueId(), serverInfo)) {
                case SERVER_NULL:
                    player.sendMessage("§cO servidor solicitado é inválido.");
                    break;
                case SERVER_MAINTENANCE:
                    player.sendMessage("§cO servidor solicitado está em manutenção.");
                    break;
                case SERVER_FULL_AND_ROOM:
                    player.sendMessage("§cA sala solicitada está cheia.");
                    break;
                case SERVER_FULL:
                    player.sendMessage("§cO servidor solicitado está cheio.");
                    break;
                case PLAYER_COOLDOWN:
                    player.sendMessage("§cVocê deve aguardar para conectar-se novamente.");
                    break;
            }
            return true;
        }

        return false;
    }

    public boolean maintenance(ServerType serverType) {
        List<ServerInfo> ls = Core.getServerController().get(serverType);
        for (ServerInfo s : ls) {
            if (s.getStatus().equals(MAINTENANCE_MODE)) {
                return true;
            }
        }
        return false;
    }


    public GameInfo game(Player player, GameType gameType, RoomType roomType) {
        Party party = Core.getDataParty().get(player.getUniqueId());
        for (GameInfo gameInfo : Core.getGameController().getAll()) {
            if (gameInfo.getType().equals(gameType)) {
                if (gameInfo.getRoomType().equals(roomType)) {
                    if (gameInfo.getName().equalsIgnoreCase(Core.getServerInfo().getName()))continue;
                    if (gameInfo.getPlayers() >= gameInfo.getMaxPlayers()) continue;
                    if (gameInfo.getState() != GameState.ESPERANDO && gameInfo.getState() != INICIANDO) continue;
                    if (party != null) {
                        if (!party.isLeader(player.getUniqueId())) {
                            continue;
                        }

                        if (gameInfo.getMaxPlayers() < party.size()) {
                            continue;
                        }

                        if ((gameInfo.getMaxPlayers() - gameInfo.getPlayers()) < party.size()) {
                            continue;
                        }
                    }
                    return gameInfo;
                }
            }
        }
        return null;
    }
}
