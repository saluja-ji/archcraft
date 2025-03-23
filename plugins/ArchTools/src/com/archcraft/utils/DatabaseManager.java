package com.archcraft.utils;

import com.archcraft.Main;
import com.archcraft.models.Project;
import com.archcraft.models.Zone;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Handles data persistence for the plugin
 * Stores and retrieves projects and zones
 */
public class DatabaseManager {
    
    private final Main plugin;
    private final File projectsFile;
    private final File zonesFile;
    private FileConfiguration projectsConfig;
    private FileConfiguration zonesConfig;
    
    /**
     * Create a new database manager
     * @param plugin The main plugin instance
     */
    public DatabaseManager(Main plugin) {
        this.plugin = plugin;
        
        // Initialize data files
        projectsFile = new File(plugin.getDataFolder(), "projects.yml");
        zonesFile = new File(plugin.getDataFolder(), "zones.yml");
        
        // Create files if they don't exist
        if (!projectsFile.exists()) {
            plugin.saveResource("projects.yml", false);
        }
        
        if (!zonesFile.exists()) {
            plugin.saveResource("zones.yml", false);
        }
        
        // Load configurations
        projectsConfig = YamlConfiguration.loadConfiguration(projectsFile);
        zonesConfig = YamlConfiguration.loadConfiguration(zonesFile);
    }
    
