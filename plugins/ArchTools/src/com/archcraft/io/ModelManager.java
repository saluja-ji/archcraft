package com.archcraft.io;

import com.archcraft.Main;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manager class for 3D model import/export operations
 * Handles registration and coordination of importers and exporters
 */
public class ModelManager {
    
    private final Main plugin;
    private final Logger logger;
    private final Map<ModelFormat, ModelImporter> importers;
    private final Map<ModelFormat, ModelExporter> exporters;
    private final File modelsDirectory;
    
    /**
     * Create a new model manager
     * @param plugin The plugin instance
     */
    public ModelManager(Main plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.importers = new HashMap<>();
        this.exporters = new HashMap<>();
        
        // Create directory for model storage
        this.modelsDirectory = new File(plugin.getDataFolder(), "models");
        if (!this.modelsDirectory.exists()) {
            this.modelsDirectory.mkdirs();
        }
        
        // Register default importers and exporters
        registerDefaultHandlers();
    }
    
    /**
     * Register the default model handlers
     */
    private void registerDefaultHandlers() {
        // We'll implement specific format handlers later
        logger.info("Registered 3D model import/export handlers");
    }
    
    /**
     * Register a model importer
     * @param importer The importer to register
     */
    public void registerImporter(ModelImporter importer) {
        importers.put(importer.getFormat(), importer);
        logger.info("Registered importer for " + importer.getFormat().getDisplayName() + " format");
    }
    
    /**
     * Register a model exporter
     * @param exporter The exporter to register
     */
    public void registerExporter(ModelExporter exporter) {
        exporters.put(exporter.getFormat(), exporter);
        logger.info("Registered exporter for " + exporter.getFormat().getDisplayName() + " format");
    }
    
    /**
     * Get supported import formats
     * @return Set of supported import formats
     */
    public Set<ModelFormat> getSupportedImportFormats() {
        return importers.keySet();
    }
    
    /**
     * Get supported export formats
     * @return Set of supported export formats
     */
    public Set<ModelFormat> getSupportedExportFormats() {
        return exporters.keySet();
    }
    
    /**
     * Import a model from a file
     * @param file The file to import
     * @param world The world to import into
     * @param origin The origin location for the import
     * @param scale The scale factor
     * @param rotationY The Y rotation in degrees
     * @param player The player performing the import
     * @return True if import was successful
     */
    public boolean importModel(File file, World world, Location origin, double scale, float rotationY, Player player) {
        String fileName = file.getName();
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        ModelFormat format = ModelFormat.fromExtension(extension);
        
        if (format == null) {
            player.sendMessage(ChatColor.RED + "Unsupported file format: " + extension);
            return false;
        }
        
        ModelImporter importer = importers.get(format);
        if (importer == null) {
            player.sendMessage(ChatColor.RED + "No importer available for " + format.getDisplayName() + " format");
            return false;
        }
        
        try {
            player.sendMessage(ChatColor.YELLOW + "Importing " + fileName + "...");
            long startTime = System.currentTimeMillis();
            
            boolean success = importer.importModel(file, world, origin, scale, rotationY, player);
            
            long endTime = System.currentTimeMillis();
            double seconds = (endTime - startTime) / 1000.0;
            
            if (success) {
                player.sendMessage(ChatColor.GREEN + "Import completed in " + String.format("%.1f", seconds) + " seconds");
                return true;
            } else {
                player.sendMessage(ChatColor.RED + "Import failed");
                return false;
            }
        } catch (IOException e) {
            player.sendMessage(ChatColor.RED + "Error reading file: " + e.getMessage());
            logger.log(Level.WARNING, "Error importing model", e);
            return false;
        } catch (UnsupportedOperationException e) {
            player.sendMessage(ChatColor.RED + "Unsupported operation: " + e.getMessage());
            return false;
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "Error importing model: " + e.getMessage());
            logger.log(Level.SEVERE, "Error importing model", e);
            return false;
        }
    }
    
    /**
     * Export a region to a model file
     * @param min Minimum corner of region
     * @param max Maximum corner of region
     * @param fileName Name of the file to export to (with extension)
     * @param scale Scale factor
     * @param player Player performing the export
     * @return True if export was successful
     */
    public boolean exportModel(Location min, Location max, String fileName, double scale, Player player) {
        if (!min.getWorld().equals(max.getWorld())) {
            player.sendMessage(ChatColor.RED + "Both locations must be in the same world");
            return false;
        }
        
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        ModelFormat format = ModelFormat.fromExtension(extension);
        
        if (format == null) {
            player.sendMessage(ChatColor.RED + "Unsupported file format: " + extension);
            return false;
        }
        
        ModelExporter exporter = exporters.get(format);
        if (exporter == null) {
            player.sendMessage(ChatColor.RED + "No exporter available for " + format.getDisplayName() + " format");
            return false;
        }
        
        File outputFile = new File(modelsDirectory, fileName);
        try {
            player.sendMessage(ChatColor.YELLOW + "Exporting region to " + fileName + "...");
            long startTime = System.currentTimeMillis();
            
            boolean success = exporter.exportModel(min, max, outputFile, scale, player);
            
            long endTime = System.currentTimeMillis();
            double seconds = (endTime - startTime) / 1000.0;
            
            if (success) {
                player.sendMessage(ChatColor.GREEN + "Export completed in " + String.format("%.1f", seconds) + " seconds");
                player.sendMessage(ChatColor.GREEN + "Saved to: " + outputFile.getAbsolutePath());
                return true;
            } else {
                player.sendMessage(ChatColor.RED + "Export failed");
                return false;
            }
        } catch (IOException e) {
            player.sendMessage(ChatColor.RED + "Error writing file: " + e.getMessage());
            logger.log(Level.WARNING, "Error exporting model", e);
            return false;
        } catch (UnsupportedOperationException e) {
            player.sendMessage(ChatColor.RED + "Unsupported operation: " + e.getMessage());
            return false;
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "Error exporting model: " + e.getMessage());
            logger.log(Level.SEVERE, "Error exporting model", e);
            return false;
        }
    }
    
    /**
     * Get the models directory
     * @return The directory where models are stored
     */
    public File getModelsDirectory() {
        return modelsDirectory;
    }
}