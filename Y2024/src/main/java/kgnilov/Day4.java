package kgnilov;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Day4 {

    public static void main(String[] args) throws IOException, URISyntaxException {
        URL url = Thread.currentThread().getContextClassLoader().getResource(resourceName());
        Path path = Paths.get(url.toURI());
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

        part1(lines);
        part2(lines);
    }

    static String resourceName() {
        return "Day4";
    }

    static void part1(List<String> lines) {
        char[][] xmasMatrix = xmasMatrix(lines);
        int count = 0;
        //horizontal
        for (int i = 0; i < xmasMatrix.length; i++) {
            char[] xmasMatrixLine = xmasMatrix[i];
            for (int j = 0; j < xmasMatrixLine.length; j++) {
                char x = xmasMatrixLine[j];
                try {
                    if (x == 'X') {
                        char m = xmasMatrixLine[j + 1];
                        if (m == 'M') {
                            char a = xmasMatrixLine[j + 2];
                            if (a == 'A') {
                                char s = xmasMatrixLine[j + 3];
                                if (s == 'S') {
                                    count++;
                                }
                            }
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException e) {

                }
            }
        }

        for (int i = 0; i < xmasMatrix.length; i++) {
            char[] xmasMatrixLine = xmasMatrix[i];
            for (int j = xmasMatrixLine.length - 1; j >= 0; j--) {
                char x = xmasMatrixLine[j];
                try {
                    if (x == 'X') {
                        char m = xmasMatrixLine[j - 1];
                        if (m == 'M') {
                            char a = xmasMatrixLine[j - 2];
                            if (a == 'A') {
                                char s = xmasMatrixLine[j - 3];
                                if (s == 'S') {
                                    count++;
                                }
                            }
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException e) {

                }
            }
        }
        //vertical
        for (int i = 0; i < xmasMatrix[0].length; i++) {
            for (int j = 0; j < xmasMatrix.length; j++) {
                char x = xmasMatrix[j][i];
                try {
                    if (x == 'X') {
                        char m = xmasMatrix[j + 1][i];
                        if (m == 'M') {
                            char a = xmasMatrix[j + 2][i];
                            if (a == 'A') {
                                char s = xmasMatrix[j + 3][i];
                                if (s == 'S') {
                                    count++;
                                }
                            }
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException e) {

                }
            }
        }

        for (int i = xmasMatrix[0].length - 1; i >= 0; i--) {
            for (int j = 0; j < xmasMatrix.length; j++) {
                char x = xmasMatrix[j][i];
                try {
                    if (x == 'X') {
                        char m = xmasMatrix[j - 1][i];
                        if (m == 'M') {
                            char a = xmasMatrix[j - 2][i];
                            if (a == 'A') {
                                char s = xmasMatrix[j - 3][i];
                                if (s == 'S') {
                                    count++;
                                }
                            }
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException e) {

                }
            }
        }
        //diagonal up right
        int i = 0;
        int j = 0;
        while (true) {
            int ix = i;
            int jy = j;
            while (true) {
                char x = 0;
                try {
                    x = xmasMatrix[ix][jy];
                } catch (Exception e) {
                    break;
                }
                try {
                    if (x == 'X') {
                        char m = xmasMatrix[ix - 1][jy + 1];
                        if (m == 'M') {
                            char a = xmasMatrix[ix - 2][jy + 2];
                            if (a == 'A') {
                                char s = xmasMatrix[ix - 3][jy + 3];
                                if (s == 'S') {
                                    count++;
                                }
                            }
                        }
                    }
                } catch (Exception e) {

                } finally {
                    ix--;
                    jy++;
                }
            }

            if (j == xmasMatrix[i].length - 1) {
                break;
            } else if (i == xmasMatrix.length - 1){
                j++;
            } else {
                i++;
            }
        }

        //diagonal down left
        i = 0;
        j = 0;
        while (true) {
            int ix = i;
            int jy = j;
            while (true) {
                char x = 0;
                try {
                    x = xmasMatrix[ix][jy];
                } catch (Exception e) {
                    break;
                }
                try {
                    if (x == 'X') {
                        char m = xmasMatrix[ix + 1][jy - 1];
                        if (m == 'M') {
                            char a = xmasMatrix[ix + 2][jy - 2];
                            if (a == 'A') {
                                char s = xmasMatrix[ix + 3][jy - 3];
                                if (s == 'S') {
                                    count++;
                                }
                            }
                        }
                    }
                } catch (Exception e) {

                } finally {
                    ix++;
                    jy--;
                }
            }

            if (i == xmasMatrix.length - 1) {
                break;
            } else if (j == xmasMatrix[i].length - 1){
                i++;
            } else {
                j++;
            }
        }

        //diagonal down right
        i = 0;
        j = xmasMatrix[0].length - 1;
        while (true) {
            int ix = i;
            int jy = j;
            while (true) {
                char x = 0;
                try {
                    x = xmasMatrix[ix][jy];
                } catch (Exception e) {
                    break;
                }
                try {
                    if (x == 'X') {
                        char m = xmasMatrix[ix + 1][jy + 1];
                        if (m == 'M') {
                            char a = xmasMatrix[ix + 2][jy + 2];
                            if (a == 'A') {
                                char s = xmasMatrix[ix + 3][jy + 3];
                                if (s == 'S') {
                                    count++;
                                }
                            }
                        }
                    }
                } catch (Exception e) {

                } finally {
                    ix++;
                    jy++;
                }
            }

            if (i == xmasMatrix.length - 1) {
                break;
            } else if (j == 0){
                i++;
            } else {
                j--;
            }
        }

        //diagonal up left
        i = 0;
        j = xmasMatrix[0].length - 1;
        while (true) {
            int ix = i;
            int jy = j;
            while (true) {
                char x = 0;
                try {
                    x = xmasMatrix[ix][jy];
                } catch (Exception e) {
                    break;
                }
                try {
                    if (x == 'X') {
                        char m = xmasMatrix[ix - 1][jy - 1];
                        if (m == 'M') {
                            char a = xmasMatrix[ix - 2][jy - 2];
                            if (a == 'A') {
                                char s = xmasMatrix[ix - 3][jy - 3];
                                if (s == 'S') {
                                    count++;
                                }
                            }
                        }
                    }
                } catch (Exception e) {

                } finally {
                    ix--;
                    jy--;
                }
            }

            if (j == 0) {
                break;
            } else if (i == xmasMatrix.length - 1){
                j--;
            } else {
                i++;
            }
        }


        System.out.println(count);
    }

    static void part2(List<String> lines) {
        char[][] xmasMatrix = xmasMatrix(lines);

        int count = 0;
        for (int i = 0; i < xmasMatrix.length; i++) {
            for (int j = 0; j < xmasMatrix[0].length; j++) {
                char a = xmasMatrix[i][j];
                try {
                    if (a == 'A') {
                        char nw = xmasMatrix[i - 1][j - 1];
                        char se = xmasMatrix[i + 1][j + 1];
                        if ((nw == 'M' && se == 'S') || (nw == 'S' && se == 'M')) {
                            char ne = xmasMatrix[i - 1][j + 1];
                            char sw = xmasMatrix[i + 1][j - 1];
                            if ((ne == 'M' && sw == 'S') || (ne == 'S' && sw == 'M')) {
                                count++;
                            }
                        }
                    }
                } catch (Exception e) {

                }
            }
        }

        System.out.println(count);
    }

    private static char[][] xmasMatrix(List<String> lines) {
        char[][] xmasMatrix = new char[lines.size()][lines.get(0).length()];
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            char[] lineChars = line.toCharArray();
            System.arraycopy(lineChars, 0, xmasMatrix[i], 0, lineChars.length);
        }
        return xmasMatrix;
    }
}
