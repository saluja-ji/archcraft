# ArchCraft User Guide

Welcome to ArchCraft, the professional Minecraft server platform designed specifically for architects, urban planners, and civil engineers. This guide will help you understand the features and tools available to you for collaborative design and visualization.

## Table of Contents

1. [Introduction](#introduction)
2. [Getting Started](#getting-started)
3. [Measurement Tools](#measurement-tools)
4. [Zoning System](#zoning-system)
5. [Project Management](#project-management)
6. [Team Collaboration](#team-collaboration)
7. [Best Practices](#best-practices)
8. [Troubleshooting](#troubleshooting)

## Introduction

ArchCraft transforms Minecraft into a powerful collaborative design tool for architecture and urban planning professionals. By leveraging Minecraft's flexibility and adding specialized tools, ArchCraft provides capabilities for:

- Precise measurements with real-world scale
- Urban zoning and planning
- Project management and version control
- Team collaboration with role-based permissions
- Visualization of designs in an immersive 3D environment

## Getting Started

### Logging In

1. Launch your Minecraft client (Java Edition 1.16.5)
2. Click "Multiplayer"
3. Click "Add Server"
4. Enter the server address provided by your administrator
5. Join the server

### Basic Controls

ArchCraft uses the standard Minecraft controls with additional commands for professional tools:

- **Movement**: WASD keys
- **Jump**: Space bar
- **Sneak/Crouch**: Shift key
- **Break block**: Left-click
- **Place block**: Right-click
- **Open inventory**: E key
- **Open chat/commands**: T key or / key

### First-Time Setup

When you first join, you'll start in the lobby area. Here you can:

1. Read information boards about available tools
2. Practice using measurement tools in the training area
3. View example projects for inspiration
4. Access your existing projects or create a new one

## Measurement Tools

ArchCraft provides precise measurement tools for architects to take accurate measurements within the virtual environment.

### Using the Measurement Tool

1. Type `/measure` to receive the measuring tool (golden axe)
2. Left-click on a block to set point A
3. Right-click on another block to set point B
4. The system will display:
   - 3D distance between points
   - Horizontal distance (2D)
   - Height difference
   - Area calculation (if applicable)
   - Volume calculation (if applicable)

### Setting Scale

ArchCraft allows you to set a custom scale to match your project requirements:

1. Type `/scale` to view the current scale
2. Type `/scale [value]` to set a new scale (e.g., `/scale 2` sets 1 block = 2 meters)
3. The scale affects all measurements and is displayed in the measurement results

Common scales:
- `1` - 1 block = 1 meter (default)
- `0.5` - 1 block = 0.5 meters (for detailed small-scale work)
- `2` - 1 block = 2 meters (for larger urban projects)
- `5` - 1 block = 5 meters (for city-scale planning)

## Zoning System

The zoning system helps urban planners designate different areas for specific purposes.

### Creating Zones

1. Type `/zone tool` to get the zoning tool (blaze rod)
2. Type `/zone create [name] [type]` to start creating a zone
   - Example: `/zone create downtown commercial`
3. Use the zoning tool to select the area:
   - Left-click for first corner
   - Right-click for second corner
4. The zone will be created and visualized according to its type

### Zone Types

ArchCraft supports various zone types, each with its own visualization color:

- `residential` - Green
- `commercial` - Blue
- `industrial` - Yellow
- `recreational` - Lime
- `educational` - Light Blue
- `transportation` - Gray
- `agricultural` - Brown
- `mixed` - Magenta

### Managing Zones

- `/zone list` - View all zones
- `/zone info [name]` - Get detailed information about a zone
- `/zone modify [name] [type]` - Change a zone's type
- `/zone delete [name]` - Delete a zone

## Project Management

ArchCraft provides project management capabilities to organize and track your architectural designs.

### Creating a Project

1. Type `/project create [name]` to create a new project
2. This will create a new world dedicated to your project

### Managing Projects

- `/project list` - View all your projects
- `/project load [name]` - Load an existing project
- `/project save [name]` - Save the current project
- `/project info [name]` - View project details
- `/project delete [name]` - Delete a project

## Team Collaboration

ArchCraft enables collaborative work with team-based permissions.

### Team Management

- `/team create [project]` - Create a team for a project
- `/team invite [player] [project]` - Invite a player to your team
- `/team remove [player] [project]` - Remove a player from your team
- `/team list [project]` - List team members
- `/team join [project]` - Join a project you've been invited to
- `/team leave [project]` - Leave a project

### Collaboration Best Practices

1. **Define roles** - Assign team members to specific tasks
2. **Regular saves** - Save your work frequently
3. **Communication** - Use in-game chat to coordinate
4. **Version control** - Create backup points for significant milestones

## Best Practices

### Efficient Workflows

1. **Plan before building** - Sketch designs on paper or design software first
2. **Use scale consistently** - Maintain the same scale throughout a project
3. **Layer approach** - Work on different elevations as separate layers
4. **Color coding** - Use different block types for different elements
5. **Documentation** - Take screenshots and notes of your progress

### Performance Tips

1. **Optimize view distance** - Reduce render distance if experiencing lag
2. **Divide large projects** - Split extensive projects into manageable sections
3. **Limit entities** - Minimize use of entities like item frames
4. **Regular backups** - Schedule regular backups to prevent data loss

## Troubleshooting

### Common Issues

**Issue**: Measurement tool doesn't work
**Solution**: Ensure you're using left-click for point A and right-click for point B

**Issue**: Can't create a zone
**Solution**: Make sure you have the correct permissions and are using the zone tool correctly

**Issue**: Project won't save
**Solution**: Check disk space on the server and try again

**Issue**: Can't join a project
**Solution**: Verify you have been invited and use `/team join [project]` to accept

### Getting Help

If you encounter issues not covered in this guide:

1. Ask an administrator on the server
2. Check the project documentation
3. Visit the support forum (if available)
4. Report bugs to your server administrator

---

This user guide provides the essential information to get started with ArchCraft. For more detailed information about specific tools and commands, refer to the [Tools Reference](tools_reference.md) document.
