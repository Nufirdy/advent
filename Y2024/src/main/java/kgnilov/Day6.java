package kgnilov;

import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static kgnilov.Day6.Lab.Dir.*;

public class Day6 {

    public static void main(String[] args) throws IOException, URISyntaxException {
        URL url = Thread.currentThread().getContextClassLoader().getResource(resourceName());
        Path path = Paths.get(url.toURI());
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

//        part1(lines);
        part2(lines);
    }

    static String resourceName() {
        return "Day6";
    }

    static void part1(List<String> lines) {
        Lab lab = new Lab(lines);
        lab.pathGuard2(lab.lab);
        System.out.println(lab.countVisited());

        printMatrix(lab.getVisited(lab.visited, lab.lab));
    }

    static void part2(List<String> lines) {
        Lab lab = new Lab(lines);
        int loopObstacles = lab.countLoopObstacles();
        System.out.println(loopObstacles);
    }

    public static class Lab {
        static final char OBSTACLE = '#';
        static final char GUARD = '^';

        Executor executor = Executors.newWorkStealingPool();

        char[][] lab;
        Pair<Integer, Integer> guardStart;
        Pair<Integer, Integer> firstObstacle;

        Pair<Integer, Integer> guardPos;
        Map<Pair<Integer, Integer>, Set<Dir>> visited = new HashMap<>();

        Set<Pair<Integer, Integer>> loopObstacles = new HashSet<>();

        public Lab(List<String> lines) {
            lab = new char[lines.size()][lines.get(0).length()];
            List<char[]> chars = new ArrayList<>();
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                chars.add(line.toCharArray());
            }
            lab = chars.toArray(lab);

            for (int i = 0; i < lab.length; i++) {
                char[] row = lab[i];
                for (int j = 0; j < row.length; j++) {
                    char c = row[j];
                    if (c == GUARD) {
                        guardStart = Pair.of(i, j);
                        guardPos = guardStart;
                    }
                }
            }
        }

        int countLoopObstacles() {
            AtomicInteger loopObstacleCount = new AtomicInteger();
            pathGuard();
            CountDownLatch latch = new CountDownLatch(visited.size());

            for (int i = 0; i < lab.length; i++) {
                for (int j = 0; j < lab[i].length; j++) {
                    if (!visited.containsKey(Pair.of(i, j))) {
                        continue;
                    }

                    int finalI = i;
                    int finalJ = j;
                    executor.execute(() -> {
                        char c = lab[finalI][finalJ];
                        if (c != OBSTACLE && c != GUARD) {
                            char[][] labCopy = copyMatrix(lab);

                            labCopy[finalI][finalJ] = OBSTACLE;
                            boolean loop = pathGuard2(labCopy);
                            if (loop) {
//                            System.out.println(i + ", " + j);
                                loopObstacleCount.getAndIncrement();
                                loopObstacles.add(Pair.of(finalI, finalJ));
                            }
                        }
                        latch.countDown();
                    });
                }
            }

            try {
                latch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return loopObstacleCount.get();
        }

        boolean pathGuard2(char[][] lab) {
            Pair<Integer, Integer> guardPos = guardStart;
            Map<Pair<Integer, Integer>, Set<Dir>> visited = new HashMap<>();
            int row = guardPos.getLeft(), col = guardPos.getRight();
            Dir dir = UP;

            while (true) {
                Set<Dir> visitedDirs = visited.get(guardPos);
                if (visitedDirs == null) {
                    visitedDirs = new HashSet<>();
                    visitedDirs.add(dir);
                    visited.put(guardPos, visitedDirs);
                } else {
                    if (visitedDirs.contains(dir)) {
                        return true;
                    }
                    visitedDirs.add(dir);
                }
                try {
                    switch (dir) {
                        case UP -> row--;
                        case DOWN -> row++;
                        case LEFT -> col--;
                        case RIGHT -> col++;
                    }

                    char next = lab[row][col];
                    while (next == OBSTACLE) {
                        switch (dir) {
                            case UP -> dir = RIGHT;
                            case DOWN -> dir = LEFT;
                            case LEFT -> dir = UP;
                            case RIGHT -> dir = DOWN;
                        }

                        row = guardPos.getLeft();
                        col = guardPos.getRight();

                        switch (dir) {
                            case UP -> row--;
                            case DOWN -> row++;
                            case LEFT -> col--;
                            case RIGHT -> col++;
                        }
                        next = lab[row][col];
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    break;
                }
                guardPos = Pair.of(row, col);
            }
            return false;
        }

        boolean pathGuard() {
            int row = guardPos.getLeft(), col = guardPos.getRight();
            Dir dir = UP;

            while (true) {
                Set<Dir> visitedDirs = visited.get(guardPos);
                if (visitedDirs == null) {
                    visitedDirs = new HashSet<>();
                    visitedDirs.add(dir);
                    visited.put(guardPos, visitedDirs);
                } else {
                    if (visitedDirs.contains(dir)) {
                        return true;
                    }
                    visitedDirs.add(dir);
                }
                try {
                    switch (dir) {
                        case UP -> row--;
                        case DOWN -> row++;
                        case LEFT -> col--;
                        case RIGHT -> col++;
                    }

                    char next = lab[row][col];
                    while (next == OBSTACLE) {
                        switch (dir) {
                            case UP -> dir = RIGHT;
                            case DOWN -> dir = LEFT;
                            case LEFT -> dir = UP;
                            case RIGHT -> dir = DOWN;
                        }

                        row = guardPos.getLeft();
                        col = guardPos.getRight();

                        switch (dir) {
                            case UP -> row--;
                            case DOWN -> row++;
                            case LEFT -> col--;
                            case RIGHT -> col++;
                        }
                        next = lab[row][col];
                    }
                } catch (IndexOutOfBoundsException e) {
                    break;
                }
                guardPos = Pair.of(row, col);
            }
            return false;
        }

        private boolean isInFrontOfGuard(int i, int j) {
            int obstI = firstObstacle.getLeft();
            int obstJ = firstObstacle.getRight();
            return j == obstJ && (i > obstI && i < guardStart.getLeft());
        }

        int countVisited() {
            return visited.size();
        }

        char[][] getVisited(Map<Pair<Integer, Integer>, Set<Dir>> visited, char[][] lab) {
            char[][] chars = copyMatrix(lab);
            for (Map.Entry<Pair<Integer, Integer>, Set<Dir>> pair : visited.entrySet()) {
                chars[pair.getKey().getLeft()][pair.getKey().getRight()] = 'X';
            }
            return chars;
        }

        char[][] getLabWithLoopObstacles() {
            char[][] chars = copyMatrix(lab);
            for (Pair<Integer, Integer> pair : loopObstacles) {
                chars[pair.getLeft()][pair.getRight()] = 'O';
            }
            return chars;
        }

        enum Dir {
            UP, DOWN, LEFT, RIGHT
        }

    }

    static char[][] copyMatrix(char[][] matrix) {
        char[][] chars = new char[matrix.length][matrix[0].length];
        for (int k = 0; k < matrix.length; k++) {
            char[] matrixRow = matrix[k];
            System.arraycopy(matrixRow, 0, chars[k], 0, matrixRow.length);
        }
        return chars;
    }

    static void printMatrix(char[][] matrix) {
        for (char[] chars : matrix) {
            System.out.println(chars);
        }
        System.out.println();
    }
}
