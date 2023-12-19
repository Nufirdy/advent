import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day1 {

    static final Map<String, Character> numbers = Map.of(
            "one", '1',
            "two", '2',
            "three", '3',
            "four", '4',
            "five", '5',
            "six", '6',
            "seven", '7',
            "eight", '8',
            "nine", '9');
    static final Map<String, Character> reversedNumbers;
    
    static {
        reversedNumbers = new HashMap<>();
        for (Map.Entry<String, Character> entry : numbers.entrySet()) {
            StringBuilder sb = new StringBuilder(entry.getKey());
            sb.reverse();
            reversedNumbers.put(sb.toString(), entry.getValue());
        }
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        URL url = Thread.currentThread().getContextClassLoader().getResource("Day1.txt");
        Path path = Paths.get(url.toURI());
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

        long calibrationSum = 0;
        for (String line : lines) {
            calibrationSum += calibrationValue(line);
        }
        System.out.println(calibrationSum);
    }

    static long calibrationValue(String inputLine) {
        char firstDigit = digitFromHead(inputLine);
        char lastDigit = digitFormTail(inputLine);
        StringBuilder sb = new StringBuilder();
        sb.append(firstDigit);
        sb.append(lastDigit);
        return Long.parseLong(sb.toString());
    }

    static char digitFromHead(String inputLine) {
        return firstDigitOrWordOccurrence(inputLine, numbers);
    }

    static char digitFormTail(String inputLine) {
        StringBuilder sb = new StringBuilder(inputLine);
        sb.reverse();
        String reversedInput = sb.toString();
        return firstDigitOrWordOccurrence(reversedInput, reversedNumbers);
    }

    /*
        метод не оставляет места для ошибки и полностью полагается на корректность ввода
     */
    static char firstDigitOrWordOccurrence(String inputLine, Map<String, Character> numbers) {
        char[] chars = inputLine.toCharArray();
        char digit = 0;
        int i = 0;
        //найти цифру, если есть
        for (; i < chars.length; i++) {
            char c = chars[i];
            if (Character.isDigit(c)) {
                digit = c;
                break;
            }
        }

        //проверить наличие слова перед цифрой
        String substringBeforeDigit = inputLine.substring(0, i);
        int lowestIndexOfWord = Integer.MAX_VALUE;
        for (Map.Entry<String, Character> entry : numbers.entrySet()) {
            //если перед цифрой несколько слов, взять первое
            int indexOfWord = substringBeforeDigit.indexOf(entry.getKey());
            if (indexOfWord != -1 && indexOfWord < lowestIndexOfWord) {
                lowestIndexOfWord = indexOfWord;
                digit = entry.getValue();
            }
        }
        return digit;
    }
}
