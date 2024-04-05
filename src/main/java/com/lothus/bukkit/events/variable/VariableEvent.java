package com.lothus.bukkit.events.variable;

import com.lothus.bukkit.events.variable.reason.VariableReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@AllArgsConstructor
public class VariableEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    VariableReason reason;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
