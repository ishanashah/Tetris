package Tetris;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JGeneticTetris extends JTetris{

    public static void main(String[] args) {
        createGUI(new JGeneticTetris());
    }

    JGeneticTetris() {
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

        // GENETIC
        registerKeyboardAction(new ActionListener() {
                                   public void actionPerformed(ActionEvent e) {


                                   }
                               },
                "startGeneticAlgorithm", KeyStroke.getKeyStroke(' '), WHEN_IN_FOCUSED_WINDOW);

        // Create the Timer object and have it send
        // tick(DOWN) periodically

        TetrisBrain PB = new TetrisBrain();
        timer = new javax.swing.Timer(0, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GeneticBrain GB = new GeneticBrain();
                int maxFitness = 0;
                double[] bestGenes = new double[3];
                for(int generation = 0; generation < GB.GENERATIONS; generation++){
                    System.out.println("GENERATION " + (1 + generation));
                    for(int i = 0; i < GB.SIZE; i++){
                        int score = 1;
                        for(int j = 0; j < 1; j++){
                            Board.Action act = PB.nextMove(board, GB.getPopulation(i));
                            while (true) {
                                tick(act);
                                score += board.getRowsCleared();
                                if(board.getMaxHeight() > board.getHeight() - TOP_SPACE){
                                    break;
                                }
                                if (score >= GB.MAX_SCORE * (j + 1)){
                                    System.out.println("MAX FITNESS: " + GB.MAX_SCORE);
                                    System.out.println("ROWS CLEARED: " + GB.getPopulation(i)[0]);
                                    System.out.println("HOLES CREATED: " + GB.getPopulation(i)[1]);
                                    System.out.println("RUGGEDNESS: " + GB.getPopulation(i)[2]);
                                    stopGame();
                                    return;
                                }
                                act = PB.nextMove(board, GB.getPopulation(i));
                            }
                            stopGame();
                            startGame();
                        }
                        GB.setFitness(i, score);
                        if(score > maxFitness){
                            maxFitness = score;
                            bestGenes = GB.getPopulation(i);
                        }
                        System.out.println("GEN:" + (generation + 1) + " MEMBER:" + (i + 1) + "/" + GB.SIZE + " FITNESS:" + score);
                        for(int d = 0; d < GB.getPopulation(i).length; d++){
                            System.out.print(GB.getPopulation(i)[d] + " ");
                        }
                        System.out.println();
                    }
                    System.out.println("MAX FITNESS: " + maxFitness);
                    System.out.println("ROWS CLEARED: " + bestGenes[0]);
                    System.out.println("HOLES CREATED: " + bestGenes[1]);
                    System.out.println("RUGGEDNESS: " + bestGenes[2]);
                    System.out.println();

                    GB.generateNewPopulation();
                }
                System.out.println();
                System.out.println();
                System.out.println("MAX FITNESS: " + maxFitness);
                System.out.println("ROWS CLEARED: " + bestGenes[0]);
                System.out.println("HOLES CREATED: " + bestGenes[1]);
                System.out.println("RUGGEDNESS: " + bestGenes[2]);
                stopGame();

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

        //repaint();
    }
}
