import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Day7 {

    static final List<Character> cards = List.of('A', 'K', 'Q', 'T', '9', '8', '7', '6', '5'
            , '4', '3', '2', 'J');

    public static void main(String[] args) throws IOException, URISyntaxException {
        URL url = Thread.currentThread().getContextClassLoader().getResource("Day7.txt");
        Path path = Paths.get(url.toURI());
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

        List<Hand> hands = new ArrayList<>();
        for (String line : lines) {
            String[] split = line.split(" ");
            hands.add(new Hand(split[0], Integer.parseInt(split[1])));
        }

        Collections.sort(hands);
        Collections.reverse(hands);

        long totalWinnings = 0;
        for (int i = 0; i < hands.size(); i++) {
            Hand hand = hands.get(i);
            totalWinnings += hand.bid * (i + 1);
        }
        System.out.println(totalWinnings);
    }

    static class Hand implements Comparable<Hand> {
        String hand;
        int bid;

        public Hand(String hand, int bid) {
            this.hand = hand;
            this.bid = bid;
        }

        HandType getType() {
            char[] chars = hand.toCharArray();
            Map<Character, Integer> charCounts = new HashMap<>();
            boolean hasJokers = false;
            for (char c : chars) {
                hasJokers = hasJokers || c == 'J';
                charCounts.merge(c, 1, Integer::sum);
            }

            if (hasJokers) {
                Integer jokerCount = charCounts.remove('J');
                char mostCommonCard = 0;
                int mostCommonCardAmount = 0;
                for (Map.Entry<Character, Integer> entry : charCounts.entrySet()) {
                    if (entry.getValue() > mostCommonCardAmount) {
                        mostCommonCard = entry.getKey();
                        mostCommonCardAmount = entry.getValue();
                    }
                }
                charCounts.merge(mostCommonCard, jokerCount, Integer::sum);
            }

            if (charCounts.size() == 5) {
                return HandType.HIGH_CARD;
            }
            if (charCounts.size() == 4) {
                return HandType.ONE_PAIR;
            }
            if (charCounts.size() == 3) {
                for (Integer value : charCounts.values()) {
                    if (value == 3) {
                        return HandType.THREE_OF_A_KIND;
                    }
                }
                return HandType.TWO_PAIR;
            }
            if (charCounts.size() == 2) {
                for (Integer value : charCounts.values()) {
                    if (value == 4) {
                        return HandType.FOUR_OF_A_KIND;
                    }
                }
                return HandType.FULL_HOUSE;
            }
            return HandType.FIVE_OF_A_KIND;
        }

        @Override
        public int compareTo(Hand o) {
            int compareHands = getType().compareTo(o.getType());
            if (compareHands != 0) {
                return compareHands;
            }
            char[] thisChars = hand.toCharArray();
            char[] thatChars = o.hand.toCharArray();
            for (int i = 0; i < 5; i++) {
                char thisChar = thisChars[i];
                char thatChar = thatChars[i];
                int compare = cards.indexOf(thisChar) - cards.indexOf(thatChar);
                if (compare != 0) {
                    return compare;
                }
            }
            return 0;
        }

        @Override
        public String toString() {
            return "Hand{" +
                    "hand='" + hand + '\'' +
                    ", bid=" + bid +
                    '}';
        }
    }

    enum HandType {
        FIVE_OF_A_KIND, FOUR_OF_A_KIND, FULL_HOUSE, THREE_OF_A_KIND, TWO_PAIR, ONE_PAIR, HIGH_CARD
    }
}
