package com.example.particlesimulator;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import java.util.Random;
public class MainActivity extends AppCompatActivity {
    private ParticleSimulationView particleSimulationView;
    private Handler handler;
    private final int interval = 1;
    private Button btnStartGame;
    private EditText etNumberOfParticles, etSpeedOfParticles, etSizeOfParticles;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
     private Switch swSpawner, swEnemy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        particleSimulationView = new ParticleSimulationView(this);
        setContentView(R.layout.activity_main);
        init();
    }


    /**
     * Sets up UI elements and handler.
     */
    private void init() {
        btnStartGame = findViewById(R.id.btnStartGame);

        etNumberOfParticles = findViewById(R.id.etNumberOfParticles);
        etSpeedOfParticles = findViewById(R.id.etSpeedOfParticles);
        etSizeOfParticles = findViewById(R.id.etSizeOfParticles);

        swSpawner = findViewById(R.id.swSpawner);
        swEnemy = findViewById(R.id.swEnemy);

        handler = new Handler();
    }


    /**
     * Takes in UI settings and starts a instance of the game, additionally starts the update loop.
     * @param v Content View
     */
    public void onStartGame(View v) {
        int amount = Integer.parseInt(etNumberOfParticles.getText().toString());
        int speed = Integer.parseInt(etSpeedOfParticles.getText().toString());
        int size = Integer.parseInt(etSizeOfParticles.getText().toString());

        boolean enemySwitchSate = swEnemy.isChecked();
        boolean spawnerSwitchState = swSpawner.isChecked();

        particleSimulationView.init(amount, speed, size, enemySwitchSate, spawnerSwitchState);

        setContentView(particleSimulationView);
        startUpdateLoop(enemySwitchSate);
    }


    /**
     * Declares what methods will run each tick/frame and stars update loop with given interval.
     * @param enemyState Is there a enemy boolean
     */
    private void startUpdateLoop(boolean enemyState) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                particleSimulationView.updateParticlePositions();
                particleSimulationView.checkForParticleOnParticleCollision();
                particleSimulationView.invalidate();
                handler.postDelayed(this, interval);
            }
        }, interval);
    }
}