import java.util.ArrayList;
import java.util.List;

public class Path {
  private List<Integer> nodeIds;
  private double totalWeight;

  public Path() {
    nodeIds = new ArrayList<>();
    totalWeight = 0;
  }

  public void addNode(int nodeId, double weight) {
    nodeIds.add(nodeId);
    totalWeight += weight;
  }

  public List<Integer> getNodeIds() {
    return nodeIds;
  }

  public double getTotalWeight() {
    return totalWeight;
  }

  public int getLength() {
    return nodeIds.size() - 1;
  }
}