package com.archcraft.tools;

import com.archcraft.Main;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tool for precise measurements in the Minecraft world
 * Allows architects to measure distances and areas with real-world scale
 */
public class MeasurementTool {
    
    private static final Map<UUID, MeasurementSession> sessions = new HashMap<>();
    
    /**
     * Register a player to use the measurement tool
     * @param player The player to register
     */
    public static void registerPlayer(Player player) {
        sessions.put(player.getUniqueId(), new MeasurementSession());
    }
    
    /**
     * Unregister a player from using the measurement tool
     * @param player The player to unregister
     */
    public static void unregisterPlayer(Player player) {
        sessions.remove(player.getUniqueId());
    }
    
    /**
     * Check if a player is registered to use the measurement tool
     * @param player The player to check
     * @return True if registered, false otherwise
     */
    public static boolean isRegistered(Player player) {
        return sessions.containsKey(player.getUniqueId());
    }
    
    /**
     * Set the first point for measurement
     * @param player The player setting the point
     * @param location The location of the point
     */
    public static void setPointA(Player player, Location location) {
        MeasurementSession session = sessions.get(player.getUniqueId());
        if (session == null) {
            return;
        }
        
        session.setPointA(location);
        player.sendMessage(ChatColor.GREEN + "Point A set at " + 
                formatLocation(location));
    }
    
    /**
     * Set the second point and calculate measurements
     * @param player The player setting the point
     * @param location The location of the point
     */
    public static void setPointB(Player player, Location location) {
        MeasurementSession session = sessions.get(player.getUniqueId());
        if (session == null) {
            return;
        }
        
        Location pointA = session.getPointA();
        if (pointA == null) {
            player.sendMessage(ChatColor.RED + "Please set point A first (left-click).");
            return;
        }
        
        // Check if both points are in the same world
        if (!pointA.getWorld().equals(location.getWorld())) {
            player.sendMessage(ChatColor.RED + "Both points must be in the same world.");
            return;
        }
        
        session.setPointB(location);
        
        // Calculate distance
        double distance = pointA.distance(location);
        
        // Calculate real-world distance based on scale
        double scale = Main.getInstance().getConfigManager().getScale();
        double realDistance = distance * scale;
        
        // Calculate horizontal (2D) distance
        Location pointAHorizontal = pointA.clone();
        Location pointBHorizontal = location.clone();
        pointAHorizontal.setY(0);
        pointBHorizontal.setY(0);
        double horizontalDistance = pointAHorizontal.distance(pointBHorizontal);
        double realHorizontalDistance = horizontalDistance * scale;
        
        // Calculate height difference
        double heightDiff = Math.abs(pointA.getY() - location.getY());
        double realHeightDiff = heightDiff * scale;
        
        // Calculate area if not a vertical line
        double area = 0;
        double realArea = 0;
        
        if (horizontalDistance > 0) {
            // Calculate the rect area defined by the points
            Vector vec = location.toVector().subtract(pointA.toVector());
            double xDiff = Math.abs(vec.getX());
            double zDiff = Math.abs(vec.getZ());
            area = xDiff * zDiff;
            realArea = area * scale * scale; // Scale squared for area
        }
        
        // Calculate volume
        double volume = area * heightDiff;
        double realVolume = volume * scale * scale * scale; // Scale cubed for volume
        
        // Send results to player
        player.sendMessage(ChatColor.GREEN + "=== Measurement Results ===");
        player.sendMessage(ChatColor.YELLOW + "Point A: " + formatLocation(pointA));
        player.sendMessage(ChatColor.YELLOW + "Point B: " + formatLocation(location));
        player.sendMessage(ChatColor.YELLOW + "Scale: 1 block = " + scale + " meters");
        
        player.sendMessage(ChatColor.AQUA + "3D Distance: " + 
                String.format("%.2f", distance) + " blocks " + 
                ChatColor.GRAY + "(" + String.format("%.2f", realDistance) + " meters)");
        
        player.sendMessage(ChatColor.AQUA + "Horizontal Distance: " + 
                String.format("%.2f", horizontalDistance) + " blocks " + 
                ChatColor.GRAY + "(" + String.format("%.2f", realHorizontalDistance) + " meters)");
        
        player.sendMessage(ChatColor.AQUA + "Height Difference: " + 
                String.format("%.2f", heightDiff) + " blocks " + 
                ChatColor.GRAY + "(" + String.format("%.2f", realHeightDiff) + " meters)");
        
        if (horizontalDistance > 0) {
            player.sendMessage(ChatColor.AQUA + "Rectangular Area: " + 
                    String.format("%.2f", area) + " sq. blocks " + 
                    ChatColor.GRAY + "(" + String.format("%.2f", realArea) + " sq. meters)");
            
            player.sendMessage(ChatColor.AQUA + "Volume: " + 
                    String.format("%.2f", volume) + " cubic blocks " + 
                    ChatColor.GRAY + "(" + String.format("%.2f", realVolume) + " cubic meters)");
        }
        
        // Reset points for next measurement or keep them as option
        if (Main.getInstance().getConfigManager().isResetAfterMeasurement()) {
            session.reset();
        }
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
     * Helper class to store measurement session data
     */
    private static class MeasurementSession {
        private Location pointA;
        private Location pointB;
        
        public Location getPointA() {
            return pointA;
        }
        
        public void setPointA(Location pointA) {
            this.pointA = pointA;
        }
        
        public Location getPointB() {
            return pointB;
        }
        
        public void setPointB(Location pointB) {
            this.pointB = pointB;
        }
        
        public void reset() {
            pointA = null;
            pointB = null;
        }
    }
}
