package com.archcraft.models;

import org.bukkit.util.Vector;

import java.util.UUID;

/**
 * Represents a zone in the world
 * Zones are used for urban planning and designating areas for specific purposes
 */
public class Zone {
    
    private final String name;
    private String type;
    private final UUID creator;
    private final String world;
    private final Vector min;
    private final Vector max;
    
    /**
     * Create a new zone
     * @param name Zone name
     * @param type Zone type (residential, commercial, etc.)
     * @param creator UUID of the zone creator
     * @param world World name
     * @param min Minimum corner coordinates
     * @param max Maximum corner coordinates
     */
    public Zone(String name, String type, UUID creator, String world, Vector min, Vector max) {
        this.name = name;
        this.type = type;
        this.creator = creator;
        this.world = world;
        this.min = min;
        this.max = max;
    }
    
    /**
     * Get the zone name
     * @return Zone name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Get the zone type
     * @return Zone type
     */
    public String getType() {
        return type;
    }
    
    /**
     * Set the zone type
     * @param type New zone type
     */
    public void setType(String type) {
        this.type = type;
    }
    
    /**
     * Get the UUID of the zone creator
     * @return Creator UUID
     */
    public UUID getCreator() {
        return creator;
    }
    
    /**
     * Get the world name
     * @return World name
     */
    public String getWorld() {
        return world;
    }
    
    /**
     * Get the minimum corner coordinates
     * @return Min vector
     */
    public Vector getMin() {
        return min.clone();
    }
    
    /**
     * Get the maximum corner coordinates
     * @return Max vector
     */
    public Vector getMax() {
        return max.clone();
    }
    
    /**
     * Check if a vector is within this zone
     * @param vector Vector to check
     * @return True if within zone, false otherwise
     */
    public boolean contains(Vector vector) {
        return vector.isInAABB(min, max);
    }
    
    /**
     * Get the volume of the zone in blocks
     * @return Zone volume
     */
    public double getVolume() {
        return (max.getX() - min.getX() + 1) * 
               (max.getY() - min.getY() + 1) * 
               (max.getZ() - min.getZ() + 1);
    }
    
    /**
     * Get the width of the zone (X axis)
     * @return Zone width
     */
    public double getWidth() {
        return max.getX() - min.getX() + 1;
    }
    
    /**
     * Get the height of the zone (Y axis)
     * @return Zone height
     */
    public double getHeight() {
        return max.getY() - min.getY() + 1;
    }
    
    /**
     * Get the length of the zone (Z axis)
     * @return Zone length
     */
    public double getLength() {
        return max.getZ() - min.getZ() + 1;
    }
    
    /**
     * Get the area of the zone footprint (X-Z plane)
     * @return Zone footprint area
     */
    public double getArea() {
        return getWidth() * getLength();
    }
    
    @Override
    public String toString() {
        return "Zone{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", min=" + min +
                ", max=" + max +
                '}';
    }
}
