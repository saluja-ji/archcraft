package com.archcraft;

import com.archcraft.commands.*;
import com.archcraft.io.ModelManager;
import com.archcraft.io.obj.ObjExporter;
import com.archcraft.io.obj.ObjImporter;
import com.archcraft.listeners.PlayerInteractListener;
import com.archcraft.utils.ConfigManager;
import com.archcraft.utils.DatabaseManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

/**
 * Main class for the ArchTools plugin
 * Provides professional architecture and urban planning tools
 */
public class Main extends JavaPlugin {
    
    private static Main instance;
    private ConfigManager configManager;
    private DatabaseManager databaseManager;
    private ModelManager modelManager;
    private Logger logger;
    
    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();
        
        // Initialize configuration
        saveDefaultConfig();
        configManager = new ConfigManager(this);
        
        // Initialize database
        databaseManager = new DatabaseManager(this);
        
        // Initialize model manager and register formats
        initializeModelManager();
        
        // Register commands
        registerCommands();
        
        // Register event listeners
        registerListeners();
        
        logger.info(ChatColor.GREEN + "ArchTools has been enabled!");
        logger.info(ChatColor.AQUA + "ArchCraft - Professional Architecture & Urban Planning Server");
    }
    
    /**
     * Initialize the model manager and register model formats
     */
    private void initializeModelManager() {
        modelManager = new ModelManager(this);
        
        // Register model importers and exporters
        modelManager.registerImporter(new ObjImporter(logger));
        modelManager.registerExporter(new ObjExporter());
        
        logger.info("Registered model import/export handlers");
    }
    
    @Override
    public void onDisable() {
        // Save any pending data
        if (databaseManager != null) {
            databaseManager.shutdown();
        }
        
        logger.info(ChatColor.RED + "ArchTools has been disabled!");
    }
    
    /**
     * Register all plugin commands
     */
    private void registerCommands() {
        getCommand("measure").setExecutor(new MeasureCommand(this));
        getCommand("scale").setExecutor(new ScaleCommand(this));
        getCommand("zone").setExecutor(new ZoneCommand(this));
        getCommand("project").setExecutor(new ProjectCommand(this));
        getCommand("team").setExecutor(new TeamCommand(this));
    }
    
    /**
     * Register all event listeners
     */
    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(this), this);
    }
    
    /**
     * Get the plugin instance
     * @return Main plugin instance
     */
    public static Main getInstance() {
        return instance;
    }
    
    /**
     * Get the config manager
     * @return ConfigManager instance
     */
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    /**
     * Get the database manager
     * @return DatabaseManager instance
     */
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
    
    /**
     * Get the model manager
     * @return ModelManager instance
     */
    public ModelManager getModelManager() {
        return modelManager;
    }
}
