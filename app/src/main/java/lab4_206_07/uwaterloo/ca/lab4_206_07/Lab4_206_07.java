package lab4_206_07.uwaterloo.ca.lab4_206_07;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Timer;

public class Lab4_206_07 extends AppCompatActivity {

    TextView currentGesture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab4_206_07);

        RelativeLayout rl = (RelativeLayout) findViewById(R.id.layout1);

        rl.getLayoutParams().width = 864;
        rl.getLayoutParams().height = 864;
        rl.setBackgroundResource(R.drawable.gameboard);

        currentGesture = new TextView(getApplicationContext()); // Create label
        rl.addView(currentGesture); // Add label
//        currentGesture.setX(500);
//        currentGesture.setY(750);
        currentGesture.setTextColor(Color.BLACK);
        currentGesture.setTextSize(25f);
        currentGesture.setText("N/A");

        Timer myGameLoop = new Timer();
        GameLoopTask myGameLoopTask = new GameLoopTask(this,rl,getApplicationContext());
        myGameLoop.schedule(myGameLoopTask, 25, 25);

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        final LinearAccelerationEventListener accelerometer = new LinearAccelerationEventListener(currentGesture, myGameLoopTask);
        sensorManager.registerListener(accelerometer, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);

    }
}
