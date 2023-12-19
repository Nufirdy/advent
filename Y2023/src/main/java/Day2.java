import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day2 {

    static int redMax = 12, greenMax = 13, blueMax = 14;

    public static void main(String[] args) throws IOException, URISyntaxException {
        URL url = Thread.currentThread().getContextClassLoader().getResource("Day2.txt");
        Path path = Paths.get(url.toURI());
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

        int possibleGamesSum = 0;
        long powersSum = 0;
        for (String line : lines) {
            Game game = parseGame(line);
            if (game.isPossible(redMax, greenMax, blueMax)) {
                possibleGamesSum += game.id;
            }
            powersSum += game.powerOfMinimalSetOfCubes();
        }
        System.out.println(possibleGamesSum);
        System.out.println(powersSum);
    }

    private static Game parseGame(String line) {
        String[] game = line.split(":");

        String[] gameAndId = game[0].split(" ");
        int id = Integer.parseInt(gameAndId[1]);

        String[] cubePullsStrings = game[1].split(";");
        List<Set<Cubes>> cubePulls = new ArrayList<>();

        for (String cubePullsString : cubePullsStrings) {
            String[] cubesStrings = cubePullsString.split(",");
            Set<Cubes> cubePull = new HashSet<>();

            for (String cubesString : cubesStrings) {
                String trim = cubesString.trim();
                String[] amountAndColor = trim.split(" ");

                long amount = Long.parseLong(amountAndColor[0]);
                Color color = Color.valueOf(amountAndColor[1].toUpperCase());

                Cubes cubes = new Cubes(color, amount);
                cubePull.add(cubes);
            }
            cubePulls.add(cubePull);
        }

        return new Game(id, cubePulls);
    }

    static class Game {
        int id;
        List<Set<Cubes>> cubePulls;

        public Game(int id, List<Set<Cubes>> cubePulls) {
            this.id = id;
            this.cubePulls = cubePulls;
        }

        boolean isPossible(int redMax, int greenMax, int blueMax) {
            for (Set<Cubes> cubePull : cubePulls) {
                for (Cubes cubes : cubePull) {
                    if (cubes.color == Color.RED) {
                        if (redMax < cubes.amount) return false;
                    } else if (cubes.color == Color.GREEN) {
                        if (greenMax < cubes.amount) return false;
                    } else if (cubes.color == Color.BLUE) {
                        if (blueMax < cubes.amount) return false;
                    }
                }
            }
            return true;
        }

        long powerOfMinimalSetOfCubes() {
            long minimalRed = minimalPossibleCubes(Color.RED);
            long minimalGreen = minimalPossibleCubes(Color.GREEN);
            long minimalBlue = minimalPossibleCubes(Color.BLUE);

            return minimalRed * minimalGreen * minimalBlue;
        }

        long minimalPossibleCubes(Color color) {
            long maxColorCubePulled = 1;
            for (Set<Cubes> cubePull : cubePulls) {
                for (Cubes cubes : cubePull) {
                    if (cubes.color != color) {
                        continue;
                    }
                    if (maxColorCubePulled < cubes.amount) {
                        maxColorCubePulled = cubes.amount;
                    }
                }
            }
            return maxColorCubePulled;
        }

        @Override
        public String toString() {
            return "Game{" +
                    "id=" + id +
                    ", cubePulls=" + cubePulls +
                    '}';
        }
    }

    static class Cubes {
        Color color;
        long amount;

        public Cubes(Color color, long amount) {
            this.color = color;
            this.amount = amount;
        }

        @Override
        public String toString() {
            return "Cubes{" +
                    "color=" + color +
                    ", amount=" + amount +
                    '}';
        }
    }

    enum Color {
        RED, GREEN, BLUE
    }
}
