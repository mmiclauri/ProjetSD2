import java.util.List;

class Artist {
    private int id;
    private String nom;
    private List<String> categories;

    public Artist(int id, String nom, List<String> categories) {
        this.id = id;
        this.nom = nom;
        this.categories = categories;
    }

    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    @Override
    public String toString() {
        return nom + " (" + String.join(";", categories) + ")";
    }
}