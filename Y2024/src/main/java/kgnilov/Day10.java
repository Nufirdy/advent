package kgnilov;

import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day10 {

    public static void main(String[] args) throws IOException, URISyntaxException {
        URL url = Thread.currentThread().getContextClassLoader().getResource(resourceName());
        Path path = Paths.get(url.toURI());
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

        part1(lines);
        part2(lines);
    }

    static String resourceName() {
        return "Day10";
    }

    static void part1(List<String> lines) {
        HikingMap hikingMap = new HikingMap(lines);
        int trailScores = hikingMap.countTrailScores();
        System.out.println(trailScores);
    }

    static void part2(List<String> lines) {
        HikingMap hikingMap = new HikingMap(lines);
        int trailScores = hikingMap.countTrailScoreAll();
        System.out.println(trailScores);
    }

    static class HikingMap {

        char[][] map;
        Set<Pair<Integer, Integer>> trailheads = new HashSet<>();

        public HikingMap(List<String> lines) {
            map = new char[lines.size()][lines.get(0).length()];
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                char[] charArray = line.toCharArray();
                for (int j = 0; j < charArray.length; j++) {
                    char c = charArray[j];
                    map[i][j] = c;
                    if (c == '0') {
                        trailheads.add(Pair.of(i, j));
                    }
                }
            }
        }

        int countTrailScores() {
            int sum = 0;
            for (Pair<Integer, Integer> trailhead : trailheads) {
                sum += countTrailScore(trailhead.getLeft(), trailhead.getRight(), new HashSet<>()).size();
            }
            return sum;
        }

        private Set<Pair<Integer, Integer>> countTrailScore(int row, int col, Set<Pair<Integer, Integer>> visited) {
            char height = map[row][col];
            if (height == '9') {
                visited.add(Pair.of(row, col));
                return visited;
            }
            for (int dir = 0; dir < 4; dir++) {
                int i = row, j = col;
                switch (dir) {
                    case 0 -> i--;
                    case 1 -> j++;
                    case 2 -> i++;
                    case 3 -> j--;
                }
                char nextHeight;
                try {
                    nextHeight = map[i][j];
                } catch (Exception e) {
                    continue;
                }
                if (nextHeight - 1 == height) {
                    countTrailScore(i, j, visited);
                }
            }
            return visited;
        }

        int countTrailScoreAll() {
            int sum = 0;
            for (Pair<Integer, Integer> trailhead : trailheads) {
                sum += countAllTrails(trailhead.getLeft(), trailhead.getRight());
            }
            return sum;
        }

        private int countAllTrails(int row, int col) {
            char height = map[row][col];
            if (height == '9') {
                return 1;
            }

            int score = 0;
            for (int dir = 0; dir < 4; dir++) {
                int i = row, j = col;
                switch (dir) {
                    case 0 -> i--;
                    case 1 -> j++;
                    case 2 -> i++;
                    case 3 -> j--;
                }
                char nextHeight;
                try {
                    nextHeight = map[i][j];
                } catch (Exception e) {
                    continue;
                }
                if (nextHeight - 1 == height) {
                    score += countAllTrails(i, j);
                }
            }
            return score;
        }
    }
}
