package com.archcraft.commands;

import com.archcraft.Main;
import com.archcraft.models.Zone;
import com.archcraft.tools.ZoningTool;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Command handler for zoning tool
 * Allows users to create and manage zones for urban planning
 */
public class ZoneCommand implements CommandExecutor, TabCompleter {
    
    private final Main plugin;
    private final List<String> subcommands = Arrays.asList(
            "create", "modify", "delete", "list", "info", "tool"
    );
    
    private final List<String> zoneTypes = Arrays.asList(
            "residential", "commercial", "industrial", "recreational", 
            "educational", "transportation", "agricultural", "mixed"
    );
    
    public ZoneCommand(Main plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be executed by a player.");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("archtools.zone")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        
        if (args.length == 0) {
            sendHelpMessage(player);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "create":
                handleCreate(player, args);
                break;
            case "modify":
                handleModify(player, args);
                break;
            case "delete":
                handleDelete(player, args);
                break;
            case "list":
                handleList(player);
                break;
            case "info":
                handleInfo(player, args);
                break;
            case "tool":
                handleTool(player);
                break;
            default:
                sendHelpMessage(player);
                break;
        }
        
        return true;
    }
    
    private void sendHelpMessage(Player player) {
        player.sendMessage(ChatColor.GREEN + "=== ArchCraft Zoning Commands ===");
        player.sendMessage(ChatColor.YELLOW + "/zone create <name> <type> - Create a new zone");
        player.sendMessage(ChatColor.YELLOW + "/zone modify <name> <type> - Change a zone's type");
        player.sendMessage(ChatColor.YELLOW + "/zone delete <name> - Delete a zone");
        player.sendMessage(ChatColor.YELLOW + "/zone list - List all zones");
        player.sendMessage(ChatColor.YELLOW + "/zone info <name> - View zone details");
        player.sendMessage(ChatColor.YELLOW + "/zone tool - Get the zoning tool");
        
        player.sendMessage(ChatColor.GREEN + "Available zone types:");
        player.sendMessage(ChatColor.YELLOW + String.join(", ", zoneTypes));
    }
    
    private void handleCreate(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(ChatColor.RED + "Usage: /zone create <name> <type>");
            return;
        }
        
        String zoneName = args[1];
        String zoneType = args[2].toLowerCase();
        
        // Validate zone type
        if (!zoneTypes.contains(zoneType)) {
            player.sendMessage(ChatColor.RED + "Invalid zone type. Use one of: " + 
                    String.join(", ", zoneTypes));
            return;
        }
        
        // Check if zone already exists
        if (plugin.getDatabaseManager().zoneExists(zoneName)) {
            player.sendMessage(ChatColor.RED + "A zone with that name already exists.");
            return;
        }
        
