package com.archcraft.utils;

import com.archcraft.Main;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * Manages plugin configuration
 */
public class ConfigManager {
    
    private final Main plugin;
    private FileConfiguration config;
    
    // Default config values
    private double scale = 1.0;
    private boolean visualizeZones = true;
    private boolean resetAfterMeasurement = false;
    private int backupInterval = 30;
    
    /**
     * Create a new config manager
     * @param plugin The main plugin instance
     */
    public ConfigManager(Main plugin) {
        this.plugin = plugin;
        loadConfig();
    }
    
    /**
     * Load the configuration from file
     */
    public void loadConfig() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
        
        scale = config.getDouble("scale", 1.0);
        visualizeZones = config.getBoolean("visualize-zones", true);
        resetAfterMeasurement = config.getBoolean("reset-after-measurement", false);
        backupInterval = config.getInt("backup-interval", 30);
    }
    
    /**
     * Save the configuration to file
     */
    public void saveConfig() {
        config.set("scale", scale);
        config.set("visualize-zones", visualizeZones);
        config.set("reset-after-measurement", resetAfterMeasurement);
        config.set("backup-interval", backupInterval);
        
        plugin.saveConfig();
    }
    
    /**
     * Get the current scale (1 block = X meters)
     * @return Current scale
     */
    public double getScale() {
        return scale;
    }
    
    /**
     * Set the current scale
     * @param scale New scale value
     */
    public void setScale(double scale) {
        this.scale = scale;
        config.set("scale", scale);
        plugin.saveConfig();
    }
    
    /**
     * Check if zones should be visualized
     * @return True if zones should be visualized
     */
    public boolean isVisualizeZones() {
        return visualizeZones;
    }
    
    /**
     * Set whether zones should be visualized
     * @param visualizeZones Whether to visualize zones
     */
    public void setVisualizeZones(boolean visualizeZones) {
        this.visualizeZones = visualizeZones;
        config.set("visualize-zones", visualizeZones);
        plugin.saveConfig();
    }
    
    /**
     * Check if measurement points should be reset after measurement
     * @return True if reset after measurement
     */
    public boolean isResetAfterMeasurement() {
        return resetAfterMeasurement;
    }
    
    /**
     * Set whether measurement points should be reset after measurement
     * @param resetAfterMeasurement Whether to reset after measurement
     */
    public void setResetAfterMeasurement(boolean resetAfterMeasurement) {
        this.resetAfterMeasurement = resetAfterMeasurement;
        config.set("reset-after-measurement", resetAfterMeasurement);
        plugin.saveConfig();
    }
    
    /**
     * Get the backup interval in minutes
     * @return Backup interval
     */
    public int getBackupInterval() {
        return backupInterval;
    }
    
    /**
     * Set the backup interval
     * @param backupInterval New backup interval in minutes
     */
    public void setBackupInterval(int backupInterval) {
        this.backupInterval = backupInterval;
        config.set("backup-interval", backupInterval);
        plugin.saveConfig();
    }
}
