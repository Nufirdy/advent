import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Day3 {

    public static void main(String[] args) throws IOException, URISyntaxException {
        URL url = Thread.currentThread().getContextClassLoader().getResource("Day3-test2.txt");
        Path path = Paths.get(url.toURI());
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

        int numberPartsSum = 0;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            char[] chars = line.toCharArray();

            for (int j = 0; j < chars.length; ) {
                char c = chars[j];
                if (!Character.isDigit(c)) {
                    j++;
                    continue;
                }
                StringBuilder numberBuilder = new StringBuilder();
                numberBuilder.append(c);

                while (true) {
                    j++;
                    try {
                        c = chars[j];
                    } catch (Exception e) {
                        break;
                    }
                    if (!Character.isDigit(c)) {
                        break;
                    }
                    numberBuilder.append(c);
                }
                String numberString = numberBuilder.toString();
                if (isPartNumber(lines, i, j, numberString.length())) {
                    numberPartsSum += Integer.parseInt(numberString);
                }
            }
        }

        System.out.println(numberPartsSum);
    }

    private static boolean isPartNumber(List<String> lines, int i, int j, int length) {
        for (int k = i - 1; k < i + 2; k++) {
            String line;
            try {
                line = lines.get(k);
            } catch (Exception ignored) {
                continue;
            }
            char[] chars = line.toCharArray();
            for (int l = j; l > j - length - 2; l--) {
                char c;
                try {
                    c = chars[l];
                } catch (Exception e) {
                    continue;
                }
                if (!Character.isDigit(c) && c != '.') {
                    return true;
                }
            }
        }

        return false;
    }
}
