import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Day9 {

    public static void main(String[] args) throws IOException, URISyntaxException {
        URL url = Thread.currentThread().getContextClassLoader().getResource("Day9.txt");
        Path path = Paths.get(url.toURI());
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

        List<List<Integer>> input = new ArrayList<>();
        for (String line : lines) {
            List<Integer> readings = new ArrayList<>();
            input.add(readings);
            for (String s : line.split(" ")) {
                readings.add(Integer.parseInt(s));
            }
        }

        int sumOfPredictedNext = 0;
        int sumOfPredictedPrevious = 0;
        for (List<Integer> integers : input) {
            sumOfPredictedNext += nextNumberIn(integers);
            sumOfPredictedPrevious += previousNumberIn(integers);
        }
        System.out.println(sumOfPredictedNext);
        System.out.println(sumOfPredictedPrevious);
    }

    static int nextNumberIn(List<Integer> sequence) {
        List<Integer> intervals = new ArrayList<>();
        for (int i = 0; i < sequence.size() - 1; i++) {
            Integer current = sequence.get(i);
            Integer next = sequence.get(i + 1);
            intervals.add(next - current);
        }

        boolean allZeroes = true;
        for (Integer interval : intervals) {
            if (interval != 0) {
                allZeroes = false;
                break;
            }
        }
        if (allZeroes) {
            return sequence.get(sequence.size() - 1);
        }
        int result = sequence.get(sequence.size() - 1) + nextNumberIn(intervals);
        return result;
    }

    static int previousNumberIn(List<Integer> sequence) {
        List<Integer> reversed = new LinkedList<>();
        for (Integer integer : sequence) {
            reversed.add(0, integer);
        }
        return nextNumberIn(reversed);
    }
}
