import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Graph {
    private Map<Integer, Artist> artistsById;
    private Map<String, Artist> artistsByName;
    private Map<Integer, Map<Integer, Double>> connections;

    public Graph(String file, String file1) {
        artistsById = new HashMap<>();
        artistsByName = new HashMap<>();
        connections = new HashMap<>();
        loadArtists(file);
        loadConnections(file1);
    }

    private void loadArtists(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                int firstCommaIndex = line.indexOf(',');
                if (firstCommaIndex == -1) {
                    continue;
                }

                String idStr = line.substring(0, firstCommaIndex);
                int id;
                try {
                    id = Integer.parseInt(idStr.trim());
                } catch (NumberFormatException e) {
                    continue;
                }

                int secondCommaIndex = line.indexOf(',', firstCommaIndex + 1);
                String name;
                List<String> categories = new ArrayList<>();

                if (secondCommaIndex == -1) {
                    name = line.substring(firstCommaIndex + 1).trim();
                } else {
                    name = line.substring(firstCommaIndex + 1, secondCommaIndex).trim();
                    String categoriesStr = line.substring(secondCommaIndex + 1).trim();
                    String[] cats = categoriesStr.split(";");
                    for (String cat : cats) {
                        categories.add(cat.trim());
                    }
                }

                Artist artist = new Artist(id, name, categories);
                artistsById.put(id, artist);
                artistsByName.put(name, artist);
                connections.put(id, new HashMap<>());
            }
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la lecture du fichier d'artistes: " + e.getMessage());
        }
    }

    private void loadConnections(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    int artistIdA = Integer.parseInt(parts[0].trim());
                    int artistIdB = Integer.parseInt(parts[1].trim());
                    double mentions = Double.parseDouble(parts[2].trim());
                    double weight = 1.0 / mentions;

                    if (!artistsById.containsKey(artistIdA) || !artistsById.containsKey(artistIdB)) {
                        continue;
                    }

                    connections.get(artistIdA).put(artistIdB, weight);
                    connections.get(artistIdB).put(artistIdA, weight);
                }
            }
        } catch (IOException | NumberFormatException e) {
            throw new RuntimeException("Erreur lors de la lecture du fichier de connexions: " + e.getMessage());
        }
    }

    public void trouverCheminLePlusCourt(String artistA, String artistB) {
        if (!artistsByName.containsKey(artistA) || !artistsByName.containsKey(artistB)) {
            throw new RuntimeException("Aucun chemin entre " + artistA + " et " + artistB);
        }

        int sourceId = artistsByName.get(artistA).getId();
        int destId = artistsByName.get(artistB).getId();

        try {
            PathFinder pathFinder = new BFS();
            Path path = pathFinder.findPath(this, sourceId, destId);

            printPath(path);
        } catch (Exception e) {
            throw new RuntimeException("Aucun chemin entre " + artistA + " et " + artistB);
        }
    }

    public void trouverCheminMaxMentions(String artistA, String artistB) {
        if (!artistsByName.containsKey(artistA) || !artistsByName.containsKey(artistB)) {
            throw new RuntimeException("Aucun chemin entre " + artistA + " et " + artistB);
        }

        int sourceId = artistsByName.get(artistA).getId();
        int destId = artistsByName.get(artistB).getId();

        try {
            PathFinder pathFinder = new Dijkstra();
            Path path = pathFinder.findPath(this, sourceId, destId);

            printPath(path);
        } catch (Exception e) {
            throw new RuntimeException("Aucun chemin entre " + artistA + " et " + artistB);
        }
    }

    private void printPath(Path path) {
        System.out.println("Longueur du chemin : " + path.getLength());
        System.out.println("Coût total du chemin : " + path.getTotalWeight());
        System.out.println("Chemin :");

        List<Integer> nodeIds = path.getNodeIds();
        for (int nodeId : nodeIds) {
            System.out.println(artistsById.get(nodeId));
        }
    }

    public Map<Integer, Double> getNeighbors(int artistId) {
        return connections.getOrDefault(artistId, new HashMap<>());
    }

    public double getConnectionWeight(int artistId1, int artistId2) {
        Map<Integer, Double> neighbors = connections.get(artistId1);
        if (neighbors != null && neighbors.containsKey(artistId2)) {
            return neighbors.get(artistId2);
        }
        return -1;
    }

    public Set<Integer> getAllArtistIds() {
        return artistsById.keySet();
    }
}