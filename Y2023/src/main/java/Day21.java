import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day21 {
    public static final char ROCK = '#';

    public static void main(String[] args) throws InterruptedException, IOException {
        List<String> lines = InputUtils.getFromResource("Day21.txt");

        part1(lines);
        part2(lines);
    }


    //поперечник 2х+1
    //7596/8580 и 7577/8580
    // 620 221 075 033 141 < x < 621 221 075 033 141
    //каждая точка повторяется каждые side.length шагов
    //зная количество шагов, можно получить расстояние от центра, до угла, они равны
    // максимум % 131
    //например 131 шаг, камень с относительной координатой -1.1, повтор камня вверх на

    //вся площадь, которую охватят 26501365 это шахматная доска, ввод имеет две части,
    //центральный ромб и составной ромб из углов, задача посчитать сколько ромбов войдет в
    //конечную раскраску, из площади 26501365^2 вычесть
    private static void part2(List<String> lines) {
        List<String> emptyLines = new ArrayList<>();
        for (String line : lines) {
            char[] chars = line.toCharArray();
            StringBuilder emptyLine = new StringBuilder();
            for (char c : chars) {
                if (c == ROCK) {
                    c = '.';
                }
                emptyLine.append(c);
            }
            emptyLines.add(emptyLine.toString());
        }
        Garden garden = new Garden(lines);
        Garden emptyGarden = new Garden(emptyLines);

        int oddSteps = 65;
        long centralOddBlockedTiles =
                ((oddSteps + 1) * (oddSteps + 1)) - garden.countReachableGardenPlots(oddSteps);

        int fullFieldOddSteps = 131;
        long cornerOddBlockedTiles =
                emptyGarden.countReachableGardenPlots(fullFieldOddSteps)
                        - garden.countReachableGardenPlots(fullFieldOddSteps)
                        - centralOddBlockedTiles;

        long evenSteps = 64;
        long centralEvenBlockedTiles =
                ((evenSteps + 1) * (evenSteps + 1)) - garden.countReachableGardenPlots((int) evenSteps);

        int fullFieldEvenSteps = 130;
        long cornerEvenBlockedTiles =
                emptyGarden.countReachableGardenPlots(fullFieldEvenSteps)
                        - garden.countReachableGardenPlots(fullFieldEvenSteps)
                        - centralEvenBlockedTiles;

        long totalSteps = 26501365;
        long totalDiamonds = ((totalSteps * 2 + 1) / 131) * ((totalSteps * 2 + 1) / 131);
        long halfTotalCornerDiamonds = (totalDiamonds / 2) / 2;
        //x^2 + (x+1)^2 = 81850984601
        long totalEvenCentralDiamonds = 202300L * 202300L;
        long totalOddCentralDiamonds = 202301L * 202301L;
        long totalReachable =
                ((totalSteps + 1) * (totalSteps + 1))
                        - (halfTotalCornerDiamonds * cornerOddBlockedTiles
                        + halfTotalCornerDiamonds * cornerEvenBlockedTiles
                        + totalEvenCentralDiamonds * centralEvenBlockedTiles
                        + totalOddCentralDiamonds * centralOddBlockedTiles);
        System.out.println(totalReachable);
        //3740 центральный
        //3906 угловой
        //сколько всего квадратов в конечной закраске
        //сколько центральных и сколько угловых

        //196^2(38416) - 34324 = 4092 камня шаг 196 на 9 квадратов
        //4356 - 3859 = 497
        //485 камней в центральном квадрате для четного
        //578 камней в угловом квадрате для четного
        //497 камней в центре на нечетном
        //491 камней в углах на нечетном

        //четность меняется на каждый последующий квадрат ввода
        //следовательно примерно половина централиных считается как четные, другая как нечетные
        //также угловые наполовину состоят из четных и нечетных квадратов, и также имеют две формы
        //условно четную и нечетную
        //четная пустая 8581 полная 7596
        //нечетная пустая 8580 полная 7577
        //угловых четных 8581 - 7596 - 485 = 500
        //угловых нечетных 8580 - 7577 - 497 = 506


        //40925290000 четных 40925694601 нечетных

        //26501366^2 - (40925492300 * 500 + 40925492300 * 506 + 40925694601 * 497 + 40925290000 *
        // 485)
    }

    private static void part1(List<String> lines) throws InterruptedException, IOException {
        Garden garden = new Garden(lines);
        System.out.println(garden.countReachableGardenPlots(64));
    }

    static class Garden {
        public static final char MARK = 'O';

        char[][] layout;
        char[][] layoutCopy;
        Pair<Integer, Integer> start;

        public Garden(List<String> lines) {
            layout = InputUtils.get2DArray(lines);
            layoutCopy = InputUtils.get2DArray(lines);

            int totalRocks = 0;
            int centralRocks = 0;
            for (int row = 0; row < layout.length; row++) {
                for (int col = 0; col < layout[0].length; col++) {
                    char c = layout[row][col];
                    if (c == 'S') {
                        start = Pair.of(row, col);
                    }
                }
            }
            currentMarkedSteps = Set.of(start);
        }

        public int countReachableGardenPlots(int stepsCount) {
            currentMarkedSteps = Set.of(start);
            for (int i = 0; i < stepsCount; i++) {
                layoutCopy = InputUtils.deepCopy(layout);
                markPossibleSteps();
            }

            return countCurrentPossibilities();
        }

        Set<Pair<Integer, Integer>> currentMarkedSteps;

        private void markPossibleSteps() {
            Set<Pair<Integer, Integer>> newMarkedSteps = new HashSet<>();
            for (Pair<Integer, Integer> location : currentMarkedSteps) {
                for (Direction dir : Direction.values()) {
                    Pair<Integer, Integer> step = tryStep(location, dir);
                    if (step != null) {
                        newMarkedSteps.add(step);
                    }
                }
            }
            currentMarkedSteps = newMarkedSteps;
        }

        private Pair<Integer, Integer> tryStep(Pair<Integer, Integer> from, Direction to) {
            int row = from.getLeft();
            int col = from.getRight();
            switch (to) {
                case UP -> {
                    row -= 1;
                    if (row == -1) {
                        return null;
                    }
                }
                case DOWN -> {
                    row += 1;
                    if (row == layout.length) {
                        return null;
                    }
                }
                case LEFT -> {
                    col -= 1;
                    if (col == -1) {
                        return null;
                    }
                }
                case RIGHT -> {
                    col += 1;
                    if (col == layout[0].length) {
                        return null;
                    }
                }
            }
            char c = layoutCopy[row][col];
            if (c != ROCK) {
                layoutCopy[row][col] = MARK;
                return Pair.of(row, col);
            }
            return null;
        }

        private int countCurrentPossibilities() {
            int count = 0;
            for (int row = 0; row < layoutCopy.length; row++) {
                for (int col = 0; col < layoutCopy[0].length; col++) {
                    char c = layoutCopy[row][col];
                    if (c == MARK) {
                        count++;
                    }
                }
            }

            return count;
        }
    }
}
