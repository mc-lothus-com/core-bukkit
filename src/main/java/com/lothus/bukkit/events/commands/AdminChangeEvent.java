package com.lothus.bukkit.events.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


@Getter
@AllArgsConstructor
public class AdminChangeEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    Player player;
    boolean status;
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
