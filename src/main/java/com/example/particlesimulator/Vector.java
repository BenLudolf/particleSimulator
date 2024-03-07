package com.example.particlesimulator;
public class Vector {
    private float x;
    private float y;

    public Vector(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    private void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    private void setY(float y) {
        this.y = y;
    }

    public Vector add(Vector other) {
        return new Vector(this.x + other.getX(), this.y + other.getY());
    }

    // Would be used for gravity or non-elastic collision
    public Vector subtract(Vector other) {
        return new Vector(this.x - other.x, this.y - other.y);
    }

    public Vector multiply(float scalar) {
        return new Vector(x * scalar, y * scalar);
    }
}
