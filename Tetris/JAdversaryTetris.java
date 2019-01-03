package Tetris;

public class JAdversaryTetris extends JTetris {

    public static void main(String[] args) {
        createGUI(new JAdversaryTetris());
    }

    @Override
    public Piece pickNextPiece() {
        Adversary a = new Adversary();
        return a.worstPiece(board, new TetrisBrain());
    }
}
