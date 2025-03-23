package com.archcraft.commands;

import com.archcraft.Main;
import com.archcraft.io.ModelFormat;
import com.archcraft.io.ModelManager;
import com.archcraft.models.Project;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Command for managing architectural projects and models
 */
public class ProjectCommand implements CommandExecutor, TabCompleter {
    
    private final Main plugin;
    private final ModelManager modelManager;
    
    /**
     * Create a new project command
     * @param plugin The plugin instance
     */
    public ProjectCommand(Main plugin) {
        this.plugin = plugin;
        this.modelManager = plugin.getModelManager();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            sendHelpMessage(player);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "create":
                handleCreate(player, args);
                break;
            case "list":
                handleList(player);
                break;
            case "import":
                handleImport(player, args);
                break;
            case "export":
                handleExport(player, args);
                break;
            case "select":
                handleSelect(player, args);
                break;
            case "info":
                handleInfo(player);
                break;
            case "formats":
                handleFormats(player);
                break;
            default:
                player.sendMessage(ChatColor.RED + "Unknown sub-command: " + subCommand);
                sendHelpMessage(player);
                break;
        }
        
        return true;
    }
    
    /**
     * Handle the 'create' sub-command
     */
    private void handleCreate(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /project create <name>");
            return;
        }
        
        String projectName = args[1];
        
        // TODO: Create a new project
        player.sendMessage(ChatColor.GREEN + "Created new project: " + projectName);
    }
    
    /**
     * Handle the 'list' sub-command
     */
    private void handleList(Player player) {
        // TODO: List all projects
        player.sendMessage(ChatColor.YELLOW + "Projects:");
        player.sendMessage(ChatColor.GRAY + "No projects found (feature under development)");
    }
    
    /**
     * Handle the 'import' sub-command
     */
    private void handleImport(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /project import <filename> [scale] [rotationY]");
            return;
        }
        
        String fileName = args[1];
        double scale = 1.0;
        float rotationY = 0.0f;
        
        if (args.length >= 3) {
            try {
                scale = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Scale must be a number");
                return;
            }
        }
        
        if (args.length >= 4) {
            try {
                rotationY = Float.parseFloat(args[3]);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Rotation must be a number");
                return;
            }
        }
        
        File modelsDir = modelManager.getModelsDirectory();
        File modelFile = new File(modelsDir, fileName);
        
        if (!modelFile.exists()) {
            player.sendMessage(ChatColor.RED + "File not found: " + fileName);
            return;
        }
        
        World world = player.getWorld();
        Location origin = player.getLocation();
        
        player.sendMessage(ChatColor.YELLOW + "Starting import at your location...");
        boolean success = modelManager.importModel(modelFile, world, origin, scale, rotationY, player);
        
        if (!success) {
            player.sendMessage(ChatColor.RED + "Import failed. Check console for details.");
        }
    }
    
    /**
     * Handle the 'export' sub-command
     */
    private void handleExport(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /project export <filename> [scale]");
            player.sendMessage(ChatColor.RED + "You must select a region first with /project select");
            return;
        }
        
        String fileName = args[1];
        double scale = 1.0;
        
        if (args.length >= 3) {
            try {
                scale = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Scale must be a number");
                return;
            }
        }
        
        // TODO: Get selected region from player metadata
        player.sendMessage(ChatColor.RED + "You need to select a region first with /project select");
        
        // This is placeholder code that would be used once selection is implemented
        /*
        Location min = getSelectionMin(player);
        Location max = getSelectionMax(player);
        
        if (min == null || max == null) {
            player.sendMessage(ChatColor.RED + "You need to select a region first with /project select");
            return;
        }
        
        modelManager.exportModel(min, max, fileName, scale, player);
        */
    }
    
    /**
     * Handle the 'select' sub-command
     */
    private void handleSelect(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /project select <pos1|pos2|clear>");
            return;
        }
        
        String action = args[1].toLowerCase();
        
        switch (action) {
            case "pos1":
                // TODO: Set pos1 selection
                player.sendMessage(ChatColor.GREEN + "Set position 1 at your location");
                break;
            case "pos2":
                // TODO: Set pos2 selection
                player.sendMessage(ChatColor.GREEN + "Set position 2 at your location");
                break;
            case "clear":
                // TODO: Clear selection
                player.sendMessage(ChatColor.YELLOW + "Cleared selection");
                break;
            default:
                player.sendMessage(ChatColor.RED + "Unknown selection action: " + action);
                player.sendMessage(ChatColor.RED + "Usage: /project select <pos1|pos2|clear>");
                break;
        }
    }
    
    /**
     * Handle the 'info' sub-command
     */
    private void handleInfo(Player player) {
        // TODO: Show current project info
        player.sendMessage(ChatColor.YELLOW + "Project Info:");
        player.sendMessage(ChatColor.GRAY + "No active project (feature under development)");
    }
    
    /**
     * Handle the 'formats' sub-command
     */
    private void handleFormats(Player player) {
        player.sendMessage(ChatColor.YELLOW + "Supported Import Formats:");
        Set<ModelFormat> importFormats = modelManager.getSupportedImportFormats();
        if (importFormats.isEmpty()) {
            player.sendMessage(ChatColor.GRAY + "  None registered yet");
        } else {
            for (ModelFormat format : importFormats) {
                player.sendMessage(ChatColor.GRAY + "  " + format.getDisplayName() + " (." + format.getExtension() + ")");
            }
        }
        
        player.sendMessage(ChatColor.YELLOW + "Supported Export Formats:");
        Set<ModelFormat> exportFormats = modelManager.getSupportedExportFormats();
        if (exportFormats.isEmpty()) {
            player.sendMessage(ChatColor.GRAY + "  None registered yet");
        } else {
            for (ModelFormat format : exportFormats) {
                player.sendMessage(ChatColor.GRAY + "  " + format.getDisplayName() + " (." + format.getExtension() + ")");
            }
        }
    }
    
    /**
     * Send help message to player
     */
    private void sendHelpMessage(Player player) {
        player.sendMessage(ChatColor.YELLOW + "=== ArchCraft Project Commands ===");
        player.sendMessage(ChatColor.GRAY + "/project create <name> " + ChatColor.WHITE + "- Create a new project");
        player.sendMessage(ChatColor.GRAY + "/project list " + ChatColor.WHITE + "- List all projects");
        player.sendMessage(ChatColor.GRAY + "/project import <filename> [scale] [rotationY] " + ChatColor.WHITE + "- Import a 3D model");
        player.sendMessage(ChatColor.GRAY + "/project export <filename> [scale] " + ChatColor.WHITE + "- Export selection as 3D model");
        player.sendMessage(ChatColor.GRAY + "/project select <pos1|pos2|clear> " + ChatColor.WHITE + "- Select a region");
        player.sendMessage(ChatColor.GRAY + "/project info " + ChatColor.WHITE + "- Show info about current project");
        player.sendMessage(ChatColor.GRAY + "/project formats " + ChatColor.WHITE + "- List supported file formats");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            String partialCommand = args[0].toLowerCase();
            List<String> subCommands = Arrays.asList("create", "list", "import", "export", "select", "info", "formats");
            
            for (String subCommand : subCommands) {
                if (subCommand.startsWith(partialCommand)) {
                    completions.add(subCommand);
                }
            }
        } else if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            String partial = args[1].toLowerCase();
            
            if (subCommand.equals("select")) {
                List<String> options = Arrays.asList("pos1", "pos2", "clear");
                for (String option : options) {
                    if (option.startsWith(partial)) {
                        completions.add(option);
                    }
                }
            } else if (subCommand.equals("import")) {
                // TODO: List available model files
                File modelsDir = modelManager.getModelsDirectory();
                if (modelsDir.exists() && modelsDir.isDirectory()) {
                    for (File file : modelsDir.listFiles()) {
                        if (file.isFile() && file.getName().toLowerCase().startsWith(partial)) {
                            completions.add(file.getName());
                        }
                    }
                }
            }
        }
        
        return completions;
    }
}