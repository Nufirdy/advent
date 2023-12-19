import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Day16 {

    public static void main(String[] args) throws IOException, URISyntaxException {
        URL url = Thread.currentThread().getContextClassLoader().getResource("Day16-test1.txt");
        Path path = Paths.get(url.toURI());
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

        part1(lines);
        part2(lines);
    }

    private static void part2(List<String> lines) {
        Contraption contraption = new Contraption(lines);
        int highestTotalEnergized = 0;
        for (int i = 0; i < contraption.grid[0].length; i++) {
            contraption.routeBeam(0, i, false, null);
            if (highestTotalEnergized < contraption.totalEnergized) {
                highestTotalEnergized = contraption.totalEnergized;
            }
            contraption.resetRouting();
        }
        for (int i = 0; i < contraption.grid[0].length; i++) {
            contraption.routeBeam(contraption.grid.length - 1, i, true, null);
            if (highestTotalEnergized < contraption.totalEnergized) {
                highestTotalEnergized = contraption.totalEnergized;
            }
            contraption.resetRouting();
        }
        for (int i = 0; i < contraption.grid.length; i++) {
            contraption.routeBeam(0, i, null, false);
            if (highestTotalEnergized < contraption.totalEnergized) {
                highestTotalEnergized = contraption.totalEnergized;
            }
            contraption.resetRouting();
        }
        for (int i = 0; i < contraption.grid.length; i++) {
            contraption.routeBeam(contraption.grid[0].length - 1, i, null, true);
            if (highestTotalEnergized < contraption.totalEnergized) {
                highestTotalEnergized = contraption.totalEnergized;
            }
            contraption.resetRouting();
        }
        System.out.println(highestTotalEnergized);
    }

    private static void part1(List<String> lines) {
        Contraption contraption = new Contraption(lines);
        contraption.routeBeam(0, 0, null, false);
        System.out.println(contraption.countEnergized());
        System.out.println(contraption.totalEnergized);
    }

    static class Contraption {
        final static char ENERGIZED = '#';
        final static byte UP = (byte) 0b1000_0000;
        final static byte DOWN = 0b0100_0000;
        final static byte LEFT = 0b0001_0000;
        final static byte RIGHT = 0b0000_0001;

        final static char VERT_SPLITTER = '|';
        final static char HOR_SPLITTER = '-';
        final static char LEFT_MIRROR = '\\';
        final static char RIGHT_MIRROR = '/';

        Set<Pair<Integer, Integer>> visitedSplitters = new HashSet<>();

        char[][] grid;
        char[][] energizedTiles;

        int totalEnergized = 0;

        public Contraption(List<String> lines) {
            grid = InputUtils.get2DArray(lines);
            energizedTiles = new char[grid.length][grid[0].length];
            resetRouting();
        }

        public void routeBeam(int startRow, int startCol, Boolean vertDir, Boolean horDir) {
            int row = startRow, col = startCol;
            Boolean upOrDown = vertDir;
            Boolean leftOrRight = horDir;

            while (true) {
                char c = 0;
                try {
                    if (energizedTiles[row][col] != ENERGIZED) {
                        totalEnergized++;
                    }
                    energizedTiles[row][col] = ENERGIZED;
                    c = grid[row][col];
                } catch (Exception e) {
                    break;
                }

                if (c == LEFT_MIRROR) {
                    if (upOrDown == null) {
                        upOrDown = leftOrRight;
                        leftOrRight = null;
                    } else {
                        leftOrRight = upOrDown;
                        upOrDown = null;
                    }
                } else if (c == RIGHT_MIRROR) {
                    if (upOrDown == null) {
                        upOrDown = !leftOrRight;
                        leftOrRight = null;
                    } else {
                        leftOrRight = !upOrDown;
                        upOrDown = null;
                    }
                } else if (c == VERT_SPLITTER && leftOrRight != null) {
                    if (visitedSplitters.contains(Pair.of(row, col))) {
                        break;
                    }
                    visitedSplitters.add(Pair.of(row, col));
                    upOrDown = false;
                    leftOrRight = null;
                    routeBeam(row - 1, col, !upOrDown, leftOrRight);
                } else if (c == HOR_SPLITTER && upOrDown != null) {
                    if (visitedSplitters.contains(Pair.of(row, col))) {
                        break;
                    }
                    visitedSplitters.add(Pair.of(row, col));
                    leftOrRight = false;
                    upOrDown = null;
                    routeBeam(row, col - 1, upOrDown, !leftOrRight);
                }

                if (leftOrRight != null) {
                    col += leftOrRight ? -1 : 1;
                } else {
                    row += upOrDown ? -1 : 1;
                }
            }
        }

        private boolean hasBitwiseDir(char c, Boolean vertDir, Boolean horDir) {
            if (vertDir != null) {
                return vertDir ? (c & UP) == UP : (c & DOWN) == DOWN;
            }
            return horDir ? (c & LEFT) == LEFT : (c & RIGHT) == RIGHT;
        }

        private char bitwiseDir(char c, Boolean vertDir, Boolean horDir) {
            if (vertDir != null) {
                return (char) (vertDir ? c | UP : c | DOWN);
            }
            return (char) (horDir ? c | LEFT : c | RIGHT);
        }

        public int countEnergized() {
            int count = 0;
            for (char[] row : energizedTiles) {
                for (char c : row) {
                    if (c != '.') {
                        count++;
                    }
                }
            }
            return count;
        }

        public void resetRouting() {
            for (char[] row : energizedTiles) {
                Arrays.fill(row, '.');
            }
            totalEnergized = 0;
        }
    }
}
