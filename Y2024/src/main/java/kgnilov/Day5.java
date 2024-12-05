package kgnilov;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Day5 {

    public static void main(String[] args) throws IOException, URISyntaxException {
        URL url = Thread.currentThread().getContextClassLoader().getResource(resourceName());
        Path path = Paths.get(url.toURI());
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

        part1(lines);
        part2(lines);
    }

    static String resourceName() {
        return "Day5";
    }

    static void part1(List<String> lines) {
        List<Pair<String, String>> rules = getRules(lines);
        List<String> updates = getUpdates(lines);

        int result = 0;
        for (String update : updates) {
            boolean isValid = true;
            for (Pair<String, String> rule : rules) {
                if (update.contains(rule.getLeft()) && update.contains(rule.getRight())) {
                    if (!(update.indexOf(rule.getLeft()) < update.indexOf(rule.getRight()))) {
                        isValid = false;
                        break;
                    }
                }
            }

            if (isValid) {
                String[] split = update.split(",");
                result += Integer.parseInt(split[split.length / 2]);
            }
        }

        System.out.println(result);
    }

    static void part2(List<String> lines) {
        List<Pair<String, String>> rules = getRules(lines);
        List<String> updates = getUpdates(lines);

        int result = 0;
        for (String update : updates) {
            boolean isValid = true;
            for (Pair<String, String> rule : rules) {
                if (update.contains(rule.getLeft()) && update.contains(rule.getRight())) {
                    if (!(update.indexOf(rule.getLeft()) < update.indexOf(rule.getRight()))) {
                        isValid = false;
                        break;
                    }
                }
            }

            if (!isValid) {
                List<String> split = Arrays.asList(update.split(","));

                split.sort((o1, o2) -> {
                    for (Pair<String, String> rule : rules) {
                        if (rule.getLeft().equals(o1) && rule.getRight().equals(o2)) {
                            return -1;
                        } else if (rule.getRight().equals(o1) && rule.getLeft().equals(o2)) {
                            return 1;
                        }
                    }
                    return 0;
                });

                result += Integer.parseInt(split.get(split.size() / 2));
            }
        }

        System.out.println(result);
    }

    private static List<Pair<String, String>> getRules(List<String> lines) {
        List<Pair<String, String>> rules = new ArrayList<>();
        for (String line : lines) {
            if (StringUtils.isBlank(line)) {
                break;
            }
            String[] split = line.split("\\|");
            rules.add(Pair.of(split[0], split[1]));
        }
        return rules;
    }

    private static List<String> getUpdates(List<String> lines) {
        return lines.stream()
                .filter(line -> line.contains(","))
                .toList();
    }
}
