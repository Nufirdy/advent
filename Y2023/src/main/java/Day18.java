import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class Day18 {

    public static void main(String[] args) throws IOException, URISyntaxException {
        URL url = Thread.currentThread().getContextClassLoader().getResource("Day18.txt");
        Path path = Paths.get(url.toURI());
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

        part1(lines);
        part2(lines);
    }

    private static void part2(List<String> lines) {
        List<DigInstruction> instructions = new ArrayList<>();
        for (String line : lines) {
            String[] split = line.split(" ");
            String encodedInstr = StringUtils.strip(split[2], "(#)");

            String dirStr = encodedInstr.substring(encodedInstr.length() - 1);
            Day17.Direction dir = null;
            switch (dirStr) {
                case "3" -> dir = Day17.Direction.UP;
                case "2" -> dir = Day17.Direction.LEFT;
                case "0" -> dir = Day17.Direction.RIGHT;
                case "1" -> dir = Day17.Direction.DOWN;

                default -> throw new IllegalStateException("Unexpected value: " + split[0]);
            }

            int distance = Integer.parseInt(encodedInstr.substring(0, encodedInstr.length() - 1), 16);

            instructions.add(new DigInstruction(dir, distance, 0));
        }

        shoelace(instructions);
    }

    private static void part1(List<String> lines) {
        List<DigInstruction> instructions = new ArrayList<>();
        for (String line : lines) {
            String[] split = line.split(" ");
            Day17.Direction dir = null;
            switch (split[0]) {
                case "U" -> dir = Day17.Direction.UP;
                case "L" -> dir = Day17.Direction.LEFT;
                case "R" -> dir = Day17.Direction.RIGHT;
                case "D" -> dir = Day17.Direction.DOWN;

                default -> throw new IllegalStateException("Unexpected value: " + split[0]);
            }

            int distance = Integer.parseInt(split[1]);
            int color = Integer.parseInt(StringUtils.strip(split[2], "(#)"), 16);

            instructions.add(new DigInstruction(dir, distance, color));
        }

        shoelace(instructions);
    }

    private static void shoelace(List<DigInstruction> instructions) {
        List<Pair<Long, Long>> points = new ArrayList<>();
        Pair<Long, Long> lastPoint = Pair.of(1L, 1L);
        int perimeterArea = 0;
        for (DigInstruction instruction : instructions) {
            long nextRow = lastPoint.getLeft();
            long nextCol = lastPoint.getRight();

            switch (instruction.direction) {
                case UP -> nextRow -= instruction.distance;
                case DOWN -> nextRow += instruction.distance;
                case LEFT -> nextCol -= instruction.distance;
                case RIGHT -> nextCol += instruction.distance;
            }

            lastPoint = Pair.of(nextRow, nextCol);
            points.add(lastPoint);
            perimeterArea += instruction.distance;
        }

        long minRow = Math.abs(points.stream()
                .mapToLong(Pair::getLeft)
                .min()
                .getAsLong());
        long minCol = Math.abs(points.stream()
                .mapToLong(Pair::getRight)
                .min()
                .getAsLong());

        points = points.stream()
                .map(point -> Pair.of(point.getLeft() + minRow + 1, point.getRight() + minCol + 1))
                .collect(Collectors.toCollection(ArrayList::new));
        Collections.reverse(points);

        long leftSum = 0, rightSum = 0;
        for (int i = 0; i < points.size(); i++) {
            int next = i + 1 == points.size() ? 0 : i + 1;
            Pair<Long, Long> thisPoint = points.get(i);
            Pair<Long, Long> nextPoint = points.get(next);

            leftSum += thisPoint.getLeft() * nextPoint.getRight();
            rightSum += thisPoint.getRight() * nextPoint.getLeft();

        }
        long area = (Math.abs(leftSum - rightSum) / 2) + (perimeterArea / 2) + 1;
        System.out.println(area);
    }

    static class DigInstruction {
        Day17.Direction direction;
        int distance;
        int color;

        public DigInstruction(Day17.Direction direction, int distance, int color) {
            this.direction = direction;
            this.distance = distance;
            this.color = color;
        }
    }
}
