package org.jwildfire.visualizer.projectm;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

/**
 * Experimental binding to libprojectM using Java 21 Foreign Function & Memory API (Preview).
 * 
 * Note: This requires --enable-preview JVM flag.
 */
public class ProjectMBinding {
    private static final Linker linker = Linker.nativeLinker();
    private static final SymbolLookup stdlib = linker.defaultLookup();
    private static final SymbolLookup loaderLookup = SymbolLookup.loaderLookup();

    // Handle to the library
    private static SymbolLookup projectMLib;

    // Method Handles
    private static MethodHandle projectM_create;
    private static MethodHandle projectM_render_frame;
    private static MethodHandle projectM_destroy;

    public static void init(String libraryPath) {
        System.load(libraryPath); // Load the DLL/SO
        projectMLib = SymbolLookup.loaderLookup();

        // Define signatures
        // projectM* projectM_create(int width, int height)
        projectM_create = linker.downcallHandle(
            projectMLib.find("projectM_create").orElseThrow(),
            FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT)
        );

        // void projectM_render_frame(projectM* instance)
        projectM_render_frame = linker.downcallHandle(
            projectMLib.find("projectM_render_frame").orElseThrow(),
            FunctionDescriptor.ofVoid(ValueLayout.ADDRESS)
        );
        
        // void projectM_destroy(projectM* instance)
        projectM_destroy = linker.downcallHandle(
            projectMLib.find("projectM_destroy").orElseThrow(),
            FunctionDescriptor.ofVoid(ValueLayout.ADDRESS)
        );
    }

    public static MemorySegment create(int width, int height) {
        try {
            return (MemorySegment) projectM_create.invokeExact(width, height);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static void renderFrame(MemorySegment instance) {
        try {
            projectM_render_frame.invokeExact(instance);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void destroy(MemorySegment instance) {
        try {
            projectM_destroy.invokeExact(instance);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
