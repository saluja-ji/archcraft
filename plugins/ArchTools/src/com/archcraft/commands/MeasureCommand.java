package com.archcraft.commands;

import com.archcraft.Main;
import com.archcraft.tools.MeasurementTool;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Command handler for the measuring tool
 */
public class MeasureCommand implements CommandExecutor {
    
    private final Main plugin;
    
    public MeasureCommand(Main plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be executed by a player.");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("archtools.measure")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        
        // Give the player a measurement tool (golden axe)
        ItemStack measureTool = new ItemStack(Material.GOLDEN_AXE);
        ItemMeta meta = measureTool.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "Architectural Measuring Tool");
            
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.YELLOW + "Left-click to set point A");
            lore.add(ChatColor.YELLOW + "Right-click to set point B and measure");
            lore.add(ChatColor.GRAY + "Scale: 1 block = " + 
                    plugin.getConfigManager().getScale() + " meters");
            
            meta.setLore(lore);
            measureTool.setItemMeta(meta);
        }
        
        player.getInventory().addItem(measureTool);
        
        player.sendMessage(ChatColor.GREEN + "You have been given an architectural measuring tool.");
        player.sendMessage(ChatColor.YELLOW + "Left-click to set point A, right-click to set point B and measure.");
        
        // Register this player with the measurement tool
        MeasurementTool.registerPlayer(player);
        
        return true;
    }
}
