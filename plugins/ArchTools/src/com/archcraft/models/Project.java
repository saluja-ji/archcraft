package com.archcraft.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Represents an architectural project
 * Stores metadata about the project and its team
 */
public class Project {
    
    private final String name;
    private final UUID owner;
    private final String creationDate;
    private double scale;
    private final Set<UUID> collaborators;
    private final Set<UUID> invitedPlayers;
    private String description;
    
    /**
     * Create a new project
     * @param name Project name
     * @param owner UUID of the project owner
     */
    public Project(String name, UUID owner) {
        this.name = name;
        this.owner = owner;
        this.creationDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
        this.scale = 1.0; // Default scale: 1 block = 1 meter
        this.collaborators = new HashSet<>();
        this.invitedPlayers = new HashSet<>();
        this.description = "";
    }
    
    /**
     * Create a project with all parameters
     * @param name Project name
     * @param owner UUID of the project owner
     * @param creationDate Creation date string
     * @param scale Project scale
     * @param collaborators Set of collaborator UUIDs
     * @param invitedPlayers Set of invited player UUIDs
     * @param description Project description
     */
    public Project(String name, UUID owner, String creationDate, double scale, 
                  Set<UUID> collaborators, Set<UUID> invitedPlayers, String description) {
        this.name = name;
        this.owner = owner;
        this.creationDate = creationDate;
        this.scale = scale;
        this.collaborators = collaborators;
        this.invitedPlayers = invitedPlayers;
        this.description = description;
    }
    
    /**
     * Get the project name
     * @return Project name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Get the project owner's UUID
     * @return Owner UUID
     */
    public UUID getOwner() {
        return owner;
    }
    
    /**
     * Get the project creation date
     * @return Creation date string
     */
    public String getCreationDate() {
        return creationDate;
    }
    
    /**
     * Get the project scale (1 block = X meters)
     * @return Project scale
     */
    public double getScale() {
        return scale;
    }
    
    /**
     * Set the project scale
     * @param scale New scale value
     */
    public void setScale(double scale) {
        this.scale = scale;
    }
    
    /**
     * Get the set of collaborator UUIDs
     * @return Set of collaborator UUIDs
     */
    public Set<UUID> getCollaborators() {
        return new HashSet<>(collaborators);
    }
    
    /**
     * Add a collaborator to the project
     * @param uuid UUID of the collaborator to add
     * @return True if added, false if already present
     */
    public boolean addCollaborator(UUID uuid) {
        return collaborators.add(uuid);
    }
    
    /**
     * Remove a collaborator from the project
     * @param uuid UUID of the collaborator to remove
     * @return True if removed, false if not present
     */
    public boolean removeCollaborator(UUID uuid) {
        return collaborators.remove(uuid);
    }
    
    /**
     * Check if a player is a collaborator on this project
     * @param uuid UUID of the player to check
     * @return True if collaborator, false otherwise
     */
    public boolean isCollaborator(UUID uuid) {
        return collaborators.contains(uuid);
    }
    
    /**
     * Get the set of invited player UUIDs
     * @return Set of invited player UUIDs
     */
    public Set<UUID> getInvitedPlayers() {
        return new HashSet<>(invitedPlayers);
    }
    
    /**
     * Add a player to the invited list
     * @param uuid UUID of the player to invite
     * @return True if added, false if already present
     */
    public boolean addInvitedPlayer(UUID uuid) {
        return invitedPlayers.add(uuid);
    }
    
    /**
     * Remove a player from the invited list
     * @param uuid UUID of the player to remove
     * @return True if removed, false if not present
     */
    public boolean removeInvitedPlayer(UUID uuid) {
        return invitedPlayers.remove(uuid);
    }
    
    /**
     * Check if a player is invited to this project
     * @param uuid UUID of the player to check
     * @return True if invited, false otherwise
     */
    public boolean isInvited(UUID uuid) {
        return invitedPlayers.contains(uuid);
    }
    
    /**
     * Get the project description
     * @return Project description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Set the project description
     * @param description New description
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
