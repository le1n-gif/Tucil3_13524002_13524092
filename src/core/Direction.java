package core;

public enum Direction {
    UP('U', -1, 0),
    DOWN('D', 1, 0),
    LEFT('L', 0, -1),
    RIGHT('R', 0, 1);

    private final char symbol;
    private final int rowDelta;
    private final int colDelta;

    Direction(char symbol, int rowDelta, int colDelta) {
        this.symbol = symbol;
        this.rowDelta = rowDelta;
        this.colDelta = colDelta;
    }

    public char getSymbol() {
        return symbol;
    }

    public int getRowDelta() {
        return rowDelta;
    }

    public int getColDelta() {
        return colDelta;
    }
}
