package lab4_206_07.uwaterloo.ca.lab4_206_07;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Aleksa on 2017-06-26.
 */

public class GameLoopTask extends TimerTask {

    private Activity myActivity;
    private Context myContext;
    private RelativeLayout myRL;
    enum gameDirection {UP, DOWN, LEFT, RIGHT, NO_MOVEMENT};
    gameDirection currentDir = gameDirection.NO_MOVEMENT;
    private GameBlock tempBlock;
    private static List<GameBlock> newBlockList = new LinkedList<GameBlock>();
    private Random randIntGen = new Random();
    private int[] randCoord = new int[2];
    static final int[] BOUNDRY = {-152,648,216};

    public static Timer createBlockDelay;
    public static Timer runEndDelay;

    public GameLoopTask(Activity actIn,RelativeLayout rlIn,Context conIn){
        myActivity = actIn;
        myRL = rlIn;
        myContext = conIn;
        createBlock();
    }

    public void setDirection (gameDirection newDirection) {
        createBlockDelay = new Timer();
        runEndDelay = new Timer();
        // mergeBlock is delayed so that it can occur after calculations have been performed
        // and for new block creation to occur after
        TimerTask myRunEndDelayTask = new TimerTask() {
            @Override
            public void run() {
                myActivity.runOnUiThread(
                        new Runnable() {
                            public void run() {
                                mergeBlock();
                            }
                        }
                );
            }
        };

        currentDir = newDirection;
        for (GameBlock newBlock : newBlockList) {
            newBlock.setBlockDirection(currentDir);
        }

//        createBlockDelay.schedule(myCreateBlockDelayTask, 1000); // TODO delay to delete things too???
        runEndDelay.schedule(myRunEndDelayTask, 250);

    }


    private void createBlock(){
        boolean[][] hasBlock = new boolean[4][4];
        int numEmpty = 16;
        for (GameBlock newBlock : newBlockList) {
            int row = (newBlock.getCoordY() - BOUNDRY[0])/BOUNDRY[2];
            int col = (newBlock.getCoordX() - BOUNDRY[0])/BOUNDRY[2];
            hasBlock[row][col] = true;
            numEmpty--;
            Log.d("There is a block at", String.format("(%s,%s)", Integer.toString(col), Integer.toString(row)));
        }
        Log.d("numEmpty", String.format("%s", Integer.toString(numEmpty)));
        // Generates a random number if the number of empty blocks is above 1. If it is one, there is
        // only one spot for the block to generate in so no number is needed to be generated
        if (numEmpty > 1){
            int tempRandCoord = randIntGen.nextInt(numEmpty - 1);
            Log.d("tempRandCoord", String.format("%s", Integer.toString(tempRandCoord)));
            for (int row = 0; row < 4; row++){
                for (int col = 0; col < 4; col++){
                    if (!(hasBlock[row][col])){
                        if (tempRandCoord == 0){
                            randCoord[0] = (col)*BOUNDRY[2] + BOUNDRY[0];
                            randCoord[1] = (row)*BOUNDRY[2] + BOUNDRY[0];
                            Log.d("Generating a block at", String.format("(%s,%s)", Integer.toString(col), Integer.toString(row)));
                        }
                        tempRandCoord--;
                    }
                }
            }
            tempBlock = new GameBlock(myContext,myRL,randCoord[0],randCoord[1]);
            newBlockList.add(tempBlock);
        }
        else if (numEmpty == 1){
            for (int row = 0; row < 4; row++){
                for (int col = 0; col < 4; col++){
                    if (!(hasBlock[row][col])){
                        Log.d("Generating a block at", String.format("(%s,%s)", Integer.toString(col), Integer.toString(row)));
                        randCoord[0] = (col)*BOUNDRY[2] + BOUNDRY[0];
                        randCoord[1] = (row)*BOUNDRY[2] + BOUNDRY[0];
                    }
                }
            }
            tempBlock = new GameBlock(myContext,myRL,randCoord[0],randCoord[1]);
            newBlockList.add(tempBlock);
        }
        else{
//        Toast.makeText(myActivity, "You lose",
//                Toast.LENGTH_LONG).show();

            Log.d("Game end condition:", "Lose");
        }

    }
    @Override
    public void run(){
        myActivity.runOnUiThread(
                new Runnable(){
                    public void run(){
                        for (GameBlock newBlock : newBlockList) {
                            newBlock.move();
                        }
                    }
                }
        );
    }

    // Checks if a particular space is occupied
    public static boolean isOccupied(int targetX, int targetY){
        boolean Occupied = false;
        for (GameBlock existingBlock : newBlockList) {
            int tempX = existingBlock.getCoordX();
            int tempY = existingBlock.getCoordY();
            if (tempX == targetX && tempY == targetY){
                Occupied = true;
            }
        }
        return Occupied;
    }

    // Gets block number for win conditions and determining if a block should be merged
    public static int getBlockNumber(int targetX, int targetY){
        int blockNumber = 0;
        for (GameBlock existingBlock : newBlockList) {
            int tempX = existingBlock.getCoordX();
            int tempY = existingBlock.getCoordY();
            if (tempX == targetX && tempY == targetY){
                blockNumber = existingBlock.getBlockNumber();
            }
        }
        return blockNumber;
    }

    // Makes a boolean on the GameBlock true, so that it can be doubled after movements have been completed
    public static void flagToDouble(int targetX, int targetY){
        for (GameBlock existingBlock : newBlockList) {
            int tempX = existingBlock.getCoordX();
            int tempY = existingBlock.getCoordY();
            if (tempX == targetX && tempY == targetY){
                existingBlock.toBeDoubled = true;
                break;
            }
        }
        return;
    }
    //TODO
    private void mergeBlock(){
        // Method called after blocks are moved to double and delete blocks as necessary
        Iterator<GameBlock> iter = newBlockList.iterator();
        while (iter.hasNext()) {
            GameBlock myGB = iter.next();
            if (myGB.toBeRemoved == true){
                Log.d("To be removed: ",String.format("(%s,%s)", Integer.toString(myGB.getCoordX()), Integer.toString(myGB.getCoordY())));
                myGB.removeMe();
                iter.remove();
            }
            else if (myGB.toBeDoubled == true){
                Log.d("To be doubled: ",String.format("(%s,%s)", Integer.toString(myGB.getCoordX()), Integer.toString(myGB.getCoordY())));
                myGB.doubleBlockNumber();
                final int WINCONDITION = 2048;
                if (myGB.getBlockNumber() == WINCONDITION){
                    Toast.makeText(myActivity, "Win condition: You got a " + String.valueOf(WINCONDITION) + "!",
                            Toast.LENGTH_LONG).show();
                    Log.d("Win condition", String.valueOf(WINCONDITION) + " tile!");
                }
            }
        }

        for (int i = 0; i < 1000000; i++);
        createBlock();
    }
}

