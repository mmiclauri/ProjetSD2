import java.util.List;

class Path {
    private List<Integer> artistIds;
    private double totalCost;

    public Path(List<Integer> artistIds, double totalCost) {
        this.artistIds = artistIds;
        this.totalCost = totalCost;
    }

    public List<Integer> getArtistIds() {
        return artistIds;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public int getLength() {
        return artistIds.size();
    }
}