    /**
     * Save a project to the database
     * @param project The project to save
     */
    public void saveProject(Project project) {
        String path = "projects." + project.getName();
        
        projectsConfig.set(path + ".owner", project.getOwner().toString());
        projectsConfig.set(path + ".creationDate", project.getCreationDate());
        projectsConfig.set(path + ".scale", project.getScale());
        projectsConfig.set(path + ".description", project.getDescription());
        
        // Save collaborators
        List<String> collaboratorsList = new ArrayList<>();
        for (UUID uuid : project.getCollaborators()) {
            collaboratorsList.add(uuid.toString());
        }
        projectsConfig.set(path + ".collaborators", collaboratorsList);
        
        // Save invited players
        List<String> invitedList = new ArrayList<>();
        for (UUID uuid : project.getInvitedPlayers()) {
            invitedList.add(uuid.toString());
        }
        projectsConfig.set(path + ".invited", invitedList);
        
        try {
            projectsConfig.save(projectsFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save project " + project.getName(), e);
        }
    }
    
    /**
     * Load a project from the database
     * @param projectName The name of the project to load
     * @return The loaded project, or null if not found
     */
    public Project loadProject(String projectName) {
        String path = "projects." + projectName;
        
        if (!projectsConfig.contains(path)) {
            return null;
        }
        
        String ownerStr = projectsConfig.getString(path + ".owner");
        UUID owner = UUID.fromString(ownerStr);
        
        String creationDate = projectsConfig.getString(path + ".creationDate");
        double scale = projectsConfig.getDouble(path + ".scale", 1.0);
        String description = projectsConfig.getString(path + ".description", "");
        
        // Load collaborators
        Set<UUID> collaborators = new HashSet<>();
        List<String> collaboratorsList = projectsConfig.getStringList(path + ".collaborators");
        for (String uuidStr : collaboratorsList) {
            collaborators.add(UUID.fromString(uuidStr));
        }
        
        // Load invited players
        Set<UUID> invited = new HashSet<>();
        List<String> invitedList = projectsConfig.getStringList(path + ".invited");
        for (String uuidStr : invitedList) {
            invited.add(UUID.fromString(uuidStr));
        }
        
        return new Project(
                projectName,
                owner,
                creationDate,
                scale,
                collaborators,
                invited,
                description
        );
    }
    
    /**
     * Delete a project from the database
     * @param projectName The name of the project to delete
     */
    public void deleteProject(String projectName) {
        projectsConfig.set("projects." + projectName, null);
        
        try {
            projectsConfig.save(projectsFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not delete project " + projectName, e);
        }
    }
    
    /**
     * Check if a project exists in the database
     * @param projectName The name of the project to check
     * @return True if the project exists
     */
    public boolean projectExists(String projectName) {
        return projectsConfig.contains("projects." + projectName);
    }
    
    /**
     * Get all projects for a specific player
     * @param playerUUID The UUID of the player
     * @return List of projects owned by or collaborated on by the player
     */
    public List<Project> getProjects(UUID playerUUID) {
        List<Project> result = new ArrayList<>();
        ConfigurationSection projectsSection = projectsConfig.getConfigurationSection("projects");
        
        if (projectsSection == null) {
            return result;
        }
        
        for (String projectName : projectsSection.getKeys(false)) {
            Project project = loadProject(projectName);
            
            if (project != null && (project.getOwner().equals(playerUUID) || 
                    project.isCollaborator(playerUUID))) {
                result.add(project);
            }
        }
        
        return result;
    }
    
    /**
     * Save a zone to the database
     * @param zone The zone to save
     */
    public void saveZone(Zone zone) {
        String path = "zones." + zone.getName();
        
        zonesConfig.set(path + ".type", zone.getType());
        zonesConfig.set(path + ".creator", zone.getCreator().toString());
        zonesConfig.set(path + ".world", zone.getWorld());
        zonesConfig.set(path + ".min.x", zone.getMin().getX());
        zonesConfig.set(path + ".min.y", zone.getMin().getY());
        zonesConfig.set(path + ".min.z", zone.getMin().getZ());
        zonesConfig.set(path + ".max.x", zone.getMax().getX());
        zonesConfig.set(path + ".max.y", zone.getMax().getY());
        zonesConfig.set(path + ".max.z", zone.getMax().getZ());
        
        try {
            zonesConfig.save(zonesFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save zone " + zone.getName(), e);
        }
    }
    
    /**
     * Load a zone from the database
     * @param zoneName The name of the zone to load
     * @return The loaded zone, or null if not found
     */
    public Zone loadZone(String zoneName) {
        String path = "zones." + zoneName;
        
        if (!zonesConfig.contains(path)) {
            return null;
        }
        
        String type = zonesConfig.getString(path + ".type");
        String creatorStr = zonesConfig.getString(path + ".creator");
        UUID creator = UUID.fromString(creatorStr);
        String world = zonesConfig.getString(path + ".world");
        
        double minX = zonesConfig.getDouble(path + ".min.x");
        double minY = zonesConfig.getDouble(path + ".min.y");
        double minZ = zonesConfig.getDouble(path + ".min.z");
        
        double maxX = zonesConfig.getDouble(path + ".max.x");
        double maxY = zonesConfig.getDouble(path + ".max.y");
        double maxZ = zonesConfig.getDouble(path + ".max.z");
        
        Vector min = new Vector(minX, minY, minZ);
        Vector max = new Vector(maxX, maxY, maxZ);
        
        return new Zone(zoneName, type, creator, world, min, max);
    }
    
    /**
     * Delete a zone from the database
     * @param zoneName The name of the zone to delete
     */
    public void deleteZone(String zoneName) {
        zonesConfig.set("zones." + zoneName, null);
        
        try {
            zonesConfig.save(zonesFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not delete zone " + zoneName, e);
        }
    }
    
    /**
     * Check if a zone exists in the database
     * @param zoneName The name of the zone to check
     * @return True if the zone exists
     */
    public boolean zoneExists(String zoneName) {
        return zonesConfig.contains("zones." + zoneName);
    }
    
    /**
     * Get all zones in the database
     * @return List of all zones
     */
    public List<Zone> getAllZones() {
        List<Zone> result = new ArrayList<>();
        ConfigurationSection zonesSection = zonesConfig.getConfigurationSection("zones");
        
        if (zonesSection == null) {
            return result;
        }
        
        for (String zoneName : zonesSection.getKeys(false)) {
            Zone zone = loadZone(zoneName);
            
            if (zone != null) {
                result.add(zone);
            }
        }
        
        return result;
    }
    
    /**
     * Reload all configurations from disk
     */
    public void reload() {
        projectsConfig = YamlConfiguration.loadConfiguration(projectsFile);
        zonesConfig = YamlConfiguration.loadConfiguration(zonesFile);
    }
    
    /**
     * Shutdown the database manager
     * Ensures all data is saved
     */
    public void shutdown() {
        try {
            projectsConfig.save(projectsFile);
            zonesConfig.save(zonesFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save data files on shutdown", e);
        }
    }
}
