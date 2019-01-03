package Tetris;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Adversary {

    public final Piece[] PIECES = new Piece[] {
            new TetrisPiece(Piece.PieceType.STICK),
            new TetrisPiece(Piece.PieceType.SQUARE),
            new TetrisPiece(Piece.PieceType.T),
            new TetrisPiece(Piece.PieceType.LEFT_L),
            new TetrisPiece(Piece.PieceType.RIGHT_L),
            new TetrisPiece(Piece.PieceType.LEFT_DOG),
            new TetrisPiece(Piece.PieceType.RIGHT_DOG)
    };

    public Piece worstPiece(Board currentBoard, Brain currentBrain){
        List<BoardState> options = new ArrayList<>();
        for(int i = 0; i < PIECES.length; i++){
            Board nextBoard = currentBoard.testMove(Board.Action.NOTHING);
            nextBoard.nextPiece(PIECES[i], new Point(nextBoard.getWidth() / 2 - PIECES[i].getWidth() / 2, nextBoard.getHeight() - 4));
            Board.Action act = currentBrain.nextMove(nextBoard);
            while(act != Board.Action.DROP){
                nextBoard = nextBoard.testMove(act);
                act = currentBrain.nextMove(nextBoard);
            }
            nextBoard = nextBoard.testMove(act);
            options.add(new BoardState(nextBoard, PIECES[i]));
        }


        int minRowsCleared = options.get(0).getRowsCleared();
        for(int i = 1; i < options.size(); i++){
            if(options.get(i).getRowsCleared() < minRowsCleared){
                minRowsCleared = options.get(i).getRowsCleared();
            }
        }
        for(int i = 0; i < options.size(); i++){
            if(options.get(i).getRowsCleared() != minRowsCleared){
                options.remove(i);
                i--;
            }
        }

        int maxBlocksCovered = options.get(0).getBlocksCovered();
        for(int i = 1; i < options.size(); i++){
            if(options.get(i).getBlocksCovered() > maxBlocksCovered) {
                maxBlocksCovered = options.get(i).getBlocksCovered();
            }
        }
        for(int i = 0; i < options.size(); i++){
            if(options.get(i).getBlocksCovered() != maxBlocksCovered){
                options.remove(i);
                i--;
            }
        }

        int size = options.size();
        for(int x = currentBoard.getWidth() - 1; x >= 0; x--){
            if(options.size() == 1){
                break;
            }
            int maxMaxHeight = options.get(0).getMinToMaxHeight(x);
            for(int i = 0 ; i < options.size(); i++){
                if(options.get(i).getMinToMaxHeight(x) > maxMaxHeight){
                    maxMaxHeight = options.get(i).getMinToMaxHeight(x);
                }
            }
            for(int i = 0 ; i < options.size(); i++){
                if(options.get(i).getMinToMaxHeight(x) < maxMaxHeight){
                    options.remove(i);
                    i--;
                }
            }
            if(size == options.size()){
                break;
                //System.out.println("hello");
            }
            size = options.size();
        }

        return options.get(0).getCurrentPiece();
    }

    protected static class BoardState{
        private Board option;
        private Piece currentPiece;

        private int rowsCleared;
        private int maxHeight;
        private int[] columnHeight;
        private int[] rowWidth;

        private int blocksCovered = 0;
        private int totalHeight = 0;
        private int[] minToMaxHeight;
        private int ruggedness;

        public BoardState(Board option, Piece currentPiece){
            this.option = option.testMove(Board.Action.NOTHING);
            this.currentPiece = currentPiece;
            rowsCleared = option.getRowsCleared();
            maxHeight = option.getMaxHeight();
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
                totalHeight += columnHeight[x];
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

        public Piece getCurrentPiece(){
            return currentPiece;
        }

        public int getRowsCleared(){
            return rowsCleared;
        }

        public int getMaxHeight(){
            return maxHeight;
        }

        public int getColumnHeight(int x){
            return columnHeight[x];
        }

        public int getRowWidth(int y){
            return rowWidth[y];
        }

        public boolean getGrid(int x, int y){
            return option.getGrid(x, y) != null;
        }

        public int getBlocksCovered(){
            return blocksCovered;
        }

        public int getTotalHeight(){
            return totalHeight;
        }

        public int getMinToMaxHeight(int x){
            return minToMaxHeight[x];
        }

        public int getRuggedness(){
            return ruggedness;
        }

        public boolean equals(Object other) {
            if(!(other instanceof TetrisBrain.BoardState)) return false;
            TetrisBrain.BoardState otherBoard = (TetrisBrain.BoardState) other;
            return option.equals(otherBoard.getOption());
        }
    }
}
