package com.example.particlesimulator;
public class Particle {
    private Vector position;
    private Vector velocity;
    private float radius;

    public Particle(Vector position, int size, Vector velocity) {
        this.position = position;
        setRadius((float)size);
        setVelocity(velocity);
    }

    public Vector getPosition() {
            return position;
    }

    public void setPosition(Vector position) {
        this.position = position;
    }

    public Vector getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector velocity) {
        this.velocity = velocity;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}
