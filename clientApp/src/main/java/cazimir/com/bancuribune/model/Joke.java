package cazimir.com.bancuribune.model;

public class Joke {
    private String jokeText;
    private boolean approved;
    private long createdAt;

    public Joke() {
    }

    public Joke(String text) {
        this.jokeText = text;
        this.approved = false;
        this.createdAt = System.currentTimeMillis();
    }

    public String getJokeText() {
        return jokeText;
    }

    public boolean isApproved() {
        return approved;
    }

    public long getCreatedAt() {
        return createdAt;
    }
}
