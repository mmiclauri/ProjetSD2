import java.util.List;

class Artist {
    private int id;
    private String name;
    private List<String> categories;

    public Artist(int id, String name, List<String> categories) {
        this.id = id;
        this.name = name;
        this.categories = categories;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + " (" + String.join(";", categories) + ")";
    }
}