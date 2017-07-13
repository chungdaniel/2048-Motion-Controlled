package lab4_206_07.uwaterloo.ca.lab4_206_07;

import android.util.Log;
import android.widget.TextView;

/**
 * Created by Daniel on 6/11/17.
 */

public class FiniteStateMachine {

    //FSM parameters
    enum FSMStates{WAIT, RISE, FALL, STABLE, DETERMINED};
    private FSMStates myStates;
    private FSMStates desiredSignature;

    //Signature parameters
    enum Signatures{LEFT, RIGHT, UNDETERMINED};  // DOWN == LEFT, UP == RIGHT
    private Signatures mySig;

    //These are the characteristic thresholds of my choice.    l
    //1st threshold: minimum slope of the response onset
    //2nd threshold: the maximum response amplitude of the first peak
    //3rd threshold: the maximum response amplitude after settling for SAMPLE_COUNTER_DEFAULT samples.
    private final float[] GESTURE_THRESHOLD = {0.5f, 2.5f, 1.0f}; //TODO

    //This is the sample counter.
    //We expect the reading to settle down to near zero after 40 samples since the
    //occurrence of the maximum of the 1st response peak.
    private int sampleCounter;
    private final int SAMPLE_COUNTER_DEFAULT = 25;  //TODO

    //Keep the most recent historical reading so we can calculate the most recent slope
    private float previousReading;

    //Keep a reference of the TextView from the layout.
    private TextView myDisplayTV;


    //Constructor.  FSM is started into WAIT state.
    public FiniteStateMachine(){
        myStates = FSMStates.WAIT;
        desiredSignature = FSMStates.DETERMINED;
        mySig = Signatures.UNDETERMINED;
        sampleCounter = SAMPLE_COUNTER_DEFAULT;
        previousReading = 0;
    }

    //Resetting the FSM back to the initial state
    public void resetFSM(){
        myStates = FSMStates.WAIT;
        desiredSignature = FSMStates.DETERMINED;
        mySig = Signatures.UNDETERMINED;
        sampleCounter = SAMPLE_COUNTER_DEFAULT;
        previousReading = 0;
    }

    //This is the main FSM body
    public Signatures activateFSM(float accInput){

        //First, calculate the slope between the most recent input and the
        //most recent historical readings
        float accSlope = accInput - previousReading;

        //Then, implement the state flow.
        switch(myStates){

            case WAIT:

                if(accSlope >= GESTURE_THRESHOLD[0]){
                    myStates = FSMStates.RISE;
                }
                //To check LEFT, implement the LEFT transition here...
                else if (Math.abs(accSlope) >= GESTURE_THRESHOLD[0]){
                    myStates = FSMStates.FALL;
                }

                break;

            case RISE:

                //crossing the maxima
                if(accSlope <= 0){

                    if(previousReading >= GESTURE_THRESHOLD[1]){
                        myStates = FSMStates.STABLE;
                        desiredSignature = FSMStates.RISE;
                    }
                    else{
                        myStates = FSMStates.DETERMINED;
                        mySig = Signatures.UNDETERMINED;
                    }

                }

                break;

            case FALL:
                //This part is used for the LEFT gesture...

                if(accSlope >= 0){

                    if(Math.abs(previousReading) >= GESTURE_THRESHOLD[1]){
                        myStates = FSMStates.STABLE;
                        desiredSignature = FSMStates.FALL;
                    }
                    else{
                        myStates = FSMStates.DETERMINED;
                        mySig = Signatures.UNDETERMINED;
                    }

                }

                break;

            case STABLE:

                //This part is to wait for the stabilization.
                //Count down from 40 to 0.
                sampleCounter--;

                //Once reached zero, check the threshold and determine the gesture.
                if(sampleCounter == 0){

                    myStates = FSMStates.DETERMINED;
                    // TODO Implement switch case that has previous state in it
                    //You will have to modify this portion to incorporate the LEFT gesture...
                    //Think about how to do it.  You may have to re-think about the state transition here...

                    if(Math.abs(accInput) < GESTURE_THRESHOLD[2]){
                        switch (desiredSignature){
                            case RISE:
                                mySig = Signatures.RIGHT;
                                break;
                            case FALL:
                                mySig = Signatures.LEFT;
                                break;
                            default:
                                //??? TODO maybe
                        }
                    }
                    else{
                        mySig = Signatures.UNDETERMINED;
                    }
                }

                break;

            case DETERMINED:

                //Once determined, report the gesture and reset the FSM.
                Log.d("My FSM Says:", String.format("I've got signature %s", mySig.toString()));

                //Show the signature on teh textview
//                myDisplayTV.setText(mySig.toString());

                resetFSM();

                break;

            default:
                resetFSM();
                break;

        }

        //After every FSM iteration, make sure to record the input as the most recent
        //history reading.
        previousReading = accInput;
        return mySig;

    }

}
