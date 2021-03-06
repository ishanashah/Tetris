package Tetris;

import java.util.*;

/*
Rows Cleared: 0.6673732246946595
Holes Created: -0.7024683486787233
Ruggedness: -0.24728768280130548
*/

/**
 * A Lame Brain implementation for JTetris; tries all possible places to put the
 * piece (but ignoring rotations, because we're lame), trying to minimize the
 * total height of pieces on the board.
 */
public class TetrisBrain implements Brain {
    private ArrayList<BoardState> options = new ArrayList<>();
    private Queue<Board.Action> actions = new LinkedList<>();


    private double ROWS_CLEARED;
    private double HOLES_CREATED;
    private double RUGGEDNESS;


    public Board.Action nextMove (Board currentBoard, double[] parameters){
        ROWS_CLEARED = parameters[0];
        HOLES_CREATED = parameters[1];
        RUGGEDNESS = parameters[2];


        //Uses the Queue to apply pre-computed actions

        if(actions.peek() != null){
            return actions.remove();
        }

        //Fill the our options array with versions of the new Board
        options = new ArrayList<>();
        enumerateOptions(currentBoard, options);

        if(options.size() == 0){
            return Board.Action.NOTHING;
        }

        options.get(0).setScore(parameters);
        double maxScore = options.get(0).getScore();
        int bestIndex = 0;
        for(int i = 1; i < options.size(); i++){
            options.get(i).setScore(parameters);
            if(options.get(i).getScore() > maxScore){
                maxScore = options.get(i).getScore();
                bestIndex = i;
            }
        }

        actions = options.get(bestIndex).getActions();
        return actions.remove();
    }


    /**
     * Decide what the next move should be based on the state of the board.
     */

    public Board.Action nextMove(Board currentBoard) {
        //the parameters in TetrisBrain are the same as the parameters generated by our gen-3 genetic algorithm
        //when tested with fair piece selection, they result in over a million lines cleared
        //without fair piece gen they typically clear more 1000 lines 90% of the time
        double[] parameters = {0.12631641382615746, -0.9694425339852508 , -0.21029820945113845};
        return nextMove(currentBoard, parameters);
    }

    private void enumerateOptions(Board currentBoard, List<BoardState> list){
        //options = new ArrayList<>();
        for(int i = 0; i < 4; i++){
            enumerateOptions(currentBoard, i, list);
        }
    }

    private void enumerateOptions(Board currentBoard, int rotation, List<BoardState> list) {
        // We can always drop our current Piece
        Queue<Board.Action> acts = new LinkedList<>();
        Board.Action act;
        Board rotatedBoard;

        switch (rotation){
            case 0:
                act = Board.Action.DROP;

                rotatedBoard = currentBoard.testMove(Board.Action.NOTHING);
                list.add(new BoardState(rotatedBoard.testMove(act), acts));
                break;
            case 1:
                act = Board.Action.CLOCKWISE;

                acts.add(Board.Action.CLOCKWISE);

                rotatedBoard = currentBoard.testMove(act);
                list.add(new BoardState(rotatedBoard.testMove(Board.Action.DROP), acts));
                break;
            case 2:
                act = Board.Action.CLOCKWISE;

                acts.add(Board.Action.CLOCKWISE);
                acts.add(Board.Action.CLOCKWISE);

                rotatedBoard = currentBoard.testMove(act).testMove(act);
                list.add(new BoardState(rotatedBoard.testMove(Board.Action.DROP), acts));
                break;
            case 3:
                act = Board.Action.COUNTERCLOCKWISE;

                acts.add(Board.Action.COUNTERCLOCKWISE);

                rotatedBoard = currentBoard.testMove(act);
                list.add(new BoardState(rotatedBoard.testMove(Board.Action.DROP), acts));
                break;
            default:
                act = Board.Action.DROP;

                rotatedBoard = currentBoard.testMove(Board.Action.NOTHING);
                list.add(new BoardState(rotatedBoard.testMove(act), acts));
                break;
        }


        // Now we'll add all the places to the left we can DROP
        Board left = rotatedBoard.testMove(Board.Action.LEFT);
        Queue<Board.Action> leftActs = new LinkedList<>(acts);
        if(rotation == 0){
            act = Board.Action.LEFT;
        }
        while (left.getLastResult() == Board.Result.SUCCESS) {
            leftActs.add(Board.Action.LEFT);

            list.add(new BoardState(left.testMove(Board.Action.DROP), leftActs));

            left.move(Board.Action.LEFT);
        }

        // And then the same thing to the right
        Board right = rotatedBoard.testMove(Board.Action.RIGHT);
        Queue<Board.Action> rightActs = new LinkedList<>(acts);
        if(rotation == 0){
            act = Board.Action.RIGHT;
        }
        while (right.getLastResult() == Board.Result.SUCCESS) {
            rightActs.add(Board.Action.RIGHT);

            list.add(new BoardState(right.testMove(Board.Action.DROP), rightActs));

            right.move(Board.Action.RIGHT);
        }
    }

    protected static class BoardState{
        private Board option;
        private Queue<Board.Action> actions;

        private int rowsCleared;
        private int[] columnHeight;
        private int[] rowWidth;

        private int blocksCovered = 0;
        private int[] minToMaxHeight;
        private int ruggedness;

        private double score = Integer.MIN_VALUE;

        public BoardState(Board option, Queue<Board.Action> actions){
            this.option = option.testMove(Board.Action.NOTHING);
            this.actions = new LinkedList<>(actions);
            this.actions.add(Board.Action.DROP);
            rowsCleared = option.getRowsCleared();
            columnHeight = new int[option.getWidth()];
            minToMaxHeight = new int[columnHeight.length];
            for(int x = 0; x < option.getWidth(); x++){
                columnHeight[x] = option.getColumnHeight(x);
                minToMaxHeight[x] = columnHeight[x];
                int filledBlocks = 0;
                for(int y = 0; y < option.getHeight(); y++){
                    if(getGrid(x, y)){
                        filledBlocks++;
                    }
                }
                blocksCovered += columnHeight[x] - filledBlocks;
                if(x != 0){
                    ruggedness += Math.abs(columnHeight[x] - columnHeight[x - 1]);
                }
            }
            Arrays.sort(minToMaxHeight);
            rowWidth = new int[option.getHeight()];
            for(int y = 0; y < option.getHeight(); y++){
                rowWidth[y] = option.getRowWidth(y);
            }
        }

        public Board getOption(){
            return option;
        }

        public Queue<Board.Action> getActions(){
            return new LinkedList<>(actions);
        }

        public boolean getGrid(int x, int y){
            return option.getGrid(x, y) != null;
        }

        public void setScore(double[] parameters){
            this.score = rowsCleared * parameters[0] + blocksCovered * parameters[1] + ruggedness * parameters[2];
        }

        public double getScore(){
            return score;
        }

        public boolean equals(Object other) {
            if(!(other instanceof BoardState)) return false;
            BoardState otherBoard = (BoardState) other;
            return option.equals(otherBoard.getOption());
        }
    }
}