        // Give player the zoning tool to select area
        ItemStack zoningTool = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = zoningTool.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "Zoning Tool");
            
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.YELLOW + "Creating zone: " + zoneName);
            lore.add(ChatColor.YELLOW + "Type: " + zoneType);
            lore.add(ChatColor.GRAY + "Left-click to set corner 1");
            lore.add(ChatColor.GRAY + "Right-click to set corner 2");
            
            meta.setLore(lore);
            zoningTool.setItemMeta(meta);
        }
        
        player.getInventory().addItem(zoningTool);
        
        // Register this zoning operation
        ZoningTool.registerZoningOperation(player, zoneName, zoneType);
        
        player.sendMessage(ChatColor.GREEN + "Starting zone creation for '" + zoneName + "'");
        player.sendMessage(ChatColor.YELLOW + "Use the zoning tool to select the area.");
        player.sendMessage(ChatColor.YELLOW + "Left-click for first corner, right-click for second corner.");
    }
    
    private void handleModify(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(ChatColor.RED + "Usage: /zone modify <name> <type>");
            return;
        }
        
        String zoneName = args[1];
        String zoneType = args[2].toLowerCase();
        
        // Validate zone type
        if (!zoneTypes.contains(zoneType)) {
            player.sendMessage(ChatColor.RED + "Invalid zone type. Use one of: " + 
                    String.join(", ", zoneTypes));
            return;
        }
        
        // Check if zone exists
        if (!plugin.getDatabaseManager().zoneExists(zoneName)) {
            player.sendMessage(ChatColor.RED + "Zone '" + zoneName + "' doesn't exist.");
            return;
        }
        
        // Load zone
        Zone zone = plugin.getDatabaseManager().loadZone(zoneName);
        
        // Update zone type
        zone.setType(zoneType);
        plugin.getDatabaseManager().saveZone(zone);
        
        player.sendMessage(ChatColor.GREEN + "Zone '" + zoneName + "' type updated to '" + zoneType + "'.");
    }
    
    private void handleDelete(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /zone delete <name>");
            return;
        }
        
        String zoneName = args[1];
        
        // Check if zone exists
        if (!plugin.getDatabaseManager().zoneExists(zoneName)) {
            player.sendMessage(ChatColor.RED + "Zone '" + zoneName + "' doesn't exist.");
            return;
        }
        
        // Delete zone
        plugin.getDatabaseManager().deleteZone(zoneName);
        
        player.sendMessage(ChatColor.GREEN + "Zone '" + zoneName + "' deleted successfully.");
    }
    
    private void handleList(Player player) {
        List<Zone> zones = plugin.getDatabaseManager().getAllZones();
        
        if (zones.isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "No zones have been created yet.");
            return;
        }
        
        player.sendMessage(ChatColor.GREEN + "=== Zones ===");
        for (Zone zone : zones) {
            player.sendMessage(ChatColor.YELLOW + "- " + zone.getName() + 
                    ChatColor.GRAY + " (" + zone.getType() + ")");
        }
    }
    
    private void handleInfo(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /zone info <name>");
            return;
        }
        
        String zoneName = args[1];
        
        // Check if zone exists
        if (!plugin.getDatabaseManager().zoneExists(zoneName)) {
            player.sendMessage(ChatColor.RED + "Zone '" + zoneName + "' doesn't exist.");
            return;
        }
        
        // Load zone
        Zone zone = plugin.getDatabaseManager().loadZone(zoneName);
        
        player.sendMessage(ChatColor.GREEN + "=== Zone: " + zone.getName() + " ===");
        player.sendMessage(ChatColor.YELLOW + "Type: " + zone.getType());
        player.sendMessage(ChatColor.YELLOW + "Created by: " + 
                plugin.getServer().getOfflinePlayer(zone.getCreator()).getName());
        player.sendMessage(ChatColor.YELLOW + "Size: " + zone.getVolume() + " blocks");
        player.sendMessage(ChatColor.YELLOW + "World: " + zone.getWorld());
    }
    
    private void handleTool(Player player) {
        // Give player the zoning tool
        ItemStack zoningTool = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = zoningTool.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "Zoning Tool");
            
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Use '/zone create' to start creating a zone");
            
            meta.setLore(lore);
            zoningTool.setItemMeta(meta);
        }
        
        player.getInventory().addItem(zoningTool);
        
        player.sendMessage(ChatColor.GREEN + "You have been given a zoning tool.");
        player.sendMessage(ChatColor.YELLOW + "Use '/zone create <name> <type>' to start creating a zone.");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            for (String subcommand : subcommands) {
                if (subcommand.startsWith(partial)) {
                    completions.add(subcommand);
                }
            }
            return completions;
        }
        
        if (args.length == 2) {
            String subcommand = args[0].toLowerCase();
            String partial = args[1].toLowerCase();
            
            if (Arrays.asList("modify", "delete", "info").contains(subcommand)) {
                // List existing zones
                List<Zone> zones = plugin.getDatabaseManager().getAllZones();
                for (Zone zone : zones) {
                    if (zone.getName().toLowerCase().startsWith(partial)) {
                        completions.add(zone.getName());
                    }
                }
            }
            return completions;
        }
        
        if (args.length == 3 && (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("modify"))) {
            String partial = args[2].toLowerCase();
            return zoneTypes.stream()
                    .filter(type -> type.startsWith(partial))
                    .collect(Collectors.toList());
        }
        
        return completions;
    }
}
