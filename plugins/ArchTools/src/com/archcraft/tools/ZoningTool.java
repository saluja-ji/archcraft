package com.archcraft.tools;

import com.archcraft.Main;
import com.archcraft.models.Zone;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tool for creating and managing zones
 * Allows urban planners to designate specific areas with different purposes
 */
public class ZoningTool {
    
    private static final Map<UUID, ZoningSession> sessions = new HashMap<>();
    
    /**
     * Register a zoning operation for a player
     * @param player The player creating the zone
     * @param zoneName The name of the zone being created
     * @param zoneType The type of the zone being created
     */
    public static void registerZoningOperation(Player player, String zoneName, String zoneType) {
        sessions.put(player.getUniqueId(), new ZoningSession(zoneName, zoneType));
    }
    
    /**
     * Unregister a player's zoning operation
     * @param player The player to unregister
     */
    public static void unregisterPlayer(Player player) {
        sessions.remove(player.getUniqueId());
    }
    
    /**
     * Check if a player has a registered zoning operation
     * @param player The player to check
     * @return True if registered, false otherwise
     */
    public static boolean isRegistered(Player player) {
        return sessions.containsKey(player.getUniqueId());
    }
    
    /**
     * Get the player's current zoning session
     * @param player The player
     * @return The zoning session, or null if not registered
     */
    public static ZoningSession getSession(Player player) {
        return sessions.get(player.getUniqueId());
    }
    
    /**
     * Set the first corner of a zone
     * @param player The player setting the corner
     * @param location The location of the corner
     */
    public static void setCorner1(Player player, Location location) {
        ZoningSession session = sessions.get(player.getUniqueId());
        if (session == null) {
            return;
        }
        
        session.setCorner1(location);
        player.sendMessage(ChatColor.GREEN + "First corner set at " + 
                formatLocation(location));
    }
    
    /**
     * Set the second corner and finalize the zone
     * @param player The player setting the corner
     * @param location The location of the corner
     */
    public static void setCorner2(Player player, Location location) {
        ZoningSession session = sessions.get(player.getUniqueId());
        if (session == null) {
            return;
        }
        
        Location corner1 = session.getCorner1();
        if (corner1 == null) {
            player.sendMessage(ChatColor.RED + "Please set the first corner first (left-click).");
            return;
        }
        
        // Check if both points are in the same world
        if (!corner1.getWorld().equals(location.getWorld())) {
            player.sendMessage(ChatColor.RED + "Both corners must be in the same world.");
            return;
        }
        
        session.setCorner2(location);
        
        // Calculate min and max points for the zone
        double minX = Math.min(corner1.getX(), location.getX());
        double minY = Math.min(corner1.getY(), location.getY());
        double minZ = Math.min(corner1.getZ(), location.getZ());
        
        double maxX = Math.max(corner1.getX(), location.getX());
        double maxY = Math.max(corner1.getY(), location.getY());
        double maxZ = Math.max(corner1.getZ(), location.getZ());
        
        // Calculate volume
        double volume = (maxX - minX + 1) * (maxY - minY + 1) * (maxZ - minZ + 1);
        
        // Create the zone
        Zone zone = new Zone(
                session.getZoneName(),
                session.getZoneType(),
                player.getUniqueId(),
                corner1.getWorld().getName(),
                new Vector(minX, minY, minZ),
                new Vector(maxX, maxY, maxZ)
        );
        
        // Save the zone
        Main.getInstance().getDatabaseManager().saveZone(zone);
        
        // Visualize the zone if configured
        if (Main.getInstance().getConfigManager().isVisualizeZones()) {
            visualizeZone(zone);
        }
        
        player.sendMessage(ChatColor.GREEN + "Zone '" + session.getZoneName() + 
                "' created successfully.");
        player.sendMessage(ChatColor.YELLOW + "Type: " + session.getZoneType());
        player.sendMessage(ChatColor.YELLOW + "Size: " + String.format("%.0f", volume) + " blocks");
        
        // Unregister the session after successful creation
        sessions.remove(player.getUniqueId());
    }
    
    /**
     * Visualize a zone with particles or temporary blocks
     * @param zone The zone to visualize
     */
    private static void visualizeZone(Zone zone) {
        // This would use particle effects or temporary blocks to outline the zone
        // For scope simplicity, we'll just log it
        Bukkit.getLogger().info("Visualizing zone: " + zone.getName());
    }
    
    /**
     * Format a location as a readable string
     * @param location The location to format
     * @return Formatted location string
     */
    private static String formatLocation(Location location) {
        return String.format("(%.1f, %.1f, %.1f)", 
                location.getX(), location.getY(), location.getZ());
    }
    
    /**
     * Get the border material for a zone type
     * @param zoneType The zone type
     * @return Material for visualizing the zone
     */
    public static Material getBorderMaterial(String zoneType) {
        switch (zoneType.toLowerCase()) {
            case "residential":
                return Material.GREEN_WOOL;
            case "commercial":
                return Material.BLUE_WOOL;
            case "industrial":
                return Material.YELLOW_WOOL;
            case "recreational":
                return Material.LIME_WOOL;
            case "educational":
                return Material.LIGHT_BLUE_WOOL;
            case "transportation":
                return Material.GRAY_WOOL;
            case "agricultural":
                return Material.BROWN_WOOL;
            case "mixed":
                return Material.MAGENTA_WOOL;
            default:
                return Material.WHITE_WOOL;
        }
    }
    
    /**
     * Helper class to store zoning session data
     */
    public static class ZoningSession {
        private final String zoneName;
        private final String zoneType;
        private Location corner1;
        private Location corner2;
        
        public ZoningSession(String zoneName, String zoneType) {
            this.zoneName = zoneName;
            this.zoneType = zoneType;
        }
        
        public String getZoneName() {
            return zoneName;
        }
        
        public String getZoneType() {
            return zoneType;
        }
        
        public Location getCorner1() {
            return corner1;
        }
        
        public void setCorner1(Location corner1) {
            this.corner1 = corner1;
        }
        
        public Location getCorner2() {
            return corner2;
        }
        
        public void setCorner2(Location corner2) {
            this.corner2 = corner2;
        }
    }
}
