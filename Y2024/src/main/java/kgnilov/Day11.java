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
import java.util.concurrent.ExecutionException;

public class Day11 {

    public static void main(String[] args) throws IOException, URISyntaxException,
            ExecutionException, InterruptedException {
        URL url = Thread.currentThread().getContextClassLoader().getResource(resourceName());
        Path path = Paths.get(url.toURI());
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

        part1(lines);
        part2(lines);
    }

    static String resourceName() {
        return "Day11";
    }

    static void part1(List<String> lines) {
        List<String> stones = new ArrayList<>(Arrays.asList(lines.get(0).split(" ")));

        long stonesCount = 0;

        Map<Pair<String, Integer>, Long> counted = new HashMap<>();
        for (String stone : stones) {
            stonesCount += countStones(stone, 0, counted, 25);
        }

        System.out.println(stonesCount);
    }

    static void part2(List<String> lines) throws ExecutionException, InterruptedException {
        List<String> stones = new ArrayList<>(Arrays.asList(lines.get(0).split(" ")));

        long stonesCount = 0;

        Map<Pair<String, Integer>, Long> counted = new HashMap<>();
        for (String stone : stones) {
            stonesCount += countStones(stone, 0, counted, 75);
        }

        System.out.println(stonesCount);
    }

    private static long countStones(String stone, int depth,
                                    Map<Pair<String, Integer>, Long> counted, int totalDepth) {
        if (counted.containsKey(Pair.of(stone, depth))) {
            return counted.get(Pair.of(stone, depth));
        }
        if (depth == totalDepth) {
            return 1;
        }

        long stoneCount = 0;
        if (stone.equals("0")) {
            stoneCount += countStones("1", depth + 1, counted, totalDepth);
        } else if (stone.length() % 2 == 0) {
            stoneCount += countStones(stone.substring(0, stone.length() / 2), depth + 1, counted, totalDepth);

            String rightStone = String.valueOf(Long.parseLong(stone.substring(stone.length() / 2)));
            stoneCount += countStones(rightStone, depth + 1, counted, totalDepth);
        } else {

            stoneCount += countStones(String.valueOf(Long.parseLong(stone) * 2024), depth + 1, counted, totalDepth);
        }

        counted.put(Pair.of(stone, depth), stoneCount);
        return stoneCount;
    }
}
