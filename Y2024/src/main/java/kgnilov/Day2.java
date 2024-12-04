package kgnilov;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Ordering;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Day2 {

    @State(Scope.Benchmark)
    public static class BenchState {
        List<String> arg;

        @Setup
        public void setupArg() throws IOException, URISyntaxException {
            URL url = Thread.currentThread().getContextClassLoader().getResource(resourceName());
            Path path = Paths.get(url.toURI());
            arg = Files.readAllLines(path, StandardCharsets.UTF_8);
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Warmup(iterations = 3)
    @Measurement(iterations = 3)
    @Timeout(time = 3, timeUnit = TimeUnit.MINUTES)
    public void measurePart2(BenchState state) {
        alternativePart2(state.arg);
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        URL url = Thread.currentThread().getContextClassLoader().getResource(resourceName());
        Path path = Paths.get(url.toURI());
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

//        part1(lines);
        Stopwatch started = Stopwatch.createStarted();
        part2(lines);
        started.stop();
        System.out.println(started.elapsed(TimeUnit.MILLISECONDS));
    }

    protected static String resourceName() {
        return "bigboy.txt";
    }

    protected static void part1(List<String> lines) {
        int totalSafeReports = 0;
        for (String reportString : lines) {
            boolean safeReport = true;

            String[] split = reportString.split(" ");
            List<Integer> report = new ArrayList<>();
            for (int i = 0; i < split.length; i++) {
                String s = split[i];
                Integer level = Integer.valueOf(s);
                report.add(level);
            }

            boolean isAscending = Ordering.natural().isOrdered(report);
            boolean isDescending = Ordering.natural().reverse().isOrdered(report);

            if (isAscending || isDescending) {
                for (int i = 1; i < report.size(); i++) {
                    int level = report.get(i);
                    int prevLevel = report.get(i - 1);
                    int levelDiff = Math.abs(prevLevel - level);
                    if (levelDiff > 3 || levelDiff == 0) {
                        safeReport = false;
                        break;
                    }
                }
            } else {
                safeReport = false;
            }

            if (safeReport) {
                totalSafeReports++;
            }
        }
        System.out.println(totalSafeReports);
    }

    protected static void part2(List<String> lines) {
        int totalSafeReports = 0;
        for (String reportString : lines) {

            String[] split = reportString.split(" ");
            List<Integer> report = new ArrayList<>();
            for (int i = 0; i < split.length; i++) {
                String s = split[i];
                Integer level = Integer.valueOf(s);
                report.add(level);
            }

            boolean safeReport = reportIsSafe(report);

            if (safeReport) {
                totalSafeReports++;
            } else {
                for (int i = 0; i < report.size(); i++) {
                    List<Integer> reportCopy = new ArrayList<>(report);
                    reportCopy.remove(i);

                    if (reportIsSafe(reportCopy)) {
                        totalSafeReports++;
                        break;
                    }
                }
            }
        }

        System.out.println(totalSafeReports);
    }

    public static void alternativePart2(List<String> lines) {
        long count = lines.parallelStream()
                .map(s -> {
                    String[] split = s.split(" ");
                    List<Integer> report = new ArrayList<>();
                    for (int i = 0; i < split.length; i++) {
                        String levelString = split[i];
                        Integer level = Integer.valueOf(levelString);
                        report.add(level);
                    }
                    return report;
                })
                .map(report -> {
                    boolean safeReport = reportIsSafe(report);

                    if (safeReport) {
                        return safeReport;
                    } else {
                        for (int i = 0; i < report.size(); i++) {
                            List<Integer> reportCopy = new ArrayList<>(report);
                            reportCopy.remove(i);

                            if (reportIsSafe(reportCopy)) {
                                return true;
                            }
                        }
                        return false;
                    }
                })
                .filter(safeReport -> safeReport)
                .count();
        System.out.println(count);
    }

    private static boolean reportIsSafe(List<Integer> report) {
        boolean isAscending = Ordering.natural().isOrdered(report);
        boolean isDescending = Ordering.natural().reverse().isOrdered(report);
        boolean safeReport = true;

        if (isAscending || isDescending) {
            for (int i = 1; i < report.size(); i++) {
                int level = report.get(i);
                int prevLevel = report.get(i - 1);
                int levelDiff = Math.abs(prevLevel - level);
                if (levelDiff > 3 || levelDiff == 0) {
                    safeReport = false;
                    break;
                }
            }
        } else {
            safeReport = false;
        }

        return safeReport;
    }
}
