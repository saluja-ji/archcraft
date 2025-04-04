package com.archcraft.io.obj;

import com.archcraft.io.ModelExporter;
import com.archcraft.io.ModelFormat;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Exporter for Wavefront OBJ format
 */
public class ObjExporter implements ModelExporter {
    
    // Material map to store unique material definitions
    private final Map<Material, String> materialMap = new HashMap<>();
    
    @Override
    public boolean exportModel(Location min, Location max, File outputFile, double scale, Player player) 
            throws IOException, UnsupportedOperationException {
        
        // Get world
        World world = min.getWorld();
        
        // Check that both locations are in the same world
        if (!world.equals(max.getWorld())) {
            throw new IllegalArgumentException("Both locations must be in the same world");
        }
        
        // Define region bounds
        int minX = Math.min(min.getBlockX(), max.getBlockX());
        int minY = Math.min(min.getBlockY(), max.getBlockY());
        int minZ = Math.min(min.getBlockZ(), max.getBlockZ());
        int maxX = Math.max(min.getBlockX(), max.getBlockX());
        int maxY = Math.max(min.getBlockY(), max.getBlockY());
        int maxZ = Math.max(min.getBlockZ(), max.getBlockZ());
        
        // Check region size (safety measure for very large regions)
        int width = maxX - minX + 1;
        int height = maxY - minY + 1;
        int depth = maxZ - minZ + 1;
        int totalBlocks = width * height * depth;
        
        if (totalBlocks > 1000000) { // Limit to 1 million blocks
            player.sendMessage("Region too large: " + totalBlocks + " blocks");
            player.sendMessage("Please select a smaller region (max 1,000,000 blocks)");
            return false;
        }
        
        // Create directory if it doesn't exist
        outputFile.getParentFile().mkdirs();
        
        // Create material library file
        File mtlFile = new File(outputFile.getParent(), outputFile.getName().replace(".obj", ".mtl"));
        
        // Initialize counters
        int vertexCount = 1; // OBJ indices start at 1, not 0
        
        // Scan blocks and identify unique materials
        Set<Material> materials = new HashSet<>();
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    Material material = block.getType();
                    
                    if (material != Material.AIR) {
                        materials.add(material);
                    }
                }
            }
        }
        
        // Create material library
        try (PrintWriter mtlWriter = new PrintWriter(new FileWriter(mtlFile))) {
            mtlWriter.println("# Material library generated by ArchCraft");
            mtlWriter.println("# Contains materials for Minecraft blocks");
            
            int materialIndex = 1;
            for (Material material : materials) {
                String materialName = "mc_" + material.name().toLowerCase();
                materialMap.put(material, materialName);
                
                // Get material color based on block type
                float[] rgb = getMaterialColor(material);
                
                mtlWriter.println("newmtl " + materialName);
                mtlWriter.println("Ka " + rgb[0] + " " + rgb[1] + " " + rgb[2]);
                mtlWriter.println("Kd " + rgb[0] + " " + rgb[1] + " " + rgb[2]);
                mtlWriter.println("Ks 0.000 0.000 0.000");
                mtlWriter.println("d 1.0");
                mtlWriter.println("illum 1");
                mtlWriter.println();
                
                materialIndex++;
            }
        }
        
        // Create OBJ file
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            // Write header
            writer.println("# Wavefront OBJ file");
            writer.println("# Generated by ArchCraft");
            writer.println("# Region: [" + minX + "," + minY + "," + minZ + "] to [" + maxX + "," + maxY + "," + maxZ + "]");
            writer.println("# Scale: " + scale);
            writer.println();
            
            // Reference material library
            writer.println("mtllib " + mtlFile.getName());
            writer.println();
            
            // Export blocks as cubes
            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        Block block = world.getBlockAt(x, y, z);
                        Material material = block.getType();
                        
                        if (material != Material.AIR) {
                            // Scale and offset coordinates
                            float sx = (float)((x - minX) * scale);
                            float sy = (float)((y - minY) * scale);
                            float sz = (float)((z - minZ) * scale);
                            
                            String materialName = materialMap.get(material);
                            
                            // Start a new object for this block
                            writer.println("o block_" + x + "_" + y + "_" + z);
                            writer.println("usemtl " + materialName);
                            
                            // Define vertices for a unit cube at the given position
                            // Each vertex is positioned relative to the minimum corner of the region
                            float cubeSize = (float)scale;
                            
                            // Vertices: 8 corners of the cube
                            writer.println("v " + sx + " " + sy + " " + sz);                              // 1: bottom-left-back
                            writer.println("v " + (sx + cubeSize) + " " + sy + " " + sz);                 // 2: bottom-right-back
                            writer.println("v " + (sx + cubeSize) + " " + sy + " " + (sz + cubeSize));    // 3: bottom-right-front
                            writer.println("v " + sx + " " + sy + " " + (sz + cubeSize));                 // 4: bottom-left-front
                            writer.println("v " + sx + " " + (sy + cubeSize) + " " + sz);                 // 5: top-left-back
                            writer.println("v " + (sx + cubeSize) + " " + (sy + cubeSize) + " " + sz);    // 6: top-right-back
                            writer.println("v " + (sx + cubeSize) + " " + (sy + cubeSize) + " " + (sz + cubeSize));  // 7: top-right-front
                            writer.println("v " + sx + " " + (sy + cubeSize) + " " + (sz + cubeSize));    // 8: top-left-front
                            
                            // Faces: 6 faces of the cube
                            // Each face is a square made of two triangles
                            // Using relative indices based on the current vertex count
                            writer.println("f " + vertexCount + " " + (vertexCount + 1) + " " + (vertexCount + 2));  // Bottom face (triangle 1)
                            writer.println("f " + vertexCount + " " + (vertexCount + 2) + " " + (vertexCount + 3));  // Bottom face (triangle 2)
                            
                            writer.println("f " + (vertexCount + 4) + " " + (vertexCount + 5) + " " + (vertexCount + 6));  // Top face (triangle 1)
                            writer.println("f " + (vertexCount + 4) + " " + (vertexCount + 6) + " " + (vertexCount + 7));  // Top face (triangle 2)
                            
                            writer.println("f " + vertexCount + " " + (vertexCount + 4) + " " + (vertexCount + 7));  // Left face (triangle 1)
                            writer.println("f " + vertexCount + " " + (vertexCount + 7) + " " + (vertexCount + 3));  // Left face (triangle 2)
                            
                            writer.println("f " + (vertexCount + 1) + " " + (vertexCount + 5) + " " + (vertexCount + 6));  // Right face (triangle 1)
                            writer.println("f " + (vertexCount + 1) + " " + (vertexCount + 6) + " " + (vertexCount + 2));  // Right face (triangle 2)
                            
                            writer.println("f " + vertexCount + " " + (vertexCount + 1) + " " + (vertexCount + 5));  // Back face (triangle 1)
                            writer.println("f " + vertexCount + " " + (vertexCount + 5) + " " + (vertexCount + 4));  // Back face (triangle 2)
                            
                            writer.println("f " + (vertexCount + 3) + " " + (vertexCount + 2) + " " + (vertexCount + 6));  // Front face (triangle 1)
                            writer.println("f " + (vertexCount + 3) + " " + (vertexCount + 6) + " " + (vertexCount + 7));  // Front face (triangle 2)
                            
                            writer.println();
                            
                            // Update vertex count for the next block
                            vertexCount += 8;
                        }
                    }
                }
            }
        }
        
        return true;
    }
    
    /**
     * Get a representative color for a material
     * @param material The material
     * @return RGB color values [r, g, b] in range 0.0-1.0
     */
    private float[] getMaterialColor(Material material) {
        // This is a simplified approach - in a real implementation, 
        // we would have a more comprehensive mapping of materials to colors
        String name = material.name();
        
        if (name.contains("STONE"))
            return new float[]{0.5f, 0.5f, 0.5f};
        else if (name.contains("DIRT") || name.contains("SOIL"))
            return new float[]{0.6f, 0.3f, 0.1f};
        else if (name.contains("GRASS"))
            return new float[]{0.1f, 0.6f, 0.1f};
        else if (name.contains("SAND"))
            return new float[]{0.9f, 0.8f, 0.2f};
        else if (name.contains("WATER"))
            return new float[]{0.0f, 0.2f, 0.9f};
        else if (name.contains("WOOD") || name.contains("LOG"))
            return new float[]{0.6f, 0.3f, 0.2f};
        else if (name.contains("LEAVES"))
            return new float[]{0.1f, 0.5f, 0.1f};
        else if (name.contains("BRICK"))
            return new float[]{0.7f, 0.3f, 0.3f};
        else if (name.contains("WOOL") && name.contains("RED"))
            return new float[]{0.9f, 0.1f, 0.1f};
        else if (name.contains("WOOL") && name.contains("BLUE"))
            return new float[]{0.1f, 0.1f, 0.9f};
        else if (name.contains("WOOL") && name.contains("GREEN"))
            return new float[]{0.1f, 0.9f, 0.1f};
        else if (name.contains("WOOL") && name.contains("YELLOW"))
            return new float[]{0.9f, 0.9f, 0.1f};
        else if (name.contains("GOLD") || name.contains("GLOWSTONE"))
            return new float[]{0.9f, 0.8f, 0.1f};
        else if (name.contains("IRON"))
            return new float[]{0.8f, 0.8f, 0.8f};
        else if (name.contains("DIAMOND"))
            return new float[]{0.0f, 0.8f, 0.8f};
        else if (name.contains("OBSIDIAN"))
            return new float[]{0.1f, 0.1f, 0.2f};
        else if (name.contains("SNOW") || name.contains("QUARTZ"))
            return new float[]{0.95f, 0.95f, 0.95f};
        else if (name.contains("COAL"))
            return new float[]{0.1f, 0.1f, 0.1f};
        else if (name.contains("GLASS"))
            return new float[]{0.8f, 0.8f, 0.9f};
        else
            return new float[]{0.5f, 0.5f, 0.5f}; // Default gray
    }
    
    @Override
    public ModelFormat getFormat() {
        return ModelFormat.OBJ;
    }
    
    @Override
    public boolean supportsFeature(ExportFeature feature) {
        switch (feature) {
            case MATERIALS:
            case COLORS:
                return true;
            case TEXTURES:
            case METADATA:
            case HOLLOW_OPTIMIZATION:
            case LOD_GENERATION:
                return false;
            default:
                return false;
        }
    }
}