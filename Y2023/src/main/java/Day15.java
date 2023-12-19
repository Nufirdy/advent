import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Day15 {

    public static void main(String[] args) throws IOException, URISyntaxException {
        URL url = Thread.currentThread().getContextClassLoader().getResource("Day15.txt");
        Path path = Paths.get(url.toURI());
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

        String line = lines.get(0);
        String[] initSeq = line.split(",");

        part1(initSeq);
        part2(initSeq);
    }

    private static void part2(String[] initSeq) {
        HASHMAP hashmap = new HASHMAP();
        for (String s : initSeq) {
            StringBuilder label = new StringBuilder();
            char command = 0;
            StringBuilder number = new StringBuilder();
            for (char c : s.toCharArray()) {
                if (c == '-' || c == '=') {
                    command = c;
                } else if (Character.isDigit(c)) {
                    number.append(c);
                } else {
                    label.append(c);
                }
            }

            Lens lens;
            if (command == '-') {
                hashmap.remove(label.toString());
            } else if (command == '=') {
                int focalLength = Integer.parseInt(number.toString());
                lens = new Lens(label.toString(), focalLength);
                hashmap.put(lens);
            } else {
                throw new IllegalArgumentException();
            }
        }

        System.out.println(hashmap.getTotalFocusingPower());
    }

    private static void part1(String[] initSeq) {
        long hashSum = 0;
        for (String s : initSeq) {
            hashSum += hash(s);
        }
        System.out.println(hashSum);
    }

    static class HASHMAP {
        List<Lens>[] boxes;

        HASHMAP() {
            boxes = new List[256];
            for (int i = 0; i < boxes.length; i++) {
                boxes[i] = new ArrayList<>();
            }
        }

        public void put(Lens lens) {
            List<Lens> box = boxes[hash(lens.label)];
            int indexOf = box.indexOf(lens);
            if (indexOf != -1) {
                box.remove(indexOf);
                box.add(indexOf, lens);
            } else {
                box.add(lens);
            }
        }

        public void remove(String label) {
            List<Lens> box = boxes[hash(label)];
            int indexOf = box.indexOf(new Lens(label, 0));
            if (indexOf != -1) box.remove(indexOf);
        }

        int getTotalFocusingPower() {
            int sum = 0;
            for (int i = 0; i < boxes.length; i++) {
                List<Lens> box = boxes[i];
                for (int j = 0; j < box.size(); j++) {
                    Lens lens = box.get(j);
                    sum += (i + 1) * (j + 1) * lens.focalLength;
                }
            }
            return sum;
        }
    }

    static class Lens {
        String label;
        int focalLength;

        public Lens(String label, int focalLength) {
            this.label = label;
            this.focalLength = focalLength;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Lens lens)) return false;
            return label.equals(lens.label);
        }

        @Override
        public int hashCode() {
            return Objects.hash(label);
        }

        @Override
        public String toString() {
            return label + " " + focalLength;
        }
    }

    static int hash(String string) {
        int currentValue = 0;
        for (char c : string.toCharArray()) {
            currentValue += c;
            currentValue *= 17;
            currentValue %= 256;
        }
        return currentValue;
    }
}
