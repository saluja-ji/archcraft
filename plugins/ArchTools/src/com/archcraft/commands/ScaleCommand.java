package com.archcraft.commands;

import com.archcraft.Main;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Command handler for the scale setting
 * Allows users to define the scale of the world (1 block = X meters)
 */
public class ScaleCommand implements CommandExecutor, TabCompleter {
    
    private final Main plugin;
    private final List<String> commonScales = Arrays.asList(
            "0.5", "1", "2", "5", "10"
    );
    
    public ScaleCommand(Main plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("archtools.scale")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        
        // If no arguments, show the current scale
        if (args.length == 0) {
            double currentScale = plugin.getConfigManager().getScale();
            sender.sendMessage(ChatColor.GREEN + "Current scale: " + ChatColor.GOLD + 
                    "1 block = " + currentScale + " meters");
            return true;
        }
        
        // If an argument is provided, try to set the scale
        try {
            double scale = Double.parseDouble(args[0]);
            
            if (scale <= 0) {
                sender.sendMessage(ChatColor.RED + "Scale must be a positive number.");
                return true;
            }
            
            plugin.getConfigManager().setScale(scale);
            
            sender.sendMessage(ChatColor.GREEN + "Scale set to: " + ChatColor.GOLD + 
                    "1 block = " + scale + " meters");
            
            // Broadcast scale change to all players
            if (sender instanceof Player) {
                plugin.getServer().broadcastMessage(
                        ChatColor.DARK_AQUA + "[ArchCraft] " + ChatColor.AQUA + 
                        "Project scale updated to " + scale + " meters per block by " + 
                        ((Player) sender).getDisplayName());
            }
            
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Invalid number format. Please enter a valid number.");
        }
        
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            for (String scale : commonScales) {
                if (scale.startsWith(partial)) {
                    completions.add(scale);
                }
            }
        }
        
        return completions;
    }
}
