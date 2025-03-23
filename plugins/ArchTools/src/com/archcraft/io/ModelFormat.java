package com.archcraft.io;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Enum representing supported 3D model formats
 * Used for import and export operations
 */
public enum ModelFormat {
    OBJ("obj", "Wavefront OBJ", true, true),
    STL("stl", "Stereolithography", true, true),
    DAE("dae", "COLLADA", true, true),
    FBX("fbx", "Autodesk FBX", true, false),
    SKP("skp", "SketchUp", true, false),
    DXF("dxf", "AutoCAD DXF", true, true),
    IFC("ifc", "Industry Foundation Classes", true, false),
    GLB("glb", "GL Transmission Format", true, true),
    GLTF("gltf", "GL Transmission Format (Text)", true, true),
    JSON("json", "ArchCraft Native Format", true, true);
    
    private final String extension;
    private final String displayName;
    private final boolean importSupported;
    private final boolean exportSupported;
    
    /**
     * Constructor for model format
     * @param extension File extension (without dot)
     * @param displayName User-friendly display name
     * @param importSupported Whether import is supported
     * @param exportSupported Whether export is supported
     */
    ModelFormat(String extension, String displayName, boolean importSupported, boolean exportSupported) {
        this.extension = extension;
        this.displayName = displayName;
        this.importSupported = importSupported;
        this.exportSupported = exportSupported;
    }
    
    /**
     * Get the file extension for this format
     * @return File extension (without dot)
     */
    public String getExtension() {
        return extension;
    }
    
    /**
     * Get the user-friendly display name
     * @return Display name
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Check if import is supported for this format
     * @return True if import is supported
     */
    public boolean isImportSupported() {
        return importSupported;
    }
    
    /**
     * Check if export is supported for this format
     * @return True if export is supported
     */
    public boolean isExportSupported() {
        return exportSupported;
    }
    
    /**
     * Get the model format from a file extension
     * @param extension File extension (with or without dot)
     * @return ModelFormat enum value or null if not supported
     */
    public static ModelFormat fromExtension(String extension) {
        if (extension == null) {
            return null;
        }
        
        // Remove dot if present
        if (extension.startsWith(".")) {
            extension = extension.substring(1);
        }
        
        String lowerExtension = extension.toLowerCase();
        
        for (ModelFormat format : values()) {
            if (format.getExtension().equals(lowerExtension)) {
                return format;
            }
        }
        
        return null;
    }
    
    /**
     * Get all supported import formats
     * @return Set of formats that support import
     */
    public static Set<ModelFormat> getImportFormats() {
        Set<ModelFormat> importFormats = new HashSet<>();
        for (ModelFormat format : values()) {
            if (format.isImportSupported()) {
                importFormats.add(format);
            }
        }
        return importFormats;
    }
    
    /**
     * Get all supported export formats
     * @return Set of formats that support export
     */
    public static Set<ModelFormat> getExportFormats() {
        Set<ModelFormat> exportFormats = new HashSet<>();
        for (ModelFormat format : values()) {
            if (format.isExportSupported()) {
                exportFormats.add(format);
            }
        }
        return exportFormats;
    }
}