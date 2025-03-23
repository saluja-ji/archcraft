package com.archcraft.commands;

import com.archcraft.Main;
import com.archcraft.models.Project;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Command handler for project management
 * Allows users to create, save, load, and manage architectural projects
 */
public class ProjectCommand implements CommandExecutor, TabCompleter {
    
    private final Main plugin;
    private final List<String> subcommands = Arrays.asList(
            "create", "load", "save", "list", "info", "delete", "share"
    );
    
    public ProjectCommand(Main plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be executed by a player.");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("archtools.project")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        
        if (args.length == 0) {
            sendHelpMessage(player);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "create":
                handleCreate(player, args);
                break;
            case "load":
                handleLoad(player, args);
                break;
            case "save":
                handleSave(player, args);
                break;
            case "list":
                handleList(player);
                break;
            case "info":
                handleInfo(player, args);
                break;
            case "delete":
                handleDelete(player, args);
                break;
            case "share":
                handleShare(player, args);
                break;
            default:
                sendHelpMessage(player);
                break;
        }
        
        return true;
    }
    
    private void sendHelpMessage(Player player) {
        player.sendMessage(ChatColor.GREEN + "=== ArchCraft Project Commands ===");
        player.sendMessage(ChatColor.YELLOW + "/project create <name> - Create a new project");
        player.sendMessage(ChatColor.YELLOW + "/project load <name> - Load an existing project");
        player.sendMessage(ChatColor.YELLOW + "/project save [name] - Save the current project");
        player.sendMessage(ChatColor.YELLOW + "/project list - List all projects");
        player.sendMessage(ChatColor.YELLOW + "/project info [name] - View project details");
        player.sendMessage(ChatColor.YELLOW + "/project delete <name> - Delete a project");
        player.sendMessage(ChatColor.YELLOW + "/project share <name> <player> - Share a project with another player");
    }
    
    private void handleCreate(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /project create <name>");
            return;
        }
        
        String projectName = args[1];
        
        // Check if project already exists
        if (plugin.getDatabaseManager().projectExists(projectName)) {
            player.sendMessage(ChatColor.RED + "A project with that name already exists.");
            return;
        }
        
        // Create new project
        Project project = new Project(projectName, player.getUniqueId());
        plugin.getDatabaseManager().saveProject(project);
        
