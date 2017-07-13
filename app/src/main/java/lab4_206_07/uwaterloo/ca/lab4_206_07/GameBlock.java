package lab4_206_07.uwaterloo.ca.lab4_206_07;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;

/**
 * Created by Aleksa on 2017-06-26.
 */

public class GameBlock extends ImageView {

    private final float IMAGE_SCALE = 0.4f;
    private int myCoordX;
    private int myCoordY;
    private GameLoopTask.gameDirection myDir = GameLoopTask.gameDirection.NO_MOVEMENT;
    private int targetY;
    private int targetX;
    private int velocity = 0;
    private final int A = 10;
    private TextView myTV;
    private int blockNumber;
    private RelativeLayout myRL;
    private Random randIntGen = new Random();
    public boolean toBeRemoved = false;
    public boolean toBeDoubled = false;


    public GameBlock (Context conIn, RelativeLayout rlIn, int coordX, int coordY){

        super(conIn);

        myRL = rlIn;
        myRL.addView(this);
        myTV = new TextView(conIn);
        myRL.addView(myTV);
        myTV.setTextColor(Color.BLACK);
        myTV.setTextSize(20f);
        myTV.bringToFront();

        blockNumber = (randIntGen.nextInt(4)==3) ? 4 : 2;
        myTV.setText(String.valueOf(blockNumber));

        this.setImageResource(R.drawable.gameblock);
        this.setScaleX(IMAGE_SCALE);
        this.setScaleY(IMAGE_SCALE);

        myCoordX = coordX;
        myCoordY = coordY;

        myTV.setX(myCoordX+200);
        myTV.setY(myCoordY+200);
        this.setX(myCoordX);
        this.setY(myCoordY);

    }

    public void setBlockDirection(GameLoopTask.gameDirection newDir){
        myDir = newDir;
    }

    public void move() {
        switch (myDir) {
            // static final int[] BOUNDRY = {-153,648,216};
            case DOWN:
                targetY = GameLoopTask.BOUNDRY[1] + GameLoopTask.BOUNDRY[0];// - freeSlots * GameLoopTask.BOUNDRY[2]; //
                targetX = myCoordX;
                break;
            case UP:
                targetY = GameLoopTask.BOUNDRY[0];// +  freeSlots * GameLoopTask.BOUNDRY[2]; //
                targetX = myCoordX;
                break;
            case LEFT:
                targetX = GameLoopTask.BOUNDRY[0];// +  freeSlots * GameLoopTask.BOUNDRY[2];
                targetY = myCoordY;
                break;
            case RIGHT:
                targetX = GameLoopTask.BOUNDRY[1] + GameLoopTask.BOUNDRY[0];// -  freeSlots * GameLoopTask.BOUNDRY[2];
                targetY = myCoordY;
                break;
            default:
                targetX = myCoordX;
                targetY = myCoordY;
                break;
        }
        setDestination();

        if ((myCoordY == targetY)&&(myCoordX == targetX)){ // If the block reaches the goal, et velocity to zero
            velocity=0;
        }
        else if (myCoordY == targetY){ // if moving left or right
            if (myCoordX < targetX){ // if moving right
                myCoordX+=velocity; // accelerated motion
                velocity+=A;
                if (myCoordX>targetX) // if overshoot, trim value to target
                    myCoordX = targetX;
                myTV.setX(myCoordX+200); // move text and block
                this.setX(myCoordX);
            }
            else if (myCoordX > targetX){ // if moving left
                myCoordX-=velocity; // accelerated motion
                velocity+=A;
                if (myCoordX<targetX) // if overshoot, trim value to target
                    myCoordX = targetX;
                myTV.setX(myCoordX+200); // move text and block
                this.setX(myCoordX);
            }

        }
        else if (myCoordX == targetX){ // If moving up or down
            if (myCoordY < targetY){ // if moving up
                myCoordY+=velocity; // accelerated motion
                velocity+=A;
                if (myCoordY>targetY) // if overshoot, trim value to target
                    myCoordY = targetY;
                myTV.setY(myCoordY+200); // move text and block
                this.setY(myCoordY);
            }
            else if (myCoordY > targetY){ // if moving down
                myCoordY-=velocity; // accelerated motion
                velocity+=A;
                if (myCoordY<targetY) // if overshoot, trim value to target
                    myCoordY = targetY;
                myTV.setY(myCoordY+200); // move text and block
                this.setY(myCoordY);
            }

        }

    }

