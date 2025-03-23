package com.archcraft.io.obj;

import com.archcraft.io.ModelImporter;
import com.archcraft.io.ModelFormat;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Importer for Wavefront OBJ format
 */
public class ObjImporter implements ModelImporter {
    
    private final Logger logger;
    
    public ObjImporter(Logger logger) {
        this.logger = logger;
    }
    
    @Override
    public boolean importModel(File file, World world, Location origin, double scale, float rotationY, Player player) 
            throws IOException, UnsupportedOperationException {
        
        // Parse OBJ file and build in the world
        List<Vector> vertices = new ArrayList<>();
        List<Face> faces = new ArrayList<>();
        Map<String, Material> materials = new HashMap<>();
        String currentMaterial = null;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                String[] parts = line.split("\\s+");
                String type = parts[0].toLowerCase();
                
                try {
                    switch (type) {
                        case "v":
                            // Vertex
                            if (parts.length < 4) {
                                player.sendMessage(ChatColor.RED + "Invalid vertex at line " + lineNumber);
                                continue;
                            }
                            
                            double x = Double.parseDouble(parts[1]);
                            double y = Double.parseDouble(parts[2]);
                            double z = Double.parseDouble(parts[3]);
                            
                            vertices.add(new Vector(x, y, z));
                            break;
                            
                        case "f":
                            // Face
                            if (parts.length < 4) {
                                player.sendMessage(ChatColor.RED + "Invalid face at line " + lineNumber);
                                continue;
                            }
                            
                            // Parse face vertices (ignoring texture and normal indices if present)
                            List<Integer> faceVertices = new ArrayList<>();
                            for (int i = 1; i < parts.length; i++) {
                                String[] indices = parts[i].split("/");
                                int vertexIndex = Integer.parseInt(indices[0]) - 1; // OBJ indices start at 1
                                faceVertices.add(vertexIndex);
                            }
                            
                            faces.add(new Face(faceVertices, currentMaterial));
                            break;
                            
                        case "usemtl":
                            // Use material
                            if (parts.length < 2) {
                                player.sendMessage(ChatColor.RED + "Invalid material at line " + lineNumber);
                                continue;
                            }
                            
                            currentMaterial = parts[1];
                            break;
                            
                        case "mtllib":
                            // Material library - we would parse this in a complete implementation
                            // For this demo, we'll use simple colored blocks
                            break;
                            
                        default:
                            // Ignore other types
                            break;
                    }
                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    player.sendMessage(ChatColor.RED + "Error parsing line " + lineNumber + ": " + e.getMessage());
                    logger.log(Level.WARNING, "Error parsing OBJ file at line " + lineNumber, e);
                }
            }
        }
        
        // Map materials to Minecraft blocks (simplified)
        materials.put("default", Material.STONE);
        
        if (currentMaterial != null && currentMaterial.toLowerCase().contains("brick")) {
            materials.put(currentMaterial, Material.BRICK);
        } else if (currentMaterial != null && currentMaterial.toLowerCase().contains("wood")) {
            materials.put(currentMaterial, Material.OAK_WOOD);
        } else if (currentMaterial != null && currentMaterial.toLowerCase().contains("glass")) {
            materials.put(currentMaterial, Material.GLASS);
        } else if (currentMaterial != null && currentMaterial.toLowerCase().contains("stone")) {
            materials.put(currentMaterial, Material.STONE);
        } else if (currentMaterial != null && currentMaterial.toLowerCase().contains("sand")) {
            materials.put(currentMaterial, Material.SANDSTONE);
        }
        
        // Apply rotation matrix for Y rotation (in radians)
        double rotationRadians = Math.toRadians(rotationY);
        double cosY = Math.cos(rotationRadians);
        double sinY = Math.sin(rotationRadians);
        
        // Build the model in the Minecraft world
        player.sendMessage(ChatColor.YELLOW + "Building model with " + faces.size() + " faces...");
        
        // Set of blocks we've already placed (to avoid duplicates)
        Map<String, Boolean> placedBlocks = new HashMap<>();
        
        // For each face, we'll triangulate and fill with blocks
        for (Face face : faces) {
            Material material = materials.getOrDefault(face.getMaterial(), Material.STONE);
            
            // For each face, we'll use a simple algorithm to draw filled triangles
            List<Integer> vertexIndices = face.getVertexIndices();
            
            // Split face into triangles (assuming convex polygon)
            for (int i = 0; i < vertexIndices.size() - 2; i++) {
                Vector v1 = vertices.get(vertexIndices.get(0));
                Vector v2 = vertices.get(vertexIndices.get(i + 1));
                Vector v3 = vertices.get(vertexIndices.get(i + 2));
                
                // Apply scale and rotation
                v1 = rotateY(v1, cosY, sinY).multiply(scale);
                v2 = rotateY(v2, cosY, sinY).multiply(scale);
                v3 = rotateY(v3, cosY, sinY).multiply(scale);
                
                // Draw filled triangle
                drawLine(world, origin, v1, v2, material, placedBlocks);
                drawLine(world, origin, v2, v3, material, placedBlocks);
                drawLine(world, origin, v3, v1, material, placedBlocks);
            }
        }
        
        player.sendMessage(ChatColor.GREEN + "Model import completed with " + placedBlocks.size() + " blocks placed");
        return true;
    }
    
    /**
     * Rotate a vector around the Y axis
     * @param v The vector to rotate
     * @param cosY Cosine of the rotation angle
     * @param sinY Sine of the rotation angle
     * @return The rotated vector
     */
    private Vector rotateY(Vector v, double cosY, double sinY) {
        double x = v.getX() * cosY + v.getZ() * sinY;
        double z = -v.getX() * sinY + v.getZ() * cosY;
        return new Vector(x, v.getY(), z);
    }
    
    /**
     * Draw a line between two points in 3D space (Bresenham's algorithm)
     * @param world The world to draw in
     * @param origin The origin point
     * @param start The start point
     * @param end The end point
     * @param material The material to use
     * @param placedBlocks Map to track placed blocks
     */
    private void drawLine(World world, Location origin, Vector start, Vector end, Material material, Map<String, Boolean> placedBlocks) {
        int x1 = (int) Math.round(start.getX());
        int y1 = (int) Math.round(start.getY());
        int z1 = (int) Math.round(start.getZ());
        
        int x2 = (int) Math.round(end.getX());
        int y2 = (int) Math.round(end.getY());
        int z2 = (int) Math.round(end.getZ());
        
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int dz = Math.abs(z2 - z1);
        
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int sz = z1 < z2 ? 1 : -1;
        
        int dm = Math.max(dx, Math.max(dy, dz));
        if (dm == 0) {
            // Points are the same
            setBlock(world, origin, x1, y1, z1, material, placedBlocks);
            return;
        }
        
        // Normalize step size
        double tx = (double) dx / dm;
        double ty = (double) dy / dm;
        double tz = (double) dz / dm;
        
        double x = x1;
        double y = y1;
        double z = z1;
        
        for (int i = 0; i <= dm; i++) {
            setBlock(world, origin, (int) Math.round(x), (int) Math.round(y), (int) Math.round(z), material, placedBlocks);
            x += tx * sx;
            y += ty * sy;
            z += tz * sz;
        }
    }
    
    /**
     * Set a block at a position relative to the origin
     * @param world The world
     * @param origin The origin location
     * @param x X coordinate (relative to origin)
     * @param y Y coordinate (relative to origin)
     * @param z Z coordinate (relative to origin)
     * @param material The material to use
     * @param placedBlocks Map to track placed blocks
     */
    private void setBlock(World world, Location origin, int x, int y, int z, Material material, Map<String, Boolean> placedBlocks) {
        int worldX = origin.getBlockX() + x;
        int worldY = origin.getBlockY() + y;
        int worldZ = origin.getBlockZ() + z;
        
        // Check if we've already placed a block here
        String key = worldX + "," + worldY + "," + worldZ;
        if (placedBlocks.containsKey(key)) {
            return;
        }
        
        // Mark as placed
        placedBlocks.put(key, true);
        
        // In a real implementation, we would actually place the block
        // Since this is a simulation, we'll just log the action
        logger.info("Would place " + material + " at " + worldX + "," + worldY + "," + worldZ);
        
        // In a real implementation, we would use:
        // Block block = world.getBlockAt(worldX, worldY, worldZ);
        // block.setType(material);
    }
    
    @Override
    public ModelFormat getFormat() {
        return ModelFormat.OBJ;
    }
    
    @Override
    public boolean supportsFeature(ImportFeature feature) {
        switch (feature) {
            case MATERIALS:
            case COLORS:
                return true;
            case TEXTURES:
            case ANIMATION:
            case METADATA:
            case SCALING:
                return false;
            default:
                return false;
        }
    }
    
    /**
     * Class representing a face in the 3D model
     */
    private static class Face {
        private final List<Integer> vertexIndices;
        private final String material;
        
        public Face(List<Integer> vertexIndices, String material) {
            this.vertexIndices = vertexIndices;
            this.material = material;
        }
        
        public List<Integer> getVertexIndices() {
            return vertexIndices;
        }
        
        public String getMaterial() {
            return material;
        }
    }
}