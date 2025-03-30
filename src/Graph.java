import java.io.*;
import java.util.*;

public class Graph {
    private Map<Integer, String> idToArtist = new HashMap<>();
    private Map<Integer, String> idToCategory = new HashMap<>();
    private Map<String, Integer> artistToId = new HashMap<>();
    private Map<Integer, List<Edge>> adjList = new HashMap<>();

    public Graph(String artistsPath, String mentionsPath) {
        loadArtists(artistsPath);
        loadMentions(mentionsPath);
    }

    private void loadArtists(String artistsPath) {
        try(BufferedReader br = new BufferedReader(new FileReader(artistsPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",", 3);
                if(data.length == 3){
                    int id = Integer.parseInt(data[0]);
                    String artist = data[1];
                    String category = data[2];
                    idToArtist.put(id, artist);
                    idToCategory.put(id, category);
                    artistToId.put(artist, id);
                    adjList.put(id, new ArrayList<>());
                }
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    private void loadMentions(String mentionsPath) {
        try(BufferedReader br = new BufferedReader(new FileReader(mentionsPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if(data.length == 3){
                    int from = Integer.parseInt(data[0]);
                    int to = Integer.parseInt(data[1]);
                    int mentions = Integer.parseInt(data[2]);
                    adjList.get(from).add(new Edge(to, mentions));
                }
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void trouverCheminLePlusCourt(String startArtist, String endArtist) {
        int start = artistToId.get(startArtist);
        int end = artistToId.get(endArtist);

        Map<Integer, Integer> parent = new HashMap<>();
        Map<Integer, Integer> distance = new HashMap<>();
        Queue<Integer> queue = new LinkedList<>();

        queue.add(start);
        distance.put(start, 0);

        while (!queue.isEmpty()) {
            int current = queue.poll();
            if (current == end) break;

            for (Edge neighbor : adjList.getOrDefault(current, Collections.emptyList())) {
                if (!distance.containsKey(neighbor.to)) {
                    distance.put(neighbor.to, distance.get(current) + 1);
                    parent.put(neighbor.to, current);
                    queue.add(neighbor.to);
                }
            }
        }

        if (!parent.containsKey(end)) {
            throw new RuntimeException("Aucun chemin entre " + startArtist + " et " + endArtist);
        }

        LinkedList<Integer> path = new LinkedList<>();
        int node = end;
        while (node != start) {
            path.addFirst(node);
            node = parent.get(node);
        }
        path.addFirst(start);

        double totalCost = 0.0;
        for (int i = 0; i < path.size() - 1; i++) {
            int from = path.get(i);
            int to = path.get(i + 1);
            totalCost += 1.0 / getMentions(from, to);
        }

        System.out.println("Longueur du chemin : " + (path.size() - 1));
        System.out.println("Coût total du chemin : " + totalCost);
        System.out.println("Chemin :");
        for (int id : path) {
            System.out.println(idToArtist.get(id) + " (" + idToCategory.get(id) + ")");
        }
    }

    private int getMentions(int from, int to) {
        int maxMentions = 0;
        for (Edge e : adjList.getOrDefault(from, Collections.emptyList())) {
            if (e.to == to && e.mentions > maxMentions) {
                maxMentions = e.mentions;
            }
        }
        return maxMentions == 0 ? 1 : maxMentions;
    }

    public void trouverCheminMaxMentions(String startArtist, String endArtist) {
        int start = artistToId.get(startArtist);
        int end = artistToId.get(endArtist);

        Map<Integer, Integer> parent = new HashMap<>();
        Map<Integer, Double> dist = new HashMap<>();
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingDouble(n -> n.cost));

        for(int node : adjList.keySet()) dist.put(node, Double.MAX_VALUE);
        dist.put(start, 0.0);
        pq.add(new Node(start, 0.0));

        while(!pq.isEmpty()){
            Node current = pq.poll();
            if(current.id == end) break;

            for(Edge e : adjList.getOrDefault(current.id, Collections.emptyList())) {
                double newCost = current.cost + 1.0 / e.mentions;
                if(newCost < dist.get(e.to)){
                    dist.put(e.to, newCost);
                    parent.put(e.to, current.id);
                    pq.add(new Node(e.to, newCost));
                }
            }
        }

        printPath(start, end, parent, dist);
    }

    private void printPath(int start, int end, Map<Integer, Integer> parent, Map<Integer, Double> cost) {
        if(!parent.containsKey(end)){
            System.out.println("Aucun chemin trouvé.");
            return;
        }
        LinkedList<Integer> path = new LinkedList<>();
        for(int at = end; at != start; at = parent.get(at)) path.addFirst(at);
        path.addFirst(start);

        System.out.println("Longueur du chemin : " + (path.size() - 1));
        System.out.println("Coût total du chemin : " + cost.get(end));
        System.out.println("Chemin :");
        for(int node : path) {
            System.out.println(idToArtist.get(node) + " (" + idToCategory.get(node) + ")");
        }
    }

    private static class Edge {
        int to, mentions;
        Edge(int to, int mentions) {
            this.to = to;
            this.mentions = mentions;
        }
    }

    private static class Node {
        int id;
        double cost;
        Node(int id, double cost) {
            this.id = id;
            this.cost = cost;
        }
    }
}

