import com.google.common.primitives.Chars;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Day11 {

    public static void main(String[] args) throws IOException, URISyntaxException {
        URL url = Thread.currentThread().getContextClassLoader().getResource("Day11.txt");
        Path path = Paths.get(url.toURI());
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

        Cosmos cosmos = new Cosmos(lines, 2L);
        cosmos.expand();
        cosmos.locateGalaxies();

        part1(lines);
        part2(lines);
    }

    private static void part2(List<String> lines) {
        Cosmos cosmos = new Cosmos(lines, 1000000L);
        cosmos.locateGalaxies();

        long distancesSum = 0;
        for (int i = 0; i < cosmos.correctedGalaxies.size(); i++) {
            Pair<Long, Long> galaxy = cosmos.correctedGalaxies.get(i);
            for (int j = i + 1; j < cosmos.correctedGalaxies.size(); j++) {
                Pair<Long, Long> otherGalaxy = cosmos.correctedGalaxies.get(j);
                distancesSum += cosmos.distanceBetween(galaxy, otherGalaxy);
            }
        }
        System.out.println(distancesSum);
    }

    private static void part1(List<String> lines) {
        Cosmos cosmos = new Cosmos(lines, 2L);
        cosmos.expand();
        cosmos.locateGalaxies();

        long distancesSum = 0;
        for (int i = 0; i < cosmos.galaxies.size(); i++) {
            Pair<Long, Long> galaxy = cosmos.galaxies.get(i);
            for (int j = i + 1; j < cosmos.galaxies.size(); j++) {
                Pair<Long, Long> otherGalaxy = cosmos.galaxies.get(j);
                distancesSum += Math.abs(galaxy.getLeft() - otherGalaxy.getLeft())
                        + Math.abs(galaxy.getRight() - otherGalaxy.getRight());
            }
        }
        System.out.println(distancesSum);
    }

    static class Cosmos {
        ArrayList<ArrayList<Character>> image;
        List<Integer> emptyRows = new ArrayList<>();
        List<Integer> emptyColumns = new ArrayList<>();
        List<Pair<Long, Long>> galaxies = new ArrayList<>();
        List<Pair<Long, Long>> correctedGalaxies = new ArrayList<>();

        long correction;

        public Cosmos(List<String> lines, long correction) {
            this.correction = correction;
            image = new ArrayList<>();
            for (int i = 0; i < lines.size(); i++) {
                image.add(new ArrayList<>(Chars.asList(lines.get(i).toCharArray())));
            }

            for (int i = 0; i < image.size(); i++) {
                List<Character> row = image.get(i);
                boolean hasGalaxies = false;
                for (char c : row) {
                    if (c == '#') {
                        hasGalaxies = true;
                        break;
                    }
                }
                if (!hasGalaxies) {
                    emptyRows.add(i);
                }
            }
            for (int i = 0; i < image.get(0).size(); i++) {
                boolean hasGalaxies = false;
                for (int j = 0; j < image.size(); j++) {
                    char c = image.get(j).get(i);
                    if (c == '#') {
                        hasGalaxies = true;
                        break;
                    }
                }
                if (!hasGalaxies) {
                    emptyColumns.add(i);
                }
            }
        }

        void locateGalaxies() {
            for (long i = 0; i < image.size(); i++) {
                List<Character> row = image.get((int) i);
                for (long j = 0; j < row.size(); j++) {
                    char c = row.get((int) j);
                    if (c == '#') {
                        galaxies.add(Pair.of(i, j));

                        long finalI = i;
                        long rowCorrectionMultiplier = emptyRows.stream()
                                .filter(rowIndex -> rowIndex < finalI)
                                .count();
                        long finalJ = j;
                        long columnCorrectionMultiplier = emptyColumns.stream()
                                .filter(colIndex -> colIndex < finalJ)
                                .count();
                        long correctedRow = i + rowCorrectionMultiplier * correction - rowCorrectionMultiplier;
                        long correctedColumn = j + columnCorrectionMultiplier * correction - columnCorrectionMultiplier;
                        correctedGalaxies.add(Pair.of(correctedRow, correctedColumn));
                    }
                }
            }
        }

        void expand() {
            //expand vertically
            for (int i = 0; i < image.size(); i++) {
                List<Character> row = image.get(i);
                boolean hasGalaxies = false;
                for (char c : row) {
                    if (c == '#') {
                        hasGalaxies = true;
                        break;
                    }
                }
                if (!hasGalaxies) {
                    image.add(i, new ArrayList<>(row));
                    i++;
                }
            }
            //expand horizontally
            for (int i = 0; i < image.get(0).size(); i++) {
                boolean hasGalaxies = false;
                for (int j = 0; j < image.size(); j++) {
                    char c = image.get(j).get(i);
                    if (c == '#') {
                        hasGalaxies = true;
                        break;
                    }
                }
                if (!hasGalaxies) {
                    for (int j = 0; j < image.size(); j++) {
                        image.get(j).add(i, '.');
                    }
                    i++;
                }
            }
        }

        long distanceBetween(Pair<Long, Long> galaxy,
                             Pair<Long, Long> otherGalaxy) {
            return Math.abs(galaxy.getLeft() - otherGalaxy.getLeft())
                    + Math.abs(galaxy.getRight() - otherGalaxy.getRight());
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (List<Character> row : image) {
                for (Character c : row) {
                    sb.append(c);
                }
                sb.append("\n");
            }
            return sb.toString();
        }
    }
}
