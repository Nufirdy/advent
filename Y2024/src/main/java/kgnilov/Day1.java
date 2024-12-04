package kgnilov;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Day1 {

    public static void main(String[] args) throws IOException, URISyntaxException {
        URL url = Thread.currentThread().getContextClassLoader().getResource(resourceName());
        Path path = Paths.get(url.toURI());
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

        part1(lines);
        part2(lines);
    }

    protected static String resourceName() {
        return "Day1.txt";
    }

    protected static void part1(List<String> lines) {
        List<Integer> left = new ArrayList<>();
        List<Integer> right = new ArrayList<>();
        for (String line : lines) {
            String[] split = StringUtils.split(line, " ");
            left.add(Integer.valueOf(split[0]));
            right.add(Integer.valueOf(split[1]));
        }
        Collections.sort(left);
        Collections.sort(right);

        long result = 0;
        for (int i = 0; i < left.size(); i++) {
            result += Math.abs(left.get(i) - right.get(i));
        }

        System.out.println(result);
    }

    protected static void part2(List<String> lines) {
        List<Integer> left = new ArrayList<>();
        List<Integer> right = new ArrayList<>();
        for (String line : lines) {
            String[] split = StringUtils.split(line, " ");
            left.add(Integer.valueOf(split[0]));
            right.add(Integer.valueOf(split[1]));
        }

        long result = 0;
        for (Integer leftId : left) {
            long count = right.stream()
                    .filter(rightId -> rightId.equals(leftId))
                    .count();
            result += leftId * count;
        }
        System.out.println(result);
    }
}
