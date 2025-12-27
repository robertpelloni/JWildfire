package org.jwildfire.visualizer;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GLFWVisualizerRunner implements Runnable {
    private final Visualizer visualizer;
    private final AudioCapture audioCapture;
    private long window;
    private volatile boolean running = false;

    public GLFWVisualizerRunner(Visualizer visualizer, AudioCapture audioCapture) {
        this.visualizer = visualizer;
        this.audioCapture = audioCapture;
    }

    @Override
    public void run() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        window = glfwCreateWindow(800, 600, "JWildfire Music Visualizer (OpenGL)", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);

        // This line is critical for LWJGL's interoperation with GLFW's OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL bindings available for use.
        GL.createCapabilities();

        visualizer.init();
        running = true;

        while (!glfwWindowShouldClose(window) && running) {
            // Update audio data
            visualizer.updateAudio(audioCapture.getPcmData(), audioCapture.getSpectrumData());

            // Render
            int[] width = new int[1];
            int[] height = new int[1];
            glfwGetWindowSize(window, width, height);
            visualizer.render(width[0], height[0]);

            glfwSwapBuffers(window);
            glfwPollEvents();
        }

        visualizer.dispose();
        glfwDestroyWindow(window);
        glfwTerminate();
    }

    public void stop() {
        running = false;
    }
}
