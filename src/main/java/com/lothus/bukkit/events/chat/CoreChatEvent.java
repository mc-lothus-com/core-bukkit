package com.lothus.bukkit.events.chat;

import com.lothus.core.player.LothPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@AllArgsConstructor
public class CoreChatEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    Player player;
    LothPlayer account;

    String message;

    boolean cancelled;

    public CoreChatEvent(Player player, LothPlayer account, String message) {
        this.player = player;
        this.account = account;
        this.message = message;
        this.cancelled = false;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = cancelled;
    }
}