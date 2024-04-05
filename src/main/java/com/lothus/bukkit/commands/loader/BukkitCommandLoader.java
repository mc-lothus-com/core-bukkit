package com.lothus.bukkit.commands.loader;

import com.lothus.bukkit.commands.CommandBase;
import com.lothus.core.Core;
import com.lothus.core.utils.bukkit.classes.ClassGetter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

public class BukkitCommandLoader {

    @SuppressWarnings("deprecation")
    public static void loadCommands(JavaPlugin instance, String packageName) {
        for (Class<?> commandClass : ClassGetter.getClassesForPackage(instance, packageName)) {
            if (CommandBase.class.isAssignableFrom(commandClass)) {
                try {
                    CommandBase commands = (CommandBase) commandClass.newInstance();
                    register(commands.getName(), commands);
                } catch (Exception e) {
                    Core.getLogger()
                            .warning("Erro ao carregar comandos da classe " + commandClass.getSimpleName() + "!");
                }
            }
        }
    }

    private static void register(String fallback, Command command) {
        try {
            Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
            commandMap.register(fallback, command);
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
    }

}
