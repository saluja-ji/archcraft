#!/bin/bash
# ArchCraft - Professional Architecture Server Startup Script

# Default settings
MEMORY_MIN="1G"
MEMORY_MAX="4G"
JAR_FILE="spigot-1.16.5.jar"
SERVER_DIR="server"
RESTART_ON_CRASH="true"
BACKUP_ON_START="true"
LOG_DIR="logs"

# Ensure logs directory exists
mkdir -p "${LOG_DIR}"

# Current date for logging
DATE=$(date +"%Y-%m-%d_%H-%M-%S")
LOG_FILE="${LOG_DIR}/server_${DATE}.log"

# Function to check for and download the JAR file if needed
check_jar_file() {
    if [ ! -f "${JAR_FILE}" ]; then
        echo "Server JAR file not found. Simulating a Minecraft server JAR presence for demo purposes..."
        
        # Create an empty JAR file for demonstration
        # In a real environment, you would download the actual Spigot/Paper JAR
        touch "${JAR_FILE}"
        echo "Created placeholder JAR file. Note: In a production environment, you would need to download the actual server JAR."
        
        # Create the eula.txt file (accepting the EULA)
        echo "# By changing the setting below to TRUE you are indicating your agreement to our EULA (https://account.mojang.com/documents/minecraft_eula)." > eula.txt
        echo "# $(date)" >> eula.txt
        echo "eula=true" >> eula.txt
        
        # Create plugins directory if it doesn't exist
        mkdir -p "${SERVER_DIR}/plugins/ArchTools/data"
        
        echo "NOTE: This is just a simulation. In a real setup, you would need to download the actual Minecraft server JAR."
        echo "For a real implementation, visit https://www.spigotmc.org/ to download Spigot or https://papermc.io/ for Paper."
    fi
}

# Create backup function
create_backup() {
    echo "Creating backup..."
    BACKUP_DIR="backups"
    mkdir -p "${BACKUP_DIR}"
    BACKUP_FILE="${BACKUP_DIR}/archcraft_backup_${DATE}.zip"
    
    # Create zip archive of world directories and essential files if they exist
    if [ -d "${SERVER_DIR}/world" ] || [ -d "${SERVER_DIR}/plugins/ArchTools/data/" ]; then
        zip -r "${BACKUP_FILE}" "${SERVER_DIR}/world" "${SERVER_DIR}/world_nether" "${SERVER_DIR}/world_the_end" "${SERVER_DIR}/plugins/ArchTools/data/" > /dev/null 2>&1
        echo "Backup created at ${BACKUP_FILE}"
    else
        echo "No world data to back up yet."
    fi
}

# Display startup banner
echo "=================================================="
echo "  ArchCraft - Professional Architecture Server"
echo "=================================================="
echo "Starting server with ${MEMORY_MAX} maximum memory"
echo "Log file: ${LOG_FILE}"
echo ""

# Check if we have the server JAR file
check_jar_file

# Create backup if enabled
if [ "${BACKUP_ON_START}" = "true" ]; then
    create_backup
fi

# Function to start the server
start_server() {
    echo "Starting ArchCraft server simulation..."
    
    # Check if this is a real JAR file or just our placeholder
    if [ -s "${JAR_FILE}" ]; then
        # This is a real JAR file with content
        echo "Starting actual Minecraft server..."
        
        # Go to server directory
        cd "${SERVER_DIR}" || exit
        
        # Start the Minecraft server with specified memory settings
        # Add GC optimizations for better performance
        java -Xms${MEMORY_MIN} -Xmx${MEMORY_MAX} \
            -XX:+UseG1GC -XX:+ParallelRefProcEnabled \
            -XX:MaxGCPauseMillis=200 -XX:+UnlockExperimentalVMOptions \
            -XX:+DisableExplicitGC -XX:+AlwaysPreTouch \
            -XX:G1NewSizePercent=30 -XX:G1MaxNewSizePercent=40 \
            -XX:G1HeapRegionSize=8M -XX:G1ReservePercent=20 \
            -XX:G1HeapWastePercent=5 -XX:G1MixedGCCountTarget=4 \
            -XX:InitiatingHeapOccupancyPercent=15 \
            -XX:G1MixedGCLiveThresholdPercent=90 \
            -XX:G1RSetUpdatingPauseTimePercent=5 \
            -XX:SurvivorRatio=32 -XX:+PerfDisableSharedMem \
            -XX:MaxTenuringThreshold=1 \
            -jar "../${JAR_FILE}" nogui | tee -a "../${LOG_FILE}"
        
        # Return to previous directory
        cd ..
        
        # Get server exit code
        EXIT_CODE=$?
        
        echo "Server stopped with exit code ${EXIT_CODE}"
        
        # If restart on crash is enabled and exit code indicates crash
        if [ "${RESTART_ON_CRASH}" = "true" ] && [ ${EXIT_CODE} -ne 0 ]; then
            echo "Server crashed, restarting in 5 seconds..."
            sleep 5
            start_server
        fi
    else
        # This is our empty placeholder JAR
        echo "SIMULATION MODE: We're running in a demo environment."
        echo "In a real implementation, a Minecraft server would be running now."
        echo "For this demo, we'll simulate the server running by displaying information about ArchCraft features."
        
        # Log information to the log file
        echo "===============================" | tee -a "${LOG_FILE}"
        echo "ArchCraft Simulation Mode" | tee -a "${LOG_FILE}"
        echo "===============================" | tee -a "${LOG_FILE}"
        echo "Time: $(date)" | tee -a "${LOG_FILE}"
        echo "Features ready for demonstration:" | tee -a "${LOG_FILE}"
        echo "  - Professional Measurement Tools" | tee -a "${LOG_FILE}"
        echo "  - Zoning and Planning Tools" | tee -a "${LOG_FILE}"
        echo "  - Project Management System" | tee -a "${LOG_FILE}"
        echo "  - Team Collaboration Tools" | tee -a "${LOG_FILE}"
        echo "  - Scale Conversion Utilities" | tee -a "${LOG_FILE}"
        echo "===============================" | tee -a "${LOG_FILE}"
        
        # Keep the script running for a while to simulate the server
        echo "Press Ctrl+C to stop the simulation."
        for i in {1..300}; do
            echo -ne "Server simulation running: $i seconds\r"
            sleep 1
        done
        
        echo "Simulation completed after 5 minutes."
        EXIT_CODE=0
    fi
}

# Start the server
start_server

echo "Server has stopped. Goodbye!"
