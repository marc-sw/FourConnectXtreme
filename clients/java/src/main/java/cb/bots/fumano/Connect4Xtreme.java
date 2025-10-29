package cb.bots.fumano;

import cb.bots.fumano.mcts.State;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Connect4Xtreme implements State {

    public static final byte BLUE = 5;
    public static final byte RED = -5;
    public static final byte None = 0;
    public static final byte DRAW = 2;
    public static final int ROWS = 6;
    public static final int COLUMNS = 7;
    private static final int[][] DIRS = {{1, 0}, {-1, 0}, {0, -1}, {0, 1}};
    private static final StringBuilder builder = new StringBuilder();

    private final byte[] tiles;
    private byte state;
    private byte round;

    public Connect4Xtreme(byte[] tiles, byte round, byte state) {
        this.tiles = tiles;
        this.round = round;
        this.state = state;
    }

    public Connect4Xtreme(Connect4Xtreme original) {
        this(Arrays.copyOf(original.tiles, original.tiles.length), original.round, original.state);
    }

    public Connect4Xtreme() {
        this(new byte[ROWS * COLUMNS], (byte) 0, None);
    }

    public byte get(int row, int column) {
        return tiles[row * COLUMNS + column];
    }

    public void set(int row, int column, byte value) {
        tiles[row * COLUMNS + column] = value;
    }

    public int getRound() {
        return round;
    }

    public void setRound(byte round) {
        this.round = round;
    }

    public void setState(byte state) {
        this.state = state;
    }

    private byte currentCoin() {
        return round % 2 == 0 ? BLUE: RED;
    }

    public byte calcState() {
        boolean blueWin = false;
        boolean redWin = false;

        byte sum;
        for (int row = 0; row < ROWS; row++) {
            sum = 0;
            for (int column = 0; column < COLUMNS - 3; column++) {
                if (column == 0) {
                    sum = (byte) (get(row, column) + get(row, column + 1) + get(row, column + 2) + get(row, column + 3));
                } else {
                    sum += (byte) (get(row, column + 3) - get(row, column - 1));
                }
                if (sum == BLUE * 4) {
                    blueWin = true;
                } else if (sum == RED * 4) {
                    redWin = true;
                }
            }
        }

        for (int column = 0; column < COLUMNS; column++) {
            sum = 0;
            for (int row = 0; row < ROWS - 3; row++) {
                if (row == 0) {
                    sum = (byte) (get(row, column) + get(row + 1, column) + get(row + 2, column) + get(row + 3, column));
                } else {
                    sum += (byte) (get(row + 3, column) - get(row - 1, column));
                }
                if (sum == BLUE * 4) {
                    blueWin = true;
                } else if (sum == RED * 4) {
                    redWin = true;
                }
            }
        }

        for (int row = 0; row < ROWS - 3; row++) {
            sum = 0;
            for (int column = 0; column < COLUMNS - 3; column++) {
                sum = (byte) (get(row, column) + get(row + 1, column + 1) + get(row + 2, column + 2) + get(row + 3, column + 3));

                if (sum == BLUE * 4) {
                    blueWin = true;
                } else if (sum == RED * 4) {
                    redWin = true;
                }
            }
        }

        for (int row = ROWS - 1; row >= 3; row--) {
            sum = 0;
            for (int column = 0; column < COLUMNS - 3; column++) {
                sum = (byte) (get(row, column) + get(row - 1, column + 1) + get(row - 2, column + 2) + get(row - 3, column + 3));

                if (sum == BLUE * 4) {
                    blueWin = true;
                } else if (sum == RED * 4) {
                    redWin = true;
                }
            }
        }



        if (blueWin && redWin) {
            return DRAW;
        }
        if (blueWin) {
            return BLUE;
        }
        if (redWin) {
            return RED;
        }

        for (int i = 0; i < COLUMNS; i++) {
            if (get(ROWS - 1, i) == None) {
                return None;
            }
        }

        return DRAW;
    }


    private void explodeBomb(int row, int column) {
        set(row, column, None);
        for (int[] dir: DIRS) {
            int dx = dir[0];
            int dy = dir[1];

            int x = column + dx;
            int y = row + dy;
            if (x < 0 || x >= COLUMNS || y < 0 || y >= ROWS) {
                continue;
            }
            if (dy == -1) {
                continue;
            }
            int offset = 1;
            if (dy == 1) {
                if (row == 0) {
                    offset = 2;
                } else {
                    offset = 3;
                }
            }
            y++;
            byte value;
            while (y < ROWS && (value = get(y, x)) != None) {
                set(y - offset, x, value);
                set(y, x, None);
                y++;
            }
        }
    }

    public void spawnBomb(int column) {
        int row = 0;
        while (get(row, column) != None) {
            row++;
        }
        set(row, column, (byte) 4);
    }

    @Override
    public double getScore() {
        if (state == BLUE) {
            return 5;
        }
        if (state == RED) {
            return -5;
        }
        return 0;
    }

    @Override
    public boolean isMaximising() {
        return round % 2 == 0;
    }

    @Override
    public boolean isTerminal() {
        return state != None;
    }

    @Override
    public void simulate(int action) {
        this.round++;
        int row = 0;
        while (get(row, action) != None) {
            row++;
        }
        set(row, action, currentCoin());
        for (int i = 0; i < ROWS * COLUMNS; i++) {
            if (tiles[i] > 0 && tiles[i] < 5) {
                tiles[i]--;
                if (tiles[i] == 0) {
                    explodeBomb(i / COLUMNS, i % COLUMNS);
                }
                break;
            }
        }
        this.state = calcState();
    }

    @Override
    public State copy() {
        return new Connect4Xtreme(this);
    }

    @Override
    public void findActions(List<Integer> result) {
        if (isTerminal()) {
            return;
        }
        for (int i = 0; i < COLUMNS; i++) {
            if (get(ROWS - 1, i) == None) {
                result.add(i);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Connect4Xtreme that = (Connect4Xtreme) o;
        return Objects.deepEquals(tiles, that.tiles);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(tiles);
    }

    public String toString() {
        builder.setLength(0);
        for (int i = 0; i < ROWS * COLUMNS; i++) {
            int y = ROWS - i / COLUMNS - 1;
            if (i > 0 && i % COLUMNS == 0) {
                builder.append(System.lineSeparator());
            }
            if (i % COLUMNS != 0) {
                builder.append(" |");
            }
            builder.append(String.format("%2d", tiles[y * COLUMNS + i % COLUMNS]));
        }
        return builder.toString();
    }
}
