import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class InputUtils {

    public static List<String> getFromResource(String fileName) {
        URL url = Thread.currentThread().getContextClassLoader().getResource(fileName);
        Path path;
        try {
            path = Paths.get(url.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        try {
            return Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static char[][] get2DArray(List<String> lines) {
        char[][] array = new char[lines.size()][lines.get(0).length()];
        List<char[]> chars = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            chars.add(line.toCharArray());
        }
        return chars.toArray(array);
    }

    public static void printCharArray(char[][] matrix) {
        for (char[] chars : matrix) {
            System.out.println(chars);
        }
        System.out.println();
    }

    public static <T> T[][] deepCopy(T[][] matrix) {
        return java.util.Arrays.stream(matrix).map(el -> el.clone()).toArray($ -> matrix.clone());
    }


}
