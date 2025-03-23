/**
 * ArchCraft Web Interface - Users JavaScript
 * Functionality for managing users in the architecture platform
 */

// User data storage
let users = [];
window.userCount = 0;

// Initialize user functionality when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    // Load users data
    loadUsers();
});

/**
 * Load users from the server
 */
function loadUsers() {
    // In a real application, this would make an API call to the server
    // For demonstration, we'll use sample data
    fetchUserData()
        .then(data => {
            users = data;
            window.userCount = users.length;
            renderUsersTable();
        })
        .catch(error => {
            console.error('Error loading users:', error);
            // Show empty state
            const tbody = document.querySelector('#usersTable tbody');
            if (tbody) {
                tbody.innerHTML = '<tr><td colspan="5" class="text-center">No users found or failed to load users.</td></tr>';
            }
        });
}

/**
 * Fetch user data
 * In a real application, this would connect to a backend API
 * @returns {Promise} Promise that resolves with user data
 */
function fetchUserData() {
    // Return a promise that resolves with empty data
    return Promise.resolve([]);
}

/**
 * Render the users table
 */
function renderUsersTable() {
    const tbody = document.querySelector('#usersTable tbody');
    if (!tbody) return;
    
    if (users.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" class="text-center">No users found.</td></tr>';
        return;
    }
    
    let html = '';
    
    users.forEach(user => {
        html += `
        <tr>
            <td>${escapeHtml(user.username)}</td>
            <td>${escapeHtml(user.role)}</td>
            <td>${user.projectCount}</td>
            <td>${escapeHtml(user.lastActive)}</td>
            <td>
                <button class="btn btn-sm btn-primary view-user-btn" data-user-id="${escapeHtml(user.id)}">
                    <i data-feather="eye" class="icon-sm"></i> View
                </button>
                <button class="btn btn-sm btn-secondary edit-user-btn" data-user-id="${escapeHtml(user.id)}">
                    <i data-feather="edit-2" class="icon-sm"></i> Edit
                </button>
                <button class="btn btn-sm btn-danger remove-user-btn" data-user-id="${escapeHtml(user.id)}">
                    <i data-feather="user-x" class="icon-sm"></i> Remove
                </button>
            </td>
        </tr>
        `;
    });
    
    tbody.innerHTML = html;
    
    // Re-initialize Feather icons for the newly added buttons
    feather.replace();
    
    // Add event listeners to the buttons
    addUserButtonListeners();
}

/**
 * Add event listeners to user action buttons
 */
function addUserButtonListeners() {
    // View user buttons
    document.querySelectorAll('.view-user-btn').forEach(button => {
        button.addEventListener('click', function() {
            const userId = this.getAttribute('data-user-id');
            viewUser(userId);
        });
    });
    
    // Edit user buttons
    document.querySelectorAll('.edit-user-btn').forEach(button => {
        button.addEventListener('click', function() {
            const userId = this.getAttribute('data-user-id');
            editUser(userId);
        });
    });
    
    // Remove user buttons
    document.querySelectorAll('.remove-user-btn').forEach(button => {
        button.addEventListener('click', function() {
            const userId = this.getAttribute('data-user-id');
            removeUser(userId);
        });
    });
}

/**
 * View a user
 * @param {string} userId - ID of the user to view
 */
function viewUser(userId) {
    const user = users.find(u => u.id === userId);
    if (!user) {
        alert(`User with ID "${userId}" not found.`);
        return;
    }
    
    alert(`Viewing user "${user.username}". In a real application, this would open the user details view.`);
}

/**
 * Edit a user
 * @param {string} userId - ID of the user to edit
 */
function editUser(userId) {
    const user = users.find(u => u.id === userId);
    if (!user) {
        alert(`User with ID "${userId}" not found.`);
        return;
    }
    
    alert(`Editing user "${user.username}". In a real application, this would open the user edit form.`);
}

/**
 * Remove a user
 * @param {string} userId - ID of the user to remove
 */
function removeUser(userId) {
    const user = users.find(u => u.id === userId);
    if (!user) {
        alert(`User with ID "${userId}" not found.`);
        return;
    }
    
    if (confirm(`Are you sure you want to remove "${user.username}" from the platform? This action cannot be undone.`)) {
        // In a real application, this would make an API call to remove the user
        users = users.filter(u => u.id !== userId);
        window.userCount = users.length;
        
        renderUsersTable();
        alert(`User "${user.username}" removed successfully.`);
    }
}

/**
 * Add a new user to the system
 * @param {Object} user - User object with details
 */
function addUser(user) {
    users.push(user);
    window.userCount = users.length;
    
    renderUsersTable();
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
