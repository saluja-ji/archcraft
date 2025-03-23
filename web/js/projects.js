/**
 * ArchCraft Web Interface - Projects JavaScript
 * Functionality for managing architecture and urban planning projects
 */

// Project data storage
let projects = [];
window.projectCount = 0;
window.zoneCount = 0;

// Initialize project functionality when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    // Load projects data
    loadProjects();
    
    // Expose the addProject function globally for use in main.js
    window.addProject = addProject;
});

/**
 * Load projects from the server
 */
function loadProjects() {
    // In a real application, this would make an API call to the server
    // For demonstration, we'll use sample data
    fetchProjectData()
        .then(data => {
            projects = data;
            window.projectCount = projects.length;
            
            // Count zones across all projects
            window.zoneCount = projects.reduce((sum, project) => sum + (project.zones ? project.zones.length : 0), 0);
            
            renderProjectsTable();
        })
        .catch(error => {
            console.error('Error loading projects:', error);
            // Show empty state
            const tbody = document.querySelector('#projectsTable tbody');
            if (tbody) {
                tbody.innerHTML = '<tr><td colspan="6" class="text-center">No projects found or failed to load projects.</td></tr>';
            }
        });
}

/**
 * Fetch project data
 * In a real application, this would connect to a backend API
 * @returns {Promise} Promise that resolves with projects data
 */
function fetchProjectData() {
    // Return a promise that resolves with empty data
    return Promise.resolve([]);
}

/**
 * Render the projects table
 */
function renderProjectsTable() {
    const tbody = document.querySelector('#projectsTable tbody');
    if (!tbody) return;
    
    if (projects.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="text-center">No projects found. Create a new project to get started.</td></tr>';
        return;
    }
    
    let html = '';
    
    projects.forEach(project => {
        html += `
        <tr>
            <td>${escapeHtml(project.name)}</td>
            <td>${escapeHtml(project.owner)}</td>
            <td>${escapeHtml(project.created)}</td>
            <td>${escapeHtml(project.scale)}</td>
            <td>${project.teamSize}</td>
            <td>
                <button class="btn btn-sm btn-primary view-btn" data-project-id="${escapeHtml(project.name)}">
                    <i data-feather="eye" class="icon-sm"></i> View
                </button>
                <button class="btn btn-sm btn-secondary edit-btn" data-project-id="${escapeHtml(project.name)}">
                    <i data-feather="edit-2" class="icon-sm"></i> Edit
                </button>
                <button class="btn btn-sm btn-danger delete-btn" data-project-id="${escapeHtml(project.name)}">
                    <i data-feather="trash-2" class="icon-sm"></i> Delete
                </button>
            </td>
        </tr>
        `;
    });
    
    tbody.innerHTML = html;
    
    // Re-initialize Feather icons for the newly added buttons
    feather.replace();
    
    // Add event listeners to the buttons
    addProjectButtonListeners();
}

/**
 * Add event listeners to project action buttons
 */
function addProjectButtonListeners() {
    // View project buttons
    document.querySelectorAll('.view-btn').forEach(button => {
        button.addEventListener('click', function() {
            const projectId = this.getAttribute('data-project-id');
            viewProject(projectId);
        });
    });
    
    // Edit project buttons
    document.querySelectorAll('.edit-btn').forEach(button => {
        button.addEventListener('click', function() {
            const projectId = this.getAttribute('data-project-id');
            editProject(projectId);
        });
    });
    
    // Delete project buttons
    document.querySelectorAll('.delete-btn').forEach(button => {
        button.addEventListener('click', function() {
            const projectId = this.getAttribute('data-project-id');
            deleteProject(projectId);
        });
    });
}

/**
 * View a project
 * @param {string} projectId - ID of the project to view
 */
function viewProject(projectId) {
    const project = projects.find(p => p.name === projectId);
    if (!project) {
        alert(`Project "${projectId}" not found.`);
        return;
    }
    
    alert(`Viewing project "${projectId}". In a real application, this would open the project details view.`);
}

/**
 * Edit a project
 * @param {string} projectId - ID of the project to edit
 */
function editProject(projectId) {
    const project = projects.find(p => p.name === projectId);
    if (!project) {
        alert(`Project "${projectId}" not found.`);
        return;
    }
    
    alert(`Editing project "${projectId}". In a real application, this would open the project edit form.`);
}

/**
 * Delete a project
 * @param {string} projectId - ID of the project to delete
 */
function deleteProject(projectId) {
    const project = projects.find(p => p.name === projectId);
    if (!project) {
        alert(`Project "${projectId}" not found.`);
        return;
    }
    
    if (confirm(`Are you sure you want to delete project "${projectId}"? This action cannot be undone.`)) {
        // In a real application, this would make an API call to delete the project
        projects = projects.filter(p => p.name !== projectId);
        window.projectCount = projects.length;
        
        renderProjectsTable();
        alert(`Project "${projectId}" deleted successfully.`);
    }
}

/**
 * Add a new project to the list
 * @param {Object} project - Project object with details
 */
function addProject(project) {
    projects.push(project);
    window.projectCount = projects.length;
    
    renderProjectsTable();
}

/**
 * Escape HTML special characters to prevent XSS
 * @param {string} unsafe - The unsafe string
 * @returns {string} - The escaped string
 */
function escapeHtml(unsafe) {
    if (unsafe === undefined || unsafe === null) {
        return '';
    }
    
    return String(unsafe)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}
