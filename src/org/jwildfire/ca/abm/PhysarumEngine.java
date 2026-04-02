package org.jwildfire.ca.abm;

import java.util.Random;

/**
 * Physarum (Slime Mold) simulation engine.
 * Based on the model by Jeff Jones (2010).
 * Agents sense and move toward chemoattractant trails, creating complex networks.
 * A signature feature of Visions of Chaos (VoC).
 */
public class PhysarumEngine {
    
    public static class Agent {
        public float x, y;
        public float angle;
        
        public Agent(float x, float y, float angle) {
            this.x = x;
            this.y = y;
            this.angle = angle;
        }
    }

    private int width;
    private int height;
    private Agent[] agents;
    private float[] trailMap;
    private float[] nextTrailMap;
    private final Random random = new Random();

    // Parameters
    private float sensorAngle = (float) Math.toRadians(45);
    private float sensorDist = 9.0f;
    private float stepSize = 1.0f;
    private float turnSpeed = (float) Math.toRadians(45);
    private float decayRate = 0.9f;
    private float diffuseRate = 0.5f;

    public void init(int width, int height, int numAgents) {
        this.width = width;
        this.height = height;
        this.trailMap = new float[width * height];
        this.nextTrailMap = new float[width * height];
        this.agents = new Agent[numAgents];
        
        for (int i = 0; i < numAgents; i++) {
            agents[i] = new Agent(
                random.nextFloat() * width,
                random.nextFloat() * height,
                random.nextFloat() * (float)Math.PI * 2
            );
        }
    }

    public void step() {
        // 1. Agent Sense and Rotate
        for (Agent a : agents) {
            float f = sense(a, 0);
            float fl = sense(a, -sensorAngle);
            float fr = sense(a, sensorAngle);

            if (f > fl && f > fr) {
                // Continue straight
            } else if (f < fl && f < fr) {
                a.angle += (random.nextBoolean() ? 1 : -1) * turnSpeed;
            } else if (fl < fr) {
                a.angle += turnSpeed;
            } else if (fr < fl) {
                a.angle -= turnSpeed;
            }
        }

        // 2. Agent Move and Deposit
        for (Agent a : agents) {
            a.x += Math.cos(a.angle) * stepSize;
            a.y += Math.sin(a.angle) * stepSize;

            // Wrap around
            a.x = (a.x + width) % width;
            a.y = (a.y + height) % height;

            // Deposit
            int ix = (int) a.x;
            int iy = (int) a.y;
            trailMap[iy * width + ix] = 1.0f;
        }

        // 3. Diffuse and Decay
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float sum = 0;
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dx = -1; dx <= 1; dx++) {
                        int nx = (x + dx + width) % width;
                        int ny = (y + dy + height) % height;
                        sum += trailMap[ny * width + nx];
                    }
                }
                float avg = sum / 9.0f;
                nextTrailMap[y * width + x] = (trailMap[y * width + x] * (1 - diffuseRate) + avg * diffuseRate) * decayRate;
            }
        }
        
        System.arraycopy(nextTrailMap, 0, trailMap, 0, trailMap.length);
    }

    private float sense(Agent a, float offsetAngle) {
        float angle = a.angle + offsetAngle;
        float sx = a.x + (float)Math.cos(angle) * sensorDist;
        float sy = a.y + (float)Math.sin(angle) * sensorDist;
        
        int ix = (int)(sx + width) % width;
        int iy = (int)(sy + height) % height;
        
        return trailMap[iy * width + ix];
    }

    public float[] getTrailMap() {
        return trailMap;
    }

    public String getName() {
        return "Physarum (Slime Mold)";
    }
}
