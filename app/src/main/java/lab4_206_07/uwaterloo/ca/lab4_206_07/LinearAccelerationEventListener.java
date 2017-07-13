package lab4_206_07.uwaterloo.ca.lab4_206_07;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.TextView;


/**
 * Created by Daniel on 5/17/17.
 */

public class LinearAccelerationEventListener implements SensorEventListener {

    private TextView currentGesture;

    private double ATTENUATION_CONSTANT = 8;    //Adjust if necessary

    private float filteredReading[] = new float[3]; // [x,y,z]

    private FiniteStateMachine Gesture_X;
    private FiniteStateMachine Gesture_Y;
    private FiniteStateMachine.Signatures xResult;
    private FiniteStateMachine.Signatures yResult;

    private GameLoopTask myGL;

    public LinearAccelerationEventListener(TextView gestureTextView, GameLoopTask loop){
        currentGesture = gestureTextView;
        Gesture_X = new FiniteStateMachine();
        Gesture_Y = new FiniteStateMachine();
        myGL = loop;
    }

    public void onAccuracyChanged(Sensor s, int i){}

    public void onSensorChanged(SensorEvent se){
        if (se.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
            for (int i = 0; i < 3; i++){
                filteredReading[i] += (se.values[i]-filteredReading[i])/ ATTENUATION_CONSTANT;
            }

            xResult = Gesture_X.activateFSM(filteredReading[0]);
            yResult = Gesture_Y.activateFSM(filteredReading[1]);

            if (xResult == FiniteStateMachine.Signatures.LEFT && yResult == FiniteStateMachine.Signatures.UNDETERMINED){
                currentGesture.setText("LEFT");
                myGL.setDirection(GameLoopTask.gameDirection.LEFT);
            }
            else if (xResult == FiniteStateMachine.Signatures.RIGHT && yResult == FiniteStateMachine.Signatures.UNDETERMINED){
                currentGesture.setText("RIGHT");
                myGL.setDirection(GameLoopTask.gameDirection.RIGHT);
            }
            else if (yResult == FiniteStateMachine.Signatures.LEFT && xResult == FiniteStateMachine.Signatures.UNDETERMINED){
                currentGesture.setText("DOWN");
                myGL.setDirection(GameLoopTask.gameDirection.DOWN);
            }
            else if (yResult == FiniteStateMachine.Signatures.RIGHT && xResult == FiniteStateMachine.Signatures.UNDETERMINED){
                currentGesture.setText("UP");
                myGL.setDirection(GameLoopTask.gameDirection.UP);
            }
        }
    }
}