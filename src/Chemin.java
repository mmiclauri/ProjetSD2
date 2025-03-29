import java.util.List;

class Chemin {
    private List<Integer> artistIds;
    private double coutTotal;

    public Chemin(List<Integer> IdsArtistes, double coutTotal) {
        this.artistIds = IdsArtistes;
        this.coutTotal = coutTotal;
    }

    public List<Integer> getArtistIds() {
        return artistIds;
    }

    public double getCoutTotalChemin() {
        return coutTotal;
    }

    public int getLongueurChemin() {
        return artistIds.size()-1;
    }
}