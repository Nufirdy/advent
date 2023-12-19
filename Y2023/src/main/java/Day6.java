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
import java.util.stream.Stream;

public class Day6 {

    public static void main(String[] args) throws IOException, URISyntaxException {
        URL url = Thread.currentThread().getContextClassLoader().getResource("Day6.txt");
        Path path = Paths.get(url.toURI());
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

        List<Long> times = new ArrayList<>();
        String[] timeAndValues = lines.get(0).split(":");
        StringBuilder partTwoTime = new StringBuilder();
        for (String s : timeAndValues[1].split(" ")) {
            if (s.isBlank()) {
                continue;
            }
            partTwoTime.append(s);
        }
        times.add(Long.MAX_VALUE/2);

        List<Long> distances = new ArrayList<>();
        String[] distanceAndValues = lines.get(1).split(":");
        StringBuilder partTwoDistance = new StringBuilder();
        for (String s : distanceAndValues[1].split(" ")) {
            if (s.isBlank()) {
                continue;
            }
            partTwoDistance.append(s);
        }
        distances.add(Long.MAX_VALUE);

        int result = 1;
        for (int i = 0; i < times.size(); i++) {
            long raceTime = times.get(i);
            long distanceRecord = distances.get(i);

            int possibleWaysToWin = 0;
            for (long j = raceTime / 2; j >= 0; j--) {
                long distance = j * (raceTime - j);
                if (distance > distanceRecord) {
                    possibleWaysToWin++;
                }
            }
            possibleWaysToWin = possibleWaysToWin * 2;
            if (raceTime % 2 == 0) {
                possibleWaysToWin--;
            }
            result *= possibleWaysToWin;
        }

        System.out.println(result);
    }
}