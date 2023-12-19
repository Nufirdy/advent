import com.google.common.base.Functions;
import com.google.common.collect.Ordering;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Day17Opt {

    public static void main(String[] args) throws IOException, URISyntaxException,
            InterruptedException {
        URL url = Thread.currentThread().getContextClassLoader().getResource("Day17.txt");
        Path path = Paths.get(url.toURI());
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

        part1(lines);
//        part2(lines);

    }

    private static void part2(List<String> lines) {
        Map<String, Map<String, Integer>> graph = buildPart2Graph(lines);
        Dijkstra dijkstra = new Dijkstra(lines, graph);
        dijkstra.processGraph();
        System.out.println(dijkstra.getLowestCost());
    }

    private static Map<String, Map<String, Integer>> buildPart2Graph(List<String> lines) {
        Map<String, Map<String, Integer>> graph = new HashMap<>();
        char[][] input = InputUtils.get2DArray(lines);
        Map<String, Node> allNodes = new HashMap<>();

        Node firstNode = new Node(0, 0, null, 0);
        allNodes.put(firstNode.getKey(), firstNode);

        Node endNode = new Node(input.length - 1, input[0].length - 1, null, 0);
        allNodes.put(endNode.getKey(), endNode);

        for (int row = 0; row < input.length; row++) {
            char[] line = input[row];
            for (int col = 0; col < line.length; col++) {
                if ((row < 4 && col < 4 && row > 0 && col > 0)
                        || (row > input.length - 5 && col > line.length - 5
                        && row < input.length - 1 && col < line.length - 1)) {
                    input[row][col] = 'X';
                } else {
                    for (Direction direction : Direction.values()) {
                        for (int i = 1; i < 11; i++) {
                            Node node = new Node(row, col, direction, i);
                            allNodes.put(node.getKey(), node);
                        }
                    }
                }
            }
        }


        //first node neighbors
        Map<String, Integer> startNodeCosts = new HashMap<>();
        Node rightStart = allNodes.get(Node.getKey(0, 4, Direction.RIGHT, 4));
        Node downStart = allNodes.get(Node.getKey(4, 0, Direction.RIGHT, 4));
        int rightStartCost = 0;
        for (int i = 0; i < 5; i++) {
            char c = input[0][i];
            rightStartCost += Integer.parseInt(String.valueOf(c));
        }

        graph.put(firstNode.getKey(), startNodeCosts);

        for (Node node : allNodes.values()) {
            for (Direction direction : Direction.values()) {
                if (direction == Direction.getOpposite(node.direction)) {
                    continue;
                }
                if (direction == node.direction && node.steps == 10) {
                    continue;
                }
//                if (node.direction != null && direction != node.direction && node.steps < 4) {
//                    continue;
//                }
                Node nextNode;






                Pair<Integer, Integer> next = nextIn(node.row, node.column, direction);
                if (next.getLeft() == input.length - 1 && next.getRight() == input[0].length - 1) {
                    if (node.steps < 4) {
                        nextNode = null;
                    } else {
                        nextNode = endNode;
                    }
                } else if (direction == node.direction && node.steps < 10) {
                    nextNode = allNodes.get(Node.getKey(next.getLeft(), next.getRight(),
                            direction, node.steps + 1));
                } else {
                    nextNode = allNodes.get(Node.getKey(next.getLeft(), next.getRight(),
                            direction, 1));
                }

                if (nextNode != null) {
                    node.neighbors.add(nextNode);
                }
            }

            Map<String, Integer> edgeCosts = new HashMap<>();
            for (Node neighbor : node.neighbors) {
                char c = input[neighbor.row][neighbor.column];
                if (c == 'X') {
                    continue;
                }
                edgeCosts.put(neighbor.getKey(), Integer.valueOf(String.valueOf(c)));
            }
            graph.put(node.getKey(), edgeCosts);
        }

        return graph;
    }

    private static void part1(List<String> lines) {
        Map<String, Map<String, Integer>> graph = buildPart1Graph(lines);
        Dijkstra dijkstra = new Dijkstra(lines, graph);
        dijkstra.processGraph();
        System.out.println(dijkstra.getLowestCost());
    }

    private static Map<String, Map<String, Integer>> buildPart1Graph(List<String> lines) {
        Map<String, Map<String, Integer>> graph = new HashMap<>();
        char[][] input = InputUtils.get2DArray(lines);
        Map<String, Node> allNodes = new HashMap<>();
        for (int row = 0; row < input.length; row++) {
            char[] line = input[row];
            for (int col = 0; col < line.length; col++) {
                if (row == 0 && col == 0) {
                    Node node = new Node(row, col, null, 0);
                    allNodes.put(node.getKey(), node);
                } else {
                    for (Direction direction : Direction.values()) {
                        for (int i = 1; i < 4; i++) {
                            Node node = new Node(row, col, direction, i);
                            allNodes.put(node.getKey(), node);
                        }
                    }
                }
            }
        }

        for (Node node : allNodes.values()) {
            for (Direction direction : Direction.values()) {
                if (direction == Direction.getOpposite(node.direction)) {
                    continue;
                }
                if (direction == node.direction && node.steps == 3) {
                    continue;
                }

                Node nextNode;
                Pair<Integer, Integer> next = nextIn(node.row, node.column, direction);
                if (direction == node.direction && node.steps < 3) {
                    nextNode = allNodes.get(Node.getKey(next.getLeft(), next.getRight(),
                            direction, node.steps + 1));
                } else {
                    nextNode = allNodes.get(Node.getKey(next.getLeft(), next.getRight(),
                            direction, 1));
                }

                if (nextNode != null) {
                    node.neighbors.add(nextNode);
                }
            }
            Map<String, Integer> edgeCosts = new HashMap<>();
            for (Node neighbor : node.neighbors) {
                char c = input[neighbor.row][neighbor.column];
                edgeCosts.put(neighbor.getKey(), Integer.valueOf(String.valueOf(c)));
            }
            graph.put(node.getKey(), edgeCosts);
        }

        return graph;
    }

    static Pair<Integer, Integer> nextIn(int row, int column, Direction direction) {
        switch (direction) {
            case UP -> {
                return Pair.of(row - 1, column);
            }
            case DOWN -> {
                return Pair.of(row + 1, column);
            }
            case LEFT -> {
                return Pair.of(row, column - 1);
            }
            case RIGHT -> {
                return Pair.of(row, column + 1);
            }
        }
        throw new IllegalArgumentException();
    }


    static class Dijkstra {
        public final String END_NODE;
        public final String START_NODE;

        char[][] input;

        Map<String, Map<String, Integer>> graph;
        ValueComparableMap<String, Integer> costs = new ValueComparableMap<>(Ordering.natural());
        Map<String, String> parents = new HashMap<>();
        Set<String> processed = new HashSet<>();

        Dijkstra(List<String> lines, Map<String, Map<String, Integer>> graph) {
            input = InputUtils.get2DArray(lines);
            this.graph = graph;

            START_NODE = Node.getKey(0, 0, null, 0);
            END_NODE = "%d %d".formatted(input.length - 1, input[0].length - 1);

            Map<String, Integer> startNeighbors = graph.get(START_NODE);
            for (Map.Entry<String, Integer> node : startNeighbors.entrySet()) {
                costs.put(node.getKey(), node.getValue());
                parents.put(node.getKey(), START_NODE);
            }
        }

        public void processGraph() {
            String node = findLowestCostNode();
            while (node != null) {
                int cost = costs.get(node);
                Map<String, Integer> neighbors = graph.get(node);
                for (String n : neighbors.keySet()) {
                    int newCost = cost + neighbors.get(n);
                    int oldCost = costs.containsKey(n) ? costs.get(n) : Integer.MAX_VALUE;
                    if (oldCost > newCost) {
                        costs.put(n, newCost);
                        parents.put(n, node);
                    }
                }
                processed.add(node);
                node = findLowestCostNode();
                printProgress();
            }
        }

        private String findLowestCostNode() {
            for (Map.Entry<String, Integer> node : costs.entrySet()) {
                if (!processed.contains(node.getKey())) {
                    return node.getKey();
                }
            }
            return null;
        }

        public int getLowestCost() {
            return costs.entrySet().stream()
                    .filter(nodeCost -> nodeCost.getKey().contains(END_NODE))
                    .mapToInt(Map.Entry::getValue)
                    .min()
                    .getAsInt();
        }

        public void printPath() {
            char[][] copy = new char[input.length][];
            for (int i = 0; i < copy.length; i++) {
                copy[i] = Arrays.copyOf(input[i], input[i].length);
            }

            String node = END_NODE;
            while (!node.equals(START_NODE)) {
                String[] coord = node.split(" ");
                int row = Integer.parseInt(coord[0]);
                int col = Integer.parseInt(coord[1]);
                copy[row][col] = '#';
                node = parents.get(node);
            }
            InputUtils.printCharArray(copy);
        }

        private void printProgress() {
            float progress = Float.valueOf(processed.size()) / Float.valueOf(graph.size()) * 100;
            System.out.print(processed.size() + "/" + graph.size() + " " + progress + "%");
            System.out.print("\r");
            System.out.flush();
        }
    }

    static class Node {
        int row;
        int column;
        Direction direction;
        int steps;

        Set<Node> neighbors = new HashSet<>();

        public Node(int row, int column, Direction direction, int steps) {
            this.row = row;
            this.column = column;
            this.direction = direction;
            this.steps = steps;
        }

        public String getKey() {
            return getKey(row, column, direction, steps);
        }

        private static String getKey(int row, int column, Direction direction, int steps) {
            return "%d %d %s %d".formatted(row, column, direction, steps);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Node node)) return false;
            return row == node.row
                    && column == node.column
                    && steps == node.steps
                    && direction == node.direction;
        }

        @Override
        public int hashCode() {
            return Objects.hash(row, column, direction, steps);
        }
    }

    enum Direction {
        UP(2),
        DOWN(3),
        LEFT(7),
        RIGHT(11);

        int number;

        Direction(int number) {
            this.number = number;
        }

        static Direction getOpposite(Direction direction) {
            if (direction == null) {
                return null;
            }
            switch (direction) {
                case UP -> {
                    return DOWN;
                }
                case DOWN -> {
                    return UP;
                }
                case LEFT -> {
                    return RIGHT;
                }
                case RIGHT -> {
                    return LEFT;
                }
            }
            throw new IllegalArgumentException();
        }
    }

    static class ValueComparableMap<K extends Comparable<K>,V> extends TreeMap<K,V> {
        //A map for doing lookups on the keys for comparison so we don't get infinite loops
        private final Map<K, V> valueMap;

        ValueComparableMap(final Ordering<? super V> partialValueOrdering) {
            this(partialValueOrdering, new HashMap<K, V>());
        }

        private ValueComparableMap(Ordering<? super V> partialValueOrdering,
                                   HashMap<K, V> valueMap) {
            super(partialValueOrdering //Apply the value ordering
                    .onResultOf(Functions.forMap(valueMap)) //On the result of getting the value for the key from the map
                    .compound(Ordering.natural())); //as well as ensuring that the keys don't get clobbered
            this.valueMap = valueMap;
        }

        public V put(K k, V v) {
            if (valueMap.containsKey(k)) {
                //remove the key in the sorted set before adding the key again
                remove(k);
            }
            valueMap.put(k, v); //To get "real" unsorted values for the comparator
            return super.put(k, v); //Put it in value order
        }

        @Override
        public boolean containsKey(Object key) {
            return valueMap.containsKey(key);
        }
    }
}