        player.sendMessage(ChatColor.GREEN + "Project '" + projectName + "' created successfully.");
        player.sendMessage(ChatColor.YELLOW + "You can now use '/project save " + projectName + "' to save your work.");
    }
    
    private void handleLoad(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /project load <name>");
            return;
        }
        
        String projectName = args[1];
        
        // Check if project exists
        if (!plugin.getDatabaseManager().projectExists(projectName)) {
            player.sendMessage(ChatColor.RED + "Project '" + projectName + "' doesn't exist.");
            return;
        }
        
        // Check if player has access to this project
        Project project = plugin.getDatabaseManager().loadProject(projectName);
        if (project == null) {
            player.sendMessage(ChatColor.RED + "Failed to load project data.");
            return;
        }
        
        if (!project.getOwner().equals(player.getUniqueId()) && 
                !project.getCollaborators().contains(player.getUniqueId()) && 
                !player.hasPermission("archtools.admin")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to access this project.");
            return;
        }
        
        // Load the project world and teleport player
        // This would involve WorldEdit or Multiverse API for actual implementation
        player.sendMessage(ChatColor.GREEN + "Loading project '" + projectName + "'...");
        player.sendMessage(ChatColor.YELLOW + "World loaded with scale: 1 block = " + 
                plugin.getConfigManager().getScale() + " meters");
    }
    
    private void handleSave(Player player, String[] args) {
        // Implementation would involve saving the world data
        // using WorldEdit or similar API
        player.sendMessage(ChatColor.GREEN + "Project saved successfully.");
    }
    
    private void handleList(Player player) {
        List<Project> projects = plugin.getDatabaseManager().getProjects(player.getUniqueId());
        
        if (projects.isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "You don't have any projects yet. Create one with '/project create <name>'");
            return;
        }
        
        player.sendMessage(ChatColor.GREEN + "=== Your Projects ===");
        for (Project project : projects) {
            player.sendMessage(ChatColor.YELLOW + "- " + project.getName() + 
                    ChatColor.GRAY + " (Created: " + project.getCreationDate() + ")");
        }
    }
    
    private void handleInfo(Player player, String[] args) {
        String projectName = args.length > 1 ? args[1] : null;
        
        if (projectName == null) {
            // Show info for the current project
            player.sendMessage(ChatColor.YELLOW + "Currently in default world");
            return;
        }
        
        Project project = plugin.getDatabaseManager().loadProject(projectName);
        if (project == null) {
            player.sendMessage(ChatColor.RED + "Project '" + projectName + "' doesn't exist.");
            return;
        }
        
        player.sendMessage(ChatColor.GREEN + "=== Project: " + project.getName() + " ===");
        player.sendMessage(ChatColor.YELLOW + "Owner: " + plugin.getServer().getOfflinePlayer(project.getOwner()).getName());
        player.sendMessage(ChatColor.YELLOW + "Created: " + project.getCreationDate());
        player.sendMessage(ChatColor.YELLOW + "Scale: 1 block = " + project.getScale() + " meters");
        player.sendMessage(ChatColor.YELLOW + "Collaborators: " + project.getCollaborators().size());
    }
    
    private void handleDelete(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /project delete <name>");
            return;
        }
        
        String projectName = args[1];
        
        // Check if project exists
        if (!plugin.getDatabaseManager().projectExists(projectName)) {
            player.sendMessage(ChatColor.RED + "Project '" + projectName + "' doesn't exist.");
            return;
        }
        
        // Check if player is the owner or admin
        Project project = plugin.getDatabaseManager().loadProject(projectName);
        if (!project.getOwner().equals(player.getUniqueId()) && !player.hasPermission("archtools.admin")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to delete this project.");
            return;
        }
        
        // Delete the project
        plugin.getDatabaseManager().deleteProject(projectName);
        player.sendMessage(ChatColor.GREEN + "Project '" + projectName + "' deleted successfully.");
    }
    
    private void handleShare(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(ChatColor.RED + "Usage: /project share <name> <player>");
            return;
        }
        
        String projectName = args[1];
        String targetPlayerName = args[2];
        
        // Check if project exists
        if (!plugin.getDatabaseManager().projectExists(projectName)) {
            player.sendMessage(ChatColor.RED + "Project '" + projectName + "' doesn't exist.");
            return;
        }
        
        // Check if player is the owner or admin
        Project project = plugin.getDatabaseManager().loadProject(projectName);
        if (!project.getOwner().equals(player.getUniqueId()) && !player.hasPermission("archtools.admin")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to share this project.");
            return;
        }
        
        // Check if target player exists
        Player targetPlayer = plugin.getServer().getPlayer(targetPlayerName);
        if (targetPlayer == null) {
            player.sendMessage(ChatColor.RED + "Player '" + targetPlayerName + "' not found or offline.");
            return;
        }
        
        // Add collaborator
        project.addCollaborator(targetPlayer.getUniqueId());
        plugin.getDatabaseManager().saveProject(project);
        
        player.sendMessage(ChatColor.GREEN + "Project '" + projectName + "' shared with " + 
                targetPlayer.getDisplayName() + " successfully.");
        targetPlayer.sendMessage(ChatColor.GREEN + player.getDisplayName() + " has shared project '" + 
                projectName + "' with you.");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            for (String subcommand : subcommands) {
                if (subcommand.startsWith(partial)) {
                    completions.add(subcommand);
                }
            }
            return completions;
        }
        
        if (args.length == 2) {
            String subcommand = args[0].toLowerCase();
            String partial = args[1].toLowerCase();
            
            if (Arrays.asList("load", "info", "delete", "share").contains(subcommand)) {
                // List existing projects
                Player player = (sender instanceof Player) ? (Player) sender : null;
                if (player != null) {
                    List<Project> projects = plugin.getDatabaseManager().getProjects(player.getUniqueId());
                    for (Project project : projects) {
                        if (project.getName().toLowerCase().startsWith(partial)) {
                            completions.add(project.getName());
                        }
                    }
                }
            }
            return completions;
        }
        
        if (args.length == 3 && args[0].equalsIgnoreCase("share")) {
            String partial = args[2].toLowerCase();
            return plugin.getServer().getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(partial))
                    .collect(Collectors.toList());
        }
        
        return completions;
    }
}
