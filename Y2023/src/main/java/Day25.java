import java.util.*;

public class Day25 {

    public static void main(String[] args) {
        List<String> lines = InputUtils.getFromResource("Day25-test1.txt");

        Map<String, List<String>> graph = new HashMap<>();
        for (String line : lines) {
            String[] fromAndTo = line.split(":");

            String[] toNodesArray = fromAndTo[1].trim().split(" ");
            List<String> toNodes = new ArrayList<>(Arrays.stream(toNodesArray).toList());
            graph.put(fromAndTo[0], toNodes);

            for (String toNode : toNodes) {
                graph.computeIfAbsent(toNode, s -> new ArrayList<>()).add(fromAndTo[0]);
            }
        }

        String start = lines.get(0).split(":")[0];

        if (isBipartite(graph, start))
            System.out.println("Yes");
        else
            System.out.println("No");
    }

    static boolean isBipartite(Map<String, List<String>> graph, String start) {
        Map<String, Integer> colorMap = new HashMap<>();
        for (String s : graph.keySet()) {
            colorMap.put(s, -1);
        }

        colorMap.put(start, 1);

        Queue<String> queue = new LinkedList<>();
        queue.add(start);

        while (!queue.isEmpty()) {
            String node = queue.poll();
            Integer nodeColor = colorMap.get(node);

            List<String> nodes = graph.get(node);
            if (nodes.contains(node)) {
                return false;
            }

            for (String to : nodes) {
                Integer color = colorMap.get(to);
                if (color == -1) {
                    colorMap.put(to, 1 - nodeColor);
                    queue.add(to);
                } else if (nodeColor == color) {
                    return false;
                }
            }
        }

        return true;
    }
}
