package com.archcraft.listeners;

import com.archcraft.Main;
import com.archcraft.tools.MeasurementTool;
import com.archcraft.tools.ZoningTool;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Listener for player interactions with architectural tools
 */
public class PlayerInteractListener implements Listener {
    
    private final Main plugin;
    
    public PlayerInteractListener(Main plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        if (item == null) {
            return;
        }
        
        ItemMeta meta = item.getItemMeta();
        
        if (meta == null || !meta.hasDisplayName()) {
            return;
        }
        
        String displayName = meta.getDisplayName();
        
        // Check for the measurement tool
        if (item.getType() == Material.GOLDEN_AXE && 
                displayName.contains("Measuring Tool") && 
                MeasurementTool.isRegistered(player)) {
            handleMeasurementTool(event);
            return;
        }
        
        // Check for the zoning tool
        if (item.getType() == Material.BLAZE_ROD && 
                displayName.contains("Zoning Tool") && 
                ZoningTool.isRegistered(player)) {
            handleZoningTool(event);
            return;
        }
    }
    
    private void handleMeasurementTool(PlayerInteractEvent event) {
        event.setCancelled(true);
        
        Player player = event.getPlayer();
        Action action = event.getAction();
        
        if (action == Action.LEFT_CLICK_BLOCK) {
            // Set point A
            MeasurementTool.setPointA(player, event.getClickedBlock().getLocation());
        } else if (action == Action.RIGHT_CLICK_BLOCK) {
            // Set point B and measure
            MeasurementTool.setPointB(player, event.getClickedBlock().getLocation());
        }
    }
    
    private void handleZoningTool(PlayerInteractEvent event) {
        event.setCancelled(true);
        
        Player player = event.getPlayer();
        Action action = event.getAction();
        
        if (action == Action.LEFT_CLICK_BLOCK) {
            // Set corner 1
            ZoningTool.setCorner1(player, event.getClickedBlock().getLocation());
        } else if (action == Action.RIGHT_CLICK_BLOCK) {
            // Set corner 2 and create zone
            ZoningTool.setCorner2(player, event.getClickedBlock().getLocation());
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        // Unregister players from tools when they log out
        if (MeasurementTool.isRegistered(player)) {
            MeasurementTool.unregisterPlayer(player);
        }
        
        if (ZoningTool.isRegistered(player)) {
            ZoningTool.unregisterPlayer(player);
        }
    }
}
