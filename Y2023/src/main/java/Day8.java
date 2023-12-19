import com.google.common.collect.Iterables;
import com.google.common.primitives.Chars;
import org.apache.commons.lang3.StringUtils;

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

public class Day8 {

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
        part2(lines, nodes);
    }

    static void part2(List<String> lines, Map<String, Node> nodes) {
        List<Node> startingPoints = new ArrayList<>();
        for (String s : nodes.keySet()) {
            if (s.endsWith("A")) {
                startingPoints.add(nodes.get(s));
            }
        }

        AtomicBoolean allOnZ = new AtomicBoolean(false);
        boolean[] threadOnZ = new boolean[startingPoints.size()];
        AtomicLong steps = new AtomicLong();
        List<Thread> threads = new ArrayList<>();
        CyclicBarrier barrier = new CyclicBarrier(startingPoints.size(), () -> {
            steps.getAndIncrement();
            allOnZ.set(true);
            for (boolean b : threadOnZ) {
                if (!b) {
                    allOnZ.set(false);
                    break;
                }
            }
            if (allOnZ.get()) {
                for (Thread thread : threads) {
                    thread.interrupt();
                }
                System.out.println(steps.get());
            }
        });

        for (int i = 0; i < startingPoints.size(); i++) {
            int finalI = i;
            Thread thread = new Thread(() -> {
                AtomicInteger id = new AtomicInteger(finalI);
                Iterator<Character> cycle = Iterables.cycle(Chars.asList(lines.get(0).toCharArray())).iterator();
                Node currentNode = startingPoints.get(id.get());
                while (cycle.hasNext()) {
                    threadOnZ[id.get()] = currentNode.name.endsWith("Z");
                    char c = cycle.next();
                    currentNode = c == 'L' ? currentNode.left : currentNode.right;
                    try {
                        barrier.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        break;
                    }
                }
            });
            threads.add(thread);
            thread.start();
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
