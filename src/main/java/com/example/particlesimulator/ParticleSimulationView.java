package com.example.particlesimulator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import androidx.annotation.NonNull;
import java.util.Random;
public class ParticleSimulationView extends View {
    private Random rand;
    private Particle enemy, spawner;
    private Particle[] particles;
    private final Paint paint = new Paint();
    private int speed, size;
    private Vector centerGrid;
    private boolean enemyState, spawnerState;
    // Constructor for custom canvas
    public ParticleSimulationView(Context context) {
        super(context);
    }


    /**
     * Takes in all params for particles including enemy and spawner if applicable.
     * @param amount Amount of particles
     * @param speed Speed of particles
     * @param tempSize Size of particles
     * @param enemySate Is there an enemy "boolean"
     * @param spawnerSwitchState Is there a spawner "boolean"
     */
    public void init(int amount, int speed, int tempSize, boolean enemySate, boolean spawnerSwitchState) {
        this.enemyState = enemySate;
        spawnerState = spawnerSwitchState;
        this.speed = speed;
        size = tempSize;


        particles = new Particle[amount];
        rand = new Random();
        centerGrid = new Vector(730f, 1350f);
        float randomVelocityEnemyX = (rand.nextFloat() - .5f) * 100f * speed;
        float randomVelocityEnemyY = (rand.nextFloat() - .5f) * 100 * speed;
        Vector randomVelocity = new Vector(randomVelocityEnemyX, randomVelocityEnemyY);

        if (enemySate) {
            enemy = new Particle(centerGrid, tempSize, randomVelocity);
        }
        if (spawnerSwitchState) {
            spawner = new Particle(centerGrid, (tempSize * 2), randomVelocity);
        }

        for (int i = 0; i < particles.length; i++) {
            float randomX = rand.nextFloat() * 1300;
            float randomY = rand.nextFloat() * 2800;
            Vector tempVectorPosition = new Vector(randomX, randomY);
            float randomVelocityX = (rand.nextFloat() - .5f) * 100 * speed;
            float randomVelocityY = (rand.nextFloat() - .5f) * 100 * speed;
            Vector tempVectorVelocity = new Vector(randomVelocityX, randomVelocityY);
            particles[i] = new Particle(tempVectorPosition, tempSize, tempVectorVelocity);
        }
    }



    /**
     * For loop for particle updates. Also null check for enemy movement update.
     */
    public void updateParticlePositions() {
        for (Particle particle : particles) {
            checkForBoarders(particle);
        }
        if (enemyState) {
            checkForBoarders(enemy);
        }
    }



    /**
     * Takes in particle and does a check if that particle is in bounds. Depending on what bound (MinX, MaxY ex...)
     * reverses that axis of velocity
     * @param particle Particle to check if out of bounds
     */
    public void checkForBoarders(Particle particle) {
        Vector oldPosition = particle.getPosition();
        Vector velocity = particle.getVelocity();
        float timeStep = 0.1f;
        Vector newPosition = oldPosition.add(velocity.multiply(timeStep));
        float minX = 80f;
        if (newPosition.getX() < minX) {
            particle.setVelocity(new Vector(-particle.getVelocity().getX(), particle.getVelocity().getY()));
            particle.setPosition(new Vector(minX + (particle.getRadius() * 2), particle.getPosition().getY()));
        }
        float minY = 80f;
        if (newPosition.getY() < minY) {
            particle.setVelocity(new Vector(particle.getVelocity().getX(), -particle.getVelocity().getY()));
            particle.setPosition(new Vector(particle.getPosition().getX(), minY - (particle.getRadius() * 2)));
        }
        float maxX = 1300f;
        if (newPosition.getX() > maxX) {
            particle.setVelocity(new Vector(-particle.getVelocity().getX(), particle.getVelocity().getY()));
            particle.setPosition(new Vector(maxX + (particle.getRadius() * 2), particle.getPosition().getY()));
        }
        float maxY = 2700f;
        if (newPosition.getY() > maxY) {
            particle.setVelocity(new Vector(particle.getVelocity().getX(), -particle.getVelocity().getY()));
            particle.setPosition(new Vector(particle.getPosition().getX(), maxY - (particle.getRadius() * 2)));
        }
        particle.setPosition(newPosition);
    }



