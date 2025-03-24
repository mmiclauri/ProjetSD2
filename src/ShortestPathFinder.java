import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class ShortestPathFinder implements PathFinder {
  @Override
  public Path findPath(Graph graph, int sourceId, int destId) {
    Map<Integer, Integer> predecessors = new HashMap<>();
    Queue<Integer> queue = new LinkedList<>();
    Set<Integer> visited = new HashSet<>();

    queue.offer(sourceId);
    visited.add(sourceId);

    boolean found = false;

    while (!queue.isEmpty() && !found) {
      int current = queue.poll();

      if (current == destId) {
        found = true;
        break;
      }

      for (Map.Entry<Integer, Double> neighbor : graph.getNeighbors(current).entrySet()) {
        int neighborId = neighbor.getKey();
        if (!visited.contains(neighborId)) {
          visited.add(neighborId);
          queue.offer(neighborId);
          predecessors.put(neighborId, current);
        }
      }
    }

    if (!found) {
      throw new RuntimeException("Aucun chemin trouvé");
    }

    return reconstructPath(graph, sourceId, destId, predecessors);
  }

  private Path reconstructPath(Graph graph, int sourceId, int destId, Map<Integer, Integer> predecessors) {
    List<Integer> nodePath = new ArrayList<>();
    int current = destId;

    while (current != sourceId) {
      nodePath.add(current);
      current = predecessors.get(current);
    }
    nodePath.add(sourceId);

    Collections.reverse(nodePath);

    Path path = new Path();

    for (int i = 0; i < nodePath.size(); i++) {
      int nodeId = nodePath.get(i);

      if (i > 0) {
        int prevNodeId = nodePath.get(i - 1);
        double weight = graph.getConnectionWeight(prevNodeId, nodeId);
        path.addNode(nodeId, weight);
      } else {
        path.addNode(nodeId, 0);
      }
    }

    return path;
  }
}