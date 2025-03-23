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
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Command handler for team management
 * Allows project owners to manage collaborators
 */
public class TeamCommand implements CommandExecutor, TabCompleter {
    
    private final Main plugin;
    private final List<String> subcommands = Arrays.asList(
            "create", "invite", "remove", "list", "join", "leave"
    );
    
    public TeamCommand(Main plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be executed by a player.");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("archtools.team")) {
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
            case "invite":
                handleInvite(player, args);
                break;
            case "remove":
                handleRemove(player, args);
                break;
            case "list":
                handleList(player, args);
                break;
            case "join":
                handleJoin(player, args);
                break;
            case "leave":
                handleLeave(player, args);
                break;
            default:
                sendHelpMessage(player);
                break;
        }
        
        return true;
    }
    
    private void sendHelpMessage(Player player) {
        player.sendMessage(ChatColor.GREEN + "=== ArchCraft Team Commands ===");
        player.sendMessage(ChatColor.YELLOW + "/team create <project> - Create a team for a project");
        player.sendMessage(ChatColor.YELLOW + "/team invite <player> [project] - Invite a player to your team");
        player.sendMessage(ChatColor.YELLOW + "/team remove <player> [project] - Remove a player from your team");
        player.sendMessage(ChatColor.YELLOW + "/team list [project] - List team members");
        player.sendMessage(ChatColor.YELLOW + "/team join <project> - Join a team you were invited to");
        player.sendMessage(ChatColor.YELLOW + "/team leave <project> - Leave a team");
    }
    
    private void handleCreate(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /team create <project>");
            return;
        }
        
        String projectName = args[1];
        
        // Check if project exists
        if (!plugin.getDatabaseManager().projectExists(projectName)) {
            player.sendMessage(ChatColor.RED + "Project '" + projectName + "' doesn't exist.");
            return;
        }
        
        // Load project
        Project project = plugin.getDatabaseManager().loadProject(projectName);
        
        // Check if player is the owner
        if (!project.getOwner().equals(player.getUniqueId()) && !player.hasPermission("archtools.admin")) {
            player.sendMessage(ChatColor.RED + "You must be the project owner to create a team.");
            return;
        }
        
        // Team is implicitly created with the project, just confirm
        player.sendMessage(ChatColor.GREEN + "Team for project '" + projectName + "' is ready.");
        player.sendMessage(ChatColor.YELLOW + "Use '/team invite <player> " + projectName + "' to invite members.");
    }
    
    private void handleInvite(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /team invite <player> [project]");
            return;
        }
        
        String targetPlayerName = args[1];
        String projectName = args.length > 2 ? args[2] : null;
        
        // If no project specified, check if player is in a project world
        if (projectName == null) {
            player.sendMessage(ChatColor.RED + "Please specify a project name.");
            return;
        }
        
        // Check if project exists
        if (!plugin.getDatabaseManager().projectExists(projectName)) {
            player.sendMessage(ChatColor.RED + "Project '" + projectName + "' doesn't exist.");
            return;
        }
        
        // Load project
        Project project = plugin.getDatabaseManager().loadProject(projectName);
        
        // Check if player is the owner
        if (!project.getOwner().equals(player.getUniqueId()) && !player.hasPermission("archtools.admin")) {
            player.sendMessage(ChatColor.RED + "You must be the project owner to invite members.");
            return;
        }
        
        // Check if target player exists
        Player targetPlayer = plugin.getServer().getPlayer(targetPlayerName);
        if (targetPlayer == null) {
            player.sendMessage(ChatColor.RED + "Player '" + targetPlayerName + "' not found or offline.");
            return;
        }
        
        // Add player to invited list
        project.addInvitedPlayer(targetPlayer.getUniqueId());
        plugin.getDatabaseManager().saveProject(project);
        
        player.sendMessage(ChatColor.GREEN + "Invited " + targetPlayer.getDisplayName() + 
                " to project '" + projectName + "'.");
        
        targetPlayer.sendMessage(ChatColor.GREEN + "You have been invited to join project '" + 
                projectName + "' by " + player.getDisplayName() + ".");
        targetPlayer.sendMessage(ChatColor.YELLOW + "Use '/team join " + projectName + "' to accept.");
    }
    
    private void handleRemove(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /team remove <player> [project]");
            return;
        }
        
        String targetPlayerName = args[1];
        String projectName = args.length > 2 ? args[2] : null;
        
        // If no project specified, check if player is in a project world
        if (projectName == null) {
            player.sendMessage(ChatColor.RED + "Please specify a project name.");
            return;
        }
        
        // Check if project exists
        if (!plugin.getDatabaseManager().projectExists(projectName)) {
            player.sendMessage(ChatColor.RED + "Project '" + projectName + "' doesn't exist.");
            return;
        }
        
        // Load project
        Project project = plugin.getDatabaseManager().loadProject(projectName);
        
        // Check if player is the owner
        if (!project.getOwner().equals(player.getUniqueId()) && !player.hasPermission("archtools.admin")) {
            player.sendMessage(ChatColor.RED + "You must be the project owner to remove members.");
            return;
        }
        
        // Check if target player exists
        Player targetPlayer = plugin.getServer().getPlayer(targetPlayerName);
        UUID targetUUID;
        
        if (targetPlayer != null) {
            targetUUID = targetPlayer.getUniqueId();
        } else {
            // Try to find offline player
            targetUUID = plugin.getServer().getOfflinePlayer(targetPlayerName).getUniqueId();
        }
        
        // Remove player from collaborators and invited list
        if (project.removeCollaborator(targetUUID) || project.removeInvitedPlayer(targetUUID)) {
            plugin.getDatabaseManager().saveProject(project);
            player.sendMessage(ChatColor.GREEN + "Removed " + targetPlayerName + 
                    " from project '" + projectName + "'.");
            
            if (targetPlayer != null) {
                targetPlayer.sendMessage(ChatColor.YELLOW + "You have been removed from project '" + 
                        projectName + "'.");
            }
        } else {
            player.sendMessage(ChatColor.RED + "Player '" + targetPlayerName + 
                    "' is not a member of this project.");
        }
    }
    
    private void handleList(Player player, String[] args) {
        String projectName = args.length > 1 ? args[1] : null;
        
        // If no project specified, check if player is in a project world
        if (projectName == null) {
            player.sendMessage(ChatColor.RED + "Please specify a project name.");
            return;
        }
        
        // Check if project exists
        if (!plugin.getDatabaseManager().projectExists(projectName)) {
            player.sendMessage(ChatColor.RED + "Project '" + projectName + "' doesn't exist.");
            return;
        }
        
        // Load project
        Project project = plugin.getDatabaseManager().loadProject(projectName);
        
        // Check if player has access to this project
        if (!project.getOwner().equals(player.getUniqueId()) && 
                !project.getCollaborators().contains(player.getUniqueId()) &&
                !player.hasPermission("archtools.admin")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to view this team.");
            return;
        }
        
        player.sendMessage(ChatColor.GREEN + "=== Team for Project: " + project.getName() + " ===");
        player.sendMessage(ChatColor.YELLOW + "Owner: " + 
                plugin.getServer().getOfflinePlayer(project.getOwner()).getName());
        
        if (project.getCollaborators().isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "Collaborators: None");
        } else {
            player.sendMessage(ChatColor.YELLOW + "Collaborators:");
            for (UUID uuid : project.getCollaborators()) {
                player.sendMessage(ChatColor.GRAY + "- " + 
                        plugin.getServer().getOfflinePlayer(uuid).getName());
            }
        }
        
        if (project.getInvitedPlayers().isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "Invited Players: None");
        } else {
            player.sendMessage(ChatColor.YELLOW + "Invited Players:");
            for (UUID uuid : project.getInvitedPlayers()) {
                player.sendMessage(ChatColor.GRAY + "- " + 
                        plugin.getServer().getOfflinePlayer(uuid).getName());
            }
        }
    }
    
    private void handleJoin(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /team join <project>");
            return;
        }
        
        String projectName = args[1];
        
        // Check if project exists
        if (!plugin.getDatabaseManager().projectExists(projectName)) {
            player.sendMessage(ChatColor.RED + "Project '" + projectName + "' doesn't exist.");
            return;
        }
        
        // Load project
        Project project = plugin.getDatabaseManager().loadProject(projectName);
        
        // Check if player is invited
        if (!project.getInvitedPlayers().contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You haven't been invited to this project.");
            return;
        }
        
        // Add player to collaborators and remove from invited list
        project.addCollaborator(player.getUniqueId());
        project.removeInvitedPlayer(player.getUniqueId());
        plugin.getDatabaseManager().saveProject(project);
        
        player.sendMessage(ChatColor.GREEN + "You have joined project '" + projectName + "'.");
        
        // Notify the owner if online
        Player owner = plugin.getServer().getPlayer(project.getOwner());
        if (owner != null) {
            owner.sendMessage(ChatColor.GREEN + player.getDisplayName() + 
                    " has joined your project '" + projectName + "'.");
        }
    }
    
    private void handleLeave(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /team leave <project>");
            return;
        }
        
        String projectName = args[1];
        
        // Check if project exists
        if (!plugin.getDatabaseManager().projectExists(projectName)) {
            player.sendMessage(ChatColor.RED + "Project '" + projectName + "' doesn't exist.");
            return;
        }
        
        // Load project
        Project project = plugin.getDatabaseManager().loadProject(projectName);
        
        // Check if player is the owner
        if (project.getOwner().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "As the owner, you cannot leave the project. " +
                    "Use '/project delete " + projectName + "' to delete it instead.");
            return;
        }
        
        // Check if player is a collaborator
        if (!project.getCollaborators().contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You are not a member of this project.");
            return;
        }
        
        // Remove player from collaborators
        project.removeCollaborator(player.getUniqueId());
        plugin.getDatabaseManager().saveProject(project);
        
        player.sendMessage(ChatColor.GREEN + "You have left project '" + projectName + "'.");
        
        // Notify the owner if online
        Player owner = plugin.getServer().getPlayer(project.getOwner());
        if (owner != null) {
            owner.sendMessage(ChatColor.YELLOW + player.getDisplayName() + 
                    " has left your project '" + projectName + "'.");
        }
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
            
            if (subcommand.equals("invite") || subcommand.equals("remove")) {
                // List online players
                return plugin.getServer().getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(name -> name.toLowerCase().startsWith(partial))
                        .collect(Collectors.toList());
            } else if (Arrays.asList("join", "leave", "list", "create").contains(subcommand)) {
                // List projects
                Player player = (sender instanceof Player) ? (Player) sender : null;
                if (player != null) {
                    List<Project> projects = plugin.getDatabaseManager().getProjects(player.getUniqueId());
                    for (Project project : projects) {
                        if (project.getName().toLowerCase().startsWith(partial)) {
                            completions.add(project.getName());
                        }
                    }
                }
                return completions;
            }
        }
        
        if (args.length == 3 && (args[0].equalsIgnoreCase("invite") || args[0].equalsIgnoreCase("remove"))) {
            String partial = args[2].toLowerCase();
            Player player = (sender instanceof Player) ? (Player) sender : null;
            if (player != null) {
                List<Project> projects = plugin.getDatabaseManager().getProjects(player.getUniqueId());
                for (Project project : projects) {
                    if (project.getName().toLowerCase().startsWith(partial)) {
                        completions.add(project.getName());
                    }
                }
            }
            return completions;
        }
        
        return completions;
    }
}
