package kgnilov;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static kgnilov.CharMatrixUtil.printMatrix;

public class Day8 {

    public static void main(String[] args) throws IOException, URISyntaxException {
        URL url = Thread.currentThread().getContextClassLoader().getResource(resourceName());
        Path path = Paths.get(url.toURI());
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

        part1(lines);
        part2(lines);
    }

    static String resourceName() {
        return "Day8";
    }

    static void part1(List<String> lines) {
        Map<Character, List<Pair>> antennas = new HashMap<>();
        char[][] matrix = new char[lines.size()][lines.get(0).length()];
        int antinodesCount = 0;

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            char[] charArray = line.toCharArray();
            for (int j = 0; j < charArray.length; j++) {
                char c = charArray[j];
                matrix[i][j] = c;
                if (c != '.') {
                    antennas.computeIfAbsent(c, ArrayList::new).add(new Pair(i, j));
                }
            }
        }

        Set<Pair> antinodes = new HashSet<>();
        for (Map.Entry<Character, List<Pair>> entry :
                antennas.entrySet()) {
            List<Pair> value = entry.getValue();
            for (int i = 0; i < value.size(); i++) {
                Pair antenna1 = value.get(i);
                for (int j = i + 1; j < value.size(); j++) {
                    Pair antenna2 = value.get(j);
                    int lDiff = antenna1.left - antenna2.left;
                    int rDiff = antenna1.right - antenna2.right;

                    Pair antinode1 = antenna1.add(lDiff, rDiff);
                    Pair antinode2 = antenna1.subtract(lDiff, rDiff);
                    Pair antinode3 = antenna2.add(lDiff, rDiff);
                    Pair antinode4 = antenna2.subtract(lDiff, rDiff);

                    if (!antinode1.equals(antenna2) && inBounds(matrix, antinode1) && !antinodes.contains(antinode1)) {
                        antinodesCount++;
                        antinodes.add(antinode1);
                    }
                    if (!antinode2.equals(antenna2) && inBounds(matrix, antinode2) && !antinodes.contains(antinode2)) {
                        antinodesCount++;
                        antinodes.add(antinode2);
                    }
                    if (!antinode3.equals(antenna1) && inBounds(matrix, antinode3) && !antinodes.contains(antinode3)) {
                        antinodesCount++;
                        antinodes.add(antinode3);
                    }
                    if (!antinode4.equals(antenna1) && inBounds(matrix, antinode4) && !antinodes.contains(antinode4)) {
                        antinodesCount++;
                        antinodes.add(antinode4);
                    }
                }
            }
        }

        System.out.println(antinodesCount);
    }

    static void part2(List<String> lines) {
        Map<Character, List<Pair>> antennas = new HashMap<>();
        char[][] matrix = new char[lines.size()][lines.get(0).length()];
        int antinodesCount = 0;

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            char[] charArray = line.toCharArray();
            for (int j = 0; j < charArray.length; j++) {
                char c = charArray[j];
                matrix[i][j] = c;
                if (c != '.') {
                    antennas.computeIfAbsent(c, ArrayList::new).add(new Pair(i, j));
                }
            }
        }

        Set<Pair> antinodes = new HashSet<>();
        for (Map.Entry<Character, List<Pair>> entry :
                antennas.entrySet()) {
            List<Pair> value = entry.getValue();
            for (int i = 0; i < value.size(); i++) {
                Pair antenna1 = value.get(i);
                for (int j = i + 1; j < value.size(); j++) {
                    Pair antenna2 = value.get(j);
                    int lDiff = antenna1.left - antenna2.left;
                    int rDiff = antenna1.right - antenna2.right;

                    for (int k = 1;; k++) {
                        Pair antinode = antenna1.add(lDiff * k, rDiff * k);
                        if (!antinodes.contains(antinode)) {
                            if (!inBounds(matrix, antinode)) {
                                break;
                            }
                            antinodesCount++;
                            antinodes.add(antinode);
                        }
                    }

                    for (int k = 0;; k++) {
                        Pair antinode = antenna1.subtract(lDiff * k, rDiff * k);
                        if (!antinodes.contains(antinode)) {
                            if (!inBounds(matrix, antinode)) {
                                break;
                            }
                            antinodesCount++;
                            antinodes.add(antinode);
                        }
                    }
                }
            }
        }

        System.out.println(antinodesCount);

//        for (Pair antinode : antinodes) {
//            matrix[antinode.left][antinode.right] = '#';
//        }
//        printMatrix(matrix);
    }

    static boolean inBounds(char[][] matrix, Pair pair) {
        return (pair.left >= 0 && pair.left < matrix.length) && (pair.right >= 0 && pair.right < matrix[0].length);
    }

    static class Pair {
        int left, right;

        public Pair(int left, int right) {
            this.left = left;
            this.right = right;
        }

        Pair add(int l, int r) {
            return new Pair(left + l, right + r);
        }

        Pair subtract(int l, int r) {
            return new Pair(left - l, right - r);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Pair pair)) return false;
            return left == pair.left && right == pair.right;
        }

        @Override
        public int hashCode() {
            return Objects.hash(left, right);
        }
    }
}
