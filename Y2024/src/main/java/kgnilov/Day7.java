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
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Day7 {

    public static void main(String[] args) throws IOException, URISyntaxException {
        URL url = Thread.currentThread().getContextClassLoader().getResource(resourceName());
        Path path = Paths.get(url.toURI());
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

        Stopwatch part1 = Stopwatch.createStarted();
        part1(lines);
        part1.stop();
        System.out.println(part1.elapsed(TimeUnit.MILLISECONDS) + "ms\n");

        Stopwatch part2 = Stopwatch.createStarted();
        part2(lines);
        part2.stop();
        System.out.println(part2.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    static String resourceName() {
        return "Day7";
    }

    static void part1(List<String> lines) {
        long sum = 0;
        for (String line : lines) {
            String[] resultAndValues = line.split(":");
            long result = Long.parseLong(resultAndValues[0]);
            String valuesStr = StringUtils.strip(resultAndValues[1]);
            String[] valuesSplit = valuesStr.split(" ");

            List<Long> values = Arrays.stream(valuesSplit)
                    .map(Long::parseLong)
                    .toList();

            Bits bits = new Bits(values.size() - 1);
            for (int i = 0; i < bits.maxValue(); i++) {
                long possibleResult = values.get(0);
                for (int j = 0; j < bits.length(); j++) {
                    boolean bit = bits.get(j);
                    if (bit) {
                        possibleResult += values.get(j + 1);
                    } else {
                        possibleResult *= values.get(j + 1);
                    }
                }

                if (possibleResult == result) {
                    sum += result;
                    break;
                }
                bits.increment();
            }
        }
        System.out.println(sum);
    }

    static void part2(List<String> lines) {
        long sum = 0;
        for (String line : lines) {
            String[] resultAndValues = line.split(":");
            long result = Long.parseLong(resultAndValues[0]);
            String valuesStr = StringUtils.strip(resultAndValues[1]);
            String[] values = valuesStr.split(" ");

            Base3Bits bits = new Base3Bits(values.length - 1);
            for (int i = 0; i < bits.maxValue(); i++) {
                long possibleResult = Long.parseLong(values[0]);
                for (int j = 0; j < bits.length(); j++) {
                    byte bit = bits.get(j);
                    if (bit == 0) {
                        possibleResult += Long.parseLong(values[j + 1]);
                    } else if (bit == 1) {
                        possibleResult *= Long.parseLong(values[j + 1]);
                    } else if (bit == 2) {
                        possibleResult = Long.parseLong(possibleResult + values[j + 1]);
                    }
                }

                if (possibleResult == result) {
                    sum += result;
                    break;
                }
                bits.increment();
            }
        }
        System.out.println(sum);
    }

    static class Bits {

        int bits = 0;
        private int length;

        public Bits(int length) {
            this.length = length;
        }

        int length() {
            return length;
        }

        int maxValue() {
            return (int) Math.pow(2.0, length);
        }

        void increment() {
            bits++;
        }

        boolean get(int index) {
            return ((bits >> index) & 1) == 1;
        }
    }

    static class Base3Bits {

        private String bits;
        private int length;

        public Base3Bits(int length) {
            this.length = length;

            bits = StringUtils.repeat("0", length);
        }

        int maxValue() {
            return (int) Math.pow(3.0, length);
        }

        int length() {
            return length;
        }

        byte get(int index) {
            return Byte.parseByte(String.valueOf(bits.toCharArray()[index]));
        }

        void increment() {
            String bits = Integer.toString(Integer.parseInt(this.bits, 3) + 1, 3);
            while (bits.length() < length) {
                bits = "0" + bits;
            }
            this.bits = bits;
        }
    }
}