    public void setDestination(){
        int blockCount = 0;
        int slotCount = 0;
        int tempY = targetY;
        int tempX = targetX;
        int[] numbersAhead = new int[3];
        int[][] numbersAheadCoords = new int[3][2];
        switch (myDir) {
            case DOWN:
                // Checks each coordinate in the same direction and col/row, starting with the
                // furthest block
                // Increments blockcount if one a space is found to be occupied
                // The space being checked has coordinates (tempX, tempY)
                while (tempY > myCoordY){
                    if (GameLoopTask.isOccupied(tempX, tempY)){
                        numbersAhead[blockCount] = GameLoopTask.getBlockNumber(tempX, tempY);
                        numbersAheadCoords[blockCount][0] = tempX;
                        numbersAheadCoords[blockCount][1] = tempY;
                        blockCount++;
                    }
                    slotCount++;
                    tempY -= GameLoopTask.BOUNDRY[2];
                }
                // TODO

                determineMerges(blockCount, numbersAhead, numbersAheadCoords);
                targetY = GameLoopTask.BOUNDRY[1] + GameLoopTask.BOUNDRY[0]  - blockCount * GameLoopTask.BOUNDRY[2];
                break;
            case UP:
                while (tempY < myCoordY){
                    if (GameLoopTask.isOccupied(tempX, tempY)){
                        numbersAhead[blockCount] = GameLoopTask.getBlockNumber(tempX, tempY);
                        numbersAheadCoords[blockCount][0] = tempX;
                        numbersAheadCoords[blockCount][1] = tempY;
                        blockCount++;
                    }
                    slotCount++;
                    tempY += GameLoopTask.BOUNDRY[2];
                }
                determineMerges(blockCount, numbersAhead, numbersAheadCoords);
                targetY = GameLoopTask.BOUNDRY[0]  + blockCount * GameLoopTask.BOUNDRY[2];
                break;
            case LEFT:
                while (tempX < myCoordX){
                    if (GameLoopTask.isOccupied(tempX, tempY)){
                        numbersAhead[blockCount] = GameLoopTask.getBlockNumber(tempX, tempY);
                        numbersAheadCoords[blockCount][0] = tempX;
                        numbersAheadCoords[blockCount][1] = tempY;
                        blockCount++;
                    }
                    slotCount++;
                    tempX += GameLoopTask.BOUNDRY[2];
                }
                determineMerges(blockCount, numbersAhead, numbersAheadCoords);
                targetX = GameLoopTask.BOUNDRY[0]  + blockCount * GameLoopTask.BOUNDRY[2];
                break;
            case RIGHT:
                while (tempX > myCoordX){
                    if (GameLoopTask.isOccupied(tempX, tempY)){
                        numbersAhead[blockCount] = GameLoopTask.getBlockNumber(tempX, tempY);
                        numbersAheadCoords[blockCount][0] = tempX;
                        numbersAheadCoords[blockCount][1] = tempY;
                        blockCount++;
                    }
                    slotCount++;
                    tempX -= GameLoopTask.BOUNDRY[2];
                }
                determineMerges(blockCount, numbersAhead, numbersAheadCoords);
                targetX = GameLoopTask.BOUNDRY[1] + GameLoopTask.BOUNDRY[0]  - blockCount * GameLoopTask.BOUNDRY[2];
                break;
            default:
                break;
        }
    }

    private int determineMerges(int blockCount, int[] numbersAhead, int[][] numbersAheadCoords) {
        int numMerges = 0;
        switch (blockCount){
            // Only the current block being checked gets flagged for being doubled (if meets conditions)
            // Only the block just before the current gets flagged for being removed
            case 0: // one case
                numMerges = 0; // not necessary, but adds for clarity?
                break;
            case 1: // two cases, if it merges, or if it doesn't
//                Log.d("Number ahead is", String.format("(%s)", Integer.toString(numbersAhead[0])));
//                Log.d("blockNumber is", String.format("(%s)", Integer.toString(blockNumber)));
                if (numbersAhead[0] == blockNumber){ // TODO flag numbersAhead[0] for deletion
                    numMerges = 1;
                    toBeRemoved = true;
                    GameLoopTask.flagToDouble(numbersAheadCoords[0][0], numbersAheadCoords[0][1]);
//                    blockNumber *= 2;
//                    myTV.setText(String.valueOf(blockNumber));
//                    Log.d("Number ahead is", String.format("(%s)", Integer.toString(numbersAhead[0])));
                }
                break;
            case 2: // three cases, if first two merge, if second and current merge, if none occur
                if (numbersAhead[0] == numbersAhead[1]){
                    numMerges = 1;
                }
                else if (numbersAhead[1] == blockNumber){
                    numMerges = 1;
                    toBeRemoved = true;
                    GameLoopTask.flagToDouble(numbersAheadCoords[1][0], numbersAheadCoords[1][1]);
                }
                break;
            case 3: // five cases
                if (numbersAhead[0] == numbersAhead[1] && numbersAhead[2] == blockNumber){
                    numMerges = 2;
                    toBeRemoved = true;
                    GameLoopTask.flagToDouble(numbersAheadCoords[2][0], numbersAheadCoords[2][1]);
                }
                else if(numbersAhead[0] == numbersAhead[1]){
                    numMerges = 1;
                }
                else if(numbersAhead[1] == numbersAhead[2]){
                    numMerges = 1;
                }
                else if (numbersAhead[2] == blockNumber){
                    numMerges = 1;
                    toBeRemoved = true;
                    GameLoopTask.flagToDouble(numbersAheadCoords[2][0], numbersAheadCoords[2][1]);
                }
                break;
            default:
                Log.d("You dun goof'd in", "determineMerges");
                break;
        }
        return numMerges;
    }

    public int getCoordX(){
        return myCoordX;
    }

    public int getCoordY(){
        return myCoordY;
    }

    public int getBlockNumber(){
        return blockNumber;
    }

    public void doubleBlockNumber(){
        blockNumber *= 2;
        toBeDoubled = false;
        myTV.setText(String.valueOf(blockNumber));
    }

    public void removeMe(){
        myRL.removeView(myTV);
        myRL.removeView(this);
    }


//    public int[] getTarget(){     //not needed?
//        int[] target = new int[2];
//        target[0] = targetX;
//        target[1] = targetY;
//        return target;
//    }
//
//    public GameLoopTask.gameDirection getMyDir(){
//        return myDir;
//    }
}
