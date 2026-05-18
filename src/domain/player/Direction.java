package domain.player;

public enum Direction {
    NORTE   ( 0, -1),
    SUR     ( 0,  1),
    ESTE    ( 1,  0),
    OESTE   (-1,  0),
    NORESTE ( 1, -1),
    NOROESTE(-1, -1),
    SURESTE ( 1,  1),
    SUROESTE(-1,  1),
    QUIETO  ( 0,  0);

    public final int dx, dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }
}
