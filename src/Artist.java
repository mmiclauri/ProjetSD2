import java.util.List;

public class Artist {
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

  public List<String> getCategories() {
    return categories;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(name).append(" (");
    if (categories != null && !categories.isEmpty()) {
      sb.append(String.join(";", categories));
    }
    sb.append(")");
    return sb.toString();
  }
}