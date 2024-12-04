package kgnilov;

import com.google.common.base.Stopwatch;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day3 {

    public static void main(String[] args) throws IOException, URISyntaxException {
        URL url = Thread.currentThread().getContextClassLoader().getResource(resourceName());
        Path path = Paths.get(url.toURI());
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

//        part1(lines);
        Stopwatch started = Stopwatch.createStarted();
        part2(lines);
        started.stop();
        System.out.println(started.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    static String resourceName() {
        return "bigboy2.txt";
    }

    static void part1(List<String> lines) {
        String memoryString = StringUtils.join(lines);
        Pattern pattern = Pattern.compile("mul\\((\\d{1,3},\\d{1,3})\\)");
        Matcher matcher = pattern.matcher(memoryString);

        long result = 0;
        while (matcher.find()) {
            String multipliers = matcher.group(1);
            String[] split = multipliers.split(",");
            result += (long) Integer.parseInt(split[0]) * Integer.parseInt(split[1]);
        }

        System.out.println(result);
    }

    static void part2(List<String> lines) {
        String memoryString = StringUtils.join(lines);
        Pattern pattern = Pattern.compile("(mul\\((\\d{1,3},\\d{1,3})\\))|(do\\(\\))|"
                + "(don't\\(\\))");
        Matcher matcher = pattern.matcher(memoryString);
        long result = 0;
        boolean multiply = true;
        while (matcher.find()) {
            if (matcher.group(3) != null || matcher.group(4) != null) {
                multiply = matcher.group(3) != null;
                continue;
            }

            String multipliers = matcher.group(2);
            String[] split = multipliers.split(",");
            if (multiply) {
                result += Integer.parseInt(split[0]) * Integer.parseInt(split[1]);
            }
        }

        System.out.println(result);
    }
}
