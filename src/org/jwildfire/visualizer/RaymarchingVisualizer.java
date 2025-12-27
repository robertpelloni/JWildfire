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

    // Raymarching fragment shader (Mandelbulb)
    public static final String SHADER_MANDELBULB = 
        "#version 120\n" +
        "uniform float time;\n" +
        "uniform vec2 resolution;\n" +
        "\n" +
        "// Mandelbulb Distance Estimator\n" +
        "float DE(vec3 pos) {\n" +
        "    float Power = 8.0 + sin(time*0.1)*2.0;\n" +
        "    vec3 z = pos;\n" +
        "    float dr = 1.0;\n" +
        "    float r = 0.0;\n" +
        "    for (int i = 0; i < 15; i++) {\n" +
        "        r = length(z);\n" +
        "        if (r > 2.0) break;\n" +
        "        \n" +
        "        // Convert to polar coordinates\n" +
        "        float theta = acos(z.z/r);\n" +
        "        float phi = atan(z.y, z.x);\n" +
        "        dr =  pow( r, Power-1.0)*Power*dr + 1.0;\n" +
        "        \n" +
        "        // Scale and rotate the point\n" +
        "        float zr = pow( r, Power);\n" +
        "        theta = theta*Power;\n" +
        "        phi = phi*Power;\n" +
        "        \n" +
        "        // Convert back to cartesian coordinates\n" +
        "        z = zr*vec3(sin(theta)*cos(phi), sin(phi)*sin(theta), cos(theta));\n" +
        "        z += pos;\n" +
        "    }\n" +
        "    return 0.5*log(r)*r/dr;\n" +
        "}\n" +
        "\n" +
        "void main() {\n" +
        "    vec2 uv = (gl_FragCoord.xy * 2.0 - resolution.xy) / resolution.y;\n" +
        "    \n" +
        "    // Camera setup\n" +
        "    float camDist = 2.5;\n" +
        "    float camAngle = time * 0.2;\n" +
        "    vec3 ro = vec3(camDist * sin(camAngle), 0.0, camDist * cos(camAngle)); // Ray Origin\n" +
        "    vec3 ta = vec3(0.0); // Target\n" +
        "    \n" +
        "    vec3 ww = normalize(ta - ro);\n" +
        "    vec3 uu = normalize(cross(ww, vec3(0.0, 1.0, 0.0)));\n" +
        "    vec3 vv = normalize(cross(uu, ww));\n" +
        "    vec3 rd = normalize(uv.x*uu + uv.y*vv + 1.5*ww); // Ray Direction\n" +
        "    \n" +
        "    float t = 0.0;\n" +
        "    float d = 0.0;\n" +
        "    int steps = 0;\n" +
        "    \n" +
        "    // Raymarching Loop\n" +
        "    for(int i=0; i<100; i++) {\n" +
        "        vec3 p = ro + rd * t;\n" +
        "        d = DE(p);\n" +
        "        if(d < 0.001 || t > 10.0) break;\n" +
        "        t += d;\n" +
        "        steps = i;\n" +
        "    }\n" +
        "    \n" +
        "    vec3 col = vec3(0.0);\n" +
        "    if(t < 10.0) {\n" +
        "        // Simple lighting based on steps (AO-like) and distance\n" +
        "        float glow = 1.0 - float(steps)/100.0;\n" +
        "        col = vec3(glow * 0.8, glow * 0.5, glow * 0.2);\n" +
        "        \n" +
        "        // Fog\n" +
        "        col = mix(col, vec3(0.0), 1.0 - exp(-0.1*t));\n" +
        "    }\n" +
        "    \n" +
        "    gl_FragColor = vec4(col, 1.0);\n" +
        "}";

    public static final String SHADER_SPHERE = 
        "#version 120\n" +
        "uniform float time;\n" +
        "uniform vec2 resolution;\n" +
        "float DE(vec3 p) { return length(p) - 1.0; }\n" +
        "void main() {\n" +
        "    vec2 uv = (gl_FragCoord.xy * 2.0 - resolution.xy) / resolution.y;\n" +
        "    vec3 ro = vec3(0.0, 0.0, -3.0);\n" +
        "    vec3 rd = normalize(vec3(uv, 1.0));\n" +
        "    float t = 0.0; float d = 0.0; int steps = 0;\n" +
        "    for(int i=0; i<64; i++) {\n" +
        "        vec3 p = ro + rd * t;\n" +
        "        d = DE(p);\n" +
        "        if(d < 0.001 || t > 10.0) break;\n" +
        "        t += d;\n" +
        "        steps = i;\n" +
        "    }\n" +
        "    vec3 col = vec3(0.0);\n" +
        "    if(t < 10.0) {\n" +
        "        float glow = 1.0 - float(steps)/64.0;\n" +
        "        col = vec3(glow * 0.5, glow * 0.8, glow);\n" +
        "    }\n" +
        "    gl_FragColor = vec4(col, 1.0);\n" +
        "}";

    private String currentFragmentShader = SHADER_MANDELBULB;

    public void setFragmentShader(String shaderSource) {
        this.currentFragmentShader = shaderSource;
        if (initialized) {
            // Recompile
            dispose();
            init();
        }
    }

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
            GL20.glShaderSource(fragmentShaderId, currentFragmentShader);
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
