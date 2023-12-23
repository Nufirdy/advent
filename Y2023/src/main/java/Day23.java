import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class Day23 {

    public static void main(String[] args) {
        List<String> lines = InputUtils.getFromResource("Day23.txt");

        part1(lines);
        part2(lines);
    }

    private static void part2(List<String> lines) {
        HikingMap hikingMap = new HikingMap(lines);
        hikingMap.buildPart2GraphFromStart();
        hikingMap.findLongestPath(hikingMap.startNode, new HashSet<>(), new HashSet<>());
        System.out.println(hikingMap.longestPath);
    }

    private static void part1(List<String> lines) {
        HikingMap hikingMap = new HikingMap(lines);
        hikingMap.buildPart1GraphFromStart();
        System.out.println(hikingMap.getPart1HighestPathCostFrom(hikingMap.startNode));
    }

    static class HikingMap {
        static final char FOREST = '#';
        static final Set<Character> SLOPES = Set.of('>', 'v', '^', '<');

        char[][] maze;
        Node startNode;
        Node endNode;
        Map<String, Node> nodes = new HashMap<>();

        public HikingMap(List<String> lines) {
            maze = InputUtils.get2DArray(lines);

            char[] chars = maze[0];
            Pair<Integer, Integer> start = null;
            for (int i = 0; i < chars.length; i++) {
                char c = chars[i];
                if (c != FOREST) {
                    start = Pair.of(0, i);
                }
            }

            chars = maze[maze.length - 1];
            Pair<Integer, Integer> end = null;
            for (int i = 0; i < chars.length; i++) {
                char c = chars[i];
                if (c != FOREST) {
                    end = Pair.of(maze.length - 1, i);
                }
            }
            startNode = new Node(start.getLeft(), start.getRight());
            endNode = new Node(end.getLeft(), end.getRight());

            nodes.put(startNode.getKey(), startNode);
            nodes.put(endNode.getKey(), endNode);

            Edge startEdge = new Edge(startNode);
            startNode.edges.add(startEdge);

        }

        public void buildPart2GraphFromStart() {
            startNode.edges = new HashSet<>();
            for (int i = 0; i < maze.length; i++) {
                char[] chars = maze[i];
                for (int j = 0; j < chars.length; j++) {
                    char c = chars[j];
                    List<Direction> directions = new ArrayList<>();
                    for (Direction dir : Direction.values()) {
                        int row = i + dir.row;
                        int col = j + dir.col;

                        char is_it_node_is_it_not_only_one_way_to_find_out;
                        try {
                            is_it_node_is_it_not_only_one_way_to_find_out = maze[row][col];
                        } catch (Exception e) {
                            continue;
                        }

                        if (c == '.'
                                && SLOPES.contains(is_it_node_is_it_not_only_one_way_to_find_out)) {
                            directions.add(dir);
                        }
                    }

                    if (directions.size() > 1) {
                        Node node = new Node(i, j);
                        nodes.put(node.getKey(), node);
                    }
                }
            }

            for (Node node : nodes.values()) {
                for (Direction dir : Direction.values()) {
                    int nextRow = node.row + dir.row;
                    int nextCol = node.col + dir.col;

                    char c;
                    try {
                        c = maze[nextRow][nextCol];
                    } catch (Exception e) {
                        continue;
                    }
                    if (c != FOREST) {
                        buildEdge(node, dir);
                    }
                }
            }
        }

        private void buildEdge(Node from, Direction direction) {
            Edge edge = new Edge(from);
            int length = 1;

            int prevRow = from.row;
            int prevCol = from.col;
            int row = from.row + direction.row;
            int col = from.col + direction.col;
            while (true) {
                int nextRow;
                int nextCol;

                Direction where_should_I_fucking_go_next = null;
                for (Direction dir : Direction.values()) {
                    nextRow = row + dir.row;
                    nextCol = col + dir.col;

                    if (nextRow == prevRow && nextCol == prevCol) {
                        continue;
                    }

                    char c;
                    try {
                        c = maze[nextRow][nextCol];
                    } catch (Exception e) {
                        continue;
                    }
                    if (c != FOREST) {
                        where_should_I_fucking_go_next = dir;
                    }
                }

                if (nodes.containsKey(Node.getKey(row, col))) {
                    edge.cost = length;
                    from.edges.add(edge);
                    edge.to = nodes.get(Node.getKey(row, col));
                    break;
                }


                prevRow = row;
                prevCol = col;
                row = row + where_should_I_fucking_go_next.row;
                col = col + where_should_I_fucking_go_next.col;
                length++;
            }
        }

        public void buildPart1GraphFromStart() {
            Edge startEdge = startNode.edges.stream().findFirst().get();
            buildPart1Graph(startNode, startEdge, Direction.DOWN);
        }

        private void buildPart1Graph(Node from, Edge edge, Direction direction) {
            int length = 1;

            int prevRow = from.row;
            int prevCol = from.col;
            int row = from.row + direction.row;
            int col = from.col + direction.col;
            while (true) {
                int nextRow;
                int nextCol;

                if (row == endNode.row && col == endNode.col) {
                    edge.to = endNode;
                    edge.cost = length;
                    break;
                }

                List<Direction> directions = new ArrayList<>();
                for (Direction dir : Direction.values()) {
                    nextRow = row + dir.row;
                    nextCol = col + dir.col;

                    if (nextRow == prevRow && nextCol == prevCol) {
                        continue;
                    }

                    char c = maze[nextRow][nextCol];
                    if (c != FOREST) {
                        if (SLOPES.contains(c)) {
                            if (c == dir.symbol) {
                                directions.add(dir);
                            }
                        } else {
                            directions.add(dir);
                        }
                    }
                }

                if (directions.size() > 1) {
                    edge.cost = length;
                    Node to;
                    if (nodes.containsKey(Node.getKey(row, col))) {
                        to = nodes.get(Node.getKey(row, col));
                        edge.to = to;
                        break;
                    } else {
                        to = new Node(row, col);
                        edge.to = to;
                    }

                    nodes.put(to.getKey(), to);

                    for (Direction dir : directions) {
                        Edge newEdge = new Edge(to);
                        newEdge.from = to;
                        to.edges.add(newEdge);
                        buildPart1Graph(to, newEdge, dir);
                    }
                    break;
                }

                Direction dir = directions.get(0);
                prevRow = row;
                prevCol = col;
                row = row + dir.row;
                col = col + dir.col;
                length++;
            }
        }

        Map<Node, Integer> costsCache = new HashMap<>();
        public int getPart1HighestPathCostFrom(Node node) {
            if (costsCache.containsKey(node)) {
                return costsCache.get(node);
            }

            int highestCost = -1;
            for (Edge edge : node.edges) {
                if (edge.to.equals(endNode)) {
                    highestCost = Math.max(edge.cost, highestCost);
                } else {
                    highestCost = Math.max(getPart1HighestPathCostFrom(edge.to) + edge.cost,
                            highestCost);
                }
            }

            costsCache.put(node, highestCost);
            return highestCost;
        }

        int longestPath = 0;
        public void findLongestPath(Node node,
                                     Set<Node> isVisited,
                                     Set<Edge> path)
        {
            if (isVisited.contains(node)) {
                return;
            }

            if (node.equals(endNode)) {
                int pathLength = path.stream()
                        .mapToInt(edge -> edge.cost)
                        .sum();
                if (longestPath < pathLength) {
                    longestPath = pathLength;
                }
                return;
            }

            isVisited.add(node);
            for (Edge edge : node.edges) {
                if (!isVisited.contains(edge)) {
                    path.add(edge);
                    findLongestPath(edge.to, isVisited, path);

                    path.remove(edge);
                }
            }

            isVisited.remove(node);
        }

        public void printDebug() {
            char[][] copy = InputUtils.deepCopy(maze);
            for (Node node : nodes.values()) {
                int size = node.edges.size();
                char c = String.valueOf(size).charAt(0);
                copy[node.row][node.col] = c;
            }
            InputUtils.printCharArray(copy);
        }
    }


    static class Node {
        int row;
        int col;
        Set<Edge> edges = new HashSet<>();

        public Node(int row, int col) {
            this.row = row;
            this.col = col;
        }

        public String getKey() {
            return getKey(row, col);
        }

        private static String getKey(int row, int column) {
            return "%d %d".formatted(row, column);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Node node)) return false;
            return row == node.row && col == node.col;
        }

        @Override
        public int hashCode() {
            return Objects.hash(row, col);
        }

        @Override
        public String toString() {
            return "Node{" +
                    "row=" + row +
                    ", col=" + col +
                    '}';
        }
    }

    static class Edge {
        int cost;
        Node from;
        Node to;

        public Edge(Node from) {
            this.from = from;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Edge edge)) return false;
            return Objects.equals(from, edge.from) && Objects.equals(to, edge.to);
        }

        @Override
        public int hashCode() {
            return Objects.hash(from, to);
        }

        @Override
        public String toString() {
            return "Edge{" +
                    "cost=" + cost +
                    ", from=" + from +
                    ", to=" + to +
                    '}';
        }
    }
}
