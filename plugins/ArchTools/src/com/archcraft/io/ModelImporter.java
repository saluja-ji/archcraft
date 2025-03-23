package com.archcraft.io;

import java.io.File;
import java.io.IOException;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * Interface for importing 3D models into Minecraft
 */
public interface ModelImporter {
    
    /**
     * Import a 3D model file into Minecraft
     * @param file The model file to import
     * @param world The world to import into
     * @param origin The origin location for the import
     * @param scale The scale factor to apply (1.0 = no scaling)
     * @param rotationY The Y-axis rotation in degrees
     * @param player The player performing the import (for notifications)
     * @return True if import was successful
     * @throws IOException If there was an error reading the file
     * @throws UnsupportedOperationException If the format is not supported
     */
    boolean importModel(File file, World world, Location origin, double scale, float rotationY, Player player) 
            throws IOException, UnsupportedOperationException;
    
    /**
     * Get the model format this importer supports
     * @return The supported model format
     */
    ModelFormat getFormat();
    
    /**
     * Check if a specific feature is supported by this importer
     * @param feature The feature to check
     * @return True if the feature is supported
     */
    boolean supportsFeature(ImportFeature feature);
    
    /**
     * Enum representing optional features an importer may support
     */
    enum ImportFeature {
        TEXTURES,
        MATERIALS,
        COLORS,
        ANIMATION,
        METADATA,
        SCALING
    }
}