    /**
     * Checks which particles should exsist. If true then checks if in the current frame any of them are touching.
     * If so It creates an elastic bounce to each particle
     */
    public void checkForParticleOnParticleCollision() {
        for (int i = 0; i < particles.length; i++) {
            for (int j = i + 1; j < particles.length; j++) {
                boolean touching = isTouching(particles[i], particles[j]);
                if (touching) {
                    handleParticleCollision(particles[i], particles[j]);
                }
            }
        }
        if (enemyState) {
            for (int i = 0; i < particles.length; i++) {
                boolean touching = isTouching(particles[i], enemy);
                if (touching) {
                    particles = deleteIndex(particles, i);
                }
            }
        }
        if (spawnerState) {
            for (Particle particle : particles) {
                boolean touching = isTouching(particle, spawner);
                if (touching) {
                    particle.setVelocity(particle.getVelocity().multiply(-1));

                    float randomVelocityX = (rand.nextFloat() - .5f) * 100 * speed;
                    float randomVelocityY = (rand.nextFloat() - .5f) * 100 * speed;
                    Vector tempVectorVelocity = new Vector(randomVelocityX, randomVelocityY);
                    float random = (rand.nextFloat() - .5f) * 2f;

                    Particle newParticle = new Particle(centerGrid.multiply(1.5f * random), size, tempVectorVelocity);
                    particles = addParticle(particles, newParticle);
                }
            }
        }
    }



    /**
     * Takes In a array and returns a new array with a new particle added on as the last index.
     * @param particles Array to be added to
     * @param newParticle Particle to be added
     * @return Returns a particle array with the new particle added on the end.
     */
    private Particle[] addParticle(Particle[] particles, Particle newParticle) {
        Particle[] tempArray = new Particle[particles.length + 1];
        System.arraycopy(particles, 0, tempArray, 0, particles.length);
        tempArray[particles.length] = newParticle;
        return tempArray;
    }



    /**
     * Takes in two particles and calculates distance, if particles are touching then it returns a boolean value.
     * @param particleA First particle/point to check
     * @param particleB Second particle/point to check
     * @return Boolean if particle A and B are touching.
     */
    public boolean isTouching(Particle particleA, Particle particleB) {
        float distance = calculateDistance(particleA, particleB);
        float minDistance = particleA.getRadius() + particleB.getRadius();
        return distance < minDistance;
    }



    /**
     * Deletes a single index of given array
     * @param arr Array to delete index from
     * @param index Index to be deleted
     * @return The array missing the index I
     */
    private Particle[] deleteIndex(Particle[] arr, int index) {
        if (arr == null || index < 0
                || index >= arr.length) {
            return arr;
        }
        Particle[] anotherArray = new Particle[arr.length - 1];
        for (int i = 0, k = 0; i < arr.length; i++) {
            if (i == index) {
                continue;
            }
            anotherArray[k++] = arr[i];
        }
        return anotherArray;
    }



    /**
     * Does a simple swap of velocities to simulate a elastic bounce
     * @param particleA First particle to be swapped
     * @param particleB Second particle to be swapped
     */
    private void handleParticleCollision(Particle particleA, Particle particleB) {
        Vector tempVelocity = particleA.getVelocity();
        particleA.setVelocity(particleB.getVelocity());
        particleB.setVelocity(tempVelocity);
    }



    /**
     * Takes in two particles and does very basic trigonometry to calc distance between particles
     * @param particleA Particle point A
     * @param particleB Particle point B
     * @return Distance of given particles
     */
    private float calculateDistance(Particle particleA, Particle particleB) {
        float x1 = particleA.getPosition().getX();
        float x2 = particleB.getPosition().getX();
        float y1 = particleA.getPosition().getY();
        float y2 = particleB.getPosition().getY();
        // Below is basically pythagorean theorem
        return (float) Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
    }



    /**
     * Draws each particles type to the custom canvas each frame.
     * @param canvas the canvas on which the background will be drawn
     */
    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.BLACK);

        for (Particle particle : particles) {
            float particleX = particle.getPosition().getX();
            float particleY = particle.getPosition().getY();
            float particleRadius = particle.getRadius();
            paint.setColor(Color.BLUE);

            canvas.drawCircle(particleX, particleY, particleRadius, paint);
        }
        if (enemyState) {
            float particleX = enemy.getPosition().getX();
            float particleY = enemy.getPosition().getY();
            float particleRadius = enemy.getRadius();
            paint.setColor(Color.RED);

            canvas.drawCircle(particleX, particleY, particleRadius, paint);
        }
        if (spawnerState) {
            float particleX = spawner.getPosition().getX();
            float particleY = spawner.getPosition().getY();
            float particleRadius = spawner.getRadius();
            paint.setColor(Color.GREEN);

            canvas.drawCircle(particleX, particleY, particleRadius, paint);
        }

        invalidate();
    }
}