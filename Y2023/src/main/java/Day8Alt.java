import com.google.common.collect.Iterables;
import com.google.common.primitives.Chars;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.ArithmeticUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Day8Alt {

    public static void main(String[] args) throws IOException, URISyntaxException {
        URL url = Thread.currentThread().getContextClassLoader().getResource("Day8.txt");
        Path path = Paths.get(url.toURI());
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

        Map<String, Node> nodes = new HashMap<>();
        for (int i = 2; i < lines.size(); i++) {
            String[] nodeStr = lines.get(i).split(" = ");

            String[] leftAndRight =
                    StringUtils.deleteWhitespace(StringUtils.strip(nodeStr[1], "()")).split(",");
            String leftStr = leftAndRight[0];
            String rightStr = leftAndRight[1];

            Node left = nodes.computeIfAbsent(leftStr, s -> new Node(leftStr));
            Node right = nodes.computeIfAbsent(rightStr, s -> new Node(rightStr));

            Node node = nodes.getOrDefault(nodeStr[0], new Node(nodeStr[0]));
            node.left = left;
            node.right = right;

            nodes.putIfAbsent(left.name, left);
            nodes.putIfAbsent(right.name, right);
            nodes.putIfAbsent(node.name, node);
        }




//        part1(lines, nodes);
//        part2(lines, nodes);
    }

    static void part2(List<String> lines, Map<String, Node> nodes) {
        List<Node> startingPoints = new ArrayList<>();
        for (String s : nodes.keySet()) {
            if (s.endsWith("A")) {
                startingPoints.add(nodes.get(s));
            }
        }

        for (Node startingPoint : startingPoints) {
            System.out.println(startingPoint);
            Iterator<Character> cycle = Iterables.cycle(Chars.asList(lines.get(0).toCharArray())).iterator();
            Node currentNode = startingPoint;
            long stepsTaken = 0;
            long thisCycle = 0;
            long lastCycle = 0;
            while (cycle.hasNext()) {
                if (currentNode.name.endsWith("Z")) {
                    System.out.println(thisCycle);
                    if (lastCycle == thisCycle) {
                        break;
                    }
                    lastCycle = thisCycle;
                    thisCycle = 0;
                }
                char c = cycle.next();
                currentNode = c == 'L' ? currentNode.left : currentNode.right;
                stepsTaken++;
                thisCycle++;
            }
            System.out.println(stepsTaken);
        }

    }

    private static void part1(List<String> lines, Map<String, Node> nodes) {
        Iterator<Character> cycle = Iterables.cycle(Chars.asList(lines.get(0).toCharArray())).iterator();
        Node currentNode = nodes.get("AAA");
        long stepsTaken = 0;
        while (cycle.hasNext()) {
            if (currentNode.name.equals("ZZZ")) {
                break;
            }
            char c = cycle.next();
            currentNode = c == 'L' ? currentNode.left : currentNode.right;
            stepsTaken++;
        }
        System.out.println(stepsTaken);
    }

    static class Node {
        String name;
        Node left;
        Node right;

        public Node(String name) {
            this.name = name;
        }

        public Node(String name, Node left, Node right) {
            this.name = name;
            this.left = left;
            this.right = right;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "name='" + name + '\'' +
                    ", left=" + left.name +
                    ", right=" + right.name +
                    '}';
        }
    }
}
