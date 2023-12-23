public enum Direction {
    UP(-1, 0, '^'),
    DOWN(1, 0, 'v'),
    LEFT(0, -1, '<'),
    RIGHT(0, 1, '>');

    int row;
    int col;
    char symbol;

    Direction(int row, int col, char symbol) {
        this.row = row;
        this.col = col;
        this.symbol = symbol;
    }
}
