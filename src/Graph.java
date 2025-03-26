import java.io.*;
import java.util.*;

class Graph {
    private Map<Integer, Artist> artists = new HashMap<>();
    private Map<String, Integer> artistNameToId = new HashMap<>();
    private Map<Integer, Map<Integer, Double>> adjacencyList = new HashMap<>();

    public Graph(String artistsFile, String mentionsFile) {
        parseArtists(artistsFile);
        parseMentions(mentionsFile);
    }

    private void parseArtists(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", 3);
                int id = Integer.parseInt(parts[0]);
                String name = parts[1];
                List<String> categories = parts.length > 2 ? Arrays.asList(parts[2].split(";")) : new ArrayList<>();
                Artist artist = new Artist(id, name, categories);
                artists.put(id, artist);
                artistNameToId.put(name, id);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseMentions(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                int fromId = Integer.parseInt(parts[0]);
                int toId = Integer.parseInt(parts[1]);
                double weight = 1.0 / Integer.parseInt(parts[2]);

                adjacencyList.putIfAbsent(fromId, new HashMap<>());
                adjacencyList.get(fromId).put(toId, weight);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void trouverCheminLePlusCourt(String artisteA, String artisteB) {
        int a = getArtistId(artisteA);
        int b = getArtistId(artisteB);
        Map<Integer, Integer> parent = new HashMap<>();
        Map<Integer, Double> distance = new HashMap<>();
        Queue<Integer> queue = new LinkedList<>();
        Set<Integer> visited = new HashSet<>();

        queue.add(a);
        visited.add(a);
        parent.put(a, null);
        distance.put(a, 0.0);

        while (!queue.isEmpty()) {
            int current = queue.poll();
            if (current == b) {
                double totalCost = distance.get(b);
                imprimerChemin(construireChemin(a, b, parent, totalCost));
                return;
            }

            for (Map.Entry<Integer, Double> entry : adjacencyList.getOrDefault(current, Collections.emptyMap()).entrySet()) {
                int neighbor = entry.getKey();
                double weight = entry.getValue();
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    parent.put(neighbor, current);
                    distance.put(neighbor, distance.get(current) + weight);
                    queue.add(neighbor);
                }
            }
        }
        throw new RuntimeException("Aucun chemin entre " + artisteA + " et " + artisteB);
    }

    public void trouverCheminMaxMentions(String artisteA, String artisteB) {
        int idA = getArtistId(artisteA);
        int idB = getArtistId(artisteB);
        Map<Integer, Double> distance = new HashMap<>();
        Map<Integer, Integer> precedent = new HashMap<>();
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingDouble(a -> a[1]));

        distance.put(idA, 0.0);
        pq.add(new int[]{idA, 0});
        precedent.put(idA, null);

        while (!pq.isEmpty()) {
            int[] node = pq.poll();
            int current = node[0];

            if (current == idB) {
                imprimerChemin(construireChemin(idA, idB, precedent, distance.get(idB)));
                return;
            }

            for (Map.Entry<Integer, Double> entry : adjacencyList.getOrDefault(current, Collections.emptyMap()).entrySet()) {
                int neighbor = entry.getKey();
                double newDist = distance.getOrDefault(current, Double.MAX_VALUE) + entry.getValue();

                if (newDist < distance.getOrDefault(neighbor, Double.MAX_VALUE)) {
                    distance.put(neighbor, newDist);
                    precedent.put(neighbor, current);
                    pq.add(new int[]{neighbor, (int) newDist});
                }
            }
        }
        throw new RuntimeException("Aucun chemin entre " + artisteA + " et " + artisteB);
    }

    private int getArtistId(String name) {
        if (!artistNameToId.containsKey(name)) {
            throw new RuntimeException("Artiste introuvable: " + name);
        }
        return artistNameToId.get(name);
    }

    private Path construireChemin(int start, int end, Map<Integer, Integer> parent, double totalWeight) {
        List<Integer> path = new ArrayList<>();
        for (Integer at = end; at != null; at = parent.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);
        return new Path(path, totalWeight);
    }

    private void imprimerChemin(Path path) {
        System.out.println("Longueur du chemin : " + path.getLength());
        System.out.println("Coût total du chemin : " + path.getTotalCost());
        System.out.println("Chemin :");

        List<Integer> nodeIds = path.getArtistIds();
        for (int nodeId : nodeIds) {
            System.out.println(artists.get(nodeId));
        }
    }
}