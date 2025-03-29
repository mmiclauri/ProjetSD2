import java.util.List;

class Chemin {
    private List<Integer> artistIds;
    private int longueurChemin;
    private double coutTotal;

    public Chemin(List<Integer> IdsArtistes, double coutTotal, int longueurChemin) {
        this.artistIds = IdsArtistes;
        this.longueurChemin = longueurChemin;
        this.coutTotal = coutTotal;
    }

    public List<Integer> getArtistIds() {
        return artistIds;
    }

    public double getCoutTotalChemin() {
        return coutTotal;
    }

    public int getLongueurChemin() {
        return longueurChemin;
    }
}