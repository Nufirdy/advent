import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Day4 {

    public static void main(String[] args) throws IOException, URISyntaxException {
        URL url = Thread.currentThread().getContextClassLoader().getResource("Day4.txt");
        Path path = Paths.get(url.toURI());
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

        long pointsSum = 0;
        Map<Integer, Long> cardAmounts = new HashMap<>();
        for (String line : lines) {
            Card card = new Card(line);
            pointsSum += card.getPoints();

            cardAmounts.merge(card.id, 1L, Long::sum);
            for (int i = card.id + 1; i <= card.id + card.winningCardNumbersAmount(); i++) {
                cardAmounts.merge(i, cardAmounts.get(card.id), Long::sum);
            }
        }

        System.out.println(pointsSum);
        long totalScratchcards = cardAmounts.values().stream().mapToLong(Long::longValue).sum();
        System.out.println(totalScratchcards);
    }

    static class Card {
        int id;
        Set<Integer> winningNumbers = new HashSet<>();
        Set<Integer> cardNumbers = new HashSet<>();

        public Card(String line) {
            String[] cardAndNumbers = line.split(":");
            String[] idArray = cardAndNumbers[0].split(" ");
            this.id = Integer.parseInt(idArray[idArray.length - 1]);

            String[] numbers = cardAndNumbers[1].split("\\|");
            String[] winningNumbersArray = numbers[0].trim().split(" ");
            for (String winningNumberStr : winningNumbersArray) {
                if (winningNumberStr.isBlank()) continue;
                winningNumbers.add(Integer.parseInt(winningNumberStr));
            }

            String[] cardNumbersArray = numbers[1].trim().split(" ");
            for (String cardNumberStr : cardNumbersArray) {
                if (cardNumberStr.isBlank()) continue;
                cardNumbers.add(Integer.parseInt(cardNumberStr));
            }
        }

        int getPoints() {
            int points = 0;
            for (Integer cardNumber : cardNumbers) {
                if (winningNumbers.contains(cardNumber)) {
                    if (points == 0) {
                        points = 1;
                    } else {
                        points *= 2;
                    }
                }
            }
            return points;
        }

        long winningCardNumbersAmount() {
            return cardNumbers.stream()
                    .filter(integer -> winningNumbers.contains(integer))
                    .count();
        }
    }
}
