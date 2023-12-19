import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Combinations;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day12 {
    final static char UNKNOWN = '?';
    final static char WHITE = '.';
    final static char BLACK = '#';

    static final Map<String, Long> cache = new HashMap<>();

    public static void main(String[] args) throws IOException, URISyntaxException {
        URL url = Thread.currentThread().getContextClassLoader().getResource("Day12.txt");
        Path path = Paths.get(url.toURI());
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

//        part1(lines);
        part2(lines);
    }

    private static void part2(List<String> lines) {
        long solutionsSum = 0;
        for (String line : lines) {
            String[] rowAndDesc = line.split(" ");
            String actualRow = StringUtils.repeat(rowAndDesc[0], String.valueOf(UNKNOWN), 5);
            char[] row = actualRow.toCharArray();

            int[] desc = Arrays.stream(StringUtils.repeat(rowAndDesc[1], ",", 5).split(","))
                    .mapToInt(Integer::parseInt)
                    .toArray();
            solutionsSum += count(row, desc);
        }

        System.out.println(solutionsSum);
    }

    private static void part1(List<String> lines) {
        int solutionsSum = 0;

        for (int i = 0; i< lines.size(); i++) {
            String line = lines.get(i);
            String[] rowAndDesc = line.split(" ");
            char[] row = rowAndDesc[0].toCharArray();
            int[] desc = Arrays.stream(rowAndDesc[1].split(","))
                    .mapToInt(Integer::parseInt)
                    .toArray();
            long solutionsCount = count(row, desc);
            solutionsSum += solutionsCount;
        }

        System.out.println(solutionsSum);
    }

    static int countAllSolutions(char[] row, int[] description) {

        int rowLength = row.length;
        int freeCells = rowLength - (Arrays.stream(description).sum() + (description.length - 1));
        Combinations combinations =
                new Combinations(description.length + freeCells,
                        description.length);

        int actualSolutions = 0;
        char[] possibleSol;
        combinations:
        for (int[] combination : combinations) {
            possibleSol = new char[row.length];
            int solutionIndex = 0;
            for (int i = 0; i < combination.length; i++) {
                int nWhites = i == 0 ? combination[i] : combination[i] - combination[i - 1];
                int blockLength = description[i];

                int breakCond = nWhites + blockLength;
                for (int j = 0; j < breakCond; j++) {
                    possibleSol[j + solutionIndex] = j < nWhites ? WHITE : BLACK;
                    if (!isActualSolution(possibleSol, row)) {
                        continue combinations;
                    }
                }
                solutionIndex += breakCond;
            }
            if (rowLength > solutionIndex) {
                for (; solutionIndex < rowLength; solutionIndex++) {
                    possibleSol[solutionIndex] = WHITE;
                }
            }
            if (isActualSolution(possibleSol, row)) {
                actualSolutions++;
            }
//            System.out.println(possibleSol);
        }

        return actualSolutions;
    }

    static boolean isActualSolution(char[] possibleSol, char[] row) {
        for (int i = 0; i < row.length; i++) {
            if (row[i] != UNKNOWN && row[i] != possibleSol[i] && possibleSol[i] != '\u0000') {
                return false;
            }
        }
        return true;
    }

    static long count(char[] row, int[] d) {
        if (row.length == 0) {
            return d.length == 0 ? 1 : 0;
        }

        if (d.length == 0) {
            return String.valueOf(row).contains("#") ? 0 : 1;
        }

        long result = 0;
        Long cached = cache.get(Arrays.hashCode(row) + " " + Arrays.hashCode(d));
        if (cached != null) {
            return cached;
        }

        if (row[0] == WHITE || row[0] == UNKNOWN) {
            char[] trimRow = Arrays.copyOfRange(row, 1, row.length);
            result += count(trimRow, d);
        }

        if (row[0] == BLACK || row[0] == UNKNOWN) {
            if (d[0] <= row.length
                    && !new String(row, 0, d[0]).contains(String.valueOf(WHITE))
                    && (row.length == d[0] || row[d[0]] != BLACK)) {
                char[] trimRow = new char[0];
                try {
                    trimRow = Arrays.copyOfRange(row, d[0] + 1, row.length);
                } catch (Exception e) {

                }
                int[] trimD = Arrays.copyOfRange(d, 1, d.length);
                result += count(trimRow, trimD);
            }
        }

        cache.put(Arrays.hashCode(row) + " " + Arrays.hashCode(d), result);
        return result;
    }
}
