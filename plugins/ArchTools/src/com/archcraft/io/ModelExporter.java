package com.archcraft.io;

import java.io.File;
import java.io.IOException;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * Interface for exporting Minecraft builds to 3D model formats
 */
public interface ModelExporter {
    
    /**
     * Export a region of a Minecraft world to a 3D model file
     * @param min The minimum corner location of the region to export
     * @param max The maximum corner location of the region to export
     * @param outputFile The file to export to
     * @param scale The scale factor to apply (1.0 = no scaling)
     * @param player The player performing the export (for notifications)
     * @return True if export was successful
     * @throws IOException If there was an error writing to the file
     * @throws UnsupportedOperationException If the format is not supported
     */
    boolean exportModel(Location min, Location max, File outputFile, double scale, Player player) 
            throws IOException, UnsupportedOperationException;
    
    /**
     * Get the model format this exporter supports
     * @return The supported model format
     */
    ModelFormat getFormat();
    
    /**
     * Check if a specific feature is supported by this exporter
     * @param feature The feature to check
     * @return True if the feature is supported
     */
    boolean supportsFeature(ExportFeature feature);
    
    /**
     * Enum representing optional features an exporter may support
     */
    enum ExportFeature {
        TEXTURES,
        MATERIALS,
        COLORS,
        METADATA,
        HOLLOW_OPTIMIZATION,
        LOD_GENERATION
    }
}