import java.io.*;
import java.util.*;

class Graph {
    private final Map<Integer, Artist> artistes = new HashMap<>();
    private final Map<String, Integer> nomArtisteVersId = new HashMap<>();
    private final Map<Integer, Map<Integer, Double>> listeAdjacence = new HashMap<>();

    public Graph(String artistsFile, String mentionsFile) {
        chargerArtists(artistsFile);
        chargerMentions(mentionsFile);
    }

    private void chargerArtists(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", 3);
                int id = Integer.parseInt(parts[0]);
                String name = parts[1];
                List<String> categories = (parts.length > 2) ? Arrays.asList(parts[2].split(";")) : new ArrayList<>();

                artistes.put(id, new Artist(id, name, categories));
                nomArtisteVersId.put(name, id);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void chargerMentions(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                int fromId = Integer.parseInt(parts[0]);
                int toId = Integer.parseInt(parts[1]);
                double weight = 1.0 / Integer.parseInt(parts[2]);

                listeAdjacence.computeIfAbsent(fromId, k -> new HashMap<>()).put(toId, weight);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void trouverCheminLePlusCourt(String artisteA, String artisteB) {
        int debut = getArtisteId(artisteA);
        int fin = getArtisteId(artisteB);

        Map<Integer, Integer> parent = new HashMap<>();
        Map<Integer, Double> distance = new HashMap<>();
        Queue<Integer> file = new LinkedList<>();
        Set<Integer> visite = new HashSet<>();

        file.add(debut);
        visite.add(debut);
        parent.put(debut, null);
        distance.put(debut, 0.0);

        while (!file.isEmpty()) {
            int actuel = file.poll();
            if (actuel == fin) {
                printPath(constructPath(fin, parent, calculerLongueurChemin(parent, fin)));
                return;
            }

            for (Map.Entry<Integer, Double> entry : listeAdjacence.getOrDefault(actuel, Collections.emptyMap()).entrySet()) {
                int neighbor = entry.getKey();
                if (visite.add(neighbor)) {
                    parent.put(neighbor, actuel);
                    file.add(neighbor);
                }
            }
        }
        throw new RuntimeException("Aucun chemin entre " + artisteA + " et " + artisteB);
    }

    public void trouverCheminMaxMentions(String artisteA, String artisteB) {
        int debut = getArtisteId(artisteA);
        int fin = getArtisteId(artisteB);

        Map<Integer, Double> distance = new HashMap<>();
        Map<Integer, Integer> parent = new HashMap<>();
        PriorityQueue<Integer> pq = new PriorityQueue<>(Comparator.comparingDouble(distance::get));
        Set<Integer> visite = new HashSet<>();

        for (Integer id : artistes.keySet()) {
            distance.put(id, Double.MAX_VALUE);
        }

        distance.put(debut, 0.0);
        pq.add(debut);
        parent.put(debut, null);

        while (!pq.isEmpty()) {
            int actuel = pq.poll();
            if (actuel == fin) {
                printPath(constructPath(fin, parent, distance.get(fin)));
                return;
            }
            if (visite.add(actuel)) {
                for (Map.Entry<Integer, Double> entry : listeAdjacence.getOrDefault(actuel, Collections.emptyMap()).entrySet()) {
                    int neighbor = entry.getKey();
                    double nvDistance = distance.get(actuel) + entry.getValue();

                    if (nvDistance < distance.get(neighbor)) {
                        distance.put(neighbor, nvDistance);
                        parent.put(neighbor, actuel);
                        pq.add(neighbor);
                    }
                }
            }
        }
        throw new RuntimeException("Aucun chemin entre " + artisteA + " et " + artisteB);
    }

    private int getArtisteId(String nom) {
        return Optional.ofNullable(nomArtisteVersId.get(nom))
                .orElseThrow(() -> new RuntimeException("Artiste introuvable: " + nom));
    }

    private double calculerLongueurChemin(Map<Integer, Integer> parent, int fin) {
        double cout = 0.0;
        Integer actuel = fin;
        while (parent.get(actuel) != null) {
            cout += listeAdjacence.get(parent.get(actuel)).get(actuel);
            actuel = parent.get(actuel);
        }
        return cout;
    }

    private Chemin constructPath(int fin, Map<Integer, Integer> parent, double coutTotal) {
        List<Integer> chemin = new ArrayList<>();
        for (Integer at = fin; at != null; at = parent.get(at)) {
            chemin.add(at);
        }
        Collections.reverse(chemin);
        return new Chemin(chemin, coutTotal);
    }

    private void printPath(Chemin chemin) {
        System.out.println("Longueur du chemin : " + chemin.getLongueurChemin());
        System.out.println("Coût total du chemin : " + chemin.getCoutTotalChemin());
        System.out.println("Chemin :");

        for (int id : chemin.getArtistIds()) {
            System.out.println(artistes.get(id));
        }
    }
}