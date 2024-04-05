package com.lothus.bukkit.events.skin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


@Getter
@AllArgsConstructor
public class PlayerChangeSkinEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    Player player;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
