package Tetris;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class JBrainTetris extends JTetris{

    public static void main(String[] args) {
        createGUI(new JBrainTetris());
    }

    JBrainTetris() {
        super();
        setPreferredSize(new Dimension(WIDTH*PIXELS+2, (HEIGHT+TOP_SPACE)*PIXELS+2));
        gameOn = false;

        board = new TetrisBoard(WIDTH, HEIGHT + TOP_SPACE);

        /**
         * Register key handlers that call
         * tick with the appropriate constant.
         */

        // LEFT
        registerKeyboardAction(new ActionListener() {
                                   public void actionPerformed(ActionEvent e) {
                                       tick(Board.Action.LEFT);
                                   }
                               },
                "left", KeyStroke.getKeyStroke('a'), WHEN_IN_FOCUSED_WINDOW);

        // RIGHT
        registerKeyboardAction(new ActionListener() {
                                   public void actionPerformed(ActionEvent e) {
                                       tick(Board.Action.DOWN);
                                   }
                               },
                "down", KeyStroke.getKeyStroke('s'), WHEN_IN_FOCUSED_WINDOW);

        // DOWN
        registerKeyboardAction(new ActionListener() {
                                   public void actionPerformed(ActionEvent e) {
                                       tick(Board.Action.RIGHT);
                                   }
                               },
                "right", KeyStroke.getKeyStroke('d'), WHEN_IN_FOCUSED_WINDOW);

        // ROTATE
        registerKeyboardAction(new ActionListener() {
                                   public void actionPerformed(ActionEvent e) {
                                       tick(Board.Action.COUNTERCLOCKWISE);
                                   }
                               },
                "counterclockwise", KeyStroke.getKeyStroke('q'), WHEN_IN_FOCUSED_WINDOW);

        // UNROTATE
        registerKeyboardAction(new ActionListener() {
                                   public void actionPerformed(ActionEvent e) {
                                       tick(Board.Action.CLOCKWISE);
                                   }
                               },
                "clockwise", KeyStroke.getKeyStroke('e'), WHEN_IN_FOCUSED_WINDOW);

        // DROP
        registerKeyboardAction(new ActionListener() {
                                   public void actionPerformed(ActionEvent e) {
                                       tick(Board.Action.DROP);
                                   }
                               },
                "drop", KeyStroke.getKeyStroke('w'), WHEN_IN_FOCUSED_WINDOW);

        // Create the Timer object and have it send
        // tick(DOWN) periodically
        TetrisBrain PB = new TetrisBrain();
        timer = new javax.swing.Timer(0, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Board.Action act = PB.nextMove(board);
                int score = 0;
                while (true) {
                    tick(act);
                    score += board.getRowsCleared();
                    if(board.getMaxHeight() > board.getHeight() - TOP_SPACE){
                        stopGame();
                        break;
                    }
                    if(act == Board.Action.DROP){
                        break;
                    }
                    act = PB.nextMove(board);
                }
                //stopGame();
            }
        });
    }

    public void tick(Board.Action verb) {
        if (!gameOn) {
            return;
        }

        Board.Result result = board.move(verb);
        switch (result) {
            case SUCCESS:
            case OUT_BOUNDS:
                // The board is responsible for staying in a good state
                break;
            case PLACE:
                if (board.getMaxHeight() > HEIGHT) {
                    stopGame();
                }
            case NO_PIECE:
                if (gameOn) {
                    addNewPiece();
                }
                break;
        }

        repaint();
    }
}
