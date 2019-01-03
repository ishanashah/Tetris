package Tetris;

import java.awt.*;


//An immutable representation of a tetris piece in a particular rotation.
public final class TetrisPiece implements Piece {
    private PieceType type;
    private int rotationIndex = 0;
    private Piece clockwisePiece;
    private Piece counterclockwisePiece;
    private int[] skirt;
    private Point[] body;

    public TetrisPiece(PieceType type) {
        // TODO: Implement me.
        this.type = type;
        this.body = this.type.getSpawnBody();
        setSkirt();

        setClockwisePiece(clockwisePiece(this));
        ((TetrisPiece) clockwisePiece).setClockwisePiece(clockwisePiece(clockwisePiece));
        ((TetrisPiece) clockwisePiece.clockwisePiece()).setClockwisePiece(clockwisePiece(clockwisePiece.clockwisePiece()));
        ((TetrisPiece) clockwisePiece.clockwisePiece().clockwisePiece()).setClockwisePiece(this);
    }

    private TetrisPiece(PieceType type, Point[] body, int rotationIndex){
        this.type = type;
        this.body = body;
        if (rotationIndex > 3){
            this.rotationIndex = 0;
        } else {
            this.rotationIndex = rotationIndex;
        }
        setSkirt();
    }

    private void setSkirt(){
        skirt = new int[(int) type.getBoundingBox().getWidth()];
        for(int i = 0; i < skirt.length; i++){
            skirt[i] = Integer.MAX_VALUE;
        }
        for(int i = 0; i < getBody().length; i++){
            if (getBody()[i].getY() <
                    skirt[(int) getBody()[i].getX()]){
                skirt[(int) getBody()[i].getX()] =
                        (int) getBody()[i].getY();
            }
        }
    }

    private Piece clockwisePiece(Piece input){
        Point[] clockwiseBody = new Point[input.getBody().length];
        for(int i = 0; i < input.getBody().length; i++){
            clockwiseBody[i] = new Point((int) input.getBody()[i].getY(),
                    input.getHeight() - (int) input.getBody()[i].getX() - 1);
        }
        return new TetrisPiece(input.getType(), clockwiseBody,input.getRotationIndex() + 1);
    }

    private void setClockwisePiece(Piece input){
        ((TetrisPiece) input).setCounterclockwisePiece(this);
        this.clockwisePiece = input;
    }

    private void setCounterclockwisePiece(Piece input){
        this.counterclockwisePiece = input;
    }

    @Override
    public PieceType getType() {
        return type;
    }

    @Override
    public int getRotationIndex() {
        return rotationIndex;
    }

    @Override
    public Piece clockwisePiece() {
        return clockwisePiece;
    }

    @Override
    public Piece counterclockwisePiece() {
        return counterclockwisePiece;
    }

    @Override
    public int getWidth() {
        return type.getBoundingBox().width;
    }

    @Override
    public int getHeight() {
        return type.getBoundingBox().height;
    }

    @Override
    public Point[] getBody() {
        return body;
    }

    @Override
    public int[] getSkirt() {
        return skirt;
    }

    @Override
    public boolean equals(Object other) {
        // Ignore objects which aren't also tetris pieces.
        if(!(other instanceof TetrisPiece)) return false;
        TetrisPiece otherPiece = (TetrisPiece) other;
        return this.type == otherPiece.getType() &&
                this.rotationIndex == otherPiece.getRotationIndex();
    }
}
