import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Day10 {

    public static void main(String[] args) throws IOException, URISyntaxException {
        URL url = Thread.currentThread().getContextClassLoader().getResource("Day10.txt");
        Path path = Paths.get(url.toURI());
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

        PiepMzae pipeMaze = new PiepMzae(lines);

        part1(pipeMaze);
        part2(pipeMaze);
    }

    private static void part2(PiepMzae pipeMaze) {
        Map<String, Corner> allCorners = new HashMap<>();
        Map<String, Corner> innerGraph = new HashMap<>();
        Map<String, Corner> outerGraph = new HashMap<>();

        int mazeWidth = pipeMaze.maze[0].length;
        int mazeHeight = pipeMaze.maze.length;
        for (short row = 0; row < mazeHeight + 1; row++) {
            for (short column = 0; column < mazeWidth + 1; column++) {
                Corner corner = new Corner(row, column);
                allCorners.put(corner.getKey(), corner);
            }
        }

        for (short row = 0; row < mazeHeight; row++) {
            for (short column = 0; column < mazeWidth; column++) {
                Corner upLeftCorner = allCorners.get(Corner.getKey(row, column));
                Corner upRightCorner = allCorners.get(Corner.getKey(row, (short) (column + 1)));
                Corner downLeftCorner = allCorners.get(Corner.getKey((short) (row + 1), column));

                boolean leftRight = pipeMaze.hasPassageBetween(upLeftCorner, upRightCorner);
                boolean upDown = pipeMaze.hasPassageBetween(upLeftCorner, downLeftCorner);
                upLeftCorner.right = leftRight;
                upLeftCorner.down = upDown;
                upRightCorner.left = leftRight;
                downLeftCorner.up = upDown;
            }
        }

        int result = 0;
        for (short row = 0; row < mazeHeight; row++) {
            for (short column = 0; column < mazeWidth; column++) {
                char c = pipeMaze.maze[row][column];
                if (c == PiepMzae.REPLACE_EMPTY) {
                    Corner corner = allCorners.get(Corner.getKey(row, column));
                    if (isPartOfInnerGraph(allCorners, innerGraph, outerGraph, corner, new HashMap<>())) {
                        result++;
                        pipeMaze.maze[row][column] = PiepMzae.REPLACE_INNER;
                    }
                }
            }
        }

        System.out.println(pipeMaze);
        System.out.println(result);
    }

    public static boolean isPartOfInnerGraph(Map<String, Corner> allCorners,
                                             Map<String, Corner> innerGraph,
                                             Map<String, Corner> outerGraph,
                                             Corner corner,
                                             Map<String, Boolean> visited) {
//        if (innerGraph.containsValue(corner)) {
//            return true;
//        }
//        if (outerGraph.containsValue(corner)) {
//            return false;
//        }


        visited.put(Corner.getKey(corner.row, corner.column), true);
        if (corner.up == null || corner.right == null || corner.down == null || corner.left == null) {
//            outerGraph.put(corner.getKey(), corner);
            return false;
        }

        List<String> coordsToCheck = new ArrayList<>();
        if (corner.up) {
            coordsToCheck.add(Corner.getKey((short) (corner.row - 1), corner.column));
        }
        if (corner.right) {
            coordsToCheck.add(Corner.getKey(corner.row, (short) (corner.column + 1)));
        }
        if (corner.down) {
            coordsToCheck.add(Corner.getKey((short) (corner.row + 1), corner.column));
        }
        if (corner.left) {
            coordsToCheck.add(Corner.getKey(corner.row, (short) (corner.column - 1)));
        }

        for (String coord : coordsToCheck) {
            if (!visited.containsKey(coord)) {
                Corner toCheck = allCorners.get(coord);
                boolean partOfInnerGraph = isPartOfInnerGraph(allCorners, innerGraph, outerGraph, toCheck, visited);
                if (!partOfInnerGraph) {
//                    outerGraph.put(toCheck.getKey(), corner);
                    return false;
                }
            }
        }

//        innerGraph.put(corner.getKey(), corner);
        return true;
    }

    private static void part1(PiepMzae pipeMaze) {
        int length = 0;
        Iterator<Character> iterator = pipeMaze.iterator();
        char character = iterator.next();
        while (iterator.hasNext()) {
            length++;
            character = iterator.next();
            if (character == 'S') {
                break;
            }
        }
        System.out.println(length / 2);
    }

    static class PiepMzae implements Iterable<Character> {
        public static final char REPLACE_EMPTY = 'O';
        public static final char REPLACE_INNER = '@';
        char[][] maze;
        Pair<Integer, Integer> startCoord;

        public PiepMzae(List<String> lines) {
            maze = new char[lines.size()][lines.get(0).length()];
            List<char[]> chars = new LinkedList<>();
            for (String line : lines) {
                chars.add(line.toCharArray());
            }
            maze = chars.toArray(maze);

            for (int i = 0; i < maze.length; i++) {
                char[] row = maze[i];
                for (int j = 0; j < row.length; j++) {
                    if (row[j] == 'S') {
                        startCoord = Pair.of(i, j);
                    }
                }
            }

            cleanMaze();
        }

        public void cleanMaze() {
            char[][] cleanMaze = new char[maze.length][maze[0].length];
            MaezIterator iterator = new MaezIterator();
            char next = iterator.next();
            while (iterator.hasNext()) {
                cleanMaze[iterator.prevRow][iterator.prevColumn] = next;
                next = iterator.next();
                if (next == 'S') {
                    break;
                }
            }

            for (int i = 0; i < cleanMaze.length; i++) {
                char[] row = cleanMaze[i];
                for (int j = 0; j < row.length; j++) {
                    if (row[j] == '\u0000') {
                        row[j] = REPLACE_EMPTY;
                    }
                }
            }
            replaceStart();
            maze = cleanMaze;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (char[] chars : maze) {
                for (char c : chars) {
                    sb.append(c);
                }
                sb.append("\n");
            }
            return sb.toString();
        }

        @Override
        public Iterator<Character> iterator() {
            return new MaezIterator();
        }

        public Iterator<Character> iteratorFrom(int row, int column) {
            return new MaezIterator(row, column);
        }

        public boolean hasPassageBetween(Corner first, Corner second) {
            if (first.row == second.row) {
                char upper;
                char lower;
                try {
                    upper = maze[first.row - 1][first.column];
                    lower = maze[first.row][first.column];
                    return (upper == REPLACE_EMPTY || lower == REPLACE_EMPTY)
                            || (upper == '-' || upper == 'J' || upper == 'L');
                } catch (IndexOutOfBoundsException e) {
                    return true;
                }
            } else if (first.column == second.column) {
                char left;
                char right;
                try {
                    left = maze[first.row][first.column - 1];
                    right = maze[first.row][first.column];
                    return (left == REPLACE_EMPTY || right == REPLACE_EMPTY)
                            || (left == '|' || left == 'J' || left == '7');
                } catch (IndexOutOfBoundsException e) {
                    return true;
                }
            }
            throw new IllegalArgumentException();
        }

        private void replaceStart() {
            int row = startCoord.getLeft();
            int column = startCoord.getRight();
            char possibleNext;
            boolean up = false, right = false, down = false, left = false;
            try {
                possibleNext = maze[row - 1][column];
                if (possibleNext == '|'
                        || possibleNext == '7'
                        || possibleNext == 'F') {
                    up = true;
                }
            } catch (IndexOutOfBoundsException ignored) {
            }

            try {
                possibleNext = maze[row][column + 1];
                if (possibleNext == '-'
                        || possibleNext == 'J'
                        || possibleNext == '7') {
                    right = true;
                }
            } catch (IndexOutOfBoundsException ignored) {
            }

            try {
                possibleNext = maze[row + 1][column];
                if (possibleNext == '|'
                        || possibleNext == 'L'
                        || possibleNext == 'J') {
                    down = true;
                }
            } catch (IndexOutOfBoundsException ignored) {
            }

            try {
                possibleNext = maze[row][column - 1];
                if (possibleNext == '-'
                        || possibleNext == 'L'
                        || possibleNext == 'F') {
                    left = true;
                }
            } catch (IndexOutOfBoundsException ignored) {
            }

            if (up && down) {
                maze[row][column] = '|';
            } else if (up && left) {
                maze[row][column] = 'J';
            } else if (up && right) {
                maze[row][column] = 'L';
            } else if (left && right) {
                maze[row][column] = '-';
            } else if (left && down) {
                maze[row][column] = '7';
            } else if (down && right) {
                maze[row][column] = 'F';
            }
        }

        class MaezIterator implements Iterator<Character> {

            int currentRow;
            int currentColumn;

            int prevRow;
            int prevColumn;

            public MaezIterator() {
                currentRow = startCoord.getLeft();
                currentColumn = startCoord.getRight();

                prevRow = currentRow;
                prevColumn = currentColumn;
            }

            public MaezIterator(int row, int column) {
                currentRow = row;
                currentColumn = column;

                prevRow = currentRow;
                prevColumn = currentColumn;
            }

            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public Character next() {
                char next = maze[currentRow][currentColumn];
                if (next == 'S' || (currentRow == prevRow && currentColumn == prevColumn)) {
                    findNextFromStart();
                } else {
                    int rowBuffer = currentRow;
                    int columnBuffer = currentColumn;
                    switch (next) {
                        case '|' ->
                                currentRow = currentRow > prevRow ? currentRow + 1 : currentRow - 1;
                        case '-' -> currentColumn = currentColumn > prevColumn ? currentColumn + 1 :
                                currentColumn - 1;
                        case 'L' -> {
                            if (currentColumn < prevColumn) {
                                currentRow--;
                            } else {
                                currentColumn++;
                            }
                        }
                        case 'J' -> {
                            if (currentColumn > prevColumn) {
                                currentRow--;
                            } else {
                                currentColumn--;
                            }
                        }
                        case '7' -> {
                            if (currentColumn > prevColumn) {
                                currentRow++;
                            } else {
                                currentColumn--;
                            }
                        }
                        case 'F' -> {
                            if (currentColumn < prevColumn) {
                                currentRow++;
                            } else {
                                currentColumn++;
                            }
                        }
                    }
                    prevRow = rowBuffer;
                    prevColumn = columnBuffer;
                }
                return next;
            }

            private void findNextFromStart() {
                char possibleNext;
                int rowBuffer = currentRow;
                int columnBuffer = currentColumn;
                try {
                    possibleNext = maze[currentRow - 1][currentColumn];
                    if (possibleNext == '|'
                            || possibleNext == '7'
                            || possibleNext == 'F') {
                        currentRow--;
                        prevRow = rowBuffer;
                        return;
                    }
                } catch (IndexOutOfBoundsException ignored) {
                }

                try {
                    possibleNext = maze[currentRow][currentColumn + 1];
                    if (possibleNext == '-'
                            || possibleNext == 'J'
                            || possibleNext == '7') {
                        currentColumn++;
                        prevColumn = columnBuffer;
                        return;
                    }
                } catch (IndexOutOfBoundsException ignored) {
                }

                try {
                    possibleNext = maze[currentRow + 1][currentColumn];
                    if (possibleNext == '|'
                            || possibleNext == 'L'
                            || possibleNext == 'J') {
                        currentRow++;
                        prevRow = rowBuffer;
                        return;
                    }
                } catch (IndexOutOfBoundsException ignored) {
                }

                try {
                    possibleNext = maze[currentRow][currentColumn - 1];
                    if (possibleNext == '-'
                            || possibleNext == 'L'
                            || possibleNext == 'F') {
                        currentColumn--;
                        prevColumn = columnBuffer;
                    }
                } catch (IndexOutOfBoundsException ignored) {
                }
            }
        }
    }

    static class Corner {
        Boolean up;
        Boolean right;
        Boolean down;
        Boolean left;

        short row;
        short column;

        public Corner(short row, short column) {
            this.row = row;
            this.column = column;
        }

        String getKey() {
            return getKey(row, column);
        }

        static String getKey(short row, short column) {
            return "%s %s".formatted(String.valueOf(row), String.valueOf(column));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Corner corner)) return false;
            return row == corner.row && column == corner.column;
        }

        @Override
        public int hashCode() {
            return Objects.hash(row, column);
        }

        @Override
        public String toString() {
            return "Corner{" +
                    "up=" + up +
                    ", right=" + right +
                    ", down=" + down +
                    ", left=" + left +
                    ", row=" + row +
                    ", column=" + column +
                    '}';
        }
    }
}
