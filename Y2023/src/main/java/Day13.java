import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day13 {

    public static void main(String[] args) throws IOException, URISyntaxException {
        URL url = Thread.currentThread().getContextClassLoader().getResource("Day13.txt");
        Path path = Paths.get(url.toURI());
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

        List<char[][]> patterns = new ArrayList<>();

        List<char[]> list = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (!line.isBlank()) list.add(line.toCharArray());
            if (line.isBlank() || i == lines.size() - 1) {
                patterns.add(list.toArray(new char[list.size()][lines.get(i - 1).length()]));
                list = new ArrayList<>();
            }
        }

        part1(patterns);
        part2(patterns);
    }

    private static void part2(List<char[][]> patterns) {
        int answer = 0;
        patterns:
        for (char[][] pattern : patterns) {
            int smudgedRows = 0;
            boolean smudgedAxis;//true for horizontal
            char[][] rotated = rotateClockwise(pattern);

            int horizontal = countMirroredRows(pattern, -1);
            if (horizontal != 0) {
                smudgedRows = horizontal;
                smudgedAxis = true;
            } else {
                smudgedRows = countMirroredRows(rotated, -1);
                smudgedAxis = false;
            }

            int horIndexSkip = smudgedAxis ? smudgedRows : -1;
            int vertIndexSkip = smudgedAxis ? -1 : smudgedRows;
            for (int i = 0; i < pattern.length; i++) {
                for (int j = 0; j < pattern[0].length; j++) {
                    pattern[i][j] = pattern[i][j] == '#' ? '.' : '#';
                    rotated[j][i] = rotated[j][i] == '#' ? '.' : '#';

                    int newHorizontal = countMirroredRows(pattern, horIndexSkip);
                    if (newHorizontal != 0) {
                        answer += newHorizontal * 100;
                        continue patterns;
                    }

                    int newVertical = countMirroredRows(rotated, vertIndexSkip);
                    if (newVertical != 0) {
                        answer += newVertical;
                        continue patterns;
                    }

                    pattern[i][j] = pattern[i][j] == '#' ? '.' : '#';
                    rotated[j][i] = rotated[j][i] == '#' ? '.' : '#';
                }
            }
        }
        System.out.println(answer);
    }

    private static void part1(List<char[][]> patterns) {
        int answer = 0;
        for (char[][] pattern : patterns) {
            char[][] rotated = rotateClockwise(pattern);
            int vertical = countMirroredRows(rotated, -1);
            if (vertical != 0) {
                answer += vertical;
            }

            int horizontal = countMirroredRows(pattern, -1);
            if (horizontal != 0) {
                answer += horizontal * 100;
            }
            if (horizontal != 0 && vertical != 0) {
                printMatrix(pattern);
            }
        }

        System.out.println(answer);
    }

    private static int countMirroredRows(char[][] matrix, int indexToSkip) {
        char[] prevRow = null;
        for (int i = 0; i < matrix.length; i++) {
            char[] row = matrix[i];
            if (i == indexToSkip) {
                prevRow = row;
                continue;
            }
            if (Arrays.equals(row, prevRow)) {
                for (int j = 1; true; j++) {
                    try {
                        char[] next = matrix[i + j];
                        char[] mirrored = matrix[i - j - 1];
                        if (!Arrays.equals(next, mirrored)) {
                            break;
                        }
                    } catch (Exception e) {
                        return i;
                    }
                }
            }
            prevRow = row;
        }
        return 0;
    }

    static char[][] rotateClockwise(char[][] mat) {
        final int M = mat.length;
        final int N = mat[0].length;
        char[][] ret = new char[N][M];
        for (int r = 0; r < M; r++) {
            for (int c = 0; c < N; c++) {
                ret[c][M-1-r] = mat[r][c];
            }
        }
        return ret;
    }

    static void printMatrix(char[][] matrix) {
        for (char[] chars : matrix) {
            System.out.println(chars);
        }
        System.out.println();
    }
}
