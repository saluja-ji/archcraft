/**
 * ArchCraft Web Interface - Main JavaScript
 * Main functionality for the ArchCraft professional architecture and urban planning platform
 */

// Initialize the application when DOM is fully loaded
document.addEventListener('DOMContentLoaded', function() {
    // Initialize Feather Icons
    feather.replace();
    
    // Initialize Bootstrap components
    initializeBootstrapComponents();
    
    // Load server status
    loadServerStatus();
    
    // Set up event listeners
    setupEventListeners();
    
    // Periodically refresh server data
    setInterval(loadServerStatus, 60000); // Refresh every minute
});

/**
 * Initialize Bootstrap components like tooltips, popovers, etc.
 */
function initializeBootstrapComponents() {
    // Initialize tooltips
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
    
    // Initialize modals
    const newProjectModal = document.getElementById('newProjectModal');
    if (newProjectModal) {
        window.newProjectModalInstance = new bootstrap.Modal(newProjectModal);
    }
}

/**
 * Set up event listeners for various UI components
 */
function setupEventListeners() {
    // Login button
    const loginBtn = document.getElementById('loginBtn');
    if (loginBtn) {
        loginBtn.addEventListener('click', function() {
            // In a real application, this would open a login modal or redirect to a login page
            alert('Login functionality would be implemented here. For now, assume you are logged in as an admin.');
            loginBtn.textContent = 'Logged In';
            loginBtn.classList.remove('btn-outline-light');
            loginBtn.classList.add('btn-light');
        });
    }
    
    // Server control buttons
    const restartBtn = document.getElementById('restartBtn');
    if (restartBtn) {
        restartBtn.addEventListener('click', function() {
            if (confirm('Are you sure you want to restart the server? All players will be disconnected.')) {
                sendServerCommand('restart');
            }
        });
    }
    
    const stopBtn = document.getElementById('stopBtn');
    if (stopBtn) {
        stopBtn.addEventListener('click', function() {
            if (confirm('Are you sure you want to stop the server? All players will be disconnected and the server will shut down.')) {
                sendServerCommand('stop');
            }
        });
    }
    
    const backupBtn = document.getElementById('backupBtn');
    if (backupBtn) {
        backupBtn.addEventListener('click', function() {
            sendServerCommand('backup');
        });
    }
    
    // New project button in modal
    const createProjectBtn = document.getElementById('createProjectBtn');
    if (createProjectBtn) {
        createProjectBtn.addEventListener('click', function() {
            createNewProject();
        });
    }
    
    // New project button to open modal
    const newProjectBtn = document.getElementById('newProjectBtn');
    if (newProjectBtn) {
        newProjectBtn.addEventListener('click', function() {
            window.newProjectModalInstance.show();
        });
    }
    
    // Manage users button
    const manageUsersBtn = document.getElementById('manageUsersBtn');
    if (manageUsersBtn) {
        manageUsersBtn.addEventListener('click', function() {
            alert('User management would be implemented here.');
        });
    }
}

/**
 * Load server status information
 */
function loadServerStatus() {
    // In a real application, this would make an API call to the server
    // For now, we'll display sample data that would be retrieved
    
    // Set server information
    document.getElementById('serverStatus').textContent = 'Online';
    document.getElementById('serverStatus').className = 'badge bg-success';
    document.getElementById('serverAddress').textContent = 'play.archcraft.example.com';
    document.getElementById('serverVersion').textContent = 'Paper 1.16.5';
    
    // Generate simulated uptime
    const now = new Date();
    const startDate = new Date(now.getTime() - (Math.random() * 7 * 24 * 60 * 60 * 1000)); // Random time in the last week
    const uptime = getTimeDifference(startDate, now);
    document.getElementById('serverUptime').textContent = uptime;
    
    // Set performance metrics
    const memoryUsed = Math.floor(Math.random() * 3 + 1); // Random number between 1-4
    document.getElementById('memoryUsage').textContent = `${memoryUsed}GB / 4GB`;
    
    const tps = (20 - Math.random() * 0.5).toFixed(1); // Random TPS between 19.5-20.0
    document.getElementById('serverTPS').textContent = tps;
    
    // Generate random online player count
    const onlinePlayers = Math.floor(Math.random() * 5);
    document.getElementById('onlinePlayers').textContent = `${onlinePlayers} / 20`;
    
    // Set world count
    document.getElementById('loadedWorlds').textContent = `${Math.floor(Math.random() * 3) + 1}`;
    
    // Update dashboard counts
    updateDashboardCounts();
}

/**
 * Send a command to the server
 * @param {string} command - The command to send
 */
function sendServerCommand(command) {
    // In a real application, this would make an API call to the server
    // For now, we'll just simulate responses
    
    switch(command) {
        case 'restart':
            alert('Server restart initiated. This will take about 30 seconds.');
            break;
        case 'stop':
            alert('Server stopping. You will need to manually restart it.');
            document.getElementById('serverStatus').textContent = 'Offline';
            document.getElementById('serverStatus').className = 'badge bg-danger';
            break;
        case 'backup':
            alert('Backup process started. This will take a few minutes depending on world size.');
            break;
        default:
            alert(`Unknown command: ${command}`);
            break;
    }
}

/**
 * Update the dashboard count displays
 */
function updateDashboardCounts() {
    // Get counts from the project and user modules
    const projectCount = window.projectCount || 0;
    const zoneCount = window.zoneCount || 0;
    const userCount = window.userCount || 0;
    
    // Update the display
    document.getElementById('projectCount').textContent = projectCount;
    document.getElementById('zoneCount').textContent = zoneCount;
    document.getElementById('userCount').textContent = userCount;
}

/**
 * Create a new project
 */
function createNewProject() {
    const projectName = document.getElementById('projectName').value;
    const projectScale = document.getElementById('projectScale').value;
    const projectDescription = document.getElementById('projectDescription').value;
    const worldType = document.getElementById('worldType').value;
    
    if (!projectName) {
        alert('Project name is required');
        return;
    }
    
    // In a real application, this would make an API call to create the project
    // For now, we'll just add it to the projects list
    
    const project = {
        name: projectName,
        owner: 'Current User',
        created: new Date().toLocaleDateString(),
        scale: projectScale,
        teamSize: 1,
        description: projectDescription,
        worldType: worldType
    };
    
    // Add the project
    window.addProject(project);
    
    // Close the modal
    window.newProjectModalInstance.hide();
    
    // Clear the form
    document.getElementById('newProjectForm').reset();
    
    alert(`Project "${projectName}" created successfully.`);
}

/**
 * Calculate time difference between two dates and return a formatted string
 * @param {Date} startDate - The start date
 * @param {Date} endDate - The end date
 * @returns {string} Formatted time difference
 */
function getTimeDifference(startDate, endDate) {
    const diffMs = endDate - startDate;
    const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24));
    const diffHrs = Math.floor((diffMs % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
    const diffMins = Math.floor((diffMs % (1000 * 60 * 60)) / (1000 * 60));
    
    let result = '';
    if (diffDays > 0) {
        result += `${diffDays} day${diffDays !== 1 ? 's' : ''}, `;
    }
    if (diffHrs > 0) {
        result += `${diffHrs} hour${diffHrs !== 1 ? 's' : ''}, `;
    }
    if (diffMins > 0) {
        result += `${diffMins} minute${diffMins !== 1 ? 's' : ''}`;
    }
    
    return result.endsWith(', ') ? result.slice(0, -2) : result;
}
