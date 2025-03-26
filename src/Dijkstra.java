import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class Dijkstra implements PathFinder {
  @Override
  public Path findPath(Graph graph, int sourceId, int destId) {
    Map<Integer, Integer> predecessors = new HashMap<>();
    Map<Integer, Double> distances = new HashMap<>();
    Set<Integer> settled = new HashSet<>();

    PriorityQueue<Integer> pq = new PriorityQueue<>(
        Comparator.comparingDouble(a -> distances.getOrDefault(a, Double.MAX_VALUE))
    );

    for (int id : graph.getAllArtistIds()) {
      distances.put(id, Double.MAX_VALUE);
    }

    distances.put(sourceId, 0.0);
    pq.add(sourceId);

    while (!pq.isEmpty()) {
      int current = pq.poll();

      if (current == destId) {
        break;
      }

      if (settled.contains(current)) {
        continue;
      }

      settled.add(current);

      for (Map.Entry<Integer, Double> neighbor : graph.getNeighbors(current).entrySet()) {
        int neighborId = neighbor.getKey();
        double weight = neighbor.getValue();

        if (!settled.contains(neighborId)) {
          double newDistance = distances.get(current) + weight;

          if (newDistance < distances.get(neighborId)) {
            distances.put(neighborId, newDistance);
            predecessors.put(neighborId, current);

            pq.remove(neighborId);
            pq.add(neighborId);
          }
        }
      }
    }

    if (!predecessors.containsKey(destId) && sourceId != destId) {
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