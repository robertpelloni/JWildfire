package org.jwildfire.visualizer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

/**
 * A prototype Raymarching renderer using a GLSL fragment shader.
 * Renders a simple Mandelbulb or similar distance field.
 */
public class RaymarchingVisualizer implements Visualizer {
    private boolean initialized = false;
    private int programId;
    private int vertexShaderId;
    private int fragmentShaderId;
    private float time = 0.0f;

    // Simple vertex shader: passes through full-screen quad coordinates
    private static final String VERTEX_SHADER = 
        "#version 120\n" +
        "void main() {\n" +
        "    gl_Position = gl_Vertex;\n" +
        "}";

    // Raymarching fragment shader (Mandelbulb-ish)
    private static final String FRAGMENT_SHADER = 
        "#version 120\n" +
        "uniform float time;\n" +
        "uniform vec2 resolution;\n" +
        "\n" +
        "// Distance Estimator for a Sphere (Placeholder for Mandelbulb)\n" +
        "float DE(vec3 p) {\n" +
        "    return length(p) - 1.0;\n" +
        "}\n" +
        "\n" +
        "void main() {\n" +
        "    vec2 uv = (gl_FragCoord.xy * 2.0 - resolution.xy) / resolution.y;\n" +
        "    vec3 ro = vec3(0.0, 0.0, -3.0); // Ray Origin\n" +
        "    vec3 rd = normalize(vec3(uv, 1.0)); // Ray Direction\n" +
        "    \n" +
        "    float t = 0.0;\n" +
        "    float d = 0.0;\n" +
        "    int steps = 0;\n" +
        "    \n" +
        "    // Raymarching Loop\n" +
        "    for(int i=0; i<64; i++) {\n" +
        "        vec3 p = ro + rd * t;\n" +
        "        d = DE(p);\n" +
        "        if(d < 0.001 || t > 10.0) break;\n" +
        "        t += d;\n" +
        "        steps = i;\n" +
        "    }\n" +
        "    \n" +
        "    vec3 col = vec3(0.0);\n" +
        "    if(t < 10.0) {\n" +
        "        // Simple lighting based on steps (AO-like)\n" +
        "        float glow = 1.0 - float(steps)/64.0;\n" +
        "        col = vec3(glow * 0.5, glow * 0.8, glow);\n" +
        "    }\n" +
        "    \n" +
        "    gl_FragColor = vec4(col, 1.0);\n" +
        "}";

    @Override
    public void init() {
        try {
            programId = GL20.glCreateProgram();
            
            vertexShaderId = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
            GL20.glShaderSource(vertexShaderId, VERTEX_SHADER);
            GL20.glCompileShader(vertexShaderId);
            if (GL20.glGetShaderi(vertexShaderId, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
                throw new RuntimeException("Vertex Shader Error: " + GL20.glGetShaderInfoLog(vertexShaderId));
            }

            fragmentShaderId = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
            GL20.glShaderSource(fragmentShaderId, FRAGMENT_SHADER);
            GL20.glCompileShader(fragmentShaderId);
            if (GL20.glGetShaderi(fragmentShaderId, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
                throw new RuntimeException("Fragment Shader Error: " + GL20.glGetShaderInfoLog(fragmentShaderId));
            }

            GL20.glAttachShader(programId, vertexShaderId);
            GL20.glAttachShader(programId, fragmentShaderId);
            GL20.glLinkProgram(programId);
            GL20.glValidateProgram(programId);

            initialized = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateAudio(float[] pcmData, float[] spectrum) {
        // Could modulate 'time' or sphere radius with audio
        time += 0.01f;
    }

    @Override
    public void render(int width, int height) {
        if (!initialized) return;

        GL11.glViewport(0, 0, width, height);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        GL20.glUseProgram(programId);
        
        int timeLoc = GL20.glGetUniformLocation(programId, "time");
        GL20.glUniform1f(timeLoc, time);
        
        int resLoc = GL20.glGetUniformLocation(programId, "resolution");
        GL20.glUniform2f(resLoc, (float)width, (float)height);

        // Draw full screen quad
        GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex2f(-1.0f, -1.0f);
            GL11.glVertex2f(1.0f, -1.0f);
            GL11.glVertex2f(1.0f, 1.0f);
            GL11.glVertex2f(-1.0f, 1.0f);
        GL11.glEnd();

        GL20.glUseProgram(0);
    }

    @Override
    public void dispose() {
        GL20.glDeleteShader(vertexShaderId);
        GL20.glDeleteShader(fragmentShaderId);
        GL20.glDeleteProgram(programId);
    }

    @Override
    public String getName() {
        return "Raymarching Prototype";
    }
}
