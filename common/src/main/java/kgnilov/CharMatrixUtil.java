package kgnilov;

import java.util.List;

public final class CharMatrixUtil {

    private CharMatrixUtil() {
    }

    public static char[][] charMatrixFrom(List<String> lines) {
        char[][] matrix = new char[lines.size()][lines.get(0).length()];
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            char[] charArray = line.toCharArray();
            for (int j = 0; j < charArray.length; j++) {
                char c = charArray[j];
                matrix[i][j] = c;
            }
        }

        return matrix;
    }

    public static char[][] copyMatrix(char[][] matrix) {
        char[][] chars = new char[matrix.length][matrix[0].length];
        for (int k = 0; k < matrix.length; k++) {
            char[] matrixRow = matrix[k];
            System.arraycopy(matrixRow, 0, chars[k], 0, matrixRow.length);
        }
        return chars;
    }

    public static void printMatrix(char[][] matrix) {
        for (char[] chars : matrix) {
            System.out.println(chars);
        }
        System.out.println();
    }
}
