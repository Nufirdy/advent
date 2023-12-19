import org.apache.commons.lang3.time.StopWatch;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Day14 {

    public static void main(String[] args) throws IOException, URISyntaxException,
            ExecutionException, InterruptedException {
        URL url = Thread.currentThread().getContextClassLoader().getResource("Day14.txt");
        Path path = Paths.get(url.toURI());
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

        int i = (1000000000 - 104) % 39;
        System.out.println(i);
//        part1(lines);
//        part2(lines);
    }

    private static void part2(List<String> lines) {
        Platform platform = new Platform(lines);
        List<Integer> cycle = new LinkedList<>();
        boolean check = false;
        for (int i = 0; i != 1; i--) {
            platform.tiltNorth();
            platform.tiltWest();
            platform.tiltSouth();
            platform.tiltEast();
            int northLoad = platform.calculateNorthLoad();
            if (cycle.contains(northLoad) && i < 0) {
                i = cycle.size();
                check = true;
            }
            if (check && !cycle.contains(northLoad)) {
                i = 0;
                check = false;
            }
            cycle.add(northLoad);
        }

        int cycleStart = trimCycle(cycle);
        int cycleLength = cycle.size();
        int answerIndex = (1000000000 - cycleStart) % cycleLength;

        System.out.println(cycle.get(answerIndex));

    }

    private static int trimCycle(List<Integer> sequence) {
        List<Integer> copy = new LinkedList<>();
        List<Integer> cycle = new LinkedList<>();
        boolean writeToCycle = false;
        int cycleStartIndex = -1;
        for (int i = 0; i < sequence.size(); i++) {
            Integer integer = sequence.get(i);
            if (copy.contains(integer) && !writeToCycle) {
                writeToCycle = true;
            }
            copy.add(integer);
            if (writeToCycle) cycle.add(integer);
        }

        return 0;
    }

    private static void part1(List<String> lines) {
        Platform platform = new Platform(lines);
        platform.tiltNorth();
        System.out.println(platform.calculateNorthLoad());
    }

    static class Platform {
        static final char ROCK = 'O';
        static final char WALL = '#';
        static final char EMPTY = '.';

        Executor executor = Executors.newWorkStealingPool();
        CyclicBarrier barrier;

        char[][] platform;

        public Platform(List<String> lines) {
            platform = new char[lines.size()][lines.get(0).length()];
            List<char[]> chars = new ArrayList<>();
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                chars.add(line.toCharArray());
            }
            platform = chars.toArray(platform);

            barrier = new CyclicBarrier(platform.length + 1);
        }

        public void tiltNorth() {
            for (int col = 0; col < platform[0].length; col++) {
                int lastWallIndex = -1;
                for (int row = 0; row < platform.length; row++) {
                    char c = platform[row][col];
                    if (c == WALL) {
                        lastWallIndex = row;
                    }
                    if (c == ROCK) {
                        platform[row][col] = EMPTY;
                        platform[lastWallIndex + 1][col] = ROCK;
                        lastWallIndex++;
                    }
                }
            }
        }

        public void tiltWest() {
            for (int row = 0; row < platform.length; row++) {
                int lastWallIndex = -1;
                for (int col = 0; col < platform[0].length; col++) {
                    char c = platform[row][col];
                    if (c == WALL) {
                        lastWallIndex = col;
                    }
                    if (c == ROCK) {
                        platform[row][col] = EMPTY;
                        platform[row][lastWallIndex + 1] = ROCK;
                        lastWallIndex++;
                    }
                }
            }
        }

        public void tiltSouth() {
            for (int col = 0; col < platform[0].length; col++) {
                int lastWallIndex = platform.length;
                for (int row = platform.length - 1; row >= 0; row--) {
                    char c = platform[row][col];
                    if (c == WALL) {
                        lastWallIndex = row;
                    }
                    if (c == ROCK) {
                        platform[row][col] = EMPTY;
                        platform[lastWallIndex - 1][col] = ROCK;
                        lastWallIndex--;
                    }
                }
            }
        }

        public void tiltEast() {
            for (int row = 0; row < platform.length; row++) {
                int lastWallIndex = platform[0].length;
                for (int col = platform[0].length - 1; col >= 0; col--) {
                    char c = platform[row][col];
                    if (c == WALL) {
                        lastWallIndex = col;
                    }
                    if (c == ROCK) {
                        platform[row][col] = EMPTY;
                        platform[row][lastWallIndex - 1] = ROCK;
                        lastWallIndex--;
                    }
                }
            }
        }

        public synchronized void tiltNorthAsync() {
            for (int col = 0; col < platform[0].length; col++) {
                int finalCol = col;
                executor.execute(() -> {
                    int lastWallIndex = -1;
                    for (int row = 0; row < platform.length; row++) {
                        char c = platform[row][finalCol];
                        if (c == WALL) {
                            lastWallIndex = row;
                        }
                        if (c == ROCK) {
                            platform[row][finalCol] = EMPTY;
                            platform[lastWallIndex + 1][finalCol] = ROCK;
                            lastWallIndex++;
                        }
                    }
                    try {
                        barrier.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } catch (BrokenBarrierException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            try {
                barrier.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
        }

        public synchronized void tiltWestAsync() {
            for (int row = 0; row < platform.length; row++) {
                int finalRow = row;
                executor.execute(() -> {
                    int lastWallIndex = -1;
                    for (int col = 0; col < platform[0].length; col++) {
                        char c = platform[finalRow][col];
                        if (c == WALL) {
                            lastWallIndex = col;
                        }
                        if (c == ROCK) {
                            platform[finalRow][col] = EMPTY;
                            platform[finalRow][lastWallIndex + 1] = ROCK;
                            lastWallIndex++;
                        }
                    }
                });
            }
        }

        public synchronized void tiltSouthAsync() {
            for (int col = 0; col < platform[0].length; col++) {
                int finalCol = col;
                executor.execute(() -> {
                    int lastWallIndex = platform.length;
                    for (int row = platform.length - 1; row >= 0; row--) {
                        char c = platform[row][finalCol];
                        if (c == WALL) {
                            lastWallIndex = row;
                        }
                        if (c == ROCK) {
                            platform[row][finalCol] = EMPTY;
                            platform[lastWallIndex - 1][finalCol] = ROCK;
                            lastWallIndex--;
                        }
                    }
                    try {
                        barrier.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } catch (BrokenBarrierException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            try {
                barrier.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
        }

        public synchronized void tiltEastAsync() {
            for (int row = 0; row < platform.length; row++) {
                int finalRow = row;
                executor.execute(() -> {
                    int lastWallIndex = platform[0].length;
                    for (int col = platform[0].length - 1; col >= 0; col--) {
                        char c = platform[finalRow][col];
                        if (c == WALL) {
                            lastWallIndex = col;
                        }
                        if (c == ROCK) {
                            platform[finalRow][col] = EMPTY;
                            platform[finalRow][lastWallIndex - 1] = ROCK;
                            lastWallIndex--;
                        }
                    }
                    try {
                        barrier.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } catch (BrokenBarrierException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            try {
                barrier.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
        }



        public int calculateNorthLoad() {
            int load = 0;
            for (int row = 0; row < platform.length; row++) {
                for (int col = 0; col < platform[0].length; col++) {
                    char c = platform[row][col];
                    if (c == ROCK) {
                        load += platform.length - row;
                    }
                }
            }
            return load;
        }
    }
}
