package Administration;

public class HighscoreItem implements Comparable<HighscoreItem> {

    private String name;
    private int score;

    public HighscoreItem(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return name + ": " + score;
    }

    @Override
    public int compareTo(HighscoreItem other) {
        // Sortieren nach Score in absteigender Reihenfolge
        return Integer.compare(other.getScore(), this.score);
    }
}
