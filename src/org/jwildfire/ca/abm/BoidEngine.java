package org.jwildfire.ca.abm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Boids (Flocking) simulation engine.
 * Implements the three rules: Separation, Alignment, and Cohesion.
 * Part of the Visions of Chaos (VoC) Agent-Based Modeling (ABM) assimilation.
 */
public class BoidEngine {
    
    public static class Boid {
        public float x, y;
        public float vx, vy;
        public float ax, ay;
        
        public Boid(float x, float y) {
            this.x = x;
            this.y = y;
            Random r = new Random();
            this.vx = r.nextFloat() * 2.0f - 1.0f;
            this.vy = r.nextFloat() * 2.0f - 1.0f;
        }
    }

    private int width;
    private int height;
    private List<Boid> boids;
    private final Random random = new Random();

    // Parameters
    private float maxSpeed = 2.0f;
    private float maxForce = 0.05f;
    private float perceptionRadius = 50.0f;
    
    // Rule weights
    private float separationWeight = 1.5f;
    private float alignmentWeight = 1.0f;
    private float cohesionWeight = 1.0f;

    public void init(int width, int height, int numBoids) {
        this.width = width;
        this.height = height;
        this.boids = new ArrayList<>();
        for (int i = 0; i < numBoids; i++) {
            boids.add(new Boid(random.nextFloat() * width, random.nextFloat() * height));
        }
    }

    public void step() {
        for (Boid b : boids) {
            applyRules(b);
        }
        
        for (Boid b : boids) {
            updateBoid(b);
        }
    }

    private void applyRules(Boid b) {
        float sepX = 0, sepY = 0;
        float aliX = 0, aliY = 0;
        float cohX = 0, cohY = 0;
        int total = 0;
        
        for (Boid other : boids) {
            if (other == b) continue;
            
            float dx = other.x - b.x;
            float dy = other.y - b.y;
            float dSq = dx * dx + dy * dy;
            
            if (dSq < perceptionRadius * perceptionRadius) {
                float d = (float) Math.sqrt(dSq);
                
                // Separation: steering away from neighbors
                sepX += (b.x - other.x) / dSq;
                sepY += (b.y - other.y) / dSq;
                
                // Alignment: steering towards the average heading
                aliX += other.vx;
                aliY += other.vy;
                
                // Cohesion: steering towards the average position
                cohX += other.x;
                cohY += other.y;
                
                total++;
            }
        }
        
        if (total > 0) {
            sepX /= total; sepY /= total;
            aliX /= total; aliY /= total;
            cohX /= total; cohY /= total;
            
            // Separation steering
            sepX = steer(b, sepX, sepY)[0] * separationWeight;
            sepY = steer(b, sepX, sepY)[1] * separationWeight;
            
            // Alignment steering
            aliX = steer(b, aliX, aliY)[0] * alignmentWeight;
            aliY = steer(b, aliX, aliY)[1] * alignmentWeight;
            
            // Cohesion steering
            cohX -= b.x; cohY -= b.y;
            cohX = steer(b, cohX, cohY)[0] * cohesionWeight;
            cohY = steer(b, cohX, cohY)[1] * cohesionWeight;
            
            b.ax = sepX + aliX + cohX;
            b.ay = sepY + aliY + cohY;
        }
    }

    private float[] steer(Boid b, float tx, float ty) {
        float dSq = tx * tx + ty * ty;
        if (dSq > 0) {
            float d = (float) Math.sqrt(dSq);
            tx = (tx / d) * maxSpeed;
            ty = (ty / d) * maxSpeed;
            tx -= b.vx;
            ty -= b.vy;
            float forceSq = tx * tx + ty * ty;
            if (forceSq > maxForce * maxForce) {
                float f = (float) Math.sqrt(forceSq);
                tx = (tx / f) * maxForce;
                ty = (ty / f) * maxForce;
            }
        }
        return new float[]{tx, ty};
    }

    private void updateBoid(Boid b) {
        b.vx += b.ax;
        b.vy += b.ay;
        
        float speedSq = b.vx * b.vx + b.vy * b.vy;
        if (speedSq > maxSpeed * maxSpeed) {
            float s = (float) Math.sqrt(speedSq);
            b.vx = (b.vx / s) * maxSpeed;
            b.vy = (b.vy / s) * maxSpeed;
        }
        
        b.x += b.vx;
        b.y += b.vy;
        
        // Wrap around
        b.x = (b.x + width) % width;
        b.y = (b.y + height) % height;
        
        b.ax = 0; b.ay = 0;
    }

    public List<Boid> getBoids() {
        return boids;
    }

    public String getName() {
        return "Boids (Flocking)";
    }
